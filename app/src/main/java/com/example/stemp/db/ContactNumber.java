package com.example.stemp.db;

import androidx.room.Entity;
import androidx.room.ForeignKey;


@Entity(tableName = "contact_number", primaryKeys = {"contact_id", "number_id"}, foreignKeys =
        {@ForeignKey(entity = Number.class, parentColumns = {"number_id"}, childColumns = {"number_id"}),
                @ForeignKey(entity = Contact.class, parentColumns = {"local_id"}, childColumns = {"contact_id"})})
public class ContactNumber {
    public int contact_id;
    public int number_id;

    public ContactNumber(){

    }
}
