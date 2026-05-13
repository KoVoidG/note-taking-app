package com.notesapp;

import com.notesapp.controller.MainController;
import com.notesapp.service.NoteService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application entry point.
 * Initializes the application with clean MVC architecture.
 */
public class MainApp extends Application {
    
    private MainController controller;
    
    @Override
    public void start(Stage stage) {
        // Initialize service layer
        NoteService noteService = new NoteService();
        
        // Initialize controller (which creates the view)
        controller = new MainController(noteService);
        
        // Create scene with the view
        Scene scene = new Scene(controller.getView().getRoot(), 1100, 700);
        
        // Load CSS stylesheet
        String css = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        // Configure stage
        stage.setTitle("Smart Notes");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(550);
        
        // Handle application close
        stage.setOnCloseRequest(e -> controller.onApplicationClose());
        
        stage.show();
    }
    
    @Override
    public void stop() {
        // Ensure notes are saved when application closes
        if (controller != null) {
            controller.onApplicationClose();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
