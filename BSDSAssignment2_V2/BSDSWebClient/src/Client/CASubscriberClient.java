package Client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by songyang on 11/20/16.
 */
public class CASubscriberClient {
    private static final String serverURI = "http://localhost:8080/BSDSWebApplication/webresources/BSDS/";

    public static int register(String topic) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        WebTarget target = client.target(serverURI + "subscriber/" + topic);
        response = target.request().post(Entity.entity(null, MediaType.TEXT_PLAIN));
        try {
            int subscriberId = Integer.parseInt((String) response.readEntity(String.class));
            response.close();
            return subscriberId;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static String getLatestMessage(int subscriberId) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        WebTarget target = client.target(serverURI + "subscriber/" + subscriberId + "/lastestContent");
        response = target.request().buildGet().invoke();
        if (response.getEntity() == null) {
            return null;
        }
        String message = response.readEntity(String.class);
        response.close();
        return message;
    }

    public static void main(String[] args) {

//        Scanner scan = new Scanner(System.in);
//        System.out.println("Please specify how many subscribers you want: ");
//        int threadNum = scan.nextInt();
        int threadNum = 2;
        for (int i = 0; i < threadNum; i++) {
//            System.out.println("Please enter a topic for subscriber " + i + ": ");
//            String topic = scan.nextLine();
            SubClientThread thread = new SubClientThread("Topic" + i);
            new Thread(thread).start();
        }
    }
}

class SubClientThread implements Runnable {
    private static final int INITIAL_WAITING_TIME = 100; // Milliseconds
    private String topic;
    private int id;

    public SubClientThread(String topic) {
        this.topic = topic;
    }

    public void run() {
        try {
            this.id = CASubscriberClient.register(this.topic);
            System.out.println("Subscriber " + this.id + " created.");

            int factor = 1;
            int messageCount = 0;
            Long startTime = null;
            while (true) {
                String message = CASubscriberClient.getLatestMessage(this.id);
                if (message == null || message.length() == 0) {
                    Thread.sleep(INITIAL_WAITING_TIME * factor);
                    factor *= 2;
                    if (startTime != null && factor == 2) {
                        System.out.println("Subscriber " + id + " runs for " + Long.toString(System.currentTimeMillis() - startTime));
//                        break; // thread exits
                    }
                } else {
                    messageCount++;
                    if (startTime == null) {
                        startTime = System.currentTimeMillis();
                    }
                    if (messageCount % 1000 == 0) {
//                        System.out.println(message + " " + message.length());
                        System.out.println("Subscriber " + id + " received " + messageCount + " messages");
                    }
                    factor = 1;
                }
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}