
/**
 * Created by Ian Gortan on 9/20/2016.
 */

package BSDSAssignment1;
import java.rmi.Remote;
import java.rmi.RemoteException;



public interface BSDSPublishInterface extends Remote{
    // returns unique publisherID. Each publisher publishes messages on a single topic
    int registerPublisher(String name, String topic) throws RemoteException;

    // publishes a message to the server
    void publishContent (int publisherID,
                            String title,
                            String message,
                            long TimeToLive) throws RemoteException;
}
