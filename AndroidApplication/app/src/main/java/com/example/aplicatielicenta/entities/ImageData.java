package com.example.aplicatielicenta.entities;

public class ImageData {
    private Long imageId;
    private byte[] imageData;

    public ImageData(Long imageId, byte[] imageData) {
        this.imageId = imageId;
        this.imageData = imageData;
    }

    public Long getImageId() {
        return imageId;
    }

    public byte[] getImageData() {
        return imageData;
    }
}

