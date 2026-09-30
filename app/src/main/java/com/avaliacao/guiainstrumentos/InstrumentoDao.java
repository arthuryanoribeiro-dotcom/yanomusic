package com.avaliacao.guiainstrumentos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;


@Dao
public interface InstrumentoDao {

    @Transaction
    @Query("SELECT * FROM instrumento WHERE id = :id")
    LiveData<InstrumentoComCategoria> getDetalhe(long id);

    @Insert
    void inserirTodos(List<Instrumento> instrumentos);
}
