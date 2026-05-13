package com.notesapp.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a note.
 * Encapsulates all note-related data with proper getters and setters.
 */
public class Note {
    
    private String id;
    private String title;
    private String content;
    private String tag;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    
    /**
     * Default constructor for JSON deserialization
     */
    public Note() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        this.title = "";
        this.content = "";
        this.tag = "";
    }
    
    /**
     * Constructor with title
     */
    public Note(String title) {
        this();
        this.title = title;
    }
    
    /**
     * Constructor with title, content, and tag
     */
    public Note(String title, String content, String tag) {
        this();
        this.title = title;
        this.content = content;
        this.tag = tag != null ? tag : "";
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
        this.modifiedAt = LocalDateTime.now();
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }
    
    public String getTag() {
        return tag;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
        this.modifiedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }
    
    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
    
    @Override
    public String toString() {
        return title != null && !title.isEmpty() ? title : "Untitled Note";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Note note = (Note) obj;
        return id.equals(note.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
