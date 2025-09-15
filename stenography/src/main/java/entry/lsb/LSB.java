package entry.lsb;

import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;

public class LSB {
    public static void embed(String input, String output, String msg) throws Exception 
    {
        BufferedImage img = ImageIO.read(new File(input));
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        int bitIndex = 0;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                for (int i = 0; i < 3; i++) {
                    if (bitIndex >= data.length * 8) 
                        break;

                    int bit = (data[bitIndex / 8] >> (7 - (bitIndex % 8))) & 1;

                    if (i == 0)
                        r = (r & 0xFE) | bit;
                    
                    if (i == 1)
                        g = (g & 0xFE) | bit;
                    
                    if (i == 2)
                        b = (b & 0xFE) | bit;

                    bitIndex++;
                }

                int newRgb = (0xFF << 24) | (r << 16) | (g << 8) | b;
                img.setRGB(x, y, newRgb);

                if (bitIndex >= data.length * 8) break;
            }
            if (bitIndex >= data.length * 8) break;
        }

        boolean status = ImageIO.write(img, "png", new File(output));
        System.out.println("embed() done, file written: " + status);
    }

    public static String extract(String file, int msgLen) throws Exception 
    {
        BufferedImage img = ImageIO.read(new File(file));
        byte[] data = new byte[msgLen];
        int bitIndex = 0;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int[] channels = {(rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF};

                for (int c : channels) {
                    if (bitIndex >= msgLen * 8) break;
                    int bit = c & 1;
                    data[bitIndex / 8] = (byte)((data[bitIndex / 8] << 1) | bit);
                    bitIndex++;
                }
                if (bitIndex >= msgLen * 8) 
                    break;
            }
            if (bitIndex >= msgLen * 8) 
                break;
        }

        return new String(data, StandardCharsets.UTF_8);
    }
}
