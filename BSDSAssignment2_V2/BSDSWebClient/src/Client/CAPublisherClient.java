package Client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by songyang on 11/20/16.
 */
public class CAPublisherClient {
//    private static final String serverURI = "http://localhost:8080/BSDSWebApplication/webresources/BSDS/";
    private static final String serverURI = "http://54.89.76.7:8080/BSDSWebApplication/webresources/BSDS/";

    public static int register(String topic, String name) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        WebTarget target = client.target(serverURI + "publisher/" + topic);
        response = target.request().post(Entity.entity(name, MediaType.TEXT_PLAIN));
        try {
            int publisherId = Integer.parseInt((String) response.readEntity(String.class));
            response.close();
            return publisherId;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static void publishContent(int publisherId, String title, String message) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(serverURI + "publisher/" + publisherId + "/content" + "?title=" + title);
        target.request().buildPost(Entity.entity(message, MediaType.TEXT_PLAIN)).invoke();
    }


    public static void main(String[] args) {
//        Scanner scan = new Scanner(System.in);
//        System.out.println("Please specify how many publishers you want: ");
//        int threadNum = scan.nextInt();
        int threadNum = 20;

        for (int i = 0; i < threadNum; i++) {

//            System.out.println("Please enter a topic for publisher " + i + " : ");
//            String topic = scan.nextLine();
//            System.out.println("Please enter a name for publisher " + i + " : ");
//            String name = scan.nextLine();
//            System.out.println("Please specify how many messages this publisher should send: ");
//            int messageNum = scan.nextInt();
            PubClientThread thread = new PubClientThread("TopicB" + i / 2, "Pub" + i, 10000);
            new Thread(thread).start();
        }
    }

}

class PubClientThread implements Runnable {
    private String topic;
    private String name;
    private int messageNum;
    private int id;

    public PubClientThread(String topic, String name, int messageNum) {
        this.topic = topic;
        this.name = name;
        this.messageNum = messageNum;
    }

