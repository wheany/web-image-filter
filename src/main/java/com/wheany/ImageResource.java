package com.wheany;

import com.vaadin.server.StreamResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class ImageResource
        implements StreamResource.StreamSource {
    int reloads = 0;

    BufferedImage myImg;

    public ImageResource(File input) throws IOException {
        BufferedImage in = ImageIO.read(input);

        myImg = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = myImg.createGraphics();
        g.drawImage(in, 0, 0, null);
        g.dispose();
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
        BufferedImage image = new BufferedImage (myImg.getWidth(), myImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics drawable = image.getGraphics();
        drawable.drawImage(myImg, 0, 0, null);
        drawable.setColor(Color.lightGray);
        drawable.fillRect(0,0,200,200);
        drawable.setColor(Color.yellow);
        drawable.fillOval(25,25,150,150);
        drawable.setColor(Color.blue);
        drawable.drawRect(0,0,199,199);
        drawable.setColor(Color.black);
        drawable.drawString("Reloads="+reloads, 75, 100);
        drawable.drawString("Value="+value, 75, 125);
        reloads++;

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
