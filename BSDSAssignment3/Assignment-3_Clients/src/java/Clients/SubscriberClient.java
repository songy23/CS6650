//package Clients;
//
//import Threads.SubscriberThread;
//import java.util.ArrayList;
//import java.util.concurrent.CyclicBarrier;
//import javax.ejb.Stateless;
//import javax.ws.rs.Path;
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//
//@Stateless
//@Path("/subscriberClient")
//public class SubscriberClient {
//    
//    private static ArrayList ids = new ArrayList();
//    
//    public static void main(String[] args){
//        createSubscribers();
//        getDataForSubscribers();
//    }
//
//    public static void createSubscribers(){
//        // Constants
//        int NUMBER_OF_SUBSCRIBERS = 2;
//        
//        // Publisher Stuff
//        String[] topic = {"Sports","News"};
//         
//        // Connection stuff
//        Client client = ClientBuilder.newClient();
//        WebTarget base = client.target("http://ec2-35-164-35-3.us-west-2.compute.amazonaws.com:8080/Assignment-2/resources/");
//        WebTarget subscriber = base.path("subscriber");
//        
//        // Loop for number of publishers
//        for(int i = 0; i < NUMBER_OF_SUBSCRIBERS; i++){
//            
//            String pubTopic = topic[i];
//            
//            long subID = subscriber.request(MediaType.TEXT_PLAIN).post(Entity.text(pubTopic), long.class);
//            
//            ids.add(subID);
//            
//            System.out.println("SubID - " + subID);
//        }
//    }
//    
//    public static void getDataForSubscribers(){
//        
//        System.out.println("IDS-" + ids);
//         
//        // Connection stuff
//        Client client = ClientBuilder.newClient();
//        WebTarget base = client.target("http://ec2-35-164-35-3.us-west-2.compute.amazonaws.com:8080/Assignment-2/resources/");
//        
//        CyclicBarrier cb = new CyclicBarrier(ids.size());
//        
//        // Loop for number of publishers
//        System.out.println("SIZE" + ids.size());
//        for(int i = 0; i < ids.size(); i++){
//            
//            long subID = (Long) ids.get(i);
//            
//            System.out.println("Subscriber ID - " + subID);
//            
//            new Thread(new SubscriberThread(subID, cb, base)).start();
//        }
//    }
//}
