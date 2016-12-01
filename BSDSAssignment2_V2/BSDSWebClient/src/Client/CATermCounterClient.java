package Client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by songyang on 11/23/16.
 */
public class CATermCounterClient {
    private static final String serverURI = "http://localhost:8080/BSDSWebApplication/webresources/BSDS/";
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
        printTerms(getTopNPopularWords(N));
    }
}
