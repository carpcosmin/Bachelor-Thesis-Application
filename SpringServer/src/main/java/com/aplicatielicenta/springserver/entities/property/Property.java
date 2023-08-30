package com.aplicatielicenta.springserver.entities.property;

import jakarta.persistence.*;

@Entity
@Table(name = "Properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //autoincrement
    @Column(name = "property_id")
    private int id;
    @Enumerated(EnumType.STRING)
    private PropertyType type;
    private String address;
    private int noOfRooms;
    private int floor;
    private float surface;
    private boolean isPetFriendly;
    private boolean hasParkingSpace;
    private boolean hasBalcony;
    private boolean hasAC;
    private boolean hasCentralHeating;
    private boolean acceptSmokers;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNoOfRooms() {
        return noOfRooms;
    }

    public void setNoOfRooms(int noOfRooms) {
        this.noOfRooms = noOfRooms;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public float getSurface() {
        return surface;
    }

    public void setSurface(float surface) {
        this.surface = surface;
    }

    public boolean isPetFriendly() {
        return isPetFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        isPetFriendly = petFriendly;
    }

    public boolean isHasParkingSpace() {
        return hasParkingSpace;
    }

    public void setHasParkingSpace(boolean hasParkingSpace) {
        this.hasParkingSpace = hasParkingSpace;
    }

    public boolean isHasBalcony() {
        return hasBalcony;
    }

    public void setHasBalcony(boolean hasBalcony) {
        this.hasBalcony = hasBalcony;
    }

    public boolean isHasAC() {
        return hasAC;
    }

    public void setHasAC(boolean hasAC) {
        this.hasAC = hasAC;
    }

    public boolean isHasCentralHeating() {
        return hasCentralHeating;
    }

    public void setHasCentralHeating(boolean hasCentralHeating) {
        this.hasCentralHeating = hasCentralHeating;
    }

    public boolean isAcceptSmokers() {
        return acceptSmokers;
    }

    public void setAcceptSmokers(boolean acceptSmokers) {
        this.acceptSmokers = acceptSmokers;
    }

    @Override
    public String toString() {
        return "Property{" +
                "id=" + id +
                ", type=" + type +
                ", address='" + address + '\'' +
                ", noOfRooms=" + noOfRooms +
                ", floor=" + floor +
                ", surface=" + surface +
                ", isPetFriendly=" + isPetFriendly +
                ", hasParkingSpace=" + hasParkingSpace +
                ", hasBalcony=" + hasBalcony +
                ", hasAC=" + hasAC +
                ", hasCentralHeating=" + hasCentralHeating +
                ", acceptSmokers=" + acceptSmokers +
                '}';
    }
}
