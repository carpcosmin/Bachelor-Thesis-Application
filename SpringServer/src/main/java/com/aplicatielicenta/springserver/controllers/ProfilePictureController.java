package com.aplicatielicenta.springserver.controllers;

import com.aplicatielicenta.springserver.entities.image.Image;
import com.aplicatielicenta.springserver.entities.image.ImageRepository;
import com.aplicatielicenta.springserver.entities.profilePicture.ProfilePictureRepository;
import com.aplicatielicenta.springserver.entities.profilePicture.profilePicture;
import com.aplicatielicenta.springserver.entities.user.User;
import com.aplicatielicenta.springserver.entities.user.UserRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProfilePictureController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfilePictureRepository profilePictureRepository;

    @GetMapping("users/{uid}/profilePicture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long uid) throws IOException {
        List<profilePicture> profilePictures = profilePictureRepository.findByUserId(uid);
        if (profilePictures.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        profilePicture profilePicture = profilePictures.get(0); // Retrieve the first profile picture
        String imagePath = profilePicture.getImagePath();
        try {
            byte[] imageBytes = Files.readAllBytes(Path.of(imagePath));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("users/{uid}/profilePicture")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Long uid, @RequestParam("profilePicture") MultipartFile file) {
        User user = userRepository.findById(uid);

        // Check if the user already has a profile picture
        List<profilePicture> existingProfilePictures = profilePictureRepository.findByUserId(uid);
        if (!existingProfilePictures.isEmpty()) {
            // Delete the existing profile picture from storage
            profilePicture existingProfilePicture = existingProfilePictures.get(0);
            String existingImagePath = existingProfilePicture.getImagePath();
            try {
                Files.deleteIfExists(Paths.get(existingImagePath));
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete existing image");
            }

            // Remove the existing profile picture from the database
            profilePictureRepository.delete(existingProfilePicture);
        }

        String filename = file.getOriginalFilename();
        String imagePath = "/Users/cosmin/Desktop/SpringServer/profile_pictures/" + filename;
        try {
            Files.write(Paths.get(imagePath), file.getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save image");
        }

        profilePicture profilePicture = new profilePicture();
        profilePicture.setUser(user);
        profilePicture.setFilename(filename);
        profilePicture.setImagePath(imagePath);

        profilePictureRepository.save(profilePicture);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("users/{uid}/profilePicture")
    public ResponseEntity<?> deleteProfilePicture(@PathVariable Long uid) {
        // Check if the user has a profile picture
        List<profilePicture> existingProfilePictures = profilePictureRepository.findByUserId(uid);
        if (existingProfilePictures.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        profilePicture existingProfilePicture = existingProfilePictures.get(0);
        String existingImagePath = existingProfilePicture.getImagePath();
        try {
            // Delete the profile picture file from storage
            Files.deleteIfExists(Paths.get(existingImagePath));

            // Remove the profile picture from the database
            profilePictureRepository.delete(existingProfilePicture);

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image");
        }
    }
}
