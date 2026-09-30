package com.avaliacao.guiainstrumentos;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class GaleriaAdapter extends BaseAdapter {

    private final Context contexto;
    private final List<Instrumento> itens = new ArrayList<>();

    public GaleriaAdapter(Context contexto) {
        this.contexto = contexto;
    }


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
        return getItem(posicao).id;
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
        // A imagem vem do caminho (String) gravado no banco.
        imagem.setImageURI(Uri.parse(item.imagemPath));
        titulo.setText(item.nome);

        return celula;
    }
}
