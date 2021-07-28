package logger;

import worker.DistributedJob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class LogServer {
    public static ArrayList<String> upProcess = new ArrayList<String>();
    public static ArrayList<String> endProcess = new ArrayList<String>();
    public static ArrayList<String> processes = new ArrayList<String>();
    public static ArrayList<String> logMessages = new ArrayList<String>();
    public static ArrayList<int[]> logicalClocks = new ArrayList<int[]>();

    private String serverName;
    private int port;
    private final String DISTOBJNAME;
    private String processFile;
    private Logger logger;
    private Registry register;

    public LogServer(String processFile) {
        this.serverName = "127.0.0.1";
        this.port = 12345;
        this.DISTOBJNAME = "Log";
        this.processFile = processFile;
    }

    private int readProcesses() {
        try {
            File file = new File(this.processFile);
            BufferedReader br = new BufferedReader(new FileReader(file));

            String proc;
            while ((proc = br.readLine()) != null)
                processes.add(proc);

            return 0;

        } catch (Exception e) {
            System.err.println(e.toString());
            return -1;
        }
    }

    public int initialize() {
        if (this.readProcesses() < 0) return -1;

        try {
            this.logger = new Logger(processes);
            System.setProperty("java.rmi.server.hostname", this.serverName);

            DistributedLogger stub = (DistributedLogger)
                    UnicastRemoteObject.exportObject(this.logger, 0);

            this.register = LocateRegistry.createRegistry(this.port);
            this.register.bind(this.DISTOBJNAME, stub);

            return 0;

        } catch (Exception e) {
            System.err.println(e.toString());
            return -1;
        }
    }

    private boolean checkUpProcess() {
        boolean up = true;
        for(int i=0;i<processes.size();i++) {
            String proc = processes.get(i);
            if ("Log".equals(proc)) continue;

            if ( !(upProcess.contains(proc)) ) {
                up = false;
                break;
            }
        }
        return up;
    }

    private boolean checkEndProcess() {
        boolean end = true;
        for(int i=0;i<processes.size();i++) {
            String proc = processes.get(i);
            if ("Log".equals(proc)) continue;

            if ( !(endProcess.contains(proc)) ) {
                end = false;
                break;
            }
        }
        return end;
    }

    private void upProcess() {
        for(int i=0;i<processes.size();i++) {
            try {
                String proc = processes.get(i);
                if ("Log".equals(proc)) continue;

                Registry reg = LocateRegistry.getRegistry("127.0.0.1", this.port);
                DistributedJob stub = (DistributedJob) reg.lookup(proc);
                stub.start();
            } catch (Exception e) {
                System.err.println("Up process Exception");
            }
        }
    }

    private void endProcess() {
        for(int i=0;i<processes.size();i++) {
            try {
                String proc = processes.get(i);
                if ("Log".equals(proc)) continue;

                Registry reg = LocateRegistry.getRegistry("127.0.0.1", this.port);
                DistributedJob stub = (DistributedJob) reg.lookup(proc);
                stub.end();
            } catch (Exception e) {
                System.err.println("End process Exception");
            }
        }
    }

    private ArrayList<int[]> listSort(ArrayList<int[]> l) {
        ArrayList<int[]> ret = new ArrayList<int []>();
        ArrayList<int[]> a = l;

        int vectSize = l.get(0).length;
        int size = a.size();
        int fixSize = size;
        for(int i=0;i<fixSize;i++) {
            int[] menor = a.get(0);
            for(int j=0;j<size;j++) {
                int[] prox = a.get(j);
                boolean check = true;
                for(int z=0;z<vectSize;z++)
                    if ( !(prox[z] <= menor[z]) ) check = false;
                if (check)
                    menor = prox;
            }
            ret.add(menor);
            a.remove(menor);
            size = a.size();
        }
        return ret;
    }

    private String vet2String(int[] v) {
        String ret = "[";
        int i;
        for(i=0;i<v.length-2;i++) {
            ret += v[i] + ",";
        }
        ret += v[i] + "]";
        return ret;
    }

    private void printEnd() {
        ArrayList<int[]> sorted = listSort(logicalClocks);
        ArrayList<String> toPrint = new ArrayList<String>();
        for(int i=0;i<sorted.size();i++) {
            String lc = vet2String(sorted.get(i));
            for(int j=0;j<logMessages.size();j++) {
                String msg = logMessages.get(j);
                if (msg.indexOf(lc) != -1) {
                    if (!toPrint.contains(msg))
                        toPrint.add(msg);
                }
            }
        }

        System.err.println("# Timestamp                ORIGIN    FROM      TO        Message     LogicalClock");
        for(int i=0;i<toPrint.size();i++) System.err.println(toPrint.get(i));
    }

    public void run() {
        try {
            this.logger.printTitle();
            while ( !checkUpProcess() ) Thread.sleep(500);
            upProcess();
            this.logger.printWaitEnd();
            while ( !checkEndProcess() ) Thread.sleep(1000);
            Thread.sleep(5000);
            endProcess();
            printEnd();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

}