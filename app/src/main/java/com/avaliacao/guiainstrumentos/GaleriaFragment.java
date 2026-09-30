package com.avaliacao.guiainstrumentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


public class GaleriaFragment extends Fragment {

    private GaleriaAdapter adaptador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_galeria, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InstrumentoViewModel viewModel =
                new ViewModelProvider(requireActivity()).get(InstrumentoViewModel.class);

        GridView grade = view.findViewById(R.id.grade_instrumentos);
        TextView cabecalho = view.findViewById(R.id.texto_cabecalho_galeria);

        adaptador = new GaleriaAdapter(requireContext());
        grade.setAdapter(adaptador);

        grade.setOnItemClickListener((pai, v, posicao, id) -> {
            Integer categoria = viewModel.getCategoria().getValue();
            Intent intent = new Intent(requireContext(), DetalheActivity.class);
            intent.putExtra(DetalheActivity.EXTRA_INSTRUMENTO, adaptador.getItem(posicao));
            intent.putExtra(DetalheActivity.EXTRA_CATEGORIA,
                    Repositorio.getTituloCategoria(
                            categoria == null ? Repositorio.CORDAS : categoria));
            startActivity(intent);
        });

        viewModel.getGaleria().observe(getViewLifecycleOwner(), adaptador::atualizar);

        viewModel.getCategoria().observe(getViewLifecycleOwner(), categoria ->
                cabecalho.setText(getString(R.string.cabecalho_galeria,
                        getString(Repositorio.getTituloCategoria(categoria)))));
    }
}
