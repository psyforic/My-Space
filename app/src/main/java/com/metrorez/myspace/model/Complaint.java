package com.metrorez.myspace.model;


public class Complaint {
    private String complaintId;
    private String complaintCategory;
    private String complaintComment;
    private String complaintDate;
    private String complaintCity;
    private String complaintResidence;
    private String complaintRoom;
    private byte[] imagePath;

    public Complaint() {
    }

    public Complaint(String complaintId, String complaintCategory, String complaintComment, String complaintDate, byte[] imagePath) {
        this.complaintId = complaintId;
        this.complaintCategory = complaintCategory;
        this.complaintComment = complaintComment;
        this.complaintDate = complaintDate;
        this.imagePath = imagePath;
    }

    public Complaint(String complaintId, String complaintCategory, String complaintComment, String complaintDate) {
        this.complaintId = complaintId;
        this.complaintCategory = complaintCategory;
        this.complaintComment = complaintComment;
        this.complaintDate = complaintDate;
    }

    public Complaint(String complaintId, String complaintCategory, String complaintComment, String complaintDate, String complaintCity, String complaintResidence, String complaintRoom) {
        this.complaintId = complaintId;
        this.complaintCategory = complaintCategory;
        this.complaintComment = complaintComment;
        this.complaintDate = complaintDate;
        this.complaintCity = complaintCity;
        this.complaintResidence = complaintResidence;
        this.complaintRoom = complaintRoom;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public String getComplaintCategory() {
        return complaintCategory;
    }

    public String getComplaintComment() {
        return complaintComment;
    }

    public String getComplaintDate() {
        return complaintDate;
    }

    public String getComplaintCity() {
        return complaintCity;
    }

    public String getComplaintResidence() {
        return complaintResidence;
    }

    public String getComplaintRoom() {
        return complaintRoom;
    }
}
