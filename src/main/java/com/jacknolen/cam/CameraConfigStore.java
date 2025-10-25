package com.jacknolen.cam;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CameraConfigStore {
    private static final File CONFIG_FILE = new File("cameras.json");

    public static class CameraEntry {
        public String name;
        public String url;
    }

    public static List<CameraEntry> load() {
        try {
            if (!CONFIG_FILE.exists()) return new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();
            CameraEntry[] entries = mapper.readValue(CONFIG_FILE, CameraEntry[].class);
            return new ArrayList<>(List.of(entries));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void save(List<CameraEntry> entries) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(CONFIG_FILE, entries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean delete(String name, String url) {
        List<CameraEntry> entries = load();
        boolean removed = entries.removeIf(entry ->
                entry.name.equalsIgnoreCase(name) &&
                        entry.url.equalsIgnoreCase(url)
        );
        if (removed) save(entries);
        return removed;
    }
}