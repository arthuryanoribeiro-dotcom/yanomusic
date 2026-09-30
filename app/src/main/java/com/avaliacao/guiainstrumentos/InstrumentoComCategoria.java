package com.avaliacao.guiainstrumentos;

import androidx.room.Embedded;
import androidx.room.Relation;


// Instrumento com a categoria a que pertence, usado na tela de detalhes.
public class InstrumentoComCategoria {

    @Embedded
    public Instrumento instrumento;

    @Relation(parentColumn = "categoriaId", entityColumn = "id")
    public Categoria categoria;
}
