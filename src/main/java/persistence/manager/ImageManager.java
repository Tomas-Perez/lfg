package persistence.manager;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class ImageManager {
    private final String IMAGE_ROOT = "./images/";



    public byte[] getImage(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
