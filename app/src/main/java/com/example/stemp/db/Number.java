package com.example.stemp.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "number")
public class Number {
    @PrimaryKey(autoGenerate = true)
    public int number_id;
    public String phone_number;
    public boolean is_known;

    public Number(String phone_number, boolean is_known){
        this.phone_number = phone_number;
        this.is_known = is_known;

        this.number_id = 0;
    }
}
