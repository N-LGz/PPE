package com.nemge.ppe.Model;

import android.arch.persistence.room.*;
import io.reactivex.annotations.NonNull;
import java.util.Date;

import com.nemge.ppe.Model.TimeStampConverter;
import com.nemge.ppe.Model.Converter;

import java.util.Date;

@Entity(tableName = "users")
public class User {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;


    @ColumnInfo(name = "name")
    private String name;

    /*
    @ColumnInfo(name = "dose")
    @TypeConverters({TimeStampConverter.class})
    private Date dose;
    */

    public User(){

    }

    @Ignore
    public User(String name){
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
    public Date getDose() {
        return dose;
    }

    public void setDose(Date dose) {
        this.dose = dose;
    }
    */

    @Override
    public String toString(){
        return new StringBuilder(name).append("\n").toString();
    }
}