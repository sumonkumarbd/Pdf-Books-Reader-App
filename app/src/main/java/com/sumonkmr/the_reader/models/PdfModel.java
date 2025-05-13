package com.sumonkmr.the_reader.models;

public class PdfModel {
    int id;
    String title;
    String slug;
    String author;
    String category;
    String language;
    String publisher;
    String description;
    String pdfUrl;
    String coverUrl;
    String uploadDate;
    String publicationDate;
    boolean featured;



    // Constructor

    public PdfModel(int id, String title, String slug, String author, String category, String language, String publisher, String description, String pdfUrl, String coverUrl, String uploadDate, String publicationDate, boolean featured) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.author = author;
        this.category = category;
        this.language = language;
        this.publisher = publisher;
        this.description = description;
        this.pdfUrl = pdfUrl;
        this.coverUrl = coverUrl;
        this.uploadDate = uploadDate;
        this.publicationDate = publicationDate;
        this.featured = featured;
    }

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
}//Model Class End
