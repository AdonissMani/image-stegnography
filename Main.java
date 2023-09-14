import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            String currentDir = System.getProperty("user.dir");
            String imagePath = "matrix.png";
            String imageFullPath = currentDir + File.separator + imagePath;

            BufferedImage buffVessel = null;
            try {
                File fileVessel = new File(imageFullPath);
                buffVessel = ImageIO.read(fileVessel);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error loading the image.");
                return; // Exit the program if image loading fails.
            }

            // Embedding
            Embedder emb = new Embedder("password", "input.txt", "output.png", "output.png", buffVessel);
            emb.embed();
             System.out.println("Embedding complete.");
             
            Extractor ext = new Extractor("password", "output.png", "output");
            ext.extract();
             System.out.println("Extraction complete go to output folder");
           
        } catch (Exception ex) {
            System.out.println("Err: " + ex);
        }
    }
}

