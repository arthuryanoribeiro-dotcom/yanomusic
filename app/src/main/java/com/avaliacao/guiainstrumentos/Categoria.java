package com.avaliacao.guiainstrumentos;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


// Tabela 2: familia de instrumentos (Cordas, Sopro, Percussao).
@Entity(tableName = "categoria")
public class Categoria {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String nome;

    // O ArrayAdapter do Spinner exibe o toString() de cada item.
    @NonNull
    @Override
    public String toString() {
        return nome;
    }
}
