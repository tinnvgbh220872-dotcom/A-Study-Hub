package com.example.final_project;

public class FileItem {
    private String name;
    private int size;
    private String uri;

    public FileItem(String name, int size, String uri) {
        this.name = name;
        this.size = size;
        this.uri = uri;
    }

    public String getName() { return name; }
    public int getSize() { return size; }
    public String getUri() { return uri; }
}
