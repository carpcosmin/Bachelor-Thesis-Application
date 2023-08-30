package com.aplicatielicenta.springserver.controllers;

import com.aplicatielicenta.springserver.ResourceNotFoundException;
import com.aplicatielicenta.springserver.entities.favorite.FavoriteOffer;
import com.aplicatielicenta.springserver.entities.favorite.FavoriteOfferRepository;
import com.aplicatielicenta.springserver.entities.image.Image;
import com.aplicatielicenta.springserver.entities.image.ImageRepository;
import com.aplicatielicenta.springserver.entities.offer.Offer;
import com.aplicatielicenta.springserver.entities.offer.OfferDao;
import com.aplicatielicenta.springserver.entities.offer.OfferRepository;
import com.aplicatielicenta.springserver.entities.property.Property;
import com.aplicatielicenta.springserver.entities.property.PropertyRepository;
import com.aplicatielicenta.springserver.entities.user.User;
import com.aplicatielicenta.springserver.entities.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class OfferController {
    @Autowired
    private OfferDao offerDao;
    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private FavoriteOfferRepository favoriteOfferRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @GetMapping("/offer/get-all")
    public List<Offer> getAllOffers(){
        return offerDao.getAllOffers();
    }

    @GetMapping("/offer/{oid}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long oid) {
        Offer offer = offerRepository.findById(oid);
        if (offer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @PostMapping("/offer/save")
    public Offer saveOffer (@RequestBody Offer offer){
        return offerRepository.save(offer);
    }

    @PutMapping("/offer/{oid}")
    public ResponseEntity<Offer> updateOfferById(@PathVariable Long oid, @RequestBody Offer offer) {
        Offer existingOffer = offerRepository.findById(oid);
        if(existingOffer != null){
            existingOffer.setUser(offer.getUser());
            existingOffer.setProperty(offer.getProperty());
            existingOffer.setTitle(offer.getTitle());
            existingOffer.setPrice(offer.getPrice());
            existingOffer.setDescription(offer.getDescription());
            existingOffer.setToRent(existingOffer.getToRent());

            Offer updatedOffer = offerRepository.save(existingOffer);
            return ResponseEntity.ok(updatedOffer);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/offer/{oid}")
    public ResponseEntity<?> deleteOfferById(@PathVariable Long oid) {
        Offer existingOffer = offerRepository.findById(oid);

        // Delete offer from favorites
        List<FavoriteOffer> favorites = favoriteOfferRepository.findByOfferId(oid);
        for (FavoriteOffer favorite : favorites) {
            favoriteOfferRepository.delete(favorite);
        }

        if (existingOffer != null) {
            // delete images associated with the offer
            List<Image> images = imageRepository.findByOfferId(oid);
            for (Image image : images) {
                // delete image file from file system
                try {
                    Files.delete(Paths.get(image.getImagePath()));
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image file");
                }
            }
            imageRepository.deleteAll(images);

            // delete offer entity from database
            offerRepository.deleteById(existingOffer.getId());
            return ResponseEntity.ok().build();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
