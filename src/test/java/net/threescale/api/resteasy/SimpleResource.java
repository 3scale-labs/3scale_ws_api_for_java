package net.threescale.api.resteasy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class SimpleResource {
    
    @GET
    @Path("/")
    @Produces("text/plain")

    public String getBasic() throws Exception {
        return "basic";

    }
}

