package com.avaliacao.guiainstrumentos;

import androidx.room.Embedded;
import androidx.room.Relation;


// Relacionamento 1:1: a sessao ativa junto com o usuario dono dela.
public class SessaoComUsuario {

    @Embedded
    public Sessao sessao;

    @Relation(parentColumn = "usuarioId", entityColumn = "id")
    public Usuario usuario;
}
