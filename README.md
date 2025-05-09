# javafx-music-app

## Overview
This project is a music application built using Java and JavaFX, inspired by popular music streaming services like Spotify. The application allows users to log in, manage playlists, and play music.

## Features
- User authentication (login and registration)
- Music playback controls (play, pause, skip)
- Playlist management (add, remove, display songs)
- Intuitive user interface built with FXML

## Project Structure
```
javafx-music-app
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── com
│   │   │   │   └── example
│   │   │   │       ├── Main.java
│   │   │   │       ├── controllers
│   │   │   │       │   ├── LoginController.java
│   │   │   │       │   ├── PlayerController.java
│   │   │   │       │   └── PlaylistController.java
│   │   │   │       ├── models
│   │   │   │       │   ├── Song.java
│   │   │   │       │   ├── Playlist.java
│   │   │   │       │   └── User.java
│   │   │   │       └── services
│   │   │   │           ├── MusicService.java
│   │   │   │           └── AuthService.java
│   │   └── resources
│   │       ├── views
│   │       │   ├── login.fxml
│   │       │   ├── player.fxml
│   │       │   └── playlist.fxml
│   │       └── application.css
├── .gitignore
├── build.gradle
└── README.md
```

## Setup Instructions
1. Clone the repository:
   ```
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```
   cd javafx-music-app
   ```
3. Build the project using Gradle:
   ```
   ./gradlew build
   ```
4. Run the application:
   ```
   ./gradlew run
   ```

## Technologies Used
- Java
- JavaFX
- FXML
- Gradle

## Contributing
Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.#   J a v a - M u s i c - P l a y e r  
 