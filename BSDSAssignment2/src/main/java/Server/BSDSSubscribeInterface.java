package Server;

import javax.ws.rs.*;

/**
 * Created by songyang on 11/21/16.
 */
public interface BSDSSubscribeInterface {
    @Path("/subscriber/{topic}")
    @POST
    @Produces(value = "text/plain")
    String registerSubscriber(@PathParam("topic") String topic);

    @Path("/subscriber/{subscriberID}/lastestContent")
    @GET
    @Produces("text/plain")
    String getLatestContent(@PathParam("subscriberID") int subscriberID);
}
