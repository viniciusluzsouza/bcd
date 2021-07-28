package logger;

public class Executer {
    public static void main(String args[]) {
        String fileName = "/Users/vini/IdeaProjects/Projeto2_Logger/src/main/java/process.txt";
        if (args.length > 0) {
            fileName = args[0];
        }

        LogServer logserver = new LogServer(fileName);
        if (logserver.initialize() < 0)
            System.err.println("Initialize error");

        logserver.run();

        try {
            Thread.sleep(1000);
            System.exit(0);
        } catch (Exception e) {
            System.exit(0);
        }

    }

}
