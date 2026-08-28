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

/**
 * FRAGMENTO 1 - o Spinner de categorias.
 *
 * Sua unica responsabilidade e ESCREVER a categoria escolhida no ViewModel.
 * Ele nao conhece o Fragmento 2 nem o Fragmento 3.
 */
public class CategoriasFragment extends Fragment {

    private InstrumentoViewModel viewModel;

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

        Spinner spinner = view.findViewById(R.id.spinner_categorias);
        TextView categoriaAtual = view.findViewById(R.id.texto_categoria_atual);
        TextView resumo = view.findViewById(R.id.texto_resumo);

        // As opcoes vem de res/values/arrays.xml, nao do codigo.
        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(
                requireContext(), R.array.categorias,
                android.R.layout.simple_spinner_item);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adaptador);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> pai, View v, int posicao, long id) {
                viewModel.setCategoria(posicao);
            }

            @Override
            public void onNothingSelected(AdapterView<?> pai) {
                // sem acao: o Spinner sempre mantem um item selecionado
            }
        });

        // Observa a propria categoria so para manter o Spinner coerente ao
        // voltar para esta aba e para mostrar o resumo na tela.
        viewModel.getCategoria().observe(getViewLifecycleOwner(), categoria -> {
            if (spinner.getSelectedItemPosition() != categoria) {
                spinner.setSelection(categoria);
            }
            categoriaAtual.setText(Repositorio.getTituloCategoria(categoria));
            resumo.setText(getString(R.string.resumo_categoria,
                    Repositorio.getLista(categoria).size(),
                    Repositorio.getGaleria(categoria).size()));
        });
    }
}
