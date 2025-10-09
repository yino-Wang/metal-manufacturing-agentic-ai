package com.example.model.valueObjects;
import jakarta.persistence.*;

@Embeddable
public class Consumable {
    @Column(name="name")
    private String name;


}
