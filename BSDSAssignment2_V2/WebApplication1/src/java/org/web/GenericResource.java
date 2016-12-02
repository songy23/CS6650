/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.BSDS.CAServerRemote;

/**
 * REST Web Service
 *
 * @author songyang
 */
@Stateless
@Path("/BSDS")
public class GenericResource {

    @EJB
    CAServerRemote cAServer = lookupCAServerRemote();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }

    /**
     * Retrieves representation of an instance of org.web.GenericResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

    @Path("/greetings")
    @GET
    @Produces("text/html")
    public String getGreeting() {
        return "<html><body><h1>Bonjour!</h1></body></html>";
    }

    @Path("/publisher/{topic}")
    @POST
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    public int registerPublisher(@PathParam("topic") String topic,
                                    @DefaultValue("Publisher") String name) {
        return cAServer.registerPublisher(topic, name);
    }

    // publishes a message to the server
    @Path("/publisher/{publisherID}/content")
    @POST
    @Consumes("text/plain")
    public void publishContent(
            @PathParam("publisherID") int publisherID,
            @DefaultValue("NoTitle") @QueryParam("title") String title,  // /{publisherID}/content?title=?
            String message) {
        // TODO: break down message and store words in database
        cAServer.publishContent(publisherID, title, message);
    }

    @Path("/subscriber/{topic}")
    @POST
    @Produces(value = "text/plain")
    public int registerSubscriber(@PathParam("topic") String topic) {
        return cAServer.registerSubscriber(topic);
    }

    // gets next outstanding message for a subscription
    @Path("/subscriber/{subscriberID}/lastestContent")
    @GET
    @Produces("text/plain")
    public String getLatestContent(@PathParam("subscriberID") int subscriberID) {
        return cAServer.getLatestContent(subscriberID);
    }

    // get the most N popular terms from all topics.
    @Path("/popularTerms/{n}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getMostPopularTerms(@PathParam("n") int n) {
        return cAServer.getTopNWords(n);
    }

    private CAServerRemote lookupCAServerRemote() {
        try {
//            System.setProperty("org.omg.CORBA.ORBInitialHost", "127.0.0.1");
//            System.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
            javax.naming.Context c = new InitialContext();
            return (CAServerRemote) c.lookup("java:global/BSDSEJBModule/CAServer!org.BSDS.CAServerRemote");
//            return (CAServerRemote) c.lookup("java:comp/env/BSDSEJBModule/CAServer!org.BSDS.CAServerRemote");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
