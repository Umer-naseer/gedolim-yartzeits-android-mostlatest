package com.saqibdb.YahrtzeitsOfGedolim.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EventDetails implements Serializable, Parcelable {

    String dateFull;
    Integer year;
    String dayHebrewStr;
    String dayHebrew;
    String monthHebrew;
    String monthHebrewStr;
    String yearHebrew;
    String sortLetter;
    boolean isPrivate = false;
    ArrayList<String> imageList;
    ArrayList<String> extras;

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("day")
    @Expose
    private Integer day;
    @SerializedName("month")
    @Expose
    private Integer month;
    @SerializedName("subjectTitle")
    @Expose
    private String subjectTitle;
    @SerializedName("subjectDescription")
    @Expose
    private String subjectDescription;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("modified_date")
    @Expose
    private String modifiedDate;

    public EventDetails(){}

    protected EventDetails(Parcel in) {
        dateFull = in.readString();
        if (in.readByte() == 0) {
            year = null;
        } else {
            year = in.readInt();
        }
        dayHebrewStr = in.readString();
        dayHebrew = in.readString();
        monthHebrew = in.readString();
        monthHebrewStr = in.readString();
        yearHebrew = in.readString();
        sortLetter = in.readString();
        isPrivate = in.readByte() != 0;
        imageList = in.createStringArrayList();
        extras = in.createStringArrayList();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            day = null;
        } else {
            day = in.readInt();
        }
        if (in.readByte() == 0) {
            month = null;
        } else {
            month = in.readInt();
        }
        subjectTitle = in.readString();
        subjectDescription = in.readString();
        createdDate = in.readString();
        modifiedDate = in.readString();
    }

    public static final Creator<EventDetails> CREATOR = new Creator<EventDetails>() {
        @Override
        public EventDetails createFromParcel(Parcel in) {
            return new EventDetails(in);
        }

        @Override
        public EventDetails[] newArray(int size) {
            return new EventDetails[size];
        }
    };

    public ArrayList<String> getExtras() {
        return extras;
    }

    public void setExtras(ArrayList<String> extras) {
        this.extras = extras;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    public String getDateFull() {
        return dateFull;
    }

    public void setDateFull(String dateFull) {
        this.dateFull = dateFull;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDayHebrewStr() {
        return dayHebrewStr;
    }

    public void setDayHebrewStr(String dayHebrewStr) {
        this.dayHebrewStr = dayHebrewStr;
    }

    public String getDayHebrew() {
        return dayHebrew;
    }

    public void setDayHebrew(String dayHebrew) {
        this.dayHebrew = dayHebrew;
    }

    public String getMonthHebrew() {
        return monthHebrew;
    }

    public void setMonthHebrew(String monthHebrew) {
        this.monthHebrew = monthHebrew;
    }

    public String getMonthHebrewStr() {
        return monthHebrewStr;
    }

    public void setMonthHebrewStr(String monthHebrewStr) {
        this.monthHebrewStr = monthHebrewStr;
    }

    public String getYearHebrew() {
        return yearHebrew;
    }

    public void setYearHebrew(String yearHebrew) {
        this.yearHebrew = yearHebrew;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getSubjectTitle() {
        return subjectTitle;
    }

    public void setSubjectTitle(String subjectTitle) {
        this.subjectTitle = subjectTitle;
    }

    public String getSubjectDescription() {
        return subjectDescription;
    }

    public void setSubjectDescription(String subjectDescription) {
        this.subjectDescription = subjectDescription;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getSortLetter() {
        return sortLetter;
    }

    public void setSortLetter(String sortLetter) {
        this.sortLetter = sortLetter;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(dateFull);
        if (year == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(year);
        }
        parcel.writeString(dayHebrewStr);
        parcel.writeString(dayHebrew);
        parcel.writeString(monthHebrew);
        parcel.writeString(monthHebrewStr);
        parcel.writeString(yearHebrew);
        parcel.writeString(sortLetter);
        parcel.writeByte((byte) (isPrivate ? 1 : 0));
        parcel.writeStringList(imageList);
        parcel.writeStringList(extras);
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        if (day == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(day);
        }
        if (month == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(month);
        }
        parcel.writeString(subjectTitle);
        parcel.writeString(subjectDescription);
        parcel.writeString(createdDate);
        parcel.writeString(modifiedDate);
    }
}