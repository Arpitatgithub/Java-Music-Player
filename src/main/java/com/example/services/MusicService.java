package com.example.services;

import com.example.models.Song;
import java.util.ArrayList;
import java.util.List;

public class MusicService {
    private List<Song> songList;
    private Song currentSong;
    private boolean isPlaying;

    public MusicService() {
        this.songList = new ArrayList<>();
        this.isPlaying = false;
    }

    public void loadSongs(List<Song> songs) {
        this.songList.clear();
        this.songList.addAll(songs);
    }

    public void playSong(Song song) {
        if (songList.contains(song)) {
            currentSong = song;
            isPlaying = true;
            // Logic to play the song
        }
    }

    public void pauseSong() {
        if (isPlaying) {
            isPlaying = false;
            // Logic to pause the song
        }
    }

    public void skipSong() {
        int currentIndex = songList.indexOf(currentSong);
        if (currentIndex < songList.size() - 1) {
            playSong(songList.get(currentIndex + 1));
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public List<Song> getSongList() {
        return songList;
    }
}