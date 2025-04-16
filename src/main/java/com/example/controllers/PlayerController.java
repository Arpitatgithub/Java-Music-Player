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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PlayerController {
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

    private List<Song> allSongs = new ArrayList<>();
    private List<Song> filteredSongs = new ArrayList<>();
    private int currentSongIndex = 0;
    private MediaPlayer mediaPlayer;
    private boolean isSeeking = false;

    @FXML
private void reloadUI() {
    try {
        Stage stage = (Stage) songLabel.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/views/player.fxml"));
        stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
    } catch (Exception e) {
        System.out.println("Failed to reload FXML: " + e.getMessage());
    }
}

    @FXML
    public void initialize() {
        // Example songs
        allSongs.add(new Song("Running up that hill", "Kate Bush", "/music/song1.mp3"));
        allSongs.add(new Song("Take it easy", "Karan Aujla", "/music/song2.mp3"));
        allSongs.add(new Song("Song Three", "Artist C", "/music/song3.mp3"));
        filteredSongs.addAll(allSongs);

        playlistListView.getItems().setAll(filteredSongs);

        playlistListView.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                playSong(filteredSongs.indexOf(selected));
            }
        });

        setAlbumArt(null);

        // Volume slider
        volumeSlider.setValue(50);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue() / 100.0);
            }
        });

        // Play first song by default
        if (!filteredSongs.isEmpty()) {
            playSong(0);
        }
    }

    @FXML
    private void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    @FXML
    private void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @FXML
    private void previousTrack() {
        if (filteredSongs.isEmpty()) return;
        currentSongIndex = (currentSongIndex - 1 + filteredSongs.size()) % filteredSongs.size();
        playSong(currentSongIndex);
    }

    @FXML
    private void nextTrack() {
        if (filteredSongs.isEmpty()) return;
        currentSongIndex = (currentSongIndex + 1) % filteredSongs.size();
        playSong(currentSongIndex);
    }

    @FXML
    private void toggleLike() {
        if (filteredSongs.isEmpty()) return;
        Song current = filteredSongs.get(currentSongIndex);
        current.setLiked(!current.isLiked());
        updateLikeButton(current);
        playlistListView.refresh();
    }

    @FXML
    private void search() {
        String query = searchField.getText().toLowerCase().trim();
        if (query.isEmpty()) {
            filteredSongs = new ArrayList<>(allSongs);
        } else {
            filteredSongs = allSongs.stream()
                .filter(song -> song.getTitle().toLowerCase().contains(query) ||
                                song.getArtist().toLowerCase().contains(query))
                .collect(Collectors.toList());
        }
        playlistListView.getItems().setAll(filteredSongs);
        // Do not auto-play
        // Optionally, clear selection or update UI as needed
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
        // Do not auto-play
        // Optionally, clear selection or update UI as needed
    }

    @FXML
    private void showAllSongs() {
        filteredSongs = new ArrayList<>(allSongs);
        playlistListView.getItems().setAll(filteredSongs);
        // Do not auto-play
        // Optionally, clear selection or update UI as needed
    }

    private void playSong(int index) {
        if (filteredSongs.isEmpty()) return;
        currentSongIndex = index;
        Song song = filteredSongs.get(index);

        // Stop previous player
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            // Check if resource exists
            var resourceUrl = getClass().getResource(song.getFilePath());
            if (resourceUrl == null) {
                songLabel.setText("Audio file not found: " + song.getFilePath());
                artistLabel.setText("");
                setAlbumArt(null);
                updateLikeButton(song);
                return;
            }
            String resource = resourceUrl.toExternalForm();
            Media media = new Media(resource);
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(() -> {
                double totalSeconds = mediaPlayer.getTotalDuration().toSeconds();
                musicSlider.setMax(totalSeconds);
                totalTimeLabel.setText(formatTime(totalSeconds));
                mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
                musicSlider.setValue(0);
                currentTimeLabel.setText("0:00");
            });

            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (!isSeeking) {
                    double seconds = newTime.toSeconds();
                    musicSlider.setValue(seconds);
                    currentTimeLabel.setText(formatTime(seconds));
                }
            });

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

            mediaPlayer.play();
            songLabel.setText(song.getTitle());
            artistLabel.setText(song.getArtist());
            setAlbumArt(song.getTitle());
            updateLikeButton(song);
            playlistListView.getSelectionModel().select(song);
        } catch (Exception e) {
            songLabel.setText("Error playing song");
            artistLabel.setText("Please check audio file");
            setAlbumArt(null);
            updateLikeButton(song);
        }
    }

    private void setAlbumArt(String songTitle) {
        try {
            String imgPath = songTitle != null
                    ? "/images/" + songTitle.replaceAll("\\s+", "").toLowerCase() + ".png"
                    : "/images/default-album.png";
            Image img = new Image(getClass().getResourceAsStream(imgPath));
            albumArt.setImage(img);
        } catch (Exception e) {
            albumArt.setImage(new Image(getClass().getResourceAsStream("/images/default-album.png")));
        }
    }

    private void updateLikeButton(Song song) {
        if (likeButton != null) {
            likeButton.setText(song.isLiked() ? "♥" : "♡");
        }
    }

    private void clearNowPlaying() {
        songLabel.setText("No song playing");
        artistLabel.setText("");
        setAlbumArt(null);
        if (likeButton != null) likeButton.setText("♡");
        currentTimeLabel.setText("0:00");
        totalTimeLabel.setText("0:00");
        musicSlider.setValue(0);
    }

    private String formatTime(double seconds) {
        int min = (int) seconds / 60;
        int sec = (int) seconds % 60;
        return String.format("%d:%02d", min, sec);
    }
}