package com.example.huski;

import java.util.UUID;

public class cardStruct {
    private String name;
    private UUID uuid;
    public cardStruct(String name){
        this.uuid = UUID.randomUUID();
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
