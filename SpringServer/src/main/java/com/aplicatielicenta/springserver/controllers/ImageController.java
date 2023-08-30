package com.aplicatielicenta.springserver.controllers;

import com.aplicatielicenta.springserver.entities.image.Image;
import com.aplicatielicenta.springserver.entities.image.ImageRepository;
import com.aplicatielicenta.springserver.entities.offer.Offer;
import com.aplicatielicenta.springserver.entities.offer.OfferRepository;
import com.aplicatielicenta.springserver.entities.profilePicture.profilePicture;
import com.aplicatielicenta.springserver.entities.user.User;
import com.aplicatielicenta.springserver.entities.user.UserRepository;
import jakarta.annotation.Resource;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class ImageController {

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ImageRepository imageRepository;

    @GetMapping("/offer/{oid}/imagesIdList")
    public ResponseEntity<List<Long>> getOfferImageIds(@PathVariable Long oid) {
        List<Image> images = imageRepository.findByOfferId(oid);
        if (images.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Long> imageIds = images.stream().map(Image::getId).collect(Collectors.toList());
        return ResponseEntity.ok().body(imageIds);
    }

    @GetMapping("/offer/{oid}/images")
    public ResponseEntity<byte[]> getOfferImages(@PathVariable Long oid) throws IOException {
        List<Image> images = imageRepository.findByOfferId(oid);
        if (images.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int numImagesProcessed = 0; // initialize counter
        String delimiter = "IMAGE_DELIMITER"; // define delimiter string
        for (Image image : images) {
            String imagePath = image.getImagePath();
            try (InputStream inputStream = new FileInputStream(imagePath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.write(delimiter.getBytes()); // write delimiter after each image
                numImagesProcessed++; // increment counter
            }
        }
        byte[] imageBytes = outputStream.toByteArray();
        log.info("Processed {} images", numImagesProcessed); // log number of images processed
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }


    @PostMapping("offer/{oid}/images")
    public ResponseEntity<?> uploadImage(@PathVariable Long oid, @RequestParam("images") List<MultipartFile> files) {
        Offer offer = offerRepository.findById(oid);

        // loop through the list of files and save each one to the file system
        List<Image> images = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            String imagePath = "/Users/cosmin/Desktop/SpringServer/offer_images/" + filename;
            try {
                Files.write(Paths.get(imagePath), file.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save image");
            }

            // create a new Image entity and associate it with the Offer
            Image image = new Image();
            image.setOffer(offer);
            image.setFilename(filename);
            image.setImagePath(imagePath);
            images.add(image);
        }

        // save the list of Image entities to the database
        imageRepository.saveAll(images);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/images/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        Image image = imageRepository.findById(id);
        if (image == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
        }

        // delete the file from the file system
        String imagePath = image.getImagePath();
        try {
            Files.deleteIfExists(Paths.get(imagePath));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image file");
        }

        // delete the Image entity from the database
        imageRepository.delete(image);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("offer/{oid}/images")
    public ResponseEntity<?> deleteImages(@PathVariable Long oid) {
        try {
            // Get the images associated with the offer from the repository
            List<Image> imagesToDelete = imageRepository.findByOfferId(oid);

            // Delete the images from the file system
            for (Image image : imagesToDelete) {
                Files.delete(Paths.get(image.getImagePath()));
            }

            // Delete the images from the database
            imageRepository.deleteAll(imagesToDelete);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete images");
        }
    }

}
