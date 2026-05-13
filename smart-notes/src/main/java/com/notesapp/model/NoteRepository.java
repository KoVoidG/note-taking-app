package com.notesapp.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Note data.
 * Provides CRUD operations and uses ObservableList for automatic UI updates.
 */
public class NoteRepository {
    
    private final ObservableList<Note> notes;
    
    public NoteRepository() {
        this.notes = FXCollections.observableArrayList();
    }
    
    /**
     * Get all notes as an observable list
     */
    public ObservableList<Note> getAllNotes() {
        return notes;
    }
    
    /**
     * Add a new note
     */
    public void addNote(Note note) {
        if (note != null) {
            notes.add(note);
        }
    }
    
    /**
     * Find a note by ID
     */
    public Optional<Note> findById(String id) {
        return notes.stream()
                .filter(note -> note.getId().equals(id))
                .findFirst();
    }
    
    /**
     * Update an existing note
     */
    public boolean updateNote(Note updatedNote) {
        Optional<Note> existingNote = findById(updatedNote.getId());
        if (existingNote.isPresent()) {
            int index = notes.indexOf(existingNote.get());
            notes.set(index, updatedNote);
            return true;
        }
        return false;
    }
    
    /**
     * Delete a note
     */
    public boolean deleteNote(String id) {
        return notes.removeIf(note -> note.getId().equals(id));
    }
    
    /**
     * Delete a note by object reference
     */
    public boolean deleteNote(Note note) {
        return notes.remove(note);
    }
    
    /**
     * Clear all notes
     */
    public void clear() {
        notes.clear();
    }
    
    /**
     * Load notes from a list (used for persistence)
     */
    public void loadNotes(List<Note> noteList) {
        notes.clear();
        if (noteList != null) {
            notes.addAll(noteList);
        }
    }
    
    /**
     * Get the count of notes
     */
    public int count() {
        return notes.size();
    }
}
