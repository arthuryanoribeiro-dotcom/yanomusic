package com.avaliacao.guiainstrumentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * FRAGMENTO 2 - ListView com adaptador personalizado.
 *
 * OBSERVA o ViewModel: quando o Spinner do Fragmento 1 muda, a lista aqui se
 * atualiza sozinha, em tempo real. Ao tocar num item, dispara a Intent que
 * abre a Activity Extra (item 3 do enunciado).
 */
public class ListaFragment extends Fragment {

    private final List<Instrumento> itens = new ArrayList<>();
    private InstrumentoAdapter adaptador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InstrumentoViewModel viewModel =
                new ViewModelProvider(requireActivity()).get(InstrumentoViewModel.class);

        ListView listView = view.findViewById(R.id.lista_instrumentos);
        TextView cabecalho = view.findViewById(R.id.texto_cabecalho_lista);

        adaptador = new InstrumentoAdapter(requireContext(), itens);
        listView.setAdapter(adaptador);

        // Item 3.1 e 3.2: Intent + putExtra com os dados do item selecionado.
        listView.setOnItemClickListener((pai, v, posicao, id) -> {
            Intent intent = new Intent(requireContext(), DetalheActivity.class);
            intent.putExtra(DetalheActivity.EXTRA_INSTRUMENTO, itens.get(posicao));
            intent.putExtra(DetalheActivity.EXTRA_CATEGORIA,
                    Repositorio.getTituloCategoria(posicaoCategoria(viewModel)));
            startActivity(intent);
        });

        viewModel.getLista().observe(getViewLifecycleOwner(), novos -> {
            itens.clear();
            itens.addAll(novos);
            adaptador.notifyDataSetChanged();
        });

        viewModel.getCategoria().observe(getViewLifecycleOwner(), categoria ->
                cabecalho.setText(getString(R.string.cabecalho_lista,
                        getString(Repositorio.getTituloCategoria(categoria)))));
    }

    private int posicaoCategoria(InstrumentoViewModel viewModel) {
        Integer categoria = viewModel.getCategoria().getValue();
        return categoria == null ? Repositorio.CORDAS : categoria;
    }
}
