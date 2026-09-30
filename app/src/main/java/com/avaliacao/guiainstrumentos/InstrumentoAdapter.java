package com.avaliacao.guiainstrumentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;


public class InstrumentoAdapter extends ArrayAdapter<Instrumento> {


    private static class Suporte {
        ImageView imagem;
        TextView titulo;
        TextView descricao;
    }

    public InstrumentoAdapter(@NonNull Context contexto, @NonNull List<Instrumento> itens) {
        super(contexto, 0, itens);
    }

    @NonNull
    @Override
    public View getView(int posicao, View viewReciclada, @NonNull ViewGroup pai) {
        View linha = viewReciclada;
        Suporte suporte;

        if (linha == null) {
            linha = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_lista, pai, false);
            suporte = new Suporte();
            suporte.imagem = linha.findViewById(R.id.imagem_item);
            suporte.titulo = linha.findViewById(R.id.titulo_item);
            suporte.descricao = linha.findViewById(R.id.descricao_item);
            linha.setTag(suporte);
        } else {
            suporte = (Suporte) linha.getTag();
        }

        Instrumento item = getItem(posicao);
        if (item != null) {
            suporte.imagem.setImageResource(item.getImagemResId());
            suporte.titulo.setText(item.getNomeResId());
            suporte.descricao.setText(item.getDescricaoResId());
        }
        return linha;
    }
}
