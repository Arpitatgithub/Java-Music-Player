package com.example.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.models.Song;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PlayerController {
    // FXML Injected Controls
    @FXML private Label songLabel;
    @FXML private Label artistLabel;
    @FXML private Label currentTimeLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Slider musicSlider;
    @FXML private Slider volumeSlider;
    @FXML private ImageView albumArt;
    @FXML private TextField searchField;
    @FXML private ListView<Song> playlistListView;
    @FXML private Button likeButton;
    @FXML private ToggleButton playPauseButton;

    // State Management
    private List<Song> allSongs = new ArrayList<>();
    private List<Song> filteredSongs = new ArrayList<>();
    private int currentSongIndex = 0;
    private MediaPlayer mediaPlayer;
    private boolean isSeeking = false;

    @FXML
    public void initialize() {
        initializeSongs();
        setupListeners();
        setupInitialState();
    }

    private void initializeSongs() {
        // Add your songs here
        allSongs.add(new Song("Running up that hill", "Kate Bush", "/music/song1.mp3"));
        allSongs.add(new Song("Take it easy", "Karan Aujla", "/music/song2.mp3"));
        allSongs.add(new Song("Sawan Ka Mahina", "Lata Mangeshkar, Mukesh ", "/music/song3.mp3"));
        allSongs.add(new Song("Skyfall", "Adele", "/music/song4.mp3"));
        allSongs.add(new Song("52 Bars", "Karan Aujla", "/music/song5.mp3"));
        allSongs.add(new Song("Sawan Ka Mahina", "Lata Mangeshkar, Mukesh ", "/music/song3.mp3"));
        allSongs.add(new Song("Sawan Ka Mahina", "Lata Mangeshkar, Mukesh ", "/music/song3.mp3"));
        allSongs.add(new Song("Sawan Ka Mahina", "Lata Mangeshkar, Mukesh ", "/music/song3.mp3"));
        allSongs.add(new Song("Sawan Ka Mahina", "Lata Mangeshkar, Mukesh ", "/music/song3.mp3"));  
        filteredSongs.addAll(allSongs);
        playlistListView.getItems().setAll(filteredSongs);
    }

    private void setupListeners() {
        // Playlist selection listener
        playlistListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, selected) -> {
                if (selected != null) {
                    playSong(filteredSongs.indexOf(selected));
                }
            }
        );

        // Volume control
        volumeSlider.setValue(50);
        volumeSlider.valueProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(newVal.doubleValue() / 100.0);
                }
            }
        );

        // Music slider control
        setupMusicSliderListeners();
    }

    private void setupMusicSliderListeners() {
        musicSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            isSeeking = isChanging;
            if (!isChanging && mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(musicSlider.getValue()));
            }
        });

        musicSlider.setOnMouseReleased(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(musicSlider.getValue()));
            }
        });
    }
    @FXML private Text lyricsText;

    private void setupInitialState() {
        setAlbumArt(null);
        playPauseButton.setSelected(true); // Initially in paused state
        if (!filteredSongs.isEmpty()) {
            playSong(0);
        }
    }

    private void playSong(int index) {
        if (index < 0 || index >= filteredSongs.size()) return;

        Song song = filteredSongs.get(index); // <-- This line fixes the errors

        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            String mediaPath = getClass().getResource(song.getFilePath()).toExternalForm();
            Media media = new Media(mediaPath);
            mediaPlayer = new MediaPlayer(media);

            // Update UI
            songLabel.setText(song.getTitle());
            artistLabel.setText(song.getArtist());
            setAlbumArt(song.getTitle());
            likeButton.setText(song.isLiked() ? "♥" : "♡");

            // Setup media player
            setupMediaPlayer();

            // Update play/pause state
            updatePlayPauseState(true);

            // Play the song
            mediaPlayer.play();

        } catch (Exception e) {
            System.err.println("Error playing song: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupMediaPlayer() {
        if (mediaPlayer == null) return;

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!isSeeking) {
                musicSlider.setValue(newTime.toSeconds());
                currentTimeLabel.setText(formatTime(newTime));
            }
        });

        mediaPlayer.setOnReady(() -> {
            Duration total = mediaPlayer.getMedia().getDuration();
            musicSlider.setMax(total.toSeconds());
            totalTimeLabel.setText(formatTime(total));
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            nextTrack();
        });

        mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
    }

    // Playback Control Methods

    @FXML
    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (playPauseButton.isSelected()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        }
    }

    private void updatePlayPauseState(boolean isPlaying) {
        playPauseButton.setSelected(!isPlaying);
        // Update the button state when a song starts/stops
        if (mediaPlayer != null) {
            mediaPlayer.setOnPlaying(() -> playPauseButton.setSelected(false));
            mediaPlayer.setOnPaused(() -> playPauseButton.setSelected(true));
            mediaPlayer.setOnStopped(() -> playPauseButton.setSelected(true));
        }
    }

    @FXML
    private void previousTrack() {
        if (!filteredSongs.isEmpty()) {
            currentSongIndex = (currentSongIndex - 1 + filteredSongs.size()) % filteredSongs.size();
            playSong(currentSongIndex);
        }
    }

    @FXML
    private void nextTrack() {
        if (!filteredSongs.isEmpty()) {
            currentSongIndex = (currentSongIndex + 1) % filteredSongs.size();
            playSong(currentSongIndex);
        }
    }

    @FXML
    private void toggleLike() {
        if (!filteredSongs.isEmpty()) {
            Song currentSong = filteredSongs.get(currentSongIndex);
            currentSong.setLiked(!currentSong.isLiked());
            likeButton.setText(currentSong.isLiked() ? "♥" : "♡");
        }
    }

    // Playlist Management Methods
    @FXML
    private void search() {
        String query = searchField.getText().toLowerCase().trim();
        filteredSongs = query.isEmpty() ? new ArrayList<>(allSongs) :
            allSongs.stream()
                .filter(song -> song.getTitle().toLowerCase().contains(query) ||
                               song.getArtist().toLowerCase().contains(query))
                .collect(Collectors.toList());

        playlistListView.getItems().setAll(filteredSongs);
        if (filteredSongs.isEmpty()) {
            clearNowPlaying();
        }
    }

    @FXML
    private void showLikedSongs() {
        filteredSongs = allSongs.stream()
                .filter(Song::isLiked)
                .collect(Collectors.toList());
        playlistListView.getItems().setAll(filteredSongs);
    }

    @FXML
    private void showAllSongs() {
        filteredSongs = new ArrayList<>(allSongs);
        playlistListView.getItems().setAll(filteredSongs);
    }

    private void clearNowPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }

        songLabel.setText("No song playing");
        artistLabel.setText("");
        currentTimeLabel.setText("0:00");
        totalTimeLabel.setText("0:00");
        musicSlider.setValue(0);
        setAlbumArt(null);
    }

    private String formatTime(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    // UI Update Methods
    @FXML
    private void reloadUI() {
        try {
            Stage stage = (Stage) songLabel.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/player.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

            PlayerController newController = loader.getController();
            newController.restoreState(this);

            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to reload FXML: " + e.getMessage());
        }
    }

    private void restoreState(PlayerController oldController) {
        this.allSongs = new ArrayList<>(oldController.allSongs);
        this.filteredSongs = new ArrayList<>(oldController.filteredSongs);
        this.currentSongIndex = oldController.currentSongIndex;
        initialize();
    }

    // Helper Methods
    private void setAlbumArt(String songTitle) {
        try {
            String imgPath = songTitle != null
                    ? "/images/" + songTitle.replaceAll("\\s+", "").toLowerCase() + ".png"
                    : "/images/default-album.png";
            Image img = new Image(getClass().getResourceAsStream(imgPath));
            albumArt.setImage(img);
        } catch (Exception e) {
            System.err.println("Error loading album art: " + e.getMessage());
            albumArt.setImage(new Image(getClass().getResourceAsStream("/images/default-album.png")));
        }
    }
}