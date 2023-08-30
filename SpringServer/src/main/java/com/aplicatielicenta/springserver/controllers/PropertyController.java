package com.aplicatielicenta.springserver.controllers;

import com.aplicatielicenta.springserver.entities.property.Property;
import com.aplicatielicenta.springserver.entities.property.PropertyDao;
import com.aplicatielicenta.springserver.entities.property.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropertyController {
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private PropertyRepository propertyRepository;

    @GetMapping("/property/get-all")
    public List<Property> getAllProperties(){
        return propertyDao.getAllProperties();
    }

    @GetMapping("/property/{pid}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long pid) {
        Property property = propertyRepository.findById(pid);
        if (property == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(property, HttpStatus.OK);
    }

    @PostMapping("/property/save")
    public Property saveProperty (@RequestBody Property property){
        return propertyDao.saveProperty(property);
    }

    @PutMapping("/property/{pid}")
    public ResponseEntity<Property> updatePropertyById(@PathVariable Long pid, @RequestBody Property property) {
        Property existingProperty = propertyRepository.findById(pid);
        if(existingProperty != null){
            existingProperty.setType(property.getType());
            existingProperty.setAddress(property.getAddress());
            existingProperty.setNoOfRooms(property.getNoOfRooms());
            existingProperty.setFloor(property.getFloor());
            existingProperty.setSurface(property.getSurface());
            existingProperty.setPetFriendly(property.isPetFriendly());
            existingProperty.setHasParkingSpace(property.isHasParkingSpace());
            existingProperty.setHasBalcony(property.isHasBalcony());
            existingProperty.setHasAC(property.isHasAC());
            existingProperty.setHasCentralHeating(property.isHasCentralHeating());
            existingProperty.setAcceptSmokers(property.isAcceptSmokers());

            Property updatedProperty = propertyRepository.save(existingProperty);
            return ResponseEntity.ok(updatedProperty);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/property/{pid}")
    public ResponseEntity<?> deletePropertyById(@PathVariable Long pid) {
        Property existingProperty = propertyRepository.findById(pid);
        if (existingProperty != null) {
            propertyRepository.deleteById(existingProperty.getId());
            return ResponseEntity.ok().build();
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
