package worker;

import logger.DistributedLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Worker {
    public static ArrayList<String> netJobs = new ArrayList<String>();
    public static ArrayList<int[]> netClocks = new ArrayList<int[]>();
    public static int regPort = 12345;
    public static int[] logicalClock;
    public static int logClockPos;

    private ArrayList<String> fileJobs;
    private String identifier;
    private HashMap<String, String> lastJob;
    private DistributedLogger distLogger;
    private ArrayList<String> processes;
    private int sortRoot;
    private Random rand;
    private boolean worked = false;


    public Worker(String identifier, int sortRoot, String processesFile, String eventsfile) {
        this.fileJobs = new ArrayList<String>();
        this.processes = new ArrayList<String>();
        this.identifier = identifier;
        this.sortRoot = sortRoot;
        this.rand = new Random(this.sortRoot);
        this.lastJob = new HashMap<String, String>();

        loadProcesses(processesFile);
        loadFileJobs(eventsfile);

        logicalClock = new int[processes.size()];
        logClockPos = processes.indexOf(this.identifier);
        for(int i=0;i<logicalClock.length;i++) logicalClock[i] = 0;

        System.err.println("FROM      TO        MESSAGE");
    }

    public int initializeLogger() {
        try {
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", regPort);
            this.distLogger = (DistributedLogger) reg.lookup("Log");
            this.distLogger.processUp(this.identifier);
        } catch (Exception e) {
            System.err.println(e.toString());
            return -1;
        }
        return 0;
    }

    private int loadFromFile(String fileName, ArrayList<String> arrayToSaveLines) {
        try {
            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            while ((line = br.readLine()) != null)
                arrayToSaveLines.add(line);

            return 0;

        } catch (Exception e) {
            System.err.println(e.toString());
            return -1;
        }
    }

    public int loadProcesses(String file) {
        return loadFromFile(file, this.processes);
    }

    public int loadFileJobs(String file) {
        return loadFromFile(file, this.fileJobs);
    }

    private String sortWorker(){
        String worker;
        while (true) {
            int idx = this.rand.nextInt(this.processes.size());
            String proc = this.processes.get(idx);
            String[] procSplit = proc.split(",");
            worker = procSplit[0];
            if ( !(worker.equals("Log")) && !(worker.equals(this.identifier)) ) break;
        }
        return worker;
    }

    private void endProcess() {
        try {
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", regPort);
            this.distLogger = (DistributedLogger) reg.lookup("Log");
            this.distLogger.processEnd(this.identifier);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private void checkNetClock(String proc) {
        if (netClocks.size() == 0) return;

        int[] netclock = netClocks.remove(0);
        for(int i=0;i<netclock.length-1;i++) {
            if ( logicalClock[i] < netclock[i] ) logicalClock[i] = netclock[i];
        }

        logicalClock[processes.indexOf(proc)] += 1;

    }

    private int doNetJob(String worker, String message) {
        logicalClock[logClockPos] += 1;
        try {
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", regPort);
            DistributedJob myWorker = (DistributedJob) reg.lookup(worker);
            myWorker.jobByString(message, this.identifier, logicalClock);
            return 0;
        } catch (Exception e){
            System.err.println(e.toString());
            return -1;
        }
    }

    public int doAJob(){
        String from = "";
        String to = "";
        String job = "";

        this.worked = false;

        // First, try to do a net job
        if (Worker.netJobs.size() > 0) {
            worked = true;
            String netjob_all = Worker.netJobs.remove(0);
            String[] netjob = netjob_all.split(",");
            from = netjob[0];
            job = netjob[1];
            to = this.identifier;
            this.worked = true;
            checkNetClock(from);

        } else if (this.fileJobs.size() > 0) {
            // Job from file

            String filejob_all = this.fileJobs.remove(0);
            String[] filejob = filejob_all.split(",");
            job = filejob[1];

            if ("L".equalsIgnoreCase(filejob[0])) {
                // Local job !
                from = "LOCAL";
                to = "LOCAL";
                logicalClock[logClockPos] += 1;
            } else if ("S".equalsIgnoreCase(filejob[0])) {
                // Net job !!
                from = this.identifier;
                to = sortWorker();
                if (doNetJob(to, job) < 0) {
                    System.err.println("Erro ao enviar a mensagem " + job + " para " + to);
                    return -1;
                }
            }

            this.worked = true;
            if (this.fileJobs.size() == 0) endProcess();

        } else {
            return -1;
        }

        if (this.worked) {
            this.lastJob.put("from", from);
            this.lastJob.put("to", to);
            this.lastJob.put("message", job);
            String line = "";
            line += String.format("%1$-8s", from) + "  ";
            line += String.format("%1$-8s", to) + "  ";
            line += String.format("%1$-10s", job);
            System.err.println(line);
        }

        return 0;
    }

    public void sendToLogger() {
        if (Executer.end || !this.worked) return;

        // logicalClock[logClockPos] += 1;  // deve ser contabilizado?

        try {
            this.distLogger.logSave(this.identifier, this.lastJob.get("from"), this.lastJob.get("to"), this.lastJob.get("message"), logicalClock);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

}
