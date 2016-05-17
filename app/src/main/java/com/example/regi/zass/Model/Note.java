package com.example.regi.zass.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable{

    private int speed;
    private int size;
    private int countdown;
    private int margin;
    private boolean showMarker;
    private boolean showTimer;
    private boolean shouldLoop;
    private boolean light;
    private String tags;
    private String readingText;
    private String readingTitle;
    private String dateCreated;
    private String key;

    public Note() {}

    public Note(int speed, int size, int countdown, int margin, boolean showMarker, boolean showTimer, boolean shouldLoop, boolean light, String tags,String readingText,String readingTitle, String dateCreated) {
        this.speed = speed;
        this.size = size;
        this.countdown = countdown;
        this.margin = margin;
        this.showMarker = showMarker;
        this.showTimer = showTimer;
        this.shouldLoop = shouldLoop;
        this.light = light;
        this.tags = tags;
        this.readingText = readingText;
        this.readingTitle = readingTitle;
        this.dateCreated = dateCreated;
    }

    public String getKey() {return key;}

    public void setKey(String key) {this.key = key;}

    public String getDateCreated() {
        return dateCreated;
    }

    public String getReadingTitle() {return readingTitle;}

    public String getReadingText() {return readingText;}

    public String getTags() {
        return tags;
    }

    public int getSpeed() {
        return speed;
    }

    public int getSize() {
        return size;
    }

    public boolean isLight() {
        return light;
    }

    public int getCountdown() {
        return countdown;
    }

    public boolean isShouldLoop() {
        return shouldLoop;
    }

    public int getMargin() {
        return margin;
    }

    public boolean isShowMarker() {
        return showMarker;
    }

    public boolean isShowTimer() {
        return showTimer;
    }


    Note(Parcel in) {
        this.speed = in.readInt();
        this.size = in.readInt();
        this.countdown = in.readInt();
        this.margin = in.readInt();

        this.showMarker = in.readByte() !=0 ;
        this.showTimer = in.readByte() !=0 ;
        this.shouldLoop = in.readByte() !=0 ;
        this.light = in.readByte() !=0 ;

        this.tags = in.readString();
        this.readingText = in.readString();
        this.readingTitle = in.readString();
        this.dateCreated = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(speed);
        dest.writeInt(size);
        dest.writeInt(countdown);
        dest.writeInt(margin);

        dest.writeByte((byte) (showMarker ? 1 : 0));
        dest.writeByte((byte) (showTimer ? 1 : 0));
        dest.writeByte((byte) (shouldLoop ? 1 : 0));
        dest.writeByte((byte) (light ? 1 : 0));

        dest.writeString(tags);
        dest.writeString(readingText);
        dest.writeString(readingTitle);
        dest.writeString(dateCreated);

    }


    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public void setShowMarker(boolean showMarker) {
        this.showMarker = showMarker;
    }

    public void setShowTimer(boolean showTimer) {
        this.showTimer = showTimer;
    }

    public void setShouldLoop(boolean shouldLoop) {
        this.shouldLoop = shouldLoop;
    }

    public void setLight(boolean light) {
        this.light = light;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setReadingText(String readingText) {
        this.readingText = readingText;
    }

    public void setReadingTitle(String readingTitle) {
        this.readingTitle = readingTitle;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
