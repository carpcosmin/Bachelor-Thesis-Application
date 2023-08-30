package com.aplicatielicenta.springserver.entities.property;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyDao {

    @Autowired
    PropertyRepository propertyRepository;

    public Property saveProperty(Property property) {
        return propertyRepository.save(property);
    }

    public List<Property> getAllProperties() {
        List<Property> properties = new ArrayList<>();
        Streamable.of(propertyRepository.findAll())
                .forEach(properties::add);
        return properties;
    }

    public void deleteUser(int propertyId) { propertyRepository.deleteById(propertyId);}

}
