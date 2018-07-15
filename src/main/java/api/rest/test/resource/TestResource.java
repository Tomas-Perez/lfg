package api.rest.test.resource;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
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
    @Path("{imgName}")
    @Produces("image/png")
    @PermitAll
    public Response getWewey(@PathParam("imgName") String imgName) throws IOException {
        logger.info("imgName: " + imgName);
        final String filePath = String.format("./images/%s.png", imgName);
        logger.info("Path: " + filePath);
        BufferedImage image = ImageIO.read(new File(filePath));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageData = baos.toByteArray();

         return Response.ok(new ByteArrayInputStream(imageData)).build();
    }

//    @POST
//    @Path("upload")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public void uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
//                           @FormDataParam("file") FormDataContentDisposition fileDetail){
//        OutputStream os = null;
//        try {
//            File fileToUpload = new File("./images/test.png");
//            os = new FileOutputStream(fileToUpload);
//            byte[] b = new byte[2048];
//            int length;
//            while ((length = uploadedInputStream.read(b)) != -1) {
//                os.write(b, 0, length);
//            }
//        } catch (IOException ex) {
//            logger.error(ex);
//        } finally {
//            try {
//                os.close();
//            } catch (IOException ex) {
//                logger.error(ex);
//            }
//        }
//    }
}
