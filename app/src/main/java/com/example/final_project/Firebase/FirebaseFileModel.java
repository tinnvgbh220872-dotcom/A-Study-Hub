package com.example.final_project.Firebase;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FirebaseFileModel {

    public String postId;
    public String email;
    public String fileUri;
    public String filename;
    public long filesize;
    public String publishedDate;
    public String status;

    public FirebaseFileModel() {
    }

    public FirebaseFileModel(String email, String fileUri, String filename, long filesize, String publishedDate, String status) {
        this.email = email;
        this.fileUri = fileUri;
        this.filename = filename;
        this.filesize = filesize;
        this.publishedDate = publishedDate;
        this.status = status;
    }
}