package api.rest.test.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import persistence.manager.ImageManager;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author Tomas Perez Molina
 */
@Path("test")
@RequestScoped
public class TestResource {
    private static final Logger logger = LogManager.getLogger(TestResource.class);

    @Inject
    private ImageManager imageManager;

    @GET
    @Path("admin")
    @RolesAllowed({"ADMIN"})
    @Produces(MediaType.TEXT_PLAIN)
    public String helloAdmin(){
        return "YOU ARE AN ADMIN";
    }

    @GET
    @Path("user")
    @RolesAllowed({"USER"})
    @Produces(MediaType.TEXT_PLAIN)
    public String helloUser(){
        return "YOU ARE A USER";
    }

    @GET
    @Path("images/{imgName}")
    @Produces("image/png")
    @PermitAll
    public Response getWewey(@PathParam("imgName") String imgName){
        byte[] imageData = imageManager.getImage(imgName);
        return Response.ok(new ByteArrayInputStream(imageData)).build();
    }

    @POST
    @Path("images/{imgName}")
    @PermitAll
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void uploadFile(@PathParam("imgName") String imgName,
                           @FormDataParam("file") InputStream uploadedInputStream,
                           @FormDataParam("file") FormDataContentDisposition fileDetail){
        imageManager.saveImage(uploadedInputStream, imgName);
    }
}
