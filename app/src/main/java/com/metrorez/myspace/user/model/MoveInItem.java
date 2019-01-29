package com.metrorez.myspace.user.model;

import android.net.Uri;

import java.io.Serializable;

public class MoveInItem implements Serializable {
    private String itemId;
    private String itemName;
    private String userId;
    private String date;
    private String imageUrl;
   // private Uri imageBitmap;

    public MoveInItem() {
    }

    public MoveInItem(String itemId, String itemName, String userId, String date, String imageUrl) {
        this.itemName = itemName;
        this.userId = userId;
        this.date = date;
        this.imageUrl = imageUrl;
        this.itemId = itemId;
    }

    public MoveInItem(String itemName, String userId, String date) {
        this.itemName = itemName;
        this.userId = userId;
        this.date = date;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

   /* public Uri getImageBitmap() {
        return imageBitmap;
    }*/

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /*public void setImageBitmap(Uri imageBitmap) {
        this.imageBitmap = imageBitmap;
    }*/
}
