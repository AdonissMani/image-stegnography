import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.IOException;
import java.awt.image.WritableRaster;



public class Embedder {
    private String toEmbed;
    private String vessel;
    private String trgtFile;
    private SecurityManager smgr;
    private BufferedImage buffVessel;

    public Embedder(String password, String toEmbed, String vessel, String trgtFile, BufferedImage buffVessel) throws Exception {
        // Test for existence
        File f1 = new File(toEmbed);
        if (!f1.exists())
            throw new Exception(toEmbed + " doesn't exist");

        smgr = new SecurityManager(password);
        this.toEmbed = toEmbed;
        this.vessel = vessel;
        this.trgtFile = trgtFile;
        this.buffVessel = buffVessel;
    }

    public void embed() throws Exception {
        try (FileInputStream srcFile = new FileInputStream(toEmbed)) {
            // Capacity check
            int w, h, tot;
            w = buffVessel.getWidth();
            h = buffVessel.getHeight();
            tot = w * h;
            File fileToEmbed = new File(toEmbed);
            if (tot < fileToEmbed.length() + HeaderManager.HEADER_LENGTH)
                throw new Exception("Embedding capacity of " + vessel + " is less than the size of " + toEmbed);

            // Create the header
            String hdr = HeaderManager.formHeader(fileToEmbed.getName(), fileToEmbed.length());

            // Get the raster (pixel matrix)
            WritableRaster wrstr = buffVessel.getRaster();

            int x, y;
            int r, g, b;
            int arr[], result[];
            int cnt = 0;
            int data;
            int flag = smgr.getPermutation();
            boolean keepEmbedding = true;

            for (y = 0; y < h && keepEmbedding; y++) {
                for (x = 0; x < w; x++) {
                    // Per pixel
                    r = wrstr.getSample(x, y, 0); // Red band
                    g = wrstr.getSample(x, y, 1); // Green band
                    b = wrstr.getSample(x, y, 2); // Blue band

                    if (cnt < HeaderManager.HEADER_LENGTH) {
                        // Embed header
                        data = hdr.charAt(cnt);
                    } else {
                        // Embed file content
                        data = srcFile.read();
                        if (data == -1) {
                            // EOF
                            keepEmbedding = false;
                            break;
                        }
                        data = smgr.primaryCrypto(data);
                    }

                    arr = ByteProcessor.slice(data, flag);
                    result = ByteProcessor.merge(r, g, b, arr, flag);

                    // Update the raster
                    wrstr.setSample(x, y, 0, result[0]);
                    wrstr.setSample(x, y, 1, result[1]);
                    wrstr.setSample(x, y, 2, result[2]);

                    cnt++;
                    flag = (flag + 1) % 3 + 1;
                }
            }

            // Save the embedded image
            File outputImageFile = new File(trgtFile);
            ImageIO.write(buffVessel, "PNG", outputImageFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error embedding the data.");
        }
    }
}

