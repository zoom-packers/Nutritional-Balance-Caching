package com.pandaismyname1.nutritional_balance_caching.db;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.List;

public class NutrientCache {
    private static HashMap<String, List<Nutrient>> nutrientCache = new HashMap<>();

    public static void loadNutrients() {
        FileSystemCache.init();
        nutrientCache = FileSystemCache.getAllNutrients();
        clearEmptyNutrients();
        FileSystemCache.writeNutrients(nutrientCache);
    }

    private static void clearEmptyNutrients() {
        nutrientCache.entrySet().forEach(entry -> {
            if (entry.getValue().size() > 1) {
                var nutrients = new java.util.ArrayList<>(entry.getValue().stream().toList());
                nutrients.removeIf(nutrient -> nutrient.name.equals("NONE"));
                nutrientCache.put(entry.getKey(), nutrients);
            }
        });
    }

    public static List<Nutrient> getNutrients(String foodName) {
        return nutrientCache.get(foodName);
    }

    public static void setNutrients(String foodName, List<Nutrient> nutrients) {
        nutrientCache.put(foodName, nutrients);
    }

    public static void saveNutrients() {
        FileSystemCache.writeNutrients(nutrientCache);
    }
}