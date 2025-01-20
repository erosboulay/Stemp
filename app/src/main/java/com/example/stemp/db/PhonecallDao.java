package com.example.stemp.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhonecallDao {
    //TODO: understand wtf i need (convenience/query methods)
    @Insert
    void insertContact(Contact contact);
    @Delete
    void deleteContact(Contact contact);

    @Query("SELECT * FROM contact")
    List<Contact> getAll();
}
