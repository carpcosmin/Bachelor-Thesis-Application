package com.aplicatielicenta.springserver.entities.offer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfferDao {

    @Autowired
    OfferRepository offerRepository;

    public Offer saveOffer(Offer offer) {

        return offerRepository.save(offer);
    }

    public List<Offer> getAllOffers() {
        List<Offer> offers = new ArrayList<>();
        Streamable.of(offerRepository.findAll())
                .forEach(offers::add);
        return offers;
    }

    public void deleteOffer(int offerId) {
        offerRepository.deleteById(offerId);
    }
}
