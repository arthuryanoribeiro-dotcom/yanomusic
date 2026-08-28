package com.avaliacao.guiainstrumentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseAdapter da GridView do Fragmento 3 (item 2 do enunciado exige
 * explicitamente que a Grid use um BaseAdapter).
 */
public class GaleriaAdapter extends BaseAdapter {

    private final Context contexto;
    private final List<Instrumento> itens = new ArrayList<>();

    public GaleriaAdapter(Context contexto) {
        this.contexto = contexto;
    }

    /** Chamado pelo observer do LiveData quando a categoria muda. */
    public void atualizar(List<Instrumento> novos) {
        itens.clear();
        itens.addAll(novos);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itens.size();
    }

    @Override
    public Instrumento getItem(int posicao) {
        return itens.get(posicao);
    }

    @Override
    public long getItemId(int posicao) {
        return posicao;
    }

    @Override
    public View getView(int posicao, View viewReciclada, ViewGroup pai) {
        View celula = viewReciclada;
        if (celula == null) {
            celula = LayoutInflater.from(contexto)
                    .inflate(R.layout.item_grid, pai, false);
        }

        Instrumento item = getItem(posicao);
        ImageView imagem = celula.findViewById(R.id.imagem_celula);
        TextView titulo = celula.findViewById(R.id.titulo_celula);
        imagem.setImageResource(item.getImagemResId());
        titulo.setText(item.getNomeResId());

        return celula;
    }
}
