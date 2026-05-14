package com.exammarker.helloworld.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

@Service
public class PdfAssemblyService {
	
	private Instant extractCreationTime(MultipartFile file) {

	    try (InputStream inputStream = file.getInputStream()) {

	        Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

	        ExifSubIFDDirectory directory =
	                metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

	        if (directory != null) {

	            Date date = directory.getDateOriginal();

	            if (date != null) {
	            	System.out.println(date.toInstant());
	                return date.toInstant();
	            }
	        }

	    } catch (Exception e) {
	        // optionally log
	    	System.out.println(e.getMessage());
	    }

	    /*
	     * fallback:
	     * if EXIF missing, use current time
	     * so sort operation still succeeds
	     */
	    return Instant.now();
	}

    public byte[] imagesToPdf(List<MultipartFile> images) throws IOException {

    	images.sort(
    		    Comparator.comparing(this::extractCreationTime)
    		              .thenComparing(MultipartFile::getOriginalFilename)
    		);
    	
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            for (MultipartFile file : images) {

                BufferedImage bufferedImage =
                        ImageIO.read(file.getInputStream());

                PDPage page = new PDPage(
                        new PDRectangle(
                                bufferedImage.getWidth(),
                                bufferedImage.getHeight()
                        )
                );

                document.addPage(page);

                PDImageXObject pdImage =
                        LosslessFactory.createFromImage(document, bufferedImage);

                try (PDPageContentStream contentStream =
                             new PDPageContentStream(document, page)) {

                    contentStream.drawImage(
                            pdImage,
                            0,
                            0,
                            bufferedImage.getWidth(),
                            bufferedImage.getHeight()
                    );
                }
            }

            document.save(outputStream);

            return outputStream.toByteArray();
        }
    }
}