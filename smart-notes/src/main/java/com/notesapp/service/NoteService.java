package com.notesapp.service;

import com.notesapp.model.Note;
import com.notesapp.model.NoteRepository;
import com.notesapp.util.JsonPersistence;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for note business logic.
 * Handles note operations and coordinates between repository and persistence.
 */
public class NoteService {
    
    private final NoteRepository repository;
    private final JsonPersistence persistence;
    
    public NoteService() {
        this.repository = new NoteRepository();
        this.persistence = new JsonPersistence();
        loadNotesFromFile();
    }
    
    /**
     * Load notes from persistent storage
     */
    private void loadNotesFromFile() {
        List<Note> loadedNotes = persistence.loadNotes();
        repository.loadNotes(loadedNotes);
        
        // If no notes exist, create sample notes
        if (repository.count() == 0) {
            createSampleNotes();
        }
    }
    
    /**
     * Create sample notes to demonstrate the app
     */
    private void createSampleNotes() {
        Note n1 = new Note("Client Meeting Review",
            "Lorem Ipsum Dolor Sit Amet, Consetetur Sadipscing Elitr, Sed Diam Nonumy Eirmod Tempor Invidunt Ut Labore Et Dolore Magna Aliquyam Erat, Sed Diam Voluptua. At Vero Eos Et Accusam Et Justo Duo Dolores Et Ea Rebum.",
            "Work");
        repository.addNote(n1);
        
        Note n2 = new Note("Water Supply Chain Power",
            "Lorem Ipsum Dolor Sit Amet, Consetetur Sadipscing Elitr, Sed Diam Nonumy Eirmod Tempor Invidunt Ut Labore Et Dolore Magna Aliquyam Erat, Sed Diam Voluptua. At Vero Eos Et Accusam Et Justo Duo Dolores Et Ea Rebum.",
            "Wishlist");
        repository.addNote(n2);
        
        Note n3 = new Note("Social Media Chat",
            "Lorem Ipsum Dolor Sit Amet, Consetetur Sadipscing Elitr, Sed Diam Nonumy Eirmod Tempor Invidunt Ut Labore Et Dolore Magna Aliquyam Erat, Sed Diam Voluptua. At Vero Eos Et Accusam Et Justo Duo Dolores Et Ea Rebum.",
            "Assignment");
        repository.addNote(n3);
        
        Note n4 = new Note("Project Architecture",
            "Lorem Ipsum Dolor Sit Amet, Consetetur Sadipscing Elitr, Sed Diam Nonumy Eirmod Tempor Invidunt Ut Labore Et Dolore Magna Aliquyam Erat, Sed Diam Voluptua. At Vero Eos Et Accusam Et Justo Duo Dolores Et Ea Rebum.",
            "Projects");
        repository.addNote(n4);
        
        Note n5 = new Note("Musical Instruments",
            "Lorem Ipsum Dolor Sit Amet, Consetetur Sadipscing Elitr, Sed Diam Nonumy Eirmod Tempor Invidunt Ut Labore Et Dolore Magna Aliquyam Erat, Sed Diam Voluptua. At Vero Eos Et Accusam Et Justo Duo Dolores Et Ea Rebum.",
            "Videos");
        repository.addNote(n5);
        
        Note n6 = new Note("Media Chapter II",
            "Lorem Ipsum Dolor Sit Amet, Consetetur Sadipscing Elitr, Sed Diam Nonumy Eirmod Tempor Invidunt Ut Labore Et Dolore Magna Aliquyam Erat, Sed Diam Voluptua. At Vero Eos Et Accusam Et Justo Duo Dolores Et Ea Rebum.",
            "Study");
        repository.addNote(n6);
        
        saveNotes();
    }
    
    /**
     * Get all notes
     */
    public ObservableList<Note> getAllNotes() {
        return repository.getAllNotes();
    }
    
    /**
     * Create a new note with tag
     */
    public Note createNote(String title, String content, String tag) {
        Note note = new Note(title, content, tag);
        repository.addNote(note);
        saveNotes();
        return note;
    }
    
    /**
     * Create a new note with auto-generated title
     */
    public Note createNewNote() {
        int noteCount = repository.count() + 1;
        String title = "Note " + noteCount;
        Note note = new Note(title, "", "General");
        repository.addNote(note);
        saveNotes();
        return note;
    }
    
    /**
     * Update a note's content
     */
    public void updateNoteContent(Note note, String content) {
        if (note != null) {
            note.setContent(content);
            note.setModifiedAt(LocalDateTime.now());
            repository.updateNote(note);
            saveNotes();
        }
    }
    
    /**
     * Update a note's title
     */
    public void updateNoteTitle(Note note, String title) {
        if (note != null) {
            note.setTitle(title);
            note.setModifiedAt(LocalDateTime.now());
            repository.updateNote(note);
            saveNotes();
        }
    }
    
    /**
     * Update a note fully (title, content, tag)
     */
    public void updateNote(Note note, String title, String content, String tag) {
        if (note != null) {
            note.setTitle(title);
            note.setContent(content);
            note.setTag(tag);
            note.setModifiedAt(LocalDateTime.now());
            repository.updateNote(note);
            saveNotes();
        }
    }
    
    /**
     * Delete a note
     */
    public boolean deleteNote(Note note) {
        boolean deleted = repository.deleteNote(note);
        if (deleted) {
            saveNotes();
        }
        return deleted;
    }
    
    /**
     * Save all notes to persistent storage
     */
    public void saveNotes() {
        List<Note> noteList = new ArrayList<>(repository.getAllNotes());
        persistence.saveNotes(noteList);
    }
    
    /**
     * Get all unique tags from existing notes (dynamically built)
     */
    public List<String> getAllTags() {
        return repository.getAllNotes().stream()
            .map(Note::getTag)
            .filter(tag -> tag != null && !tag.trim().isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
    
    /**
     * Get counts per tag category (dynamically built from notes)
     */
    public Map<String, Integer> getTagCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Note note : repository.getAllNotes()) {
            String tag = note.getTag();
            if (tag != null && !tag.trim().isEmpty()) {
                counts.merge(tag, 1, Integer::sum);
            }
        }
        return counts;
    }
    
    /**
     * Get the notes file path
     */
    public String getNotesFilePath() {
        return persistence.getNotesFilePath();
    }
}
