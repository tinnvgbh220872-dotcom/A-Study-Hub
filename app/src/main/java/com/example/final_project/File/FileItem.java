package com.example.final_project.File;

public class FileItem {
    private int fileId;
    private String name;
    private int size;
    private String uri;
    private String email;
    private String publishedDate;
    private String status;
    private String firebaseKey;

    public FileItem(int fileId,String name, int size, String uri, String email, String publishedDate, String status, String firebaseKey) {
        this.fileId = fileId;
        this.name = name;
        this.size = size;
        this.uri = uri;
        this.email = email;
        this.publishedDate = publishedDate;
        this.status = status;
        this.firebaseKey = firebaseKey;

    }

    public int getFileId() { return fileId; }

    public String getName() { return name; }
    public int getSize() { return size; }
    public String getUri() { return uri; }
    public String getEmail() { return email; }
    public String getPublishedDate() { return publishedDate; }
    public String getStatus() { return status; }
    public String getFirebaseKey() { return firebaseKey; }
    public void setFirebaseKey(String firebaseKey) { this.firebaseKey = firebaseKey; }
}
