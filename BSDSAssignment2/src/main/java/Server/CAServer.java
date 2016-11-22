package Server;

import JDBC.WordFrequencyDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import java.io.BufferedReader;
import java.io.File;

/**
 * Created by songyang on 11/14/16.
 */
// The Java class will be hosted at the URI path "/BSDS"
@Path("/BSDS")
public class CAServer implements BSDSPublishInterface, BSDSSubscribeInterface {

    private static Map<String, ArrayList<BSDSContent>> messagesByTopic = new ConcurrentHashMap<String, ArrayList<BSDSContent>>();
    private static Map<Integer, Pair> subscriberidToTopicPositionMap = new HashMap<Integer, Pair>();  // don't need to be concurrent
    private static Map<Integer, String> publisheridToTopicMap = new HashMap<Integer, String>();
    private WordFrequencyDAO wordFrequencyDAO = WordFrequencyDAO.getInstance();

    private static final Set<String> STOP_WORDS = new HashSet<String>();
    static {
        File stopWords = new File("stop_words");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(stopWords));
            String line = null;
            while ((line = reader.readLine()) != null) {
                STOP_WORDS.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

    }

    private static Integer publisherID = 0;
    private static Integer subscriberID = 0;

    @Path("/publisher/{topic}")
    @POST
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    public String registerPublisher(@PathParam("topic") String topic,
                                    @DefaultValue("Publisher") String name) {
//        System.out.println("Publisher: " + name + " " + topic);
        synchronized (publisherID) {
            publisherID++;  // Starts from 1
            publisheridToTopicMap.put(publisherID, topic);
            return publisherID.toString();
        }
    }

    // publishes a message to the server
    @Path("/publisher/{publisherID}/content")
    @POST
    @Consumes("text/plain")
    public void publishContent(
            @PathParam("publisherID") int publisherID,
            @DefaultValue("NoTitle") @QueryParam("title") String title,  // /{publisherID}/content?title=?
            String message) {
        BSDSContent content = new BSDSContent();
        content.setMessage(message);
        content.setTitle(title);

        String topic = publisheridToTopicMap.get(publisherID);
        ArrayList<BSDSContent> messages = messagesByTopic.get(topic);
        if (messages == null) {
            messages = new ArrayList<BSDSContent>();
            messagesByTopic.put(topic, messages);
        }
        synchronized (messages) {
            messages.add(content);
        }

//        wordFrequencyCounter(message);
    }

    @Path("/subscriber/{topic}")
    @POST
    @Produces(value = "text/plain")
    public String registerSubscriber(@PathParam("topic") String topic) {
//        System.out.println("Subscriber " + (subscriberID + 1) + " Topic is  " + topic);
        synchronized (subscriberID) {
            subscriberID++;
            Pair pair = new Pair(topic, 0);
            subscriberidToTopicPositionMap.put(subscriberID, pair);
        }
        return subscriberID.toString();
    }

    // gets next outstanding message for a subscription
    @Path("/subscriber/{subscriberID}/lastestContent")
    @GET
    @Produces("text/plain")
    public String getLatestContent(@PathParam("subscriberID") int subscriberID) {
        BSDSContent lastestContent = null;
        Pair pair = subscriberidToTopicPositionMap.get(subscriberID);
        if (pair == null) {
            return null;
        }
        String topic = pair.topic;
        int position = pair.position;
        ArrayList<BSDSContent> messages = messagesByTopic.get(topic);

        if (messages != null && position < messages.size()) {
            lastestContent = messages.get(position);
            pair.position++;
        }

        if (lastestContent == null) {
            return null;
        } else {
            return lastestContent.getMessage();
        }

    }

    // get the most N popular terms from all topics.
    @Path("/publisher/popularTerms/{n}")
    @GET
    @Produces({"application/xml", "text/html"})
    public List<String> getMostPopularTerms(@PathParam("n") int n) {
        List<String> terms = null;
        try {
            terms = wordFrequencyDAO.getTopNPopularWords(n);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return terms;
    }

    private void wordFrequencyCounter(String message) {
        String[] words = message.split(" ");
        for (String word : words) {
            if (word.length() == 0 || STOP_WORDS.contains(word)) {
                continue;
            } else {
                try {
                    wordFrequencyDAO.updateWordFrequency(word);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    public static void main(String[] args) {
//        System.out.println(Response.ok("a").build());
//    }

}

/**
 * A simple class used to store the topic and message position of a subscriber.
 */
class Pair {
    String topic;
    int position;

    public Pair() {}
    public Pair(String s, int p) {
        this.topic = s;
        this.position = p;
    }
}
