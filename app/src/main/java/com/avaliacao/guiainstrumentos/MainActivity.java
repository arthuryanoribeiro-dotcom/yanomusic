package com.avaliacao.guiainstrumentos;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * ACTIVITY PRINCIPAL (unica Activity de suporte).
 *
 * Hospeda os 3 Fragmentos num FrameLayout e troca entre eles pelo
 * BottomNavigationView. Nenhum dos fragmentos e uma Activity separada.
 * A Toolbar traz o menu Configuracoes, que altera tema e cores.
 */
public class MainActivity extends BaseActivity {

    private static final int[] MODOS_NOTURNOS = {
            AppCompatDelegate.MODE_NIGHT_NO,
            AppCompatDelegate.MODE_NIGHT_YES,
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navegacao = findViewById(R.id.navegacao_inferior);
        navegacao.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.aba_lista) {
                return trocarFragmento(new ListaFragment());
            } else if (id == R.id.aba_galeria) {
                return trocarFragmento(new GaleriaFragment());
            }
            return trocarFragmento(new CategoriasFragment());
        });

        // Estado inicial: Fragmento 1. Em rotacao o FragmentManager ja restaura,
        // por isso so montamos quando savedInstanceState e nulo. O replace e
        // feito na mao porque o BottomNavigationView ja marca o primeiro item
        // sozinho e, nesse caso, nao dispara o listener.
        if (savedInstanceState == null) {
            trocarFragmento(new CategoriasFragment());
            navegacao.setSelectedItemId(R.id.aba_categorias);
        }
    }

    private boolean trocarFragmento(Fragment fragmento) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_fragmento, fragmento)
                .commit();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.acao_configuracoes) {
            abrirDialogoTema();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Configuracoes, parte 1: Modo Claro / Modo Noturno / Seguir o sistema. */
    private void abrirDialogoTema() {
        int modoAtual = Prefs.getModoNoturno(this);
        int selecionado = 2;
        for (int i = 0; i < MODOS_NOTURNOS.length; i++) {
            if (MODOS_NOTURNOS[i] == modoAtual) {
                selecionado = i;
            }
        }

        CharSequence[] opcoes = {
                getString(R.string.tema_claro),
                getString(R.string.tema_escuro),
                getString(R.string.tema_sistema)
        };

        new AlertDialog.Builder(this)
                .setTitle(R.string.config_tema)
                .setSingleChoiceItems(opcoes, selecionado, (dialogo, qual) -> {
                    dialogo.dismiss();
                    // Recria a Activity sozinho quando o modo muda de fato.
                    Prefs.setModoNoturno(this, MODOS_NOTURNOS[qual]);
                })
                .setNeutralButton(R.string.config_cor,
                        (dialogo, qual) -> abrirDialogoCor())
                .setNegativeButton(R.string.config_fechar, null)
                .show();
    }

    /** Configuracoes, parte 2: cor de destaque aplicada por overlay de tema. */
    private void abrirDialogoCor() {
        CharSequence[] cores = {
                getString(R.string.cor_ambar),
                getString(R.string.cor_turquesa),
                getString(R.string.cor_roxo)
        };

        new AlertDialog.Builder(this)
                .setTitle(R.string.config_cor)
                .setSingleChoiceItems(cores, Prefs.getCor(this), (dialogo, qual) -> {
                    dialogo.dismiss();
                    Prefs.setCor(this, qual);
                    recreate();
                })
                .setNegativeButton(R.string.config_fechar, null)
                .show();
    }
}
