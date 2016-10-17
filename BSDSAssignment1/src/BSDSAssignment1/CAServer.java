/**
 * Created by Ian Gortan on 9/19/2016.
 */
package BSDSAssignment1;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

//Skeleton of CAServer supporting both BSDS interfaces

public class CAServer implements BSDSPublishInterface, BSDSSubscribeInterface {
    private static Map<String, LinkedList<BSDSContent>> messagesByTopic = new HashMap<>();
    private static Map<Integer, String> subscriberidToTopicMap = new HashMap<>();
    private static Map<Integer, String> publisheridToTopicMap = new HashMap<>();
    private static Map<BSDSContent, Set<Integer>> sentSubscriberIdByMessage = new HashMap<>();

    private static Integer publisherID = 0;
    private static Integer subscriberID = 0;

    public static synchronized Map<String, LinkedList<BSDSContent>> getMessagesByTopic() {
        return messagesByTopic;
    }

    public int registerPublisher(String name, String topic)
            throws RemoteException {

        System.out.println("Publisher: " + name + " Topic: " + topic);
        synchronized (publisherID) {
            publisherID++;

            synchronized (publisheridToTopicMap) {
                publisheridToTopicMap.put(publisherID, topic);
            }
        }

        return publisherID;
    }

    // publishes a message to the server
    public void publishContent(int publisherID, String title, String message, long timeToLive)
            throws RemoteException {
        BSDSContent content = new BSDSContent();
        content.setMessage(message);
        content.setTitle(title);
        content.setTimeToLIve(timeToLive);

        String topic = publisheridToTopicMap.get(publisherID);

        synchronized (messagesByTopic) {
            LinkedList<BSDSContent> messages = messagesByTopic.get(topic);
            if (messages == null) {
                messages = new LinkedList<>();
                messagesByTopic.put(topic, messages);
            }
            synchronized (messages) {
                messages.add(content);
            }
        }

        synchronized (sentSubscriberIdByMessage) {
            sentSubscriberIdByMessage.put(content, new HashSet<>());
        }
    }

    public int registerSubscriber(String topic) throws RemoteException {
        System.out.println("Subscriber " + subscriberID + " Topic is  " + topic);
        synchronized (subscriberID) {
            subscriberID++;
            subscriberidToTopicMap.put(subscriberID, topic);
        }

        return subscriberID;
    }

    // gets next outstanding message for a subscription
    public String getLatestContent(int subscriberID) throws RemoteException {
        BSDSContent lastestContent = null;
        String topic = subscriberidToTopicMap.get(subscriberID);
        LinkedList<BSDSContent> messages = messagesByTopic.get(topic);

        if (messages != null) {
            synchronized (messages) {
                for (BSDSContent message : messages) {
                    synchronized (sentSubscriberIdByMessage) {
                        if (!sentSubscriberIdByMessage.get(message).contains(subscriberID)) {
                            lastestContent = message;
                            break;
                        }
                    }
                }
            }
        }


        if (lastestContent == null) {
            return null;
        } else {
            synchronized (sentSubscriberIdByMessage) {
                sentSubscriberIdByMessage.get(lastestContent).add(subscriberID);
            }
            return lastestContent.getMessage();
        }
    }

    public static void main(String args[]) {

        // When requests arrive, RMI do a thread per request
        try {
            CAServer objPub = new CAServer();
            CAServer objSub = new CAServer();
            System.out.println("Server Initializing");
            BSDSPublishInterface pStub = (BSDSPublishInterface) UnicastRemoteObject.exportObject(objPub, 0);
            BSDSSubscribeInterface sStub = (BSDSSubscribeInterface) UnicastRemoteObject.exportObject(objSub, 0);
            System.out.println("stubs created ....");
            // Bind the remote object's stub in the local host registry
            LocateRegistry.createRegistry(1099);

            Registry registry = LocateRegistry.getRegistry();
            System.out.println("Ref to Registry ok");
            try {
                registry.bind("CAServerPublisher", pStub);
                registry.bind("CAServerSubscriber", sStub);
            } catch (Exception e) {
                System.out.println("Caught already bound exception, probably safe to continue in dev mode" + e.toString());
            }
            System.out.println("CAServer ready");
            Timer timer = new Timer();
            timer.schedule(new deleteOutdatedMessages(), 0, 5000);
            timer.schedule(new sizeMonitor(), 0, 1000);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

        // Java plotting
    }
}

class deleteOutdatedMessages extends TimerTask {

    private static int counter = 0;

    @Override
    public void run() {
//        System.out.println("Current seconds: " + counter);
        Map<String, LinkedList<BSDSContent>> messagesByTopic = CAServer.getMessagesByTopic();
        for (String topic : messagesByTopic.keySet()) {
            LinkedList<BSDSContent> messages = messagesByTopic.get(topic);
//            System.out.println("Topic: " + topic + " has " + messages.size() + " messages.");
            synchronized (messages) {
                while (!messages.isEmpty() && messages.peek().getTimeToLIve() < System.currentTimeMillis()) {
                    messages.poll();
                }
            }
        }
        counter += 5;
    }
}

class sizeMonitor extends TimerTask {

    private static int counter = 0;

    @Override
    public void run() {
        System.out.println("Current seconds: " + counter);
        Map<String, LinkedList<BSDSContent>> messagesByTopic = CAServer.getMessagesByTopic();
        for (String topic : messagesByTopic.keySet()) {
            System.out.print("Topic: " + topic + " has " + messagesByTopic.get(topic).size() + " messages;  ");
        }
        System.out.print('\n');
        counter += 1;
    }
}