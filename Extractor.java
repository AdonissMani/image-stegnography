import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Extractor {
    private String vessel;
    private String extractedFile;
    private SecurityManager smgr;
    private String trgtFolder;

    public Extractor(String password, String vessel, String trgtFolder) throws Exception {
        // Test for existence
        File f1 = new File(vessel);
        if (!f1.exists())
            throw new Exception(vessel + " doesn't exist");

        smgr = new SecurityManager(password);
        this.vessel = vessel;
        this.trgtFolder = trgtFolder;
    }

    public void extract() throws Exception {
        // Ensure the target folder exists
        File targetFolder = new File(trgtFolder);
        if (!targetFolder.exists()) {
            targetFolder.mkdirs(); // Create the folder and its parent directories if needed
        }

        // Load the vessel image in memory
        File fileVessel = new File(vessel);
        BufferedImage buffVessel = null;

        try {
            buffVessel = ImageIO.read(fileVessel);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading the image.");
            return; // Exit the program if image loading fails.
        }

        Raster rstr = buffVessel.getData();
        int w, h;
        w = buffVessel.getWidth();
        h = buffVessel.getHeight();

        int x, y;
        int r, g, b;
        int arr[], result[];
        int cnt = 0;
        int data;
        int flag = smgr.getPermutation();
        int fileSize = 0;
        boolean keepExtracting = true;
        String hdr = "";

        // Target File
        FileOutputStream fout = null;

        for (y = 0; y < h && keepExtracting; y++) {
            for (x = 0; x < w; x++) {
                // Per pixel
                r = rstr.getSample(x, y, 0); // Red band
                g = rstr.getSample(x, y, 1); // Green band
                b = rstr.getSample(x, y, 2); // Blue band

                arr = ByteProcessor.extract(r, g, b, flag);
                data = ByteProcessor.combine(arr, flag);

                if (cnt < HeaderManager.HEADER_LENGTH) {
                    hdr = hdr + (char) data;

                    if (cnt == HeaderManager.HEADER_LENGTH - 1) {
                        // We have the header, extract the file name
                        extractedFile = HeaderManager.getFileName(hdr);
                        fileSize = HeaderManager.getFileSize(hdr);
                        System.out.println("FILESIZE : " + fileSize);

                        // Open the file for writing
                        fout = new FileOutputStream(targetFolder + "/" + extractedFile);
                    }
                } else {
                    data = smgr.primaryCrypto(data);
                    fout.write(data);

                    if (cnt == fileSize + HeaderManager.HEADER_LENGTH) {
                        // EOF
                        System.out.println("***" + cnt);
                        keepExtracting = false;
                        fout.close();
                        break;
                    }
                }

                cnt++;
                flag = (flag + 1) % 3 + 1;
            }
        }
    }
}

