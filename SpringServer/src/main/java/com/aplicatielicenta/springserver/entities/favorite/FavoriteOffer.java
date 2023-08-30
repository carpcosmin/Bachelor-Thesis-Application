package com.aplicatielicenta.springserver.entities.favorite;

import com.aplicatielicenta.springserver.entities.offer.Offer;
import com.aplicatielicenta.springserver.entities.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "Favorites")
public class FavoriteOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;

    public FavoriteOffer(){}

    public FavoriteOffer(User user, Offer offer){
        this.user = user;
        this.offer = offer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    @Override
    public String toString() {
        return "FavoriteOffer{" +
                "id=" + id +
                ", user=" + user +
                ", offer=" + offer +
                '}';
    }
}
