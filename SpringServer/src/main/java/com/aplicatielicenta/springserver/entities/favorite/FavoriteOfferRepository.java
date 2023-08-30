package com.aplicatielicenta.springserver.entities.favorite;

import com.aplicatielicenta.springserver.entities.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteOfferRepository extends JpaRepository<FavoriteOffer, Integer> {
    List<FavoriteOffer> findByUserId(int userId);
    List<FavoriteOffer> findByOfferId(Long offerId);
    FavoriteOffer findById(@Param("id") Long id);
}
