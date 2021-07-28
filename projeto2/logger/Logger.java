package logger;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Logger implements DistributedLogger {
    private ArrayList<String> _acceptableProcess;

    public Logger(ArrayList<String> acceptableProcess) {
        this._acceptableProcess = acceptableProcess;
    }

    @Override
    public void processUp(String process) throws RemoteException {
        if ( !LogServer.processes.contains(process) ) return;
        LogServer.upProcess.add(process);
    }

    @Override
    public void processEnd(String process) throws RemoteException {
        if ( !LogServer.processes.contains(process) ) return;
        LogServer.endProcess.add(process);
    }

    @Override
    public void logSave(String origin, String from, String to, String message, int[] logicClock) throws RemoteException {
        if ( !LogServer.processes.contains(origin) ) return;

        LocalDateTime date = LocalDateTime.now();
        String line = "";
        line += String.format("%1$-25s", date) + "  ";
        line += String.format("%1$-8s", origin) + "  ";
        line += String.format("%1$-8s", from) + "  ";
        line += String.format("%1$-8s", to) + "  ";
        line += String.format("%1$-10s", message) + "  ";
        String logClock = "[";
        int i;
        for(i=0;i<logicClock.length-2;i++) {
            logClock += logicClock[i] + ",";
        }
        logClock += logicClock[i] + "]";

        line += String.format("%1$-10s", logClock);

        LogServer.logicalClocks.add(logicClock);
        LogServer.logMessages.add(line);

    }

    public void printTitle() {
        System.err.println("Aguardando a inicializacao dos processos ...");
    }

    public void printWaitEnd() {
        System.err.println("O resultado sera exibido no fim.\nAguarde a finalizacao dos eventos ....\n");
    }
}
