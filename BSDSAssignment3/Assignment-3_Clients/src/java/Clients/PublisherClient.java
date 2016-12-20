//package Clients;
//
//import Threads.PublisherThread;
//import java.util.concurrent.CyclicBarrier;
//import javax.ws.rs.client.WebTarget;
//
//public class PublisherClient {
//    
//    public static void main(int numPub, int numMsg, String[] topics, WebTarget base, CyclicBarrier cb, long startTime){      
////        System.out.println(numPub);
////        System.out.println(numMsg);
////        System.out.println(topics);
////        System.out.println(base);
////        System.out.println(cb);
////        System.out.println(startTime);
////        
//        try{
//            // Loop for number of publishers
//            for(int i = 0; i < numPub; i++){
//                new Thread(new PublisherThread(topics[i], numMsg, base, startTime)).start();
//            }
//            System.out.println("RAWR" + java.lang.Thread.activeCount());
//        cb.await();
//        System.out.println(java.lang.Thread.activeCount());
//        }catch(Exception e){
//            System.out.println("ERROR - " + e);
//        }
//    }
//}
