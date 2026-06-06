package com.batu.zfile.thumbnail.strategy;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;

import net.coobird.thumbnailator.Thumbnails;

@Component("application/pdf")
public class PDFThumbnailGenerator implements ThumbnailGeneratorStrategy{

@Override
public byte[] generateThumbnail(InputStream source) {
    Path tempPdf = null;

    try {
        tempPdf = Files.createTempFile("zfile-thumbnail-source-", ".pdf");

        Files.copy(source, tempPdf, StandardCopyOption.REPLACE_EXISTING);

        try (
                PDDocument document = Loader.loadPDF(tempPdf.toFile());
                ByteArrayOutputStream output = new ByteArrayOutputStream()
        ) {
            if (document.getNumberOfPages() == 0) {
                throw new IllegalArgumentException("PDF has no pages.");
            }

            PDFRenderer renderer = new PDFRenderer(document);
            renderer.setSubsamplingAllowed(true);

            BufferedImage firstPage = renderer.renderImageWithDPI(0, 72, ImageType.RGB);

            Thumbnails.of(firstPage)
                    .size(320, 320)
                    .outputFormat("jpg")
                    .toOutputStream(output);

            return output.toByteArray();
        }
    } catch (IOException exception) {
        throw new IllegalStateException("Failed to generate PDF thumbnail.", exception);
    } finally {
        if (tempPdf != null) {
            try {
                Files.deleteIfExists(tempPdf);
            } catch (IOException ignored) {
            }
        }
    }
}

}
