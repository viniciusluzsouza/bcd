package logger;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DistributedLogger extends Remote {
    public void processUp(String process) throws RemoteException;
    public void processEnd(String process) throws RemoteException;
    public void logSave(String origin, String from, String to, String message, int[] logicClock) throws RemoteException;
}