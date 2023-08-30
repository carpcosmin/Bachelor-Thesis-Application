package com.aplicatielicenta.springserver.services;

import com.aplicatielicenta.springserver.entities.image.Image;
import com.aplicatielicenta.springserver.entities.image.ImageRepository;
import com.aplicatielicenta.springserver.entities.offer.Offer;
import com.aplicatielicenta.springserver.entities.offer.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class OfferService {
    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Value("${upload.path}")
    private String uploadPath; // the path where the images will be saved on the server

    public void saveOffer(List<MultipartFile> images, Offer offer) throws IOException {
        // save the offer
        Offer savedOffer = offerRepository.save(offer);

//        // save the images
//        for (MultipartFile imageFile : images) {
//            // create a new image object
//            Image image = new Image();
//            image.setOffer(savedOffer);
//
//            // save the image to the server directory
//            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
//            File file = new File(uploadPath + "/" + fileName);
//            imageFile.transferTo(file);
//
//            // set the image URL in the image object
//            image.setUrl("/images/" + fileName);
//
//            // save the image object to the database
//            imageRepository.save(image);
        }
    }
