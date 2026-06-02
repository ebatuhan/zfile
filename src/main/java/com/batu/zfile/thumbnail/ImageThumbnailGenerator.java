package com.batu.zfile.thumbnail;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

import com.batu.zfile.exception.ThumbnailGenerationException;

@Component
public class ImageThumbnailGenerator implements ThumbnailGenerator {

    private static final int MAX_SIZE = 320;
    private static final String OUTPUT_FORMAT = "jpg";
    private static final String OUTPUT_CONTENT_TYPE = "image/jpeg";

    @Override
    public boolean supports(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    @Override
    public GeneratedThumbnail generate(InputStream source, String contentType) {
        try {
            var image = ImageIO.read(source);
            if (image == null) {
                throw new ThumbnailGenerationException("Unsupported image content", null);
            }

            var ratio = Math.min((double) MAX_SIZE / image.getWidth(), (double) MAX_SIZE / image.getHeight());
            var width = Math.max(1, (int) Math.round(image.getWidth() * Math.min(1.0, ratio)));
            var height = Math.max(1, (int) Math.round(image.getHeight() * Math.min(1.0, ratio)));
            var thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            var graphics = thumbnail.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawImage(image, 0, 0, width, height, null);
            graphics.dispose();

            var output = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, OUTPUT_FORMAT, output);
            var bytes = output.toByteArray();

            return new GeneratedThumbnail(new ByteArrayInputStream(bytes), bytes.length, OUTPUT_CONTENT_TYPE);
        } catch (ThumbnailGenerationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ThumbnailGenerationException("Failed to generate image thumbnail", exception);
        }
    }
}
