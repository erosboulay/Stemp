package com.example.stemp.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contact")
public class Contact{
    @PrimaryKey(autoGenerate = true) // to make id auto_increment
    public int local_id;

    public int phone_id;
    public String lookup_key;
    public String name;

    public Contact(){
    }
}
