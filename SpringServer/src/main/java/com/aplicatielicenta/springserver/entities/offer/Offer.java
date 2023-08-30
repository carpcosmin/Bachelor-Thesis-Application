package com.aplicatielicenta.springserver.entities.offer;

import com.aplicatielicenta.springserver.entities.favorite.FavoriteOffer;
import com.aplicatielicenta.springserver.entities.image.Image;
import com.aplicatielicenta.springserver.entities.property.Property;
import com.aplicatielicenta.springserver.entities.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //autoincrement
    @Column(name = "offer_id")
    private int id;
    @OneToOne
    @JoinColumn(name = "property_id", referencedColumnName = "property_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Property property;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    private String title;
    private float price;
    @Column(length = 65555)
    private String description;

    private Boolean toRent;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Image> images;

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
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
                ", property=" + property +
                ", user=" + user +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", toRent=" + toRent +
                ", images=" + images +
                '}';
    }
}
