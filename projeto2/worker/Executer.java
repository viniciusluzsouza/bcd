package worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Executer {
    public static boolean start = false;
    public static boolean end = false;

    public static void main(String args[]) {
        String DISTOBJNAME, processesFile, eventsfile;
        int seed, waitTime, jitterTimer;

        if (args.length < 6) {
            System.err.println("Execute no seguinte formato:");
            System.err.println("java nomeDaClasse pID Semente tempoEspera tempoJitter arquivo-com-processos arquivo-com-eventos");
            return;
        } else {
            try {
                DISTOBJNAME = args[0];
                seed = Integer.parseInt(args[1]);
                waitTime = Integer.parseInt(args[2]);
                jitterTimer = Integer.parseInt(args[3]);
                processesFile = args[4];
                eventsfile = args[5];
            } catch (Exception e) {
                System.err.println("Parâmetros inválidos.");
                return;
            }
        }

        try {
            // Create distributed job
            JobDispatcher jobDispatcher = new JobDispatcher();
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");

            DistributedJob distributedJob = (DistributedJob) UnicastRemoteObject.exportObject(jobDispatcher, 0);
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 12345);
            reg.bind(DISTOBJNAME, distributedJob);

            // Create worker
            Worker worker = new Worker(DISTOBJNAME, seed, processesFile, eventsfile);
            if (worker.initializeLogger() < 0)
                System.err.println("Erro ao inicializar logger.");

            while (true) {
                if (start) {
                    if (end) break;

                    Thread.sleep(waitTime);
                    worker.doAJob();
                    Thread.sleep(jitterTimer);
                    worker.sendToLogger();
                } else {
                    Thread.sleep(500);
                }
            }

        } catch (Exception e) {
            System.err.println(e.toString());
            return;
        }

        try {
            Thread.sleep(1000);
            System.exit(0);
        } catch (Exception e) {
            System.exit(0);
        }

    }
}

