package com.avaliacao.guiainstrumentos;

import java.io.Serializable;


public class Instrumento implements Serializable {

    private final int nomeResId;
    private final int descricaoResId;
    private final int detalheResId;
    private final int fichaResId;
    private final int imagemResId;
    private final int somResId;

    public Instrumento(int nomeResId, int descricaoResId, int detalheResId,
                       int fichaResId, int imagemResId, int somResId) {
        this.nomeResId = nomeResId;
        this.descricaoResId = descricaoResId;
        this.detalheResId = detalheResId;
        this.fichaResId = fichaResId;
        this.imagemResId = imagemResId;
        this.somResId = somResId;
    }

    public int getNomeResId() {
        return nomeResId;
    }

    public int getDescricaoResId() {
        return descricaoResId;
    }

    public int getDetalheResId() {
        return detalheResId;
    }

    public int getFichaResId() {
        return fichaResId;
    }

    public int getImagemResId() {
        return imagemResId;
    }

    public int getSomResId() {
        return somResId;
    }
}
