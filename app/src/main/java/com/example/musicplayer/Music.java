package com.example.musicplayer;

public class Music {
    private String musicTitle;
    private String singerName;
    private String songDuration;

    public String getMusicTitle() {
        return musicTitle;
    }

    public String getSingerName() {
        return singerName;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public Music(String musicTitle, String singerName, String songDuration) {
        this.musicTitle = musicTitle;
        this.singerName = singerName;
        this.songDuration = songDuration;
    }
}
