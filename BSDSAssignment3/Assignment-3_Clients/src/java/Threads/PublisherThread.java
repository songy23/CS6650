package Threads;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import static javax.ws.rs.client.Entity.json;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;


public class PublisherThread extends Thread {
    
    private final String topic;
    private final int numMessages;
    private final CyclicBarrier cb;
    private final WebTarget base;
    private final long startTime;
    private static int errors;
    
    private final String[] randoStr = {
            "Specifying a connection factory JNDI name and a destination JNDI name. Specifying a connection factory JNDI name is usually not necessary if the connection factory is hosted on the same cluster or server as the MDB. The default usually suffices.",
            "If the destination is not located in the same cluster or server as the MDB pool, administratively configure a mapping from the remote destination and connection factory JNDI entries to local JNDI entries that match those specified in #2, above. There are alternative approaches to referencing remote resources, but the mapping approach is the Oracle-recommended best practice",
            "For each free pool, the MDB container creates a connection using the specified connection factory, then uses the connection to find or create one or more subscriptions on its destination, and finally uses the connection to create JMS consumers that receive the messages from the subscription(s).",
            "Non-durable subscriptions exist only for the length of time their subscribers exist. When a subscription has no more subscribers, the subscription is automatically deleted. Messages stored in a non-durable subscription are never recovered after a JMS server shut down or crash."
        };

    public PublisherThread(String topic, int numMessages, CyclicBarrier cb, WebTarget base, long startTime) {
        this.topic = topic;
        this.numMessages = numMessages;
        this.cb = cb;
        this.base = base;
        this.startTime = startTime;
        this.errors = 0;
    }
    
    private static String randStr(){
        int STRINGLENGTH = 140; //140
        String POSSCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rand = new Random();
        char[] text = new char[STRINGLENGTH];
        for (int i = 0; i < STRINGLENGTH; i++) {
            text[i] = POSSCHARS.charAt(rand.nextInt(POSSCHARS.length()));
        }
        return new String(text);
    }
    
    @Override
    public void run(){
        try {
            System.out.println("NEW Publisher THREAD");
            WebTarget publisher = base.path("handleMessage");
            for(int k = 0; k < numMessages; k++){//randStr()
                String msgbody = randoStr[k % randoStr.length].substring(0, 139);
                JsonObject body = Json.createObjectBuilder()
                    .add("topic", topic)
                    .add("title", topic+ "-Title")
                    .add("pubName", topic + "-Name")
                    .add("msgBody", msgbody)
                    .build();
                JsonObject j = Json.createObjectBuilder()
                    .add("httpMethod", "POST")
                    .add("body", body)
                    .build();
//                JsonObject response = publisher.request(MediaType.APPLICATION_JSON).post(Entity.entity(j, MediaType.APPLICATION_JSON_TYPE), JsonObject.class);
                String response = publisher.request(MediaType.TEXT_PLAIN).post(Entity.entity(j, MediaType.APPLICATION_JSON_TYPE), String.class);
                if(!"null".equals(response)){
                    System.out.println(response);  
                    errors++;
                }
                
                
            }
            System.out.println("Published " + numMessages + " Messages for " + topic + " with " + errors + " errors");
            cb.await();
        } catch (InterruptedException e) {
            System.out.println(e);
        } catch (BrokenBarrierException e) {
            System.out.println(e);
        }
    }
    
    
}
