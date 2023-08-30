package com.aplicatielicenta.springserver.entities.offer;

import com.aplicatielicenta.springserver.entities.property.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Integer> {
    Offer findById(@Param("id") Long id);
}
