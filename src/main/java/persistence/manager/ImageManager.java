package persistence.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.NoSuchElementException;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class ImageManager {
    private static final Logger logger = LogManager.getLogger(ImageManager.class);
    private static final String IMAGE_ROOT = "./images/";
    private static final int IMAGE_SIZE = 2048;

    public String saveImage(InputStream uploadedInputStream, String filename) {
        OutputStream os = null;
        String path = null;
        try {
            path = String.format("%s%s.png", IMAGE_ROOT, filename);
            File fileToUpload = new File(path);
            os = new FileOutputStream(fileToUpload);
            byte[] b = new byte[IMAGE_SIZE];
            int length;
            while ((length = uploadedInputStream.read(b)) != -1) {
                os.write(b, 0, length);
            }
        } catch (IOException ex) {
            logger.error(ex);
        } finally {
            try {
                if(os != null) os.close();
            } catch (IOException ex) {
                logger.error(ex);
            }
        }
        return path;
    }

    public byte[] getImage(String filename){
        try {
            BufferedImage image = ImageIO.read(new File(String.format("%s%s.png", IMAGE_ROOT, filename)));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException exc){
            throw new NoSuchElementException("No image found");
        }
    }
}
