package Clients;

import Threads.PublisherThread;
import Threads.SubscriberThread;
import Threads.WordThread;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class WrapperClient {
    
    private static Boolean isDone = false;
    
    static byte[] HmacSHA256(String data, byte[] key) throws Exception {
            String algorithm="HmacSHA256";
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(data.getBytes("UTF8"));
        }

    static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception {
        byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
        byte[] kDate = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion = HmacSHA256(regionName, kDate);
        byte[] kService = HmacSHA256(serviceName, kRegion);
        byte[] kSigning = HmacSHA256("aws4_request", kService);
        return kSigning;
    }
    
    static byte[] getSig() throws Exception{
        String key = "AKIAI2NP7JNP72FUYJQA";
        String dateStamp = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        String regionName = "us-west-2";
        String serviceName = "sns";
        return getSignatureKey(key, dateStamp, regionName, serviceName);
    }
    
    public static void main(String[] args) throws InterruptedException, BrokenBarrierException, Exception{
        
        //// Constants
        String WEBTARGET = "https://4a85kzkkg6.execute-api.us-west-2.amazonaws.com/prod/";
        
        // Publishers
        int NUMBER_OF_PUBLISHERS = 100;
        int NUMBER_OF_MESSAGES = 1000;
//        String[] pubTopics = {"Sports","News","Books", "Beer", "MTG",
//            "Eyes", "CS", "Northeastern", "College", "Summer"};
        String[] pubTopics = {"sausage","blubber","pencil","cloud","moon","water","computer","school","network","hammer","walking","violently","mediocre","literature","chair","two","window","cords","musical","zebra","xylophone","penguin","home","dog","final","ink","teacher","fun","website","banana","uncle","softly","mega","ten","awesome","attatch","blue","internet","bottle","tight","zone","tomato","prison","hydro","cleaning","telivision","send","frog","cup","book","zooming","falling","evily","gamer","lid","juice","moniter","captain","bonding","loudly","thudding","guitar","shaving","hair","soccer","water","racket","table","late","media","desktop","flipper","club","flying","smooth","monster","purple","guardian","bold","hyperlink","presentation","world","national","comment","element","magic","lion","sand","crust","toast","jam","hunter","forest","foraging","silently","tawesomated","joshing","pong","RANDOM","WORD"};
        
        // Subscribers
        int NUMBER_OF_SUBSCRIBERS = 10;
//        String[] subTopics = {"Sports","News","Books", "Beer", "MTG",
//            "Eyes", "CS", "Northeastern", "College", "Summer"};
        String[] subTopics = {"sausage","blubber","pencil","cloud","moon","water","computer","school","network","hammer","walking","violently","mediocre","literature","chair","two","window","cords","musical","zebra","xylophone","penguin","home","dog","final","ink","teacher","fun","website","banana","uncle","softly","mega","ten","awesome","attatch","blue","internet","bottle","tight","zone","tomato","prison","hydro","cleaning","telivision","send","frog","cup","book","zooming","falling","evily","gamer","lid","juice","moniter","captain","bonding","loudly","thudding","guitar","shaving","hair","soccer","water","racket","table","late","media","desktop","flipper","club","flying","smooth","monster","purple","guardian","bold","hyperlink","presentation","world","national","comment","element","magic","lion","sand","crust","toast","jam","hunter","forest","foraging","silently","tawesomated","joshing","pong","RANDOM","WORD"};
        int END = 10000;
        
        // Words
//        int NUMBER_OF_WORD_COUNTS = 5;
//        int NUMBER_OF_WORD_THREADS = 20;
        //// END
        
        // Connection
        Client client = ClientBuilder.newClient();
        WebTarget base = client.target(WEBTARGET);
        
        // Start time
        long startTime = System.currentTimeMillis();     
        
        
        
        // CyclicBarriers
        CyclicBarrier pcb = new CyclicBarrier(NUMBER_OF_PUBLISHERS + 1);
        CyclicBarrier scb = new CyclicBarrier(NUMBER_OF_SUBSCRIBERS + 1);
        
        // Threads
        Thread[] publisherThreads = new Thread[NUMBER_OF_PUBLISHERS];
        Thread[] subscriberThreads = new Thread[NUMBER_OF_SUBSCRIBERS];
//        Thread[] wordThreads = new Thread[NUMBER_OF_WORD_THREADS];
        
        // Start subscriberThreads
        for(int i = 0; i < NUMBER_OF_SUBSCRIBERS; i++){
            subscriberThreads[i] = new Thread(new SubscriberThread(subTopics[i], scb, base, END));
            subscriberThreads[i].start();
            Thread.sleep(200);
        }
        
//        Thread.sleep(10000);

        // Start publisherThreads     
        for(int i = 0; i < NUMBER_OF_PUBLISHERS; i++){//i%pubTopics.length
            publisherThreads[i] = new Thread(new PublisherThread(pubTopics[i%10], NUMBER_OF_MESSAGES, pcb, base, startTime));
            publisherThreads[i].start();
        }
        

        
//        Thread.sleep(5000);        
        // Start wordThreads
//        for(int i = 0; i < NUMBER_OF_WORD_THREADS; i++){
//            wordThreads[i] = new Thread(new WordThread(NUMBER_OF_WORD_COUNTS, base));
//            wordThreads[i].start();
//        }
        
        pcb.await();
        scb.await();
        Thread.sleep(100);
        WrapperClient.isDone = true;
        long endTime = System.currentTimeMillis();
        System.out.println("----- Review -----");
        System.out.println("Publishers and Subscribers took: " + (endTime - startTime)/1000.0 + "s");
        System.out.println("AKA: " + Math.floor((endTime - startTime)/1000.0/60.0) + " min " 
                + Math.round((endTime - startTime)/1000.0)%60 + " s");
    }
    
    public static boolean isDoneFn(){
        return isDone;
    }    
}