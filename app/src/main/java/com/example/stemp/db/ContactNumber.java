package com.example.stemp.db;

import androidx.room.Entity;


@Entity(tableName = "contact_number", primaryKeys = {"contact_id", "number_id"})
public class ContactNumber {
    public int contact_id;
    public int number_id;

    public ContactNumber(){

    }
}
