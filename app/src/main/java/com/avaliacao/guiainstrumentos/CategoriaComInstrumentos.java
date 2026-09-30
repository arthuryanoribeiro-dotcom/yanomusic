package com.avaliacao.guiainstrumentos;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;


// Relacionamento 1:N: uma categoria com todos os seus instrumentos.
public class CategoriaComInstrumentos {

    @Embedded
    public Categoria categoria;

    @Relation(parentColumn = "id", entityColumn = "categoriaId")
    public List<Instrumento> instrumentos;

    public List<Instrumento> filtrar(String exibicao) {
        List<Instrumento> filtrados = new ArrayList<>();
        for (Instrumento instrumento : instrumentos) {
            if (exibicao.equals(instrumento.exibicao)) {
                filtrados.add(instrumento);
            }
        }
        return filtrados;
    }
}
