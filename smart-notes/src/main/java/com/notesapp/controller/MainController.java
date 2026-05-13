package com.notesapp.controller;

import com.notesapp.model.Note;
import com.notesapp.service.NoteService;
import com.notesapp.view.MainView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for the main application view.
 * Handles user interactions and coordinates between view and service layers.
 */
public class MainController {
    
    private final MainView view;
    private final NoteService noteService;
    private String currentCategoryFilter = null;
    private Timeline autoSaveTimeline;
    
    public MainController(NoteService noteService) {
        this.noteService = noteService;
        this.view = new MainView();
        
        setupCallbacks();
        refreshView();
        startAutoSave();
    }
    
    /**
     * Set up view callbacks
     */
    private void setupCallbacks() {
        view.setOnNoteClick(this::handleNoteClick);
        view.setOnNoteDelete(this::handleDeleteNote);
        view.setOnCategoryFilter(this::handleCategoryFilter);
        view.setOnAddNote(this::handleAddNote);
        view.setOnSearch(this::handleSearch);
        
        // Theme toggle refreshes cards (colors change per theme)
        view.getThemeToggleButton().setOnAction(e -> {
            boolean dark = view.getThemeToggleButton().isSelected();
            view.applyTheme(dark);
            refreshView();
        });
    }
    
    /**
     * Refresh the entire view (grid + sidebar counts)
     */
    private void refreshView() {
        List<Note> notes = getFilteredNotes();
        view.refreshNoteGrid(notes);
        
        Map<String, Integer> counts = noteService.getTagCounts();
        view.rebuildCategorySidebar(counts);
    }
    
