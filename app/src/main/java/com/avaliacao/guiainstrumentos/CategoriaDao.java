package com.avaliacao.guiainstrumentos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;


@Dao
public interface CategoriaDao {

    @Query("SELECT * FROM categoria ORDER BY id")
    LiveData<List<Categoria>> getTodas();

    @Transaction
    @Query("SELECT * FROM categoria WHERE id = :id")
    LiveData<CategoriaComInstrumentos> getComInstrumentos(long id);

    @Insert
    long inserir(Categoria categoria);
}
