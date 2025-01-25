package com.example.stemp.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhonecallDao {
    @Insert
    void insertCall(Call call);
    @Insert
    void insertContact(Contact contact);
    @Insert
    void insertContactNumber(ContactNumber contactNumber);
    @Insert//(onConflict = OnConflictStrategy.REPLACE)
    long insertNumber(Number number);
    @Delete
    void deleteContact(Contact contact);

    @Query("SELECT * FROM contact")
    List<Contact> getAll();

    @Query("SELECT contact.local_id FROM contact WHERE contact.phone_id = (:phone_id)")
    int getLocalId(int phone_id);

    @Query("SELECT number.number_id FROM number WHERE number.phone_number = (:phoneNumber)")
    int getNumberId(String phoneNumber);

    @Query("SELECT COUNT(*) FROM number WHERE phone_number = :phoneNumber")
    int countPhoneNumber(String phoneNumber);

}
