package com.avaliacao.guiainstrumentos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


// Tabela 1: perfil do usuario, senha criptografada e foto em bytes (BLOB).
@Entity(tableName = "usuario", indices = @Index(value = "email", unique = true))
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String nome;
    public String email;

    // Nunca a senha em texto: apenas o hash SHA-256 e o salt usado nele.
    public String senhaHash;
    public String salt;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] foto;
}
