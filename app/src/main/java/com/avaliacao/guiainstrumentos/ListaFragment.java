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

        // So o id viaja no Intent: a DetalheActivity busca o resto no Room.
        listView.setOnItemClickListener((pai, v, posicao, id) -> {
            Intent intent = new Intent(requireContext(), DetalheActivity.class);
            intent.putExtra(DetalheActivity.EXTRA_INSTRUMENTO_ID, itens.get(posicao).id);
            startActivity(intent);
        });

        viewModel.getLista().observe(getViewLifecycleOwner(), novos -> {
            itens.clear();
            itens.addAll(novos);
            adaptador.notifyDataSetChanged();
        });

        viewModel.getCategoriaSelecionada().observe(getViewLifecycleOwner(), selecionada ->
                cabecalho.setText(getString(R.string.cabecalho_lista,
                        selecionada.categoria.nome)));
    }
}
