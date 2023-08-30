package com.aplicatielicenta.springserver.entities.property;

import com.aplicatielicenta.springserver.entities.property.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {

    @Query(value = "SELECT * FROM properties WHERE property_id = :id", nativeQuery = true)
    Property findById(@Param("id") Long id);
}
