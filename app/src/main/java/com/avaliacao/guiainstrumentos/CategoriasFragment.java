package com.avaliacao.guiainstrumentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;


public class CategoriasFragment extends Fragment {

    private InstrumentoViewModel viewModel;
    private Spinner spinner;
    private ArrayAdapter<Categoria> adaptador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categorias, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Escopo da ACTIVITY: e o que faz os 3 fragmentos compartilharem
        // a mesma instancia do ViewModel.
        viewModel = new ViewModelProvider(requireActivity()).get(InstrumentoViewModel.class);

        spinner = view.findViewById(R.id.spinner_categorias);
        TextView categoriaAtual = view.findViewById(R.id.texto_categoria_atual);
        TextView resumo = view.findViewById(R.id.texto_resumo);

        // As opcoes vem da tabela "categoria" do Room, nao de um array fixo.
        adaptador = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new ArrayList<>());
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adaptador);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> pai, View v, int posicao, long id) {
                viewModel.setCategoria(adaptador.getItem(posicao).id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> pai) {
                // sem acao: o Spinner sempre mantem um item selecionado
            }
        });

        viewModel.getCategorias().observe(getViewLifecycleOwner(), categorias -> {
            adaptador.clear();
            adaptador.addAll(categorias);
            sincronizarSpinner();
        });

        // Mantem o Spinner coerente ao voltar para esta aba e mostra o resumo.
        viewModel.getCategoriaSelecionada().observe(getViewLifecycleOwner(), selecionada -> {
            sincronizarSpinner();
            categoriaAtual.setText(selecionada.categoria.nome);
            resumo.setText(getString(R.string.resumo_categoria,
                    selecionada.filtrar(Instrumento.EXIBICAO_LISTA).size(),
                    selecionada.filtrar(Instrumento.EXIBICAO_GALERIA).size()));
        });
    }

    private void sincronizarSpinner() {
        Long categoriaId = viewModel.getCategoriaId();
        if (categoriaId == null) {
            return;
        }
        for (int posicao = 0; posicao < adaptador.getCount(); posicao++) {
            if (adaptador.getItem(posicao).id == categoriaId
                    && spinner.getSelectedItemPosition() != posicao) {
                spinner.setSelection(posicao);
            }
        }
    }
}
