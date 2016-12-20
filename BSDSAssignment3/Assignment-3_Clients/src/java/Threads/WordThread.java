package Threads;

import Clients.WrapperClient;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;


public class WordThread extends Thread {
    
    private final int numTimes;
//    private final CyclicBarrier cb;
    private final WebTarget base;
    

    public WordThread(int numTimes, WebTarget base) {
        this.numTimes = numTimes;
//        this.cb = cb;
        this.base = base;
    }
    
    @Override
    public void run(){
        System.out.println("NEW Word THREAD");
        try {
//            for(int i = 0; i < numTimes; i++){
            while(!WrapperClient.isDoneFn()){
                int randN = ThreadLocalRandom.current().nextInt(0, 100 + 1);
                WebTarget words = base.path("words")
                        .queryParam("topN", randN);
                
//                System.out.println(words.request(MediaType.TEXT_PLAIN).get(String.class));
                
                Thread.sleep(1000);
            }
        } catch (InterruptedException ex) {
                Logger.getLogger(WordThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
