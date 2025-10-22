package com.example.interfaces.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class MaterialConsumedByName {

        private String name;
        private long quantity;  // Changed to long

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }
}
