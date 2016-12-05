package Client;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by songyang on 11/23/16.
 */
public class CATermCounterClient {
    private static final String serverURI = "http://localhost:8080/BSDSWebApplication/webresources/BSDS/";
//    private static final String serverURI = "http://54.89.76.7:8080/BSDSWebApplication/webresources/BSDS/";
    private static final int N = 50;

    public static String getTopNPopularWords(int n) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(serverURI + "popularTerms/" + n);
        Response response = target.request().get();
        String terms = response.readEntity(String.class);
        response.close();
        return terms;
    }

    public static void printTerms(String termString) {
        if (termString == null) {
            return;
        }
        String[] terms = termString.split(" ");
        int i = 0;
        for (String term : terms) {
            System.out.print(term + ' ');
            i++;
            if (i % 10 == 0) {
                System.out.print('\n');
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            int n = new Random().nextInt(100);
            Thread thread = new Thread(new TermCountThread(n, i));
            thread.start();
        }
    }
}

class TermCountThread implements Runnable {
    private int N;
    private int id;
    
    public TermCountThread(int N, int id) {
        this.N = N;
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Word Counter thread " + this.id + " Created.");
        while (true) {
            String terms = CATermCounterClient.getTopNPopularWords(N);
            CATermCounterClient.printTerms(terms);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TermCountThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}