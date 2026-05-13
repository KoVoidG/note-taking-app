# 📝 Smart Notes

A modern, feature-rich desktop note-taking application built with **JavaFX** and **Java 17**, following a clean MVC architecture. Smart Notes lets you create, organise, search, and manage your notes with a polished, theme-switchable UI.

---

## ✨ Features

- **Create & Edit Notes** — Add notes with a title, content, and a custom tag/category
- **Tag System** — Organise notes into categories; type a new tag or pick from existing ones
- **Search** — Real-time search across note titles, content, and tags
- **Category Sidebar** — Filter notes by category with live note counts
- **Dark / Light Mode** — Toggle between dark and light themes at any time
- **Auto-Save** — Notes are automatically saved to disk every 30 seconds
- **Save on Close** — Notes are persisted whenever the app is closed
- **Delete with Confirmation** — Safe deletion with a confirmation dialog
- **Draggable Dialogs** — The note editor dialog is fully draggable

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17+ | Core language |
| JavaFX | 21.0.2 | Desktop UI framework |
| Gson | 2.10.1 | JSON serialisation / deserialisation |
| Maven | 3.x | Build & dependency management |

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **JDK 17 or later** — [Download OpenJDK](https://adoptium.net/)
- **Apache Maven 3.6+** — [Download Maven](https://maven.apache.org/download.cgi)

> **Note:** JavaFX is **not** bundled with the JDK (since Java 11). The `javafx-maven-plugin` handles downloading and wiring it up automatically — you don't need to install JavaFX separately.

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/smart-notes.git
cd smart-notes
```

### 2. Run the application

```bash
mvn javafx:run
```

That's it! Maven will download all dependencies and launch the app.

### 3. Build only (without running)

```bash
mvn compile
```

### 4. Clean build artifacts

```bash
mvn clean
```

---

## 🗂️ Project Structure

```
smart-notes/
├── pom.xml                                  # Maven build config & dependencies
├── .gitignore
├── .vscode/
│   ├── launch.json                          # VS Code run configuration
│   └── settings.json
└── src/
    └── main/
        ├── java/com/notesapp/
        │   ├── MainApp.java                 # Application entry point
        │   ├── controller/
        │   │   └── MainController.java      # Handles all user interactions
        │   ├── model/
        │   │   ├── Note.java                # Note domain model
        │   │   └── NoteRepository.java      # In-memory note store
        │   ├── service/
        │   │   └── NoteService.java         # Business logic layer
        │   ├── util/
        │   │   ├── JsonPersistence.java     # Save/load notes as JSON
        │   │   └── LocalDateTimeAdapter.java# Gson adapter for LocalDateTime
        │   └── view/
        │       └── MainView.java            # JavaFX UI layout & components
        └── resources/
            └── styles.css                   # Application stylesheet
```

---

## 🏛️ Architecture

The project follows a clean **MVC (Model-View-Controller)** pattern:

```
┌─────────────┐     callbacks      ┌──────────────────┐
│  MainView   │ ◄─────────────────► │  MainController  │
│  (JavaFX)   │                    │                  │
└─────────────┘                    └────────┬─────────┘
                                            │
                                            ▼
                                   ┌──────────────────┐
                                   │   NoteService    │  ← Business logic
                                   └────────┬─────────┘
                                            │
                              ┌─────────────┴──────────────┐
                              ▼                            ▼
                     ┌────────────────┐         ┌──────────────────┐
                     │ NoteRepository │         │ JsonPersistence  │
                     │ (in-memory)    │         │ (disk storage)   │
                     └────────────────┘         └──────────────────┘
```

| Layer | Class | Responsibility |
|---|---|---|
| **View** | `MainView` | Renders the UI; fires callbacks on user actions |
| **Controller** | `MainController` | Wires view events to service calls; manages state |
| **Service** | `NoteService` | Business logic: create, update, delete, search, filter |
| **Repository** | `NoteRepository` | In-memory `ObservableList<Note>` store |
| **Persistence** | `JsonPersistence` | Serialises notes to/from JSON on disk |
| **Model** | `Note` | Immutable ID, title, content, tag, timestamps |

---

## 💾 Data Storage

Notes are persisted as a **JSON file** on your local machine:

| OS | File Location |
|---|---|
| Windows | `C:\Users\<you>\.smart-notes\notes.json` |
| macOS / Linux | `~/.smart-notes/notes.json` |

The directory and file are created automatically on first launch. You can back up or transfer your notes by copying this file.

---

## ⌨️ Running from VS Code

A pre-configured launch config is included in `.vscode/launch.json`.

1. Open the project folder in VS Code
2. Install the **Extension Pack for Java** (by Microsoft)
3. Press `F5` → select **"Run via Maven (mvn javafx:run)"**

> **Why not just press the ▶ Run button on `MainApp.java`?**  
> The standard Java run button launches the class directly without passing `--module-path` and `--add-modules` for JavaFX — causing the *"JavaFX runtime components are missing"* error. Always use `mvn javafx:run`.

---

## 🤝 Contributing

1. Fork the repo
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m "Add my feature"`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

---

## 📄 License

This project is open source. Feel free to use, modify, and distribute it.
