package com.example.model;
import jakarta.persistence.*;

@Entity
public class Consumable {
    @Id
    @GeneratedValue
    @Column(name="consumableId")
    private Integer id;
    @Column(name="name")
    private String name;

    public Consumable() {
    }

    public Consumable(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Consumable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
