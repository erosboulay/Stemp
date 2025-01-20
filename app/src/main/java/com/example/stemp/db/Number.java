package com.example.stemp.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "number")
public class Number {
    @PrimaryKey(autoGenerate = true)
    public int number_id;
    public String phone_number;
    public boolean is_known;

    public Number(){

    }
}