    public void run() {
        try {
            this.id = CAPublisherClient.register(this.topic, this.name);
            System.out.println("Publisher " + this.id + " created.");

            long startTime = System.currentTimeMillis();
            for (int i = 0; i < messageNum; i++) {
                StringBuilder messageBuilder = new StringBuilder();
                // Pick 5 words from A Tale of Two Cities
                messageBuilder.append(RandomMessage.getWordFromATaleofTwoCities(i % 1000)).append(' ')
                        .append(RandomMessage.getWordFromATaleofTwoCities(i % 111)).append(' ')
                        .append(RandomMessage.getWordFromATaleofTwoCities(i % 777)).append(' ')
                        .append(RandomMessage.getWordFromATaleofTwoCities(i % 871)).append(' ')
                        .append(RandomMessage.getWordFromATaleofTwoCities(i % 547));
                CAPublisherClient.publishContent(this.id, RandomMessage.generateTitle(), messageBuilder.toString());
            }

            System.out.println("Publisher Thread " + Thread.currentThread().getId() + " runs for " + Long.toString(System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

class RandomMessage {
    private static final int TITLE_LENGTH = 7;
    private static final int WORDS = 10;
    private static final int WORD_LENGTH = 5;
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /* From Chapter 1 of A Tale of Two Cities by Charles Dickens */
    private static final List<String> A_Tale_of_Two_Cities = Arrays.asList("It", "was", "the", "best", "of", "times", "it", "was", "the", "worst", "of", "times", "it", "was", "the", "age", "of", "wisdom", "it", "was", "the", "age", "of", "foolishness", "it", "was", "the", "epoch", "of", "belief", "it", "was", "the", "epoch", "of", "incredulity", "it", "was", "the", "season", "of", "Light", "it", "was", "the", "season", "of", "Darkness", "it", "was", "the", "spring", "of", "hope", "it", "was", "the", "winter", "of", "despair", "we", "had", "everything", "before", "us", "we", "had", "nothing", "before", "us", "we", "were", "all", "going", "direct", "to", "Heaven", "we", "were", "all", "going", "direct", "the", "other", "way--", "in", "short", "the", "period", "was", "so", "far", "like", "the", "present", "period", "that", "some", "of", "its", "noisiest", "authorities", "insisted", "on", "its", "being", "received", "for", "good", "or", "for", "evil", "in", "the", "superlative", "degree", "of", "comparison", "only", "", "There", "were", "a", "king", "with", "a", "large", "jaw", "and", "a", "queen", "with", "a", "plain", "face", "on", "the", "throne", "of", "England", "there", "were", "a", "king", "with", "a", "large", "jaw", "and", "a", "queen", "with", "a", "fair", "face", "on", "the", "throne", "of", "France", "In", "both", "countries", "it", "was", "clearer", "than", "crystal", "to", "the", "lords", "of", "the", "State", "preserves", "of", "loaves", "and", "fishes", "that", "things", "in", "general", "were", "settled", "for", "ever", "", "It", "was", "the", "year", "of", "Our", "Lord", "one", "thousand", "seven", "hundred", "and", "seventy-five", "Spiritual", "revelations", "were", "conceded", "to", "England", "at", "that", "favoured", "period", "as", "at", "this", "Mrs", "Southcott", "had", "recently", "attained", "her", "five-and-twentieth", "blessed", "birthday", "of", "whom", "a", "prophetic", "private", "in", "the", "Life", "Guards", "had", "heralded", "the", "sublime", "appearance", "by", "announcing", "that", "arrangements", "were", "made", "for", "the", "swallowing", "up", "of", "London", "and", "Westminster", "Even", "the", "Cock-lane", "ghost", "had", "been", "laid", "only", "a", "round", "dozen", "of", "years", "after", "rapping", "out", "its", "messages", "as", "the", "spirits", "of", "this", "very", "year", "last", "past", "(supernaturally", "deficient", "in", "originality)", "rapped", "out", "theirs", "Mere", "messages", "in", "the", "earthly", "order", "of", "events", "had", "lately", "come", "to", "the", "English", "Crown", "and", "People", "from", "a", "congress", "of", "British", "subjects", "in", "America", "which", "strange", "to", "relate", "have", "proved", "more", "important", "to", "the", "human", "race", "than", "any", "communications", "yet", "received", "through", "any", "of", "the", "chickens", "of", "the", "Cock-lane", "brood", "", "France", "less", "favoured", "on", "the", "whole", "as", "to", "matters", "spiritual", "than", "her", "sister", "of", "the", "shield", "and", "trident", "rolled", "with", "exceeding", "smoothness", "down", "hill", "making", "paper", "money", "and", "spending", "it", "Under", "the", "guidance", "of", "her", "Christian", "pastors", "she", "entertained", "herself", "besides", "with", "such", "humane", "achievements", "as", "sentencing", "a", "youth", "to", "have", "his", "hands", "cut", "off", "his", "tongue", "torn", "out", "with", "pincers", "and", "his", "body", "burned", "alive", "because", "he", "had", "not", "kneeled", "down", "in", "the", "rain", "to", "do", "honour", "to", "a", "dirty", "procession", "of", "monks", "which", "passed", "within", "his", "view", "at", "a", "distance", "of", "some", "fifty", "or", "sixty", "yards", "It", "is", "likely", "enough", "that", "rooted", "in", "the", "woods", "of", "France", "and", "Norway", "there", "were", "growing", "trees", "when", "that", "sufferer", "was", "put", "to", "death", "already", "marked", "by", "the", "Woodman", "Fate", "to", "come", "down", "and", "be", "sawn", "into", "boards", "to", "make", "a", "certain", "movable", "framework", "with", "a", "sack", "and", "a", "knife", "in", "it", "terrible", "in", "history", "It", "is", "likely", "enough", "that", "in", "the", "rough", "outhouses", "of", "some", "tillers", "of", "the", "heavy", "lands", "adjacent", "to", "Paris", "there", "were", "sheltered", "from", "the", "weather", "that", "very", "day", "rude", "carts", "bespattered", "with", "rustic", "mire", "snuffed", "about", "by", "pigs", "and", "roosted", "in", "by", "poultry", "which", "the", "Farmer", "Death", "had", "already", "set", "apart", "to", "be", "his", "tumbrils", "of", "the", "Revolution", "But", "that", "Woodman", "and", "that", "Farmer", "though", "they", "work", "unceasingly", "work", "silently", "and", "no", "one", "heard", "them", "as", "they", "went", "about", "with", "muffled", "tread", "the", "rather", "forasmuch", "as", "to", "entertain", "any", "suspicion", "that", "they", "were", "awake", "was", "to", "be", "atheistical", "and", "traitorous", "", "In", "England", "there", "was", "scarcely", "an", "amount", "of", "order", "and", "protection", "to", "justify", "much", "national", "boasting", "Daring", "burglaries", "by", "armed", "men", "and", "highway", "robberies", "took", "place", "in", "the", "capital", "itself", "every", "night", "families", "were", "publicly", "cautioned", "not", "to", "go", "out", "of", "town", "without", "removing", "their", "furniture", "to", "upholsterers’", "warehouses", "for", "security", "the", "highwayman", "in", "the", "dark", "was", "a", "City", "tradesman", "in", "the", "light", "and", "being", "recognised", "and", "challenged", "by", "his", "fellow-tradesman", "whom", "he", "stopped", "in", "his", "character", "of", "“the", "Captain,”", "gallantly", "shot", "him", "through", "the", "head", "and", "rode", "away", "the", "mail", "was", "waylaid", "by", "seven", "robbers", "and", "the", "guard", "shot", "three", "dead", "and", "then", "got", "shot", "dead", "himself", "by", "the", "other", "four", "“in", "consequence", "of", "the", "failure", "of", "his", "ammunition:”", "after", "which", "the", "mail", "was", "robbed", "in", "peace", "that", "magnificent", "potentate", "the", "Lord", "Mayor", "of", "London", "was", "made", "to", "stand", "and", "deliver", "on", "Turnham", "Green", "by", "one", "highwayman", "who", "despoiled", "the", "illustrious", "creature", "in", "sight", "of", "all", "his", "retinue", "prisoners", "in", "London", "gaols", "fought", "battles", "with", "their", "turnkeys", "and", "the", "majesty", "of", "the", "law", "fired", "blunderbusses", "in", "among", "them", "loaded", "with", "rounds", "of", "shot", "and", "ball", "thieves", "snipped", "off", "diamond", "crosses", "from", "the", "necks", "of", "noble", "lords", "at", "Court", "drawing-rooms", "musketeers", "went", "into", "St", "Giles’s", "to", "search", "for", "contraband", "goods", "and", "the", "mob", "fired", "on", "the", "musketeers", "and", "the", "musketeers", "fired", "on", "the", "mob", "and", "nobody", "thought", "any", "of", "these", "occurrences", "much", "out", "of", "the", "common", "way", "In", "the", "midst", "of", "them", "the", "hangman", "ever", "busy", "and", "ever", "worse", "than", "useless", "was", "in", "constant", "requisition", "now", "stringing", "up", "long", "rows", "of", "miscellaneous", "criminals", "now", "hanging", "a", "housebreaker", "on", "Saturday", "who", "had", "been", "taken", "on", "Tuesday", "now", "burning", "people", "in", "the", "hand", "at", "Newgate", "by", "the", "dozen", "and", "now", "burning", "pamphlets", "at", "the", "door", "of", "Westminster", "Hall", "to-day", "taking", "the", "life", "of", "an", "atrocious", "murderer", "and", "to-morrow", "of", "a", "wretched", "pilferer", "who", "had", "robbed", "a", "farmer’s", "boy", "of", "sixpence", "", "All", "these", "things", "and", "a", "thousand", "like", "them", "came", "to", "pass", "in", "and", "close", "upon", "the", "dear", "old", "year", "one", "thousand", "seven", "hundred", "and", "seventy-five", "Environed", "by", "them", "while", "the", "Woodman", "and", "the", "Farmer", "worked", "unheeded", "those", "two", "of", "the", "large", "jaws", "and", "those", "other", "two", "of", "the", "plain", "and", "the", "fair", "faces", "trod", "with", "stir", "enough", "and", "carried", "their", "divine", "rights", "with", "a", "high", "hand", "Thus", "did", "the", "year", "one", "thousand", "seven", "hundred", "and", "seventy-five", "conduct", "their", "Greatnesses", "and", "myriads", "of", "small", "creatures--the", "creatures", "of", "this", "chronicle", "among", "the", "rest--along", "the", "roads", "that", "lay", "before", "them");

    private RandomMessage() {
    }

    public static String generateTitle() {
        return generateRandomString(TITLE_LENGTH);
    }

    public static String generateMessageContent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < WORDS; i++) {
            sb.append(generateRandomString(WORD_LENGTH)).append(' ');
        }
        return sb.toString();
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    public static String getWordFromATaleofTwoCities(int pos) {
        return A_Tale_of_Two_Cities.get(pos);
    }

}