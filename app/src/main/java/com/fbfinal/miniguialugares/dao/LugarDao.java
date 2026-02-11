package com.fbfinal.miniguialugares.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fbfinal.miniguialugares.model.Lugar;

import java.util.List;

@Dao
public interface LugarDao {
    @Query("SELECT * from Lugar WHERE id = :id")
    Lugar findById(int id);

    @Query("SELECT * from Lugar WHERE nombre = :nombre")
    Lugar findByNombre(String nombre);

    @Query("SELECT * from Lugar")
    List<Lugar> getAll();

    @Query("SELECT * FROM Lugar WHERE id IN (:animalIds)")
    List<Lugar> loadAllByIds(int[] animalIds);

    @Query("SELECT * FROM Lugar WHERE nombre LIKE :name LIMIT 1")
    Lugar findByName(String name);

    @Query("SELECT * FROM Lugar WHERE esFavorita = true")
    List<Lugar> getFavoritos();


    @Query("SELECT count(*) from Lugar")
    int countLugares();

    @Insert
    void insertLugar(Lugar lugar);

    @Insert
    void insertAll(Lugar... lugares);

    @Delete
    void delete(Lugar lugar);

    @Update
    void update(Lugar lugar);

    @Query("DELETE FROM Lugar")
    void deleteAll();
}
