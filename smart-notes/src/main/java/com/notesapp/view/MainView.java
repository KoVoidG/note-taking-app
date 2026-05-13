package com.notesapp.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import com.notesapp.model.Note;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

/**
 * Main view builder for the application.
 * Card-grid layout with dynamic tag sidebar matching the provided mockups.
 */
public class MainView {
    
    private BorderPane root;
    private Button addNoteButton;
    private ToggleButton themeToggleButton;
    private TextField searchField;
    private FlowPane noteGrid;
    private ScrollPane scrollPane;
    private VBox categorySidebar;
    private Label allNotesLabel;
    private HBox searchBox;
    
    // Callbacks
    private Consumer<Note> onNoteClick;
    private Consumer<Note> onNoteDelete;
    private Consumer<String> onCategoryFilter;
    private Runnable onAddNote;
    private Consumer<String> onSearch;
    
    // Rotating color palette for tag dots, badges, and cards
    private static final String[] DOT_COLORS = {
        "#E91E63", "#FF9800", "#2196F3", "#4CAF50", "#9C27B0", "#FFC107",
        "#00BCD4", "#FF5722", "#3F51B5", "#8BC34A", "#E040FB", "#FF6F00"
    };
    
    // Dark mode card colors - vibrant
    private static final String[] CARD_COLORS_DARK = {
        "#C62866", "#D4851A", "#1A73C7", "#2E8B57", "#7B1FA2", "#C6930E",
        "#00838F", "#D84315", "#283593", "#558B2F", "#AA00FF", "#E65100"
    };
    
    // Light mode card colors - warm, richer pastels for better visual appeal
    private static final String[] CARD_COLORS_LIGHT = {
        "#F8B4C8", "#F5C882", "#8FBCF0", "#8FD4A4", "#C8A8E8", "#F0D86C",
        "#7CD0D8", "#F09878", "#9898E0", "#A8D878", "#D898E8", "#F0B868"
    };
    
    // Map tag names to color indices for consistent coloring
    private final Map<String, Integer> tagColorMap = new LinkedHashMap<>();
    private int nextColorIndex = 0;
    
    private boolean isDarkMode = true;
    private String selectedCategory = null;
    
    public MainView() {
        buildUI();
    }
    
    // Callback setters
    public void setOnNoteClick(Consumer<Note> handler) { this.onNoteClick = handler; }
    public void setOnNoteDelete(Consumer<Note> handler) { this.onNoteDelete = handler; }
    public void setOnCategoryFilter(Consumer<String> handler) { this.onCategoryFilter = handler; }
    public void setOnAddNote(Runnable handler) { this.onAddNote = handler; }
    public void setOnSearch(Consumer<String> handler) { this.onSearch = handler; }
    
    /**
     * Get a stable color index for a tag (assigns one if new)
     */
    private int getColorIndex(String tag) {
        if (!tagColorMap.containsKey(tag)) {
            tagColorMap.put(tag, nextColorIndex % DOT_COLORS.length);
            nextColorIndex++;
        }
        return tagColorMap.get(tag);
    }
    
    /**
     * Build the complete UI structure
     */
    private void buildUI() {
        root = new BorderPane();
        root.getStyleClass().add("root");
        root.getStyleClass().add("dark-mode");
        
        root.setTop(buildTopBar());
        
        categorySidebar = new VBox(4);
        categorySidebar.getStyleClass().add("category-sidebar");
        categorySidebar.setPrefWidth(185);
        categorySidebar.setMinWidth(160);
        categorySidebar.setPadding(new Insets(20, 12, 20, 12));
        root.setLeft(categorySidebar);
        
        root.setCenter(buildNoteGrid());
    }
    
