package com.aplicatielicenta.springserver.entities.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findByOfferId(Long offerId);

    @Query(value = "SELECT * FROM images WHERE id = :id", nativeQuery = true)
    Image findById(@Param("id") Long id);

}
