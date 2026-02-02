package com.fbfinal.miniguialugares.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.fbfinal.miniguialugares.dao.LugarDao;
import com.fbfinal.miniguialugares.model.Lugar;

@Database(entities = {Lugar.class}, version = 9)
abstract public class AppDatabase extends RoomDatabase {
    public abstract LugarDao lugarDao();
}

