package com.sorasync.sorasync.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UploadSong {
    private String albumName, songName, songDuration, songLink, mKey, username, fullName;
    private Date timeStamp;

    public UploadSong() {
    }

    public UploadSong(String albumName, String songName, String songDuration, String songLink, String username, String fullName) {
        if (albumName.trim().equals("")) {
            albumName = "No Album";
        }
        this.albumName = albumName;
        this.songName = songName;
        this.songDuration = songDuration;
        this.songLink = songLink;
        this.username = username;
        this.fullName = fullName;
        this.timeStamp = Calendar.getInstance(Locale.US).getTime();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
