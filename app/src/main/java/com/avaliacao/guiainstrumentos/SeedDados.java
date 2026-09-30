package com.avaliacao.guiainstrumentos;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;

import java.util.Arrays;


// Carga inicial do banco: converte o conteudo que antes era estatico no
// Repositorio em linhas das tabelas "categoria" e "instrumento".
final class SeedDados {

    private static final String LISTA = Instrumento.EXIBICAO_LISTA;
    private static final String GALERIA = Instrumento.EXIBICAO_GALERIA;

    private final Context contexto;
    private final AppDatabase banco;

    private SeedDados(Context contexto, AppDatabase banco) {
        this.contexto = contexto;
        this.banco = banco;
    }

    static void popular(Context contexto, AppDatabase banco) {
        SeedDados seed = new SeedDados(contexto, banco);
        banco.runInTransaction(seed::inserirTudo);
    }

    private void inserirTudo() {
        inserirCordas();
        inserirSopro();
        inserirPercussao();
    }

    private void inserirCordas() {
        long cordas = inserirCategoria(R.string.categoria_cordas);
        banco.instrumentoDao().inserirTodos(Arrays.asList(
                instrumento(cordas, LISTA, R.string.nome_violao, R.string.desc_violao,
                        R.string.detalhe_violao, R.string.ficha_violao,
                        R.drawable.img_violao, R.raw.som_violao),
                instrumento(cordas, LISTA, R.string.nome_violino, R.string.desc_violino,
                        R.string.detalhe_violino, R.string.ficha_violino,
                        R.drawable.img_violino, R.raw.som_violino),
                instrumento(cordas, LISTA, R.string.nome_harpa, R.string.desc_harpa,
                        R.string.detalhe_harpa, R.string.ficha_harpa,
                        R.drawable.img_harpa, R.raw.som_harpa),
                instrumento(cordas, GALERIA, R.string.nome_viola_caipira, R.string.desc_viola_caipira,
                        R.string.detalhe_viola_caipira, R.string.ficha_viola_caipira,
                        R.drawable.img_viola_caipira, R.raw.som_viola_caipira),
                instrumento(cordas, GALERIA, R.string.nome_cavaquinho, R.string.desc_cavaquinho,
                        R.string.detalhe_cavaquinho, R.string.ficha_cavaquinho,
                        R.drawable.img_cavaquinho, R.raw.som_cavaquinho),
                instrumento(cordas, GALERIA, R.string.nome_contrabaixo, R.string.desc_contrabaixo,
                        R.string.detalhe_contrabaixo, R.string.ficha_contrabaixo,
                        R.drawable.img_contrabaixo, R.raw.som_contrabaixo),
                instrumento(cordas, GALERIA, R.string.nome_bandolim, R.string.desc_bandolim,
                        R.string.detalhe_bandolim, R.string.ficha_bandolim,
                        R.drawable.img_bandolim, R.raw.som_bandolim)));
    }

    private void inserirSopro() {
        long sopro = inserirCategoria(R.string.categoria_sopro);
        banco.instrumentoDao().inserirTodos(Arrays.asList(
                instrumento(sopro, LISTA, R.string.nome_flauta, R.string.desc_flauta,
                        R.string.detalhe_flauta, R.string.ficha_flauta,
                        R.drawable.img_flauta, R.raw.som_flauta),
                instrumento(sopro, LISTA, R.string.nome_trompete, R.string.desc_trompete,
                        R.string.detalhe_trompete, R.string.ficha_trompete,
                        R.drawable.img_trompete, R.raw.som_trompete),
                instrumento(sopro, LISTA, R.string.nome_clarinete, R.string.desc_clarinete,
                        R.string.detalhe_clarinete, R.string.ficha_clarinete,
                        R.drawable.img_clarinete, R.raw.som_clarinete),
                instrumento(sopro, GALERIA, R.string.nome_saxofone, R.string.desc_saxofone,
                        R.string.detalhe_saxofone, R.string.ficha_saxofone,
                        R.drawable.img_saxofone, R.raw.som_saxofone),
                instrumento(sopro, GALERIA, R.string.nome_trombone, R.string.desc_trombone,
                        R.string.detalhe_trombone, R.string.ficha_trombone,
                        R.drawable.img_trombone, R.raw.som_trombone),
                instrumento(sopro, GALERIA, R.string.nome_tuba, R.string.desc_tuba,
                        R.string.detalhe_tuba, R.string.ficha_tuba,
                        R.drawable.img_tuba, R.raw.som_tuba),
                instrumento(sopro, GALERIA, R.string.nome_gaita, R.string.desc_gaita,
                        R.string.detalhe_gaita, R.string.ficha_gaita,
                        R.drawable.img_gaita, R.raw.som_gaita)));
    }

