package com.avaliacao.guiainstrumentos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Fonte unica de dados do app. Cada categoria tem duas colecoes:
 * a da ListView (Fragmento 2) e a da GridView (Fragmento 3).
 *
 * A posicao no array e a MESMA posicao do Spinner (0 = Cordas, 1 = Sopro,
 * 2 = Percussao), definida em res/values/arrays.xml.
 */
public final class Repositorio {

    public static final int CORDAS = 0;
    public static final int SOPRO = 1;
    public static final int PERCUSSAO = 2;

    private static final int[] TITULOS_CATEGORIA = {
            R.string.categoria_cordas,
            R.string.categoria_sopro,
            R.string.categoria_percussao
    };

    private static final List<List<Instrumento>> LISTAS = criarListas();
    private static final List<List<Instrumento>> GALERIAS = criarGalerias();

    private Repositorio() {
        // classe utilitaria: nao deve ser instanciada
    }

    public static int getTituloCategoria(int categoria) {
        return TITULOS_CATEGORIA[categoria];
    }

    public static int getTotalCategorias() {
        return TITULOS_CATEGORIA.length;
    }

    /** Itens da ListView para a categoria escolhida no Spinner. */
    public static List<Instrumento> getLista(int categoria) {
        return LISTAS.get(categoria);
    }

    /** Itens da GridView para a categoria escolhida no Spinner. */
    public static List<Instrumento> getGaleria(int categoria) {
        return GALERIAS.get(categoria);
    }

    private static List<List<Instrumento>> criarListas() {
        List<List<Instrumento>> tudo = new ArrayList<>();

        tudo.add(Arrays.asList(
                new Instrumento(R.string.nome_violao, R.string.desc_violao,
                        R.string.detalhe_violao, R.string.ficha_violao,
                        R.drawable.img_violao, R.raw.som_violao),
                new Instrumento(R.string.nome_violino, R.string.desc_violino,
                        R.string.detalhe_violino, R.string.ficha_violino,
                        R.drawable.img_violino, R.raw.som_violino),
                new Instrumento(R.string.nome_harpa, R.string.desc_harpa,
                        R.string.detalhe_harpa, R.string.ficha_harpa,
                        R.drawable.img_harpa, R.raw.som_harpa)));

        tudo.add(Arrays.asList(
                new Instrumento(R.string.nome_flauta, R.string.desc_flauta,
                        R.string.detalhe_flauta, R.string.ficha_flauta,
                        R.drawable.img_flauta, R.raw.som_flauta),
                new Instrumento(R.string.nome_trompete, R.string.desc_trompete,
                        R.string.detalhe_trompete, R.string.ficha_trompete,
                        R.drawable.img_trompete, R.raw.som_trompete),
                new Instrumento(R.string.nome_clarinete, R.string.desc_clarinete,
                        R.string.detalhe_clarinete, R.string.ficha_clarinete,
                        R.drawable.img_clarinete, R.raw.som_clarinete)));

        tudo.add(Arrays.asList(
                new Instrumento(R.string.nome_bateria, R.string.desc_bateria,
                        R.string.detalhe_bateria, R.string.ficha_bateria,
                        R.drawable.img_bateria, R.raw.som_bateria),
                new Instrumento(R.string.nome_pandeiro, R.string.desc_pandeiro,
                        R.string.detalhe_pandeiro, R.string.ficha_pandeiro,
                        R.drawable.img_pandeiro, R.raw.som_pandeiro),
                new Instrumento(R.string.nome_xilofone, R.string.desc_xilofone,
                        R.string.detalhe_xilofone, R.string.ficha_xilofone,
                        R.drawable.img_xilofone, R.raw.som_xilofone)));

        return Collections.unmodifiableList(tudo);
    }

    private static List<List<Instrumento>> criarGalerias() {
        List<List<Instrumento>> tudo = new ArrayList<>();

        tudo.add(Arrays.asList(
                new Instrumento(R.string.nome_viola_caipira, R.string.desc_viola_caipira,
                        R.string.detalhe_viola_caipira, R.string.ficha_viola_caipira,
                        R.drawable.img_viola_caipira, R.raw.som_viola_caipira),
                new Instrumento(R.string.nome_cavaquinho, R.string.desc_cavaquinho,
                        R.string.detalhe_cavaquinho, R.string.ficha_cavaquinho,
                        R.drawable.img_cavaquinho, R.raw.som_cavaquinho),
                new Instrumento(R.string.nome_contrabaixo, R.string.desc_contrabaixo,
                        R.string.detalhe_contrabaixo, R.string.ficha_contrabaixo,
                        R.drawable.img_contrabaixo, R.raw.som_contrabaixo),
                new Instrumento(R.string.nome_bandolim, R.string.desc_bandolim,
                        R.string.detalhe_bandolim, R.string.ficha_bandolim,
                        R.drawable.img_bandolim, R.raw.som_bandolim)));

        tudo.add(Arrays.asList(
                new Instrumento(R.string.nome_saxofone, R.string.desc_saxofone,
                        R.string.detalhe_saxofone, R.string.ficha_saxofone,
                        R.drawable.img_saxofone, R.raw.som_saxofone),
                new Instrumento(R.string.nome_trombone, R.string.desc_trombone,
                        R.string.detalhe_trombone, R.string.ficha_trombone,
                        R.drawable.img_trombone, R.raw.som_trombone),
                new Instrumento(R.string.nome_tuba, R.string.desc_tuba,
                        R.string.detalhe_tuba, R.string.ficha_tuba,
                        R.drawable.img_tuba, R.raw.som_tuba),
                new Instrumento(R.string.nome_gaita, R.string.desc_gaita,
                        R.string.detalhe_gaita, R.string.ficha_gaita,
                        R.drawable.img_gaita, R.raw.som_gaita)));

        tudo.add(Arrays.asList(
                new Instrumento(R.string.nome_atabaque, R.string.desc_atabaque,
                        R.string.detalhe_atabaque, R.string.ficha_atabaque,
                        R.drawable.img_atabaque, R.raw.som_atabaque),
                new Instrumento(R.string.nome_triangulo, R.string.desc_triangulo,
                        R.string.detalhe_triangulo, R.string.ficha_triangulo,
                        R.drawable.img_triangulo, R.raw.som_triangulo),
                new Instrumento(R.string.nome_agogo, R.string.desc_agogo,
                        R.string.detalhe_agogo, R.string.ficha_agogo,
                        R.drawable.img_agogo, R.raw.som_agogo),
                new Instrumento(R.string.nome_tambor, R.string.desc_tambor,
                        R.string.detalhe_tambor, R.string.ficha_tambor,
                        R.drawable.img_tambor, R.raw.som_tambor)));

        return Collections.unmodifiableList(tudo);
    }
}
