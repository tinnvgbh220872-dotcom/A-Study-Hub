package com.example.final_project;

public class FileItem {
    private String name;
    private int size;
    private String uri;
    private String email;
    private String publishedDate;

    public FileItem(String name, int size, String uri, String email, String publishedDate) {
        this.name = name;
        this.size = size;
        this.uri = uri;
        this.email = email;
        this.publishedDate = publishedDate;
    }

    public String getName() { return name; }
    public int getSize() { return size; }
    public String getUri() { return uri; }
    public String getEmail() { return email; }
    public String getPublishedDate() { return publishedDate; }
}
