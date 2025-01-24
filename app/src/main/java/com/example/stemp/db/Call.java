package com.example.stemp.db;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "call"/*, foreignKeys =
@ForeignKey(entity = Number.class, parentColumns = {"number_id"}, childColumns = {"number_id"})*/)
public class Call {
    @PrimaryKey
    public int row_id;

    public int number_id;
    public int type;
    public long date;
    public long duration;

    public Call(){

    }
}
