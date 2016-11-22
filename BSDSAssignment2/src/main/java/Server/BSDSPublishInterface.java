package Server;

import javax.ws.rs.*;
import java.util.List;

/**
 * Created by songyang on 11/21/16.
 */
public interface BSDSPublishInterface {

    @Path("/publisher/{topic}")
    @POST
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    String registerPublisher(@PathParam("topic") String topic,
                                    @DefaultValue("Publisher") String name);

    @Path("/publisher/{publisherID}/content")
    @POST
    @Consumes("text/plain")
    void publishContent(
            @PathParam("publisherID") int publisherID,
            @DefaultValue("NoTitle") @QueryParam("title") String title,  // /{publisherID}/content?title=?
            String message);

    // get the most N popular terms from all topics.
    @Path("/publisher/popularTerms/{n}")
    @GET
    @Produces({"application/xml", "text/html"})
    List<String> getMostPopularTerms(@PathParam("n") int n);
}
