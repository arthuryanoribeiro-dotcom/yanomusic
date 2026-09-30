package com.avaliacao.guiainstrumentos;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


// Sessao ativa (relacionamento 1:1): a chave primaria E a chave estrangeira,
// entao cada usuario tem no maximo uma sessao.
@Entity(tableName = "sessao",
        foreignKeys = @ForeignKey(entity = Usuario.class,
                parentColumns = "id",
                childColumns = "usuarioId",
                onDelete = ForeignKey.CASCADE))
public class Sessao {

    @PrimaryKey
    public long usuarioId;

    public long dataLogin;

    public Sessao(long usuarioId, long dataLogin) {
        this.usuarioId = usuarioId;
        this.dataLogin = dataLogin;
    }
}
