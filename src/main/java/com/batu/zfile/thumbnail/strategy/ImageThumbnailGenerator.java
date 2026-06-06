package com.batu.zfile.thumbnail.strategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;

import net.coobird.thumbnailator.Thumbnails;

@Component("/image")
public class ImageThumbnailGenerator implements ThumbnailGeneratorStrategy {

    private final String OUTPUT_FORMAT = "jpg";
    private final int MAX_H = 320;
    private final int MAX_V = 320;

    @Override
    public byte[] generateThumbnail(InputStream source) {
        try{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(source)
            .size(MAX_V, MAX_H)
            .outputFormat(OUTPUT_FORMAT)
            .toOutputStream(outputStream);
            
            return outputStream.toByteArray();
        }

        catch(IOException ex){
            throw new IllegalStateException("Thumbnail generation was failed.");
        }
    }

}
