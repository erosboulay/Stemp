package com.example.stemp.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhonecallDao {
    //TODO: understand wtf i need (convenience/query methods)
    @Insert
    void insertContact(Contact contact);
    @Insert//(onConflict = OnConflictStrategy.REPLACE)
    long insertNumber(Number number);
    @Insert
    void insertContactNumber(ContactNumber contactNumber);

    @Delete
    void deleteContact(Contact contact);

    @Query("SELECT * FROM contact")
    List<Contact> getAll();

    @Query("SELECT DISTINCT 1 FROM contact WHERE contact.phone_id = (:phone_id)")
    boolean isInContact(int phone_id);

    @Query("SELECT contact.local_id FROM contact WHERE contact.phone_id = (:phone_id)")
    int getLocalId(int phone_id);

    @Query("SELECT COUNT(*) FROM number WHERE phone_number = :phoneNumber")
    int countPhoneNumber(String phoneNumber);

}
