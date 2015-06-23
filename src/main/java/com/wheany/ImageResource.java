package com.wheany;

import com.vaadin.server.StreamResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

import static java.lang.Math.*;

public class ImageResource
        implements StreamResource.StreamSource {
    BufferedImage original;
    BufferedImage adjusted;

    public int getRAdjust() {
        return RAdjust;
    }

    public void setRAdjust(int RAdjust) {
        this.RAdjust = RAdjust;
    }

    public int getGAdjust() {
        return GAdjust;
    }

    public void setGAdjust(int GAdjust) {
        this.GAdjust = GAdjust;
    }

    public int getBAdjust() {
        return BAdjust;
    }

    public void setBAdjust(int BAdjust) {
        this.BAdjust = BAdjust;
    }

    private int RAdjust = 0;
    private int GAdjust = 0;
    private int BAdjust = 0;

    public ImageResource(File input) throws IOException {
        BufferedImage in = ImageIO.read(input);

        original = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = original.createGraphics();
        g.drawImage(in, 0, 0, null);
        g.dispose();

        adjusted = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
        updateAdjusted();
    }
    
    private int clamp_0_255(int value) {
        return max(0, min(255, value));
    }

    private void updateAdjusted() {
        int[] originalRGB = original.getRGB(0, 0, original.getWidth(), original.getHeight(), null, 0, original.getWidth());
        int[] adjustedRGB = new int[originalRGB.length];
        
        for (int i = 0; i < originalRGB.length; i++) {
            int r = (originalRGB[i] & 0x00FF0000) >>> 16;
            int g = (originalRGB[i] & 0x0000FF00) >>> 8;
            int b = (originalRGB[i] & 0x000000FF);
            
            int adjustedR = clamp_0_255(r + RAdjust);
            int adjustedG = clamp_0_255(g + GAdjust);
            int adjustedB = clamp_0_255(b + BAdjust);
            
            adjustedRGB[i] = (adjustedR << 16) | (adjustedG << 8) | adjustedB;
        }

        adjusted.setRGB(0,0,original.getWidth(),original.getHeight(),adjustedRGB,0,original.getWidth());
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    private double value = 0;

    /* We need to implement this method that returns
     * the resource as a stream. */
    public InputStream getStream () {
        /* Create an image and draw something on it. */
        updateAdjusted();
        BufferedImage image = new BufferedImage (adjusted.getWidth(), adjusted.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics drawable = image.getGraphics();
        drawable.drawImage(adjusted, 0, 0, null);

        try {
            /* Write the image to a buffer. */
            ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
            ImageIO.write(image, "png", imagebuffer);

            /* Return a stream from the buffer. */
            return new ByteArrayInputStream(
                    imagebuffer.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }
}