    private void inserirPercussao() {
        long percussao = inserirCategoria(R.string.categoria_percussao);
        banco.instrumentoDao().inserirTodos(Arrays.asList(
                instrumento(percussao, LISTA, R.string.nome_bateria, R.string.desc_bateria,
                        R.string.detalhe_bateria, R.string.ficha_bateria,
                        R.drawable.img_bateria, R.raw.som_bateria),
                instrumento(percussao, LISTA, R.string.nome_pandeiro, R.string.desc_pandeiro,
                        R.string.detalhe_pandeiro, R.string.ficha_pandeiro,
                        R.drawable.img_pandeiro, R.raw.som_pandeiro),
                instrumento(percussao, LISTA, R.string.nome_xilofone, R.string.desc_xilofone,
                        R.string.detalhe_xilofone, R.string.ficha_xilofone,
                        R.drawable.img_xilofone, R.raw.som_xilofone),
                instrumento(percussao, GALERIA, R.string.nome_atabaque, R.string.desc_atabaque,
                        R.string.detalhe_atabaque, R.string.ficha_atabaque,
                        R.drawable.img_atabaque, R.raw.som_atabaque),
                instrumento(percussao, GALERIA, R.string.nome_triangulo, R.string.desc_triangulo,
                        R.string.detalhe_triangulo, R.string.ficha_triangulo,
                        R.drawable.img_triangulo, R.raw.som_triangulo),
                instrumento(percussao, GALERIA, R.string.nome_agogo, R.string.desc_agogo,
                        R.string.detalhe_agogo, R.string.ficha_agogo,
                        R.drawable.img_agogo, R.raw.som_agogo),
                instrumento(percussao, GALERIA, R.string.nome_tambor, R.string.desc_tambor,
                        R.string.detalhe_tambor, R.string.ficha_tambor,
                        R.drawable.img_tambor, R.raw.som_tambor)));
    }

    private long inserirCategoria(int nomeResId) {
        Categoria categoria = new Categoria();
        categoria.nome = contexto.getString(nomeResId);
        return banco.categoriaDao().inserir(categoria);
    }

    private Instrumento instrumento(long categoriaId, String exibicao, int nomeResId,
                                    int descricaoResId, int detalheResId, int fichaResId,
                                    int imagemResId, int somResId) {
        Instrumento instrumento = new Instrumento();
        instrumento.categoriaId = categoriaId;
        instrumento.exibicao = exibicao;
        instrumento.nome = contexto.getString(nomeResId);
        instrumento.descricao = contexto.getString(descricaoResId);
        instrumento.detalhe = contexto.getString(detalheResId);
        instrumento.ficha = contexto.getString(fichaResId);
        instrumento.imagemPath = caminhoDoRecurso(imagemResId);
        instrumento.audioPath = caminhoDoRecurso(somResId);
        return instrumento;
    }

    // Ex.: android.resource://com.avaliacao.guiainstrumentos/drawable/img_violao
    // Usa o NOME do recurso, que nao muda entre builds (o id numerico muda).
    private String caminhoDoRecurso(int resId) {
        Resources recursos = contexto.getResources();
        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + contexto.getPackageName() + "/"
                + recursos.getResourceTypeName(resId) + "/"
                + recursos.getResourceEntryName(resId);
    }
}
