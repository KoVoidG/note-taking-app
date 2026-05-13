package com.notesapp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.notesapp.model.Note;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for JSON-based file persistence.
 * Handles saving and loading notes to/from a JSON file.
 */
public class JsonPersistence {
    
    private static final String APP_DIR_NAME = ".smart-notes";
    private static final String NOTES_FILE_NAME = "notes.json";
    private final File notesFile;
    private final Gson gson;
    
    public JsonPersistence() {
        // Create Gson instance with custom adapters for LocalDateTime
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        
        // Set up the notes file path in user's home directory
        String userHome = System.getProperty("user.home");
        File appDir = new File(userHome, APP_DIR_NAME);
        
        // Create directory if it doesn't exist
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        
        this.notesFile = new File(appDir, NOTES_FILE_NAME);
    }
    
    /**
     * Save notes to JSON file
     */
    public void saveNotes(List<Note> notes) {
        try (FileWriter writer = new FileWriter(notesFile)) {
            gson.toJson(notes, writer);
        } catch (IOException e) {
            System.err.println("Error saving notes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load notes from JSON file
     */
    public List<Note> loadNotes() {
        if (!notesFile.exists()) {
            return new ArrayList<>();
        }
        
        try (FileReader reader = new FileReader(notesFile)) {
            Type noteListType = new TypeToken<ArrayList<Note>>(){}.getType();
            List<Note> notes = gson.fromJson(reader, noteListType);
            return notes != null ? notes : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error loading notes: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Get the notes file path
     */
    public String getNotesFilePath() {
        return notesFile.getAbsolutePath();
    }
    
    /**
     * Check if notes file exists
     */
    public boolean notesFileExists() {
        return notesFile.exists();
    }
}
