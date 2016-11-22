package Client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;

/**
 * Created by songyang on 11/20/16.
 */
public class CAPublisherClient {
    private static String serverURI = "http://localhost:8080/BSDSAssignment2_war_exploded/api/BSDS/";

    public static int register(String topic, String name) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        WebTarget target = client.target(serverURI + "publisher/" + topic);
        response = target.request().post(Entity.entity(null, MediaType.TEXT_PLAIN));
        try {
            int publisherId = Integer.parseInt((String) response.readEntity(String.class));
            response.close();
            return publisherId;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static void publishContent(int publisherId, String title, String message) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(serverURI + "publisher/" + publisherId + "/content" + "?title=" + title);
        target.request().buildPost(Entity.entity(message, MediaType.TEXT_PLAIN)).invoke();
    }

    public static List<String> getMostPopularTerms(int nums) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        WebTarget target = client.target(serverURI + "publisher/popularTerms/" + nums);
        response = target.request().buildGet().invoke();
        List<String> terms = (List<String>) response.readEntity(List.class);
        return terms;
    }


    public static void main(String[] args) {
//        Scanner scan = new Scanner(System.in);
//        System.out.println("Please specify how many publishers you want: ");
//        int threadNum = scan.nextInt();
        int threadNum = 3;

        for (int i = 0; i < threadNum; i++) {

//            System.out.println("Please enter a topic for publisher " + i + " : ");
//            String topic = scan.nextLine();
//            System.out.println("Please enter a name for publisher " + i + " : ");
//            String name = scan.nextLine();
//            System.out.println("Please specify how many messages this publisher should send: ");
//            int messageNum = scan.nextInt();
            PubClientThread thread = new PubClientThread("Topic" + i / 2, "Pub" + i, 10000);
            new Thread(thread).start();
        }
    }

}

class PubClientThread implements Runnable {
    private String topic;
    private String name;
    private int messageNum;
    private int id;

    public PubClientThread(String topic, String name, int messageNum) {
        this.topic = topic;
        this.name = name;
        this.messageNum = messageNum;
    }

    public void run() {
        try {
            this.id = CAPublisherClient.register(this.topic, this.name);
            System.out.println("Publisher " + this.id + " created.");

            long startTime = System.currentTimeMillis();
            for (int i = 0; i < messageNum; i++) {
                CAPublisherClient.publishContent(this.id, RandomMessage.generateTitle(), RandomMessage.generateMessageContent());
            }

            System.out.println("Publisher Thread " + Thread.currentThread().getId() + " runs for " + Long.toString(System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

class RandomMessage {
    private RandomMessage() {}

    private static final int TITLE_LENGTH = 7;
    private static final int WORDS = 7;
    private static final int WORD_LENGTH = 10;
    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String generateTitle() {
        return generateRandomString(TITLE_LENGTH);
    }

    public static String generateMessageContent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < WORDS; i++) {
            sb.append(generateRandomString(WORD_LENGTH)).append(' ');
        }
        return sb.toString();
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