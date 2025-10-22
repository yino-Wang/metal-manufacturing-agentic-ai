package com.example.application.services;

import com.example.infrastructure.repositories.MaterialRepository;
import com.example.domain.model.Material;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class InventoryService {

    @Autowired
    private MaterialRepository materialRepository;

    @PostConstruct
    public void preloadMaterials() {
        preloadMaterial("Steel", 150);
        preloadMaterial("Aluminium", 150);
        preloadMaterial("Copper", 150);
        preloadMaterial("Iron", 150);
    }

    private void preloadMaterial(String materialName, int amount) {
        Material material = materialRepository.findByMaterialName(materialName);
        if (material == null) {
            material = new Material(materialName, amount);
        } else {
            material.setMaterialAmount(amount);
        }
        materialRepository.save(material);
    }

    public boolean deductMaterial(String materialName, int amount) {
        Material material = materialRepository.findByMaterialName(materialName);
        if (material != null && material.getMaterialAmount() >= amount) {
            material.deductMaterial(amount);
            materialRepository.save(material);
            checkAndRestock(material);
            return true;
        }
        return false;
    }

    private void checkAndRestock(Material material) {
        if (material.getMaterialAmount() < 100) {
            material.restockMaterial(100);
            materialRepository.save(material);
            System.out.println("Material " + material.getMaterialName() + " was restocked by +100 units.");
        }
    }

    public void checkAllMaterialsAndRestock() {
        materialRepository.findAll().forEach(this::checkAndRestock);
    }

    public Material getMaterialByName(String materialName) {
        return materialRepository.findByMaterialName(materialName);
    }
}
