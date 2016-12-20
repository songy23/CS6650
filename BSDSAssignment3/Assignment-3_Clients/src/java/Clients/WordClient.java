package Clients;

import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class WordClient {
    public static void main(String[] args){
        // Constants
        int NUMBER_OF_TERMS = 50;
         
        // Connection stuff
        Client client = ClientBuilder.newClient();
        WebTarget base = client.target("http://ec2-35-164-35-3.us-west-2.compute.amazonaws.com:8080/Assignment-2/resources/");

        WebTarget words = base.path("words")
                .queryParam("topN", NUMBER_OF_TERMS);

        String returned = words.request(MediaType.TEXT_PLAIN).get(String.class);
        System.out.println(returned);
    }
}
