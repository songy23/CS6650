package BSDSAssignment1;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Scanner;

// Simple client to test publishing to CAServer over RMI

public class CAPubClient {

    private CAPubClient() {}

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        Scanner scan = new Scanner(System.in);
//        System.out.println("Please specify how many publishers you want: ");
//        int threadNum = scan.nextInt();
        int threadNum = 4;
        String[] topics = new String[]{"News", "News", "Soccer", "Junk"};
        String[] names = new String[]{"Pub1", "Pub2", "Pub3", "Pub4"};
        int[] messageNums = new int[]{10000, 10000, 10000, 10000};
        for (int i = 0; i < threadNum; i++) {

//            System.out.println("Please enter a topic for publisher " + i + " : ");
//            String topic = scan.nextLine();
//            System.out.println("Please enter a name for publisher " + i + " : ");
//            String name = scan.nextLine();
//            System.out.println("Please specify how many messages this publisher should send: ");
//            int messageNum = scan.nextInt();
            PubClientThread thread = new PubClientThread(topics[i], names[i], messageNums[i], host);

            new Thread(thread).start();
        }
    }
}    

class PubClientThread implements Runnable {
    private String host;
    private String topic;
    private String name;
    private int messageNum;
    private static final int TIME_TO_LIVE = 45;

    public PubClientThread() {}
    public PubClientThread(String topic, String name, int messageNum, String host) {
        this.topic = topic;
        this.name = name;
        this.messageNum = messageNum;
        this.host = host;
    }

    @Override
    public void run() {
        try {
//            System.out.println ("Publisher Client Starter");
            Registry registry = LocateRegistry.getRegistry(this.host, 1099);
//            System.out.println ("Connected to registry");
            BSDSPublishInterface CAServerStub = (BSDSPublishInterface) registry.lookup("CAServerPublisher");
//            System.out.println ("Stub initialized");

            int id = CAServerStub.registerPublisher(name, topic);
//            System.out.println("Pub id = " + Integer.toString(id));

            for (int i = 0; i < messageNum; i++) {
                BSDSMessage randomMessage = RandomMessage.generateRandomMessage();
                CAServerStub.publishContent(id, randomMessage.getTitle(), randomMessage.getMessage(), System.currentTimeMillis() + 1000 * TIME_TO_LIVE);
            }

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

class RandomMessage {
    private RandomMessage() {}

    private static final int TITLE_LENGTH = 7;
    private static final int CONTENT_LENGTH = 140;
    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static BSDSMessage generateRandomMessage() {
        BSDSMessage message = new BSDSMessage();
        message.setTitle(generateRandomString(TITLE_LENGTH));
        message.setMessage(generateRandomString(CONTENT_LENGTH));
        return message;
    }

    public static String generateRandomString (int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
}