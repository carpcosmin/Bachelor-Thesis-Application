package com.example.aplicatielicenta.entities;

public class FavoriteOffer {
    private int id;
    private User user;
    private Offer offer;

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
