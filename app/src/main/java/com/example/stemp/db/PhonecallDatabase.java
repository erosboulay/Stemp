package com.example.stemp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Call.class, Contact.class, ContactNumber.class, Number.class}, version = 1)
public abstract class PhonecallDatabase extends RoomDatabase {
    public abstract PhonecallDao phonecallDao();
    private static volatile PhonecallDatabase INSTANCE;

    // create singleton
    public static PhonecallDatabase getInstance(Context context){
        if (INSTANCE == null){
            synchronized (PhonecallDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PhonecallDatabase.class, "phone_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
