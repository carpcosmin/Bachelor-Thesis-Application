package com.aplicatielicenta.springserver.controllers;

import com.aplicatielicenta.springserver.entities.favorite.FavoriteOffer;
import com.aplicatielicenta.springserver.entities.favorite.FavoriteOfferRepository;
import com.aplicatielicenta.springserver.entities.offer.Offer;
import com.aplicatielicenta.springserver.entities.offer.OfferRepository;
import com.aplicatielicenta.springserver.entities.user.User;
import com.aplicatielicenta.springserver.entities.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class FavoriteOfferController {

    @Autowired
    private FavoriteOfferRepository favoriteOfferRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/{userId}/favorite-offers")
    public List<FavoriteOffer> getFavoriteOffersForUser(@PathVariable int userId) {
        return favoriteOfferRepository.findByUserId(userId);
    }

    @PostMapping("/users/{userId}/offers/{offerId}/favorite")
    public ResponseEntity<String> addOfferToFavorite(@PathVariable Long userId, @PathVariable Long offerId) {
        User user = userRepository.findById(userId);
        Offer offer = offerRepository.findById(offerId);
        if (user != null && offer != null) {
            FavoriteOffer favoriteOffer = new FavoriteOffer(user, offer);
            favoriteOfferRepository.save(favoriteOffer);
            return ResponseEntity.ok("Offer added to favorite successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Offer not found");
    }

    @DeleteMapping("/favorites/{fid}")
    public ResponseEntity<?> deleteFavoriteOfferById(@PathVariable Long fid) {
        FavoriteOffer favoriteOffer = favoriteOfferRepository.findById(fid);
        if (favoriteOffer != null) {
            favoriteOfferRepository.deleteById(favoriteOffer.getId());
            return ResponseEntity.ok().build();
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
