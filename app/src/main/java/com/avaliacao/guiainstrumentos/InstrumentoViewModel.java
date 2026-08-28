package com.avaliacao.guiainstrumentos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

/**
 * ESTE E O NUCLEO DO REQUISITO DE COMUNICACAO REATIVA.
 *
 * O Fragmento 1 (Spinner) so ESCREVE em `categoria`. Os Fragmentos 2 e 3 so
 * OBSERVAM `lista` e `galeria`, que sao derivadas de `categoria` por um
 * MediatorLiveData. Nenhum dos tres fragmentos conhece a existencia dos
 * outros: o acoplamento entre eles e zero.
 *
 * Como os tres pegam o ViewModel no escopo da Activity
 * (new ViewModelProvider(requireActivity())), todos recebem a MESMA instancia,
 * e por isso a mudanca do Spinner chega aos outros dois em tempo real.
 */
public class InstrumentoViewModel extends ViewModel {

    private final MutableLiveData<Integer> categoria =
            new MutableLiveData<>(Repositorio.CORDAS);

    private final MediatorLiveData<List<Instrumento>> lista = new MediatorLiveData<>();
    private final MediatorLiveData<List<Instrumento>> galeria = new MediatorLiveData<>();

    public InstrumentoViewModel() {
        // Cada derivada se reescreve sozinha sempre que a categoria muda.
        lista.addSource(categoria, novaCategoria ->
                lista.setValue(Repositorio.getLista(novaCategoria)));
        galeria.addSource(categoria, novaCategoria ->
                galeria.setValue(Repositorio.getGaleria(novaCategoria)));
    }

    /** Chamado pelo Fragmento 1 quando o usuario mexe no Spinner. */
    public void setCategoria(int novaCategoria) {
        Integer atual = categoria.getValue();
        if (atual == null || atual != novaCategoria) {
            categoria.setValue(novaCategoria);
        }
    }

    public LiveData<Integer> getCategoria() {
        return categoria;
    }

    public LiveData<List<Instrumento>> getLista() {
        return lista;
    }

    public LiveData<List<Instrumento>> getGaleria() {
        return galeria;
    }
}
