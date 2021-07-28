package worker;

import java.rmi.RemoteException;

public class JobDispatcher implements DistributedJob {
    @Override
    public void start() throws RemoteException {
        Executer.start = true;
    }

    @Override
    public void end() throws RemoteException {
        Executer.end = true;
    }

    @Override
    public void jobByString(String something, String from, int[] logicalClock) throws RemoteException {
        String netjob = from + ',' + something;
        Worker.netJobs.add(netjob);
        Worker.netClocks.add(logicalClock);
    }

}
