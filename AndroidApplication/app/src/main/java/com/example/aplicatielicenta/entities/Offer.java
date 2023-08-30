package com.example.aplicatielicenta.entities;

import java.util.List;

public class Offer {
    private int id;
    private User user;
    private Property property;
    private String title;
    private float price;
    private String description;
    private Boolean toRent;
    private List<Image> images;



    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getToRent() {
        return toRent;
    }

    public void setToRent(Boolean toRent) {
        this.toRent = toRent;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", user=" + user +
                ", property=" + property +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", toRent=" + toRent +
                ", images=" + images +
                '}';
    }
}
