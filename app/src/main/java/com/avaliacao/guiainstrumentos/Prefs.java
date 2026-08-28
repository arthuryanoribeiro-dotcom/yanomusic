package com.avaliacao.guiainstrumentos;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Guarda as escolhas do menu Configuracoes (modo noturno e cor de destaque)
 * e as aplica. Usa SharedPreferences para sobreviver ao fechamento do app.
 */
public final class Prefs {

    private static final String ARQUIVO = "configuracoes";
    private static final String CHAVE_MODO = "modo_noturno";
    private static final String CHAVE_COR = "cor_destaque";

    public static final int COR_AMBAR = 0;
    public static final int COR_TURQUESA = 1;
    public static final int COR_ROXO = 2;

    private Prefs() {
    }

    private static SharedPreferences abrir(Context contexto) {
        return contexto.getApplicationContext()
                .getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
    }

    /** Valor no formato aceito por AppCompatDelegate.setDefaultNightMode. */
    public static int getModoNoturno(Context contexto) {
        return abrir(contexto).getInt(CHAVE_MODO,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static void setModoNoturno(Context contexto, int modo) {
        abrir(contexto).edit().putInt(CHAVE_MODO, modo).apply();
        AppCompatDelegate.setDefaultNightMode(modo);
    }

    public static int getCor(Context contexto) {
        return abrir(contexto).getInt(CHAVE_COR, COR_AMBAR);
    }

    public static void setCor(Context contexto, int cor) {
        abrir(contexto).edit().putInt(CHAVE_COR, cor).apply();
    }

    /** Estilo correspondente a cor escolhida, definido em res/values/themes.xml. */
    public static int getTemaDaCor(Context contexto) {
        switch (getCor(contexto)) {
            case COR_TURQUESA:
                return R.style.Theme_GuiaInstrumentos_Turquesa;
            case COR_ROXO:
                return R.style.Theme_GuiaInstrumentos_Roxo;
            default:
                return R.style.Theme_GuiaInstrumentos;
        }
    }
}
