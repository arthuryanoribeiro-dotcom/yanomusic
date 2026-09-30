package com.avaliacao.guiainstrumentos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;


@Dao
public interface SessaoDao {

    // Emite de novo sempre que "sessao" ou "usuario" mudam:
    // login, logout e edicao de perfil chegam sozinhos na MainActivity.
    @Transaction
    @Query("SELECT * FROM sessao LIMIT 1")
    LiveData<SessaoComUsuario> getSessaoAtiva();

    @Insert
    void inserir(Sessao sessao);

    @Query("DELETE FROM sessao")
    void apagarTodas();
}
