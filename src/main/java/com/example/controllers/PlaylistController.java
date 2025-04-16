package com.example.controllers;

import com.example.models.Playlist;
import com.example.models.Song;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.List;
import java.util.stream.Collectors;

public class PlaylistController {

    @FXML
    private ListView<Song> playlistView;

    @FXML
    private TextField songTitleField;

    @FXML
    private TextField searchField; // For search bar

    private Playlist playlist;

    public PlaylistController() {
        this.playlist = new Playlist("My Playlist");
    }

    @FXML
    public void initialize() {
        updatePlaylistView();
    }

    @FXML
    private void goToPlayer() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/player.fxml"));
            Stage stage = (Stage) playlistView.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            // Handle error
        }
    }

    @FXML
    public void addSong() {
        String title = songTitleField.getText();
        if (!title.isEmpty()) {
            Song newSong = new Song(title, "Unknown Artist", "unknown.mp3");
            playlist.addSong(newSong);
            updatePlaylistView();
            songTitleField.clear();
        }
    }

    @FXML
    public void removeSong() {
        Song selectedSong = playlistView.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            playlist.removeSong(selectedSong);
            updatePlaylistView();
        }
    }

    @FXML
    private void searchSongs() {
        String query = searchField.getText().toLowerCase();
        List<Song> filtered = playlist.getSongs().stream()
            .filter(song -> song.getTitle().toLowerCase().contains(query) || song.getArtist().toLowerCase().contains(query))
            .collect(Collectors.toList());
        playlistView.getItems().setAll(filtered);
    }

    @FXML
    private void showLikedSongs() {
        List<Song> liked = playlist.getSongs().stream()
            .filter(Song::isLiked)
            .collect(Collectors.toList());
        playlistView.getItems().setAll(liked);
    }

    private void updatePlaylistView() {
        playlistView.getItems().clear();
        playlistView.getItems().addAll(playlist.getSongs());
    }
}