    /**
     * Build the top bar
     */
    private HBox buildTopBar() {
        // User avatar
        StackPane avatar = new StackPane();
        Circle avatarCircle = new Circle(18);
        avatarCircle.getStyleClass().add("avatar-circle");
        Label avatarLabel = new Label("SD");
        avatarLabel.getStyleClass().add("avatar-text");
        avatar.getChildren().addAll(avatarCircle, avatarLabel);
        
        Label userNameLabel = new Label("Steve Dean");
        userNameLabel.getStyleClass().add("user-name");
        
        HBox userSection = new HBox(10, avatar, userNameLabel);
        userSection.setAlignment(Pos.CENTER_LEFT);
        
        // "All Notes" title
        allNotesLabel = new Label("All Notes");
        allNotesLabel.getStyleClass().add("all-notes-title");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Search icon button
        Button searchButton = new Button("\uD83D\uDD0D");
        searchButton.getStyleClass().add("icon-button");
        searchButton.setOnAction(e -> toggleSearch());
        
        // Search field (initially hidden)
        searchField = new TextField();
        searchField.getStyleClass().add("search-field");
        searchField.setPromptText("Search notes...");
        searchField.setPrefWidth(200);
        searchField.setVisible(false);
        searchField.setManaged(false);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (onSearch != null) onSearch.accept(newVal);
        });
        
        searchBox = new HBox(5, searchButton, searchField);
        searchBox.setAlignment(Pos.CENTER);
        
        // Theme toggle
        themeToggleButton = new ToggleButton("ON");
        themeToggleButton.getStyleClass().add("theme-toggle");
        themeToggleButton.setSelected(true);
        
        // Add New Note button
        addNoteButton = new Button("Add New Note");
        addNoteButton.getStyleClass().add("add-note-button");
        addNoteButton.setOnAction(e -> {
            if (onAddNote != null) onAddNote.run();
        });
        
        HBox topBar = new HBox(15);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().addAll(userSection, allNotesLabel, spacer, searchBox, themeToggleButton, addNoteButton);
        
        return topBar;
    }
    
    private void toggleSearch() {
        boolean show = !searchField.isVisible();
        searchField.setVisible(show);
        searchField.setManaged(show);
        if (show) {
            searchField.requestFocus();
        } else {
            searchField.clear();
        }
    }
    
    /**
     * Rebuild the category sidebar dynamically from current tag counts
     */
    public void rebuildCategorySidebar(Map<String, Integer> tagCounts) {
        categorySidebar.getChildren().clear();
        
        // "All" row at top — shows total count and resets filter
        int totalCount = tagCounts.values().stream().mapToInt(Integer::intValue).sum();
        Circle allDot = new Circle(5);
        allDot.setFill(Color.web("#667eea"));
        Label allLabel = new Label("All");
        allLabel.getStyleClass().add("category-name");
        allLabel.setStyle("-fx-font-weight: bold;");
        Region allSpacer = new Region();
        HBox.setHgrow(allSpacer, Priority.ALWAYS);
        Label allCount = new Label(String.format("%02d", totalCount));
        allCount.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; "
            + "-fx-background-radius: 10; -fx-padding: 2 8 2 8; -fx-font-size: 11px; -fx-font-weight: bold;");
        HBox allRow = new HBox(10, allDot, allLabel, allSpacer, allCount);
        allRow.setAlignment(Pos.CENTER_LEFT);
        allRow.getStyleClass().add("category-row");
        allRow.setPadding(new Insets(10, 12, 10, 12));
        if (selectedCategory == null) {
            allRow.getStyleClass().add("category-row-selected");
        }
        allRow.setOnMouseClicked(e -> {
            if (onCategoryFilter != null) {
                onCategoryFilter.accept(null);
            }
        });
        categorySidebar.getChildren().add(allRow);
        
        // Separator
        Region sep = new Region();
        sep.setMinHeight(1);
        sep.setMaxHeight(1);
        sep.setStyle("-fx-background-color: #CCCCCC;");
        VBox.setMargin(sep, new Insets(4, 8, 4, 8));
        categorySidebar.getChildren().add(sep);
        
        // Individual tag rows
        for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
            String tag = entry.getKey();
            int count = entry.getValue();
            int colorIdx = getColorIndex(tag);
            String dotColor = DOT_COLORS[colorIdx];
            
            Circle dot = new Circle(5);
            dot.setFill(Color.web(dotColor));
            
            Label nameLabel = new Label(tag);
            nameLabel.getStyleClass().add("category-name");
            
            Region catSpacer = new Region();
            HBox.setHgrow(catSpacer, Priority.ALWAYS);
            
            Label countLabel = new Label(String.format("%02d", count));
            countLabel.setStyle("-fx-background-color: " + dotColor + "; -fx-text-fill: white; "
                + "-fx-background-radius: 10; -fx-padding: 2 8 2 8; -fx-font-size: 11px; -fx-font-weight: bold;");
            
            HBox row = new HBox(10, dot, nameLabel, catSpacer, countLabel);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("category-row");
            row.setPadding(new Insets(10, 12, 10, 12));
            
            if (tag.equals(selectedCategory)) {
                row.getStyleClass().add("category-row-selected");
            }
            
            row.setOnMouseClicked(e -> {
                if (onCategoryFilter != null) {
                    onCategoryFilter.accept(tag);
                }
            });
            
            categorySidebar.getChildren().add(row);
        }
    }
    
    /**
     * Set the selected category and update title
     */
    public void setSelectedCategory(String tag) {
        this.selectedCategory = tag;
        if (tag == null) {
            allNotesLabel.setText("All Notes");
        } else {
            allNotesLabel.setText(tag + " Notes");
        }
    }
    
    /**
     * Build the scrollable note card grid
     */
    private ScrollPane buildNoteGrid() {
        noteGrid = new FlowPane();
        noteGrid.getStyleClass().add("note-grid");
        noteGrid.setHgap(20);
        noteGrid.setVgap(20);
        noteGrid.setPadding(new Insets(25));
        noteGrid.setPrefWrapLength(700);
        
        scrollPane = new ScrollPane(noteGrid);
        scrollPane.getStyleClass().add("note-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }
    
    /**
     * Create a note card for the grid
     */
    public VBox createNoteCard(Note note) {
        VBox card = new VBox(10);
        card.getStyleClass().add("note-card");
        card.setPrefWidth(380);
        card.setMinWidth(300);
        card.setMaxWidth(450);
        card.setPadding(new Insets(20));
        
        // Get card color based on tag
        String tag = note.getTag() != null && !note.getTag().isEmpty() ? note.getTag() : "General";
        int colorIdx = getColorIndex(tag);
        String cardColor;
        if (isDarkMode) {
            cardColor = CARD_COLORS_DARK[colorIdx];
        } else {
            cardColor = CARD_COLORS_LIGHT[colorIdx];
        }
        card.setStyle("-fx-background-color: " + cardColor + "; -fx-background-radius: 16;");
        
        // Header: title + tag badge
        Label titleLabel = new Label(note.getTitle());
        titleLabel.getStyleClass().add("card-title");
        if (!isDarkMode) {
            titleLabel.setStyle("-fx-text-fill: #1a1a2e; -fx-font-size: 17px; -fx-font-weight: bold;");
        }
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(250);
        
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        
        Label tagBadge = new Label(tag);
        String badgeColor = DOT_COLORS[colorIdx];
        if (isDarkMode) {
            tagBadge.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-text-fill: white; "
                + "-fx-background-radius: 12; -fx-padding: 4 12 4 12; -fx-font-size: 10px; -fx-font-weight: bold;");
        } else {
            tagBadge.setStyle("-fx-background-color: " + badgeColor + "; -fx-text-fill: white; "
                + "-fx-background-radius: 12; -fx-padding: 4 12 4 12; -fx-font-size: 10px; -fx-font-weight: bold;");
        }
        
        HBox header = new HBox(10, titleLabel, headerSpacer, tagBadge);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Content preview
        Label contentLabel = new Label(truncateContent(note.getContent(), 120));
        contentLabel.getStyleClass().add("card-content");
        if (!isDarkMode) {
            contentLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 12px;");
        }
        contentLabel.setWrapText(true);
        
        Region middleSpacer = new Region();
        VBox.setVgrow(middleSpacer, Priority.ALWAYS);
        
        // Footer: time and date
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mma");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        
        String timeStr = note.getModifiedAt().format(timeFormat);
        String dateStr = note.getModifiedAt().format(dateFormat).toUpperCase();
        
        Label timeLabel = new Label(timeStr);
        timeLabel.getStyleClass().add("card-time");
        if (!isDarkMode) {
            timeLabel.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px; -fx-font-weight: bold;");
        }
        
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        
        Label dateLabel = new Label(dateStr);
        dateLabel.getStyleClass().add("card-date");
        if (!isDarkMode) {
            dateLabel.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px; -fx-font-weight: bold;");
        }
        
        HBox footer = new HBox(10, timeLabel, footerSpacer, dateLabel);
        footer.setAlignment(Pos.CENTER_LEFT);
        
        card.getChildren().addAll(header, contentLabel, middleSpacer, footer);
        
        // Click to edit
        card.setOnMouseClicked(e -> {
            if (onNoteClick != null) onNoteClick.accept(note);
        });
        
        // Context menu for delete
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete Note");
        deleteItem.setOnAction(e -> {
            if (onNoteDelete != null) onNoteDelete.accept(note);
        });
        contextMenu.getItems().add(deleteItem);
        card.setOnContextMenuRequested(e -> contextMenu.show(card, e.getScreenX(), e.getScreenY()));
        
        return card;
    }
    
    /**
     * Refresh all note cards in the grid
     */
    public void refreshNoteGrid(List<Note> notes) {
        noteGrid.getChildren().clear();
        for (Note note : notes) {
            noteGrid.getChildren().add(createNoteCard(note));
        }
    }
    
    /**
     * Handle theme toggle (called from controller)
     */
    public void applyTheme(boolean dark) {
        isDarkMode = dark;
        if (dark) {
            if (!root.getStyleClass().contains("dark-mode")) {
                root.getStyleClass().add("dark-mode");
            }
            themeToggleButton.setText("ON");
        } else {
            root.getStyleClass().remove("dark-mode");
            themeToggleButton.setText("OFF");
        }
    }
    
    private String truncateContent(String content, int maxLen) {
        if (content == null || content.isEmpty()) return "";
        if (content.length() <= maxLen) return content;
        return content.substring(0, maxLen) + "...";
    }
    
    // Getters
    public BorderPane getRoot() { return root; }
    public Button getAddNoteButton() { return addNoteButton; }
    public ToggleButton getThemeToggleButton() { return themeToggleButton; }
    public TextField getSearchField() { return searchField; }
    public FlowPane getNoteGrid() { return noteGrid; }
    public boolean isDarkMode() { return isDarkMode; }
    public String getSelectedCategory() { return selectedCategory; }
}
