package com.example.jobhunt;

public class Save {
    private String title;
    private String description;
    private String documentId;
    private String photoResource;

    public Save(String title, String description, String documentId, String photoResource) {
        this.title = title;
        this.description = description;
        this.documentId = documentId;
        this.photoResource = photoResource;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getPhotoResource() {
        return photoResource;
    }
}
