package com.avaliacao.guiainstrumentos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


// Consultas sincronas: o Repositorio as executa fora da thread principal.
@Dao
public interface UsuarioDao {

    @Insert
    long inserir(Usuario usuario);

    @Update
    void atualizar(Usuario usuario);

    @Query("SELECT * FROM usuario WHERE email = :email LIMIT 1")
    Usuario buscarPorEmail(String email);
}
