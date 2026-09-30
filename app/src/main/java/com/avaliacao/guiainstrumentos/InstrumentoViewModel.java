package com.avaliacao.guiainstrumentos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;


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
