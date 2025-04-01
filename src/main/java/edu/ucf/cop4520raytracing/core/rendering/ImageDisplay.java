package edu.ucf.cop4520raytracing.core.rendering;

import lombok.Getter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Wrapper for a BufferedImage that implements double-buffering to prevent screen tearing.
 * Probably not necessary now that the JFrame is double-buffered, but I like this <3
 * <p>
 * We swap between two rasters and copy the entire rendered image to the display in one go,
 * so we don't get screen tearing from the render being caught in the middle of initializing a frame.
 */
public class ImageDisplay {
    public final int width;
    public final int height;

    @Getter
    private final BufferedImage image;

    private final WritableRaster buffer1;
    private final WritableRaster buffer2;

    private WritableRaster currentBuffer;

    public ImageDisplay(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        buffer1 = image.getRaster().createCompatibleWritableRaster();
        buffer2 = image.getRaster().createCompatibleWritableRaster();
        currentBuffer = buffer1;
        this.width = width;
        this.height = height;
    }

    public void setPixel(int x, int y, Color color) {
        currentBuffer.setPixel(x, y, new int[]{color.getRed(), color.getGreen(), color.getBlue()});
    }

    private void swapBuffers() {
        if (currentBuffer == buffer1) {
            currentBuffer = buffer2;
        } else {
            currentBuffer = buffer1;
        }
    }

    public void initNextFrame() {
        image.getRaster().setDataElements(0, 0, currentBuffer);
        swapBuffers();
    }
}
