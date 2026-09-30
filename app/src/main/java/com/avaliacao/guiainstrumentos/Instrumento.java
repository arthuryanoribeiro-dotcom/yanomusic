package com.avaliacao.guiainstrumentos;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


// Tabela 3: cada instrumento pertence a uma categoria (lado N do 1:N).
@Entity(tableName = "instrumento",
        foreignKeys = @ForeignKey(entity = Categoria.class,
                parentColumns = "id",
                childColumns = "categoriaId",
                onDelete = ForeignKey.CASCADE),
        indices = @Index("categoriaId"))
public class Instrumento {

    // Em qual fragmento o item aparece: ListView (Fragmento 2) ou GridView (Fragmento 3).
    public static final String EXIBICAO_LISTA = "LISTA";
    public static final String EXIBICAO_GALERIA = "GALERIA";

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long categoriaId;
    public String exibicao;

    public String nome;
    public String descricao;
    public String detalhe;
    public String ficha;

    // Midias ficam em res/; o banco guarda apenas o caminho (URI) como String.
    public String imagemPath;
    public String audioPath;
}
