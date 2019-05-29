package com.hrishi_3331.devstudio3331.cricmania;

public class Player {

    private String name;
    private String image;
    private String id;
    private int key;

    public Player() {

    }

    public Player(String name, String image, String id, int key) {
        this.name = name;
        this.image = image;
        this.id = id;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(int key){
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
