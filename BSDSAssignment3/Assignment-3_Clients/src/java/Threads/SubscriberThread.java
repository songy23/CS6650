package Threads;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;


public class SubscriberThread extends Thread {
    
    private final String topic;
    private final CyclicBarrier cb;
    private final WebTarget base;
    private int count;
    private int end;
    private int endCount;

    public SubscriberThread(String topic, CyclicBarrier cb, WebTarget base, int end) {
        this.topic = topic;
        this.cb = cb;
        this.base = base;
        this.count = 0;
        this.end = end;
        this.endCount = 0;
    }
    
    @Override
    public void run(){
        try {
            System.out.println("NEW Subscriber Thread");
            
            // Register subscriber
            WebTarget subscriber = base.path("registersubscriber");
            JsonObject body = Json.createObjectBuilder()
                    .add("topic", topic)
                    .add("subName", topic + "-Name")
                    .build();
            JsonObject j = Json.createObjectBuilder()
                .add("httpMethod", "POST")
                .add("body", body)
                .build();
            String subID = subscriber.request(MediaType.APPLICATION_JSON).post(Entity.entity(j, MediaType.APPLICATION_JSON_TYPE), String.class);

            System.out.println("Subscriber ID - " + subID + " for " + topic);

            Thread.sleep(10000);
            
            int waitMultiplier = 1;
//            
//            long tempTime = System.currentTimeMillis();
//            
//            int tries = 3;
//
            while(count < end && endCount < end){
                WebTarget getter = base.path("getMsg").queryParam("qurl", subID);
//                JsonObject getbody = Json.createObjectBuilder()
//                        .add("subID", subID)
//                        .build();
//                JsonObject gj = Json.createObjectBuilder()
//                    .add("httpMethod", "GET")
//                    .add("body", getbody)
//                    .build();
//                String msg = getter.request(MediaType.APPLICATION_JSON).post(Entity.entity(gj, MediaType.APPLICATION_JSON_TYPE), String.class);
                String msg = getter.request(MediaType.TEXT_PLAIN).get(String.class);
//                if(count % 100 == 0){
//                    System.out.println("RESPONSE for "+ topic + ": " + msg);
//                }

                if(count % 100 == 0){
                    System.out.println("COUNT for "+ topic + ": " + count);                
                }
                if(msg == null || msg.isEmpty() || "null".equals(msg)){
//                    System.out.println("----- MISSED for "+ subID + " " + topic + " with count:" + count + " and wait " + waitMultiplier + " -----");
//                    Thread.sleep(waitMultiplier * 100);
//                    Thread.sleep(100);
//                    waitMultiplier *= 2;
                    endCount++;
                    if(endCount % 100 == 0){
                        System.out.println("ENDCOUNT for "+ topic + ": " + endCount + " COUNT for "+ topic + ": " + count);
                    }
                }else{
                    count++;
                }

            }
            cb.await();
            System.out.println("Subscriber for " + topic + " retrieved: " + count + " messages and had " + endCount + " misses");
        } catch (InterruptedException e) {
            System.out.println(e);
        } catch (BrokenBarrierException e) {
            System.out.println(e);
        }
    }
    
    
}
