package com.example.domain.model;

import jakarta.persistence.*;

/**
 * Represents a material record in the inventory table.
 */
@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    public Material() {}

    public Material(int id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public Material(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isLowStock() {
        return quantity < 100;
    }

    @Override
    public String toString() {
        return String.format("%s (ID: %d) - %d units", name, id, quantity);
    }
}
