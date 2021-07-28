package worker;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DistributedJob extends Remote {
    public void start() throws RemoteException;
    public void end() throws RemoteException;
    public void jobByString(String something, String from, int[] logicalClock) throws RemoteException;
}
