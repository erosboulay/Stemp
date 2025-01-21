package com.example.stemp.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "contact")
public class Contact{
    @PrimaryKey(autoGenerate = true) // to make id auto_increment
    public int local_id;

    public int phone_id;
    public String lookup_key;
    public String name;
    public int has_number;

    // idk why it needs an empty constructor
    @Ignore
    public Contact(){
    }

    public Contact(int phone_id, String lookup_key, String name, int has_number){
        this.phone_id = phone_id;
        this.lookup_key = lookup_key;
        this.name = name;
        this.has_number = has_number;

        this.local_id = 0;
    }
}