    /**
     * Get notes filtered by current category and search
     */
    private List<Note> getFilteredNotes() {
        ObservableList<Note> allNotes = noteService.getAllNotes();
        List<Note> filtered = new ArrayList<>(allNotes);
        
        // Filter by category
        if (currentCategoryFilter != null) {
            filtered = filtered.stream()
                .filter(n -> currentCategoryFilter.equals(n.getTag()))
                .collect(Collectors.toList());
        }
        
        // Filter by search text
        String searchText = view.getSearchField().getText();
        if (searchText != null && !searchText.trim().isEmpty()) {
            String lower = searchText.toLowerCase();
            filtered = filtered.stream()
                .filter(n -> 
                    n.getTitle().toLowerCase().contains(lower) ||
                    n.getContent().toLowerCase().contains(lower) ||
                    (n.getTag() != null && n.getTag().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
        }
        
        return filtered;
    }
    
    /**
     * Handle clicking a note card — open editor dialog
     */
    private void handleNoteClick(Note note) {
        showNoteEditorDialog(note, false);
    }
    
    /**
     * Handle adding a new note
     */
    private void handleAddNote() {
        String defaultTag = currentCategoryFilter != null ? currentCategoryFilter : "General";
        Note newNote = noteService.createNote("New Note", "", defaultTag);
        refreshView();
        showNoteEditorDialog(newNote, true);
    }
    
    /**
     * Handle deleting a note
     */
    private void handleDeleteNote(Note note) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Note");
        alert.setHeaderText("Delete \"" + note.getTitle() + "\"?");
        alert.setContentText("This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                noteService.deleteNote(note);
                refreshView();
            }
        });
    }
    
    /**
     * Handle category filter
     */
    private void handleCategoryFilter(String tag) {
        if (tag == null || tag.equals(currentCategoryFilter)) {
            // Clicking "All" or the same category again resets to show all
            currentCategoryFilter = null;
            view.setSelectedCategory(null);
        } else {
            currentCategoryFilter = tag;
            view.setSelectedCategory(tag);
        }
        refreshView();
    }
    
    /**
     * Handle search text changes
     */
    private void handleSearch(String searchText) {
        refreshView();
    }
    
    /**
     * Show the note editor dialog.
     * Tag field is a ComboBox with existing tags + ability to type a new one.
     */
    private void showNoteEditorDialog(Note note, boolean isNew) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setMinWidth(560);
        dialog.setMinHeight(540);
        
        boolean dark = view.isDarkMode();
        
        // Colors
        String bgMain = dark ? "#1E1E2E" : "#F7F7FA";
        String bgCard = dark ? "#282840" : "#FFFFFF";
        String textPrimary = dark ? "#E8E8F0" : "#1A1A2E";
        String textSecondary = dark ? "#A0A0B8" : "#6B7280";

        String accentGrad = "linear-gradient(to right, #667eea, #764ba2)";
        
        // === Outer wrapper (the whole dialog) ===
        VBox outerRoot = new VBox(0);
        outerRoot.setStyle("-fx-background-color: " + bgMain + "; -fx-background-radius: 14; "
            + "-fx-border-radius: 14; -fx-border-color: " + (dark ? "#3A3A55" : "#D1D5DB") + "; "
            + "-fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 8);");
        
        // === Header bar with gradient accent ===
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 24, 18, 24));
        header.setStyle("-fx-background-color: " + accentGrad + "; "
            + "-fx-background-radius: 13 13 0 0;");
        
        Label headerTitle = new Label(isNew ? "\u2728  New Note" : "\u270F\uFE0F  Edit Note");
        headerTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        
        Button closeBtn = new Button("\u2715");
        closeBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; "
            + "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 4 10 4 10; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> {
            if (isNew) { noteService.deleteNote(note); refreshView(); }
            dialog.close();
        });
        
        header.getChildren().addAll(headerTitle, headerSpacer, closeBtn);
        
        // === Form content card ===
        VBox formCard = new VBox(18);
        formCard.setPadding(new Insets(24));
        formCard.setStyle("-fx-background-color: " + bgCard + "; -fx-background-radius: 12; "
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);");
        VBox.setMargin(formCard, new Insets(20, 24, 12, 24));
        
        // --- Title field with accent left border ---
        Label titleFieldLabel = new Label("TITLE");
        titleFieldLabel.setStyle("-fx-text-fill: " + textSecondary + "; -fx-font-weight: bold; "
            + "-fx-font-size: 11px; -fx-letter-spacing: 1;");
        TextField titleField = new TextField(note.getTitle());
        titleField.setPromptText("Give your note a title...");
        titleField.getStyleClass().add("dialog-title-field");
        if (dark) titleField.getStyleClass().add("dialog-title-field-dark");
        VBox titleSection = new VBox(6, titleFieldLabel, titleField);
        
        // --- Tag field with custom styled dropdown ---
        Label tagFieldLabel = new Label("CATEGORY / TAG");
        tagFieldLabel.setStyle("-fx-text-fill: " + textSecondary + "; -fx-font-weight: bold; "
            + "-fx-font-size: 11px; -fx-letter-spacing: 1;");
        
        ComboBox<String> tagCombo = new ComboBox<>();
        tagCombo.setEditable(true);
        tagCombo.getItems().addAll(noteService.getAllTags());
        String currentTag = note.getTag() != null && !note.getTag().isEmpty() ? note.getTag() : "General";
        if (!tagCombo.getItems().contains(currentTag)) {
            tagCombo.getItems().add(currentTag);
        }
        tagCombo.setValue(currentTag);
        tagCombo.setMaxWidth(Double.MAX_VALUE);
        tagCombo.setPromptText("Type new or select existing...");
        tagCombo.getStyleClass().add("dialog-tag-combo");
        if (dark) tagCombo.getStyleClass().add("dialog-tag-combo-dark");
        
        Label tagHint = new Label("\uD83D\uDCA1 Type to create a new tag, or pick from existing ones");
        tagHint.setStyle("-fx-text-fill: " + (dark ? "#666680" : "#9CA3AF") + "; -fx-font-size: 11px;");
        VBox tagSection = new VBox(6, tagFieldLabel, tagCombo, tagHint);
        
        // --- Content area with accent stripe ---
        Label contentFieldLabel = new Label("CONTENT");
        contentFieldLabel.setStyle("-fx-text-fill: " + textSecondary + "; -fx-font-weight: bold; "
            + "-fx-font-size: 11px; -fx-letter-spacing: 1;");
        TextArea contentArea = new TextArea(note.getContent());
        contentArea.setPromptText("Write your thoughts here...");
        contentArea.setWrapText(true);
        contentArea.setPrefRowCount(8);
        contentArea.getStyleClass().add("dialog-content-area");
        if (dark) contentArea.getStyleClass().add("dialog-content-area-dark");
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        VBox contentSection = new VBox(6, contentFieldLabel, contentArea);
        VBox.setVgrow(contentSection, Priority.ALWAYS);
        
        formCard.getChildren().addAll(titleSection, tagSection, contentSection);
        VBox.setVgrow(formCard, Priority.ALWAYS);
        
        // === Bottom button bar ===
        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(0, 24, 20, 24));
        
        Button deleteButton = new Button("\uD83D\uDDD1  Delete");
        deleteButton.setStyle("-fx-background-color: #EF4444; "
            + "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; "
            + "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-size: 12px;");
        deleteButton.setVisible(!isNew);
        deleteButton.setManaged(!isNew);
        
        Region buttonSpacer = new Region();
        HBox.setHgrow(buttonSpacer, Priority.ALWAYS);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: " + (dark ? "#3A3A55" : "#E5E7EB") + "; "
            + "-fx-text-fill: " + textPrimary + "; -fx-padding: 10 24; "
            + "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        Button saveButton = new Button("\u2714  Save Note");
        saveButton.setStyle("-fx-background-color: " + accentGrad + "; "
            + "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 28; "
            + "-fx-background-radius: 10; -fx-cursor: hand; -fx-font-size: 13px; "
            + "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 8, 0, 0, 3);");
        
        buttons.getChildren().addAll(deleteButton, buttonSpacer, cancelButton, saveButton);
        
        // === Actions ===
        saveButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) title = "Untitled Note";
            String tag = tagCombo.getEditor().getText().trim();
            if (tag.isEmpty()) tag = "General";
            noteService.updateNote(note, title, contentArea.getText(), tag);
            refreshView();
            dialog.close();
        });
        
        cancelButton.setOnAction(e -> {
            if (isNew) { noteService.deleteNote(note); refreshView(); }
            dialog.close();
        });
        
        deleteButton.setOnAction(e -> {
            noteService.deleteNote(note);
            refreshView();
            dialog.close();
        });
        
        outerRoot.getChildren().addAll(header, formCard, buttons);
        
        // Make dialog draggable (since UNDECORATED)
        final double[] dragOffset = new double[2];
        header.setOnMousePressed(e -> {
            dragOffset[0] = e.getSceneX();
            dragOffset[1] = e.getSceneY();
        });
        header.setOnMouseDragged(e -> {
            dialog.setX(e.getScreenX() - dragOffset[0]);
            dialog.setY(e.getScreenY() - dragOffset[1]);
        });
        
        Scene dialogScene = new Scene(outerRoot, 560, 560);
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        // Load the shared stylesheet so CSS classes apply
        String css = getClass().getResource("/styles.css").toExternalForm();
        dialogScene.getStylesheets().add(css);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }
    
    /**
     * Start auto-save timeline (every 30 seconds)
     */
    private void startAutoSave() {
        autoSaveTimeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> {
            noteService.saveNotes();
            System.out.println("[Auto-save] Notes saved at " + java.time.LocalDateTime.now());
        }));
        autoSaveTimeline.setCycleCount(Timeline.INDEFINITE);
        autoSaveTimeline.play();
    }
    
    /**
     * Called when the application is closing
     */
    public void onApplicationClose() {
        if (autoSaveTimeline != null) {
            autoSaveTimeline.stop();
        }
        noteService.saveNotes();
    }
    
    /**
     * Get the main view
     */
    public MainView getView() {
        return view;
    }
}
