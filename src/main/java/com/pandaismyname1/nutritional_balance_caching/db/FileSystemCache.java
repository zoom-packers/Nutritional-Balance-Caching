package com.pandaismyname1.nutritional_balance_caching.db;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class FileSystemCache {
    private static final String FILE_NAME = "nutritionalbalance.data";

    public static void init() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static HashMap<String, List<Nutrient>> getAllNutrients() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            var diskData = (HashMap<String, List<String>>) ois.readObject();
            var result = new HashMap<String, List<Nutrient>>();
            diskData.forEach((key, value) -> {
                var nutrients = value.stream().map(Nutrient::new).toList();
                result.put(key, nutrients);
            });
            return result;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public static void writeNutrients(HashMap<String, List<Nutrient>> nutrients) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            var diskData = new HashMap<String, List<String>>();
            nutrients.forEach((key, value) -> {
                var nutrientNames = value.stream().map(nutrient -> nutrient.name).toList();
                diskData.put(key, nutrientNames);
            });
            oos.writeObject(diskData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}