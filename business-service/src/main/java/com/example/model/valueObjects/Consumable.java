package com.example.model.valueObjects;
import jakarta.persistence.*;

@Embeddable
public class Consumable {
    @Column(name="name")
    private String name;

    public Consumable() {
    }

    public Consumable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Consumable{" +
                "name='" + name + '\'' +
                '}';
    }
}
