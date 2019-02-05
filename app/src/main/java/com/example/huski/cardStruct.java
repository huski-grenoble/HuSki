package com.example.huski;

import java.util.UUID;

public class cardStruct {
    protected String name;
    UUID uuid;
    public cardStruct(String name){
        uuid = UUID.randomUUID();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }
}
