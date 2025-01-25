package com.example.stemp.db;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "call"/*, foreignKeys =
@ForeignKey(entity = Number.class, parentColumns = {"number_id"}, childColumns = {"number_id"})*/)
public class Call {
    @PrimaryKey(autoGenerate = true)
    public int row_id;

    public int number_id;
    public int type;
    public long date;
    public long duration;

    public Call(int number_id, int type, long date, long duration){
        this.number_id = number_id;
        this.type = type;
        this.date = date;
        this.duration = duration;

        this.row_id = 0;
    }
}
