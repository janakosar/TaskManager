package com.nandy.taskmanager.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by yana on 17.01.18.
 */

public class DateTypeConverter {

    @TypeConverter
    public Date fromTimestamp(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public Long toTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }
}