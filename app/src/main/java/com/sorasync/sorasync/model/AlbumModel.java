package com.sorasync.sorasync.model;

import java.util.Objects;

public class AlbumModel {
    private String albumName;
    private String artistName;

    public AlbumModel(String albumName, String artistName){
        this.albumName = albumName;
        this.artistName = artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlbumModel that = (AlbumModel) o;
        return Objects.equals(albumName, that.albumName) &&
                Objects.equals(artistName, that.artistName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(albumName, artistName);
    }
}
