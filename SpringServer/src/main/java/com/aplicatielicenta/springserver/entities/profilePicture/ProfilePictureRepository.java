package com.aplicatielicenta.springserver.entities.profilePicture;

import com.aplicatielicenta.springserver.entities.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfilePictureRepository extends JpaRepository<profilePicture, Integer> {
    List<profilePicture> findByUserId(Long userId);

    @Query(value = "SELECT * FROM profile_pictures WHERE id = :id", nativeQuery = true)
    profilePicture findById(@Param("id") Long id);
}
