package com.avaliacao.guiainstrumentos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;


public class InstrumentoViewModel extends AndroidViewModel {

    private final Repositorio repositorio;
    private final LiveData<List<Categoria>> categorias;

    private final MediatorLiveData<CategoriaComInstrumentos> categoriaSelecionada =
            new MediatorLiveData<>();
    private final MediatorLiveData<List<Instrumento>> lista = new MediatorLiveData<>();
    private final MediatorLiveData<List<Instrumento>> galeria = new MediatorLiveData<>();

    private Long categoriaId;
    private LiveData<CategoriaComInstrumentos> consultaAtual;

    public InstrumentoViewModel(@NonNull Application aplicacao) {
        super(aplicacao);
        repositorio = new Repositorio(aplicacao);
        categorias = repositorio.getCategorias();

        // Cada derivada se reescreve sozinha sempre que a categoria muda.
        lista.addSource(categoriaSelecionada, selecionada ->
                lista.setValue(selecionada.filtrar(Instrumento.EXIBICAO_LISTA)));
        galeria.addSource(categoriaSelecionada, selecionada ->
                galeria.setValue(selecionada.filtrar(Instrumento.EXIBICAO_GALERIA)));
    }

    // Troca a consulta do Room observada: a nova categoria chega com seus instrumentos.
    public void setCategoria(long novaCategoriaId) {
        if (categoriaId != null && categoriaId == novaCategoriaId) {
            return;
        }
        categoriaId = novaCategoriaId;
        if (consultaAtual != null) {
            categoriaSelecionada.removeSource(consultaAtual);
        }
        consultaAtual = repositorio.getCategoriaComInstrumentos(novaCategoriaId);
        categoriaSelecionada.addSource(consultaAtual, selecionada -> {
            if (selecionada != null) {
                categoriaSelecionada.setValue(selecionada);
            }
        });
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public LiveData<List<Categoria>> getCategorias() {
        return categorias;
    }

    public LiveData<CategoriaComInstrumentos> getCategoriaSelecionada() {
        return categoriaSelecionada;
    }

    public LiveData<List<Instrumento>> getLista() {
        return lista;
    }

    public LiveData<List<Instrumento>> getGaleria() {
        return galeria;
    }

    public LiveData<InstrumentoComCategoria> getInstrumento(long instrumentoId) {
        return repositorio.getInstrumento(instrumentoId);
    }
}
