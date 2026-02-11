package com.fbfinal.miniguialugares.db;

import android.content.Context;

import androidx.room.Room;

import com.fbfinal.miniguialugares.dao.LugarDao;
import com.fbfinal.miniguialugares.model.Lugar;

import java.util.List;
import java.util.logging.Logger;

public class DbManager {
    private static DbManager instance;
    private LugarDao lugarDao;
    private AppDatabase db;
    private Logger logger = Logger.getLogger(DbManager.class.getName());


    public static DbManager getDbManager(Context context){
        if (instance == null) {
            instance = new DbManager(context);
        }
        return instance;
    }

    private DbManager(Context context){
        db = Room.databaseBuilder(context, AppDatabase.class, "lugares_db").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        lugarDao = this.db.lugarDao();
    }

    public List<Lugar> getAll(){
        return lugarDao.getAll();
    }

    public Lugar getLugarById(int id) {
        return lugarDao.findById(id);
    }

    public void addLugar(Lugar Lugar){
        lugarDao.insertLugar(Lugar);
    }
    public void addLugares(List<Lugar> lugares){
        lugarDao.insertAll(lugares.toArray(new Lugar[0]));
    }

    public void updateLugar(Lugar Lugar){
        lugarDao.update(Lugar);
    }

    public void deleteLugar(Lugar Lugar) {
        lugarDao.delete(Lugar);
    }

    public void clearAll() {
        lugarDao.deleteAll();
    }

    public Lugar getByName(String nombre) {
        return lugarDao.findByNombre(nombre);
    }

    public List<Lugar> getFavoritos() {
        logger.info("Obteniendo favoritos...");
        return lugarDao.getFavoritos();
    }
}
