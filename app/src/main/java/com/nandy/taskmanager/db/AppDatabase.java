package com.nandy.taskmanager.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Task;

/**
 * Created by yana on 16.01.18.
 */

@Database(entities = {Task.class}, version = 1)
@TypeConverters({
        LocationTypeConverter.class,
        DateTypeConverter.class,
        TaskStatusConventer.class})
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "taskmanager";
    private static AppDatabase INSTANCE = null;


    public synchronized static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = create(context);
        }

        return (INSTANCE);
    }

    private static AppDatabase create(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .build();
    }


    public abstract TasksDao tasksDao();
}
