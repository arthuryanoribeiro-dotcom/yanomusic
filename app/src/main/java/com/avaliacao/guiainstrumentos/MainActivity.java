package com.avaliacao.guiainstrumentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends BaseActivity {

    private static final int[] MODOS_NOTURNOS = {
            AppCompatDelegate.MODE_NIGHT_NO,
            AppCompatDelegate.MODE_NIGHT_YES,
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    };

    private Toolbar toolbar;
    private BottomNavigationView navegacao;
    private SessaoViewModel sessaoViewModel;
    private boolean logado;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navegacao = findViewById(R.id.navegacao_inferior);
        navegacao.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.aba_lista) {
                return trocarFragmento(new ListaFragment());
            } else if (id == R.id.aba_galeria) {
                return trocarFragmento(new GaleriaFragment());
            }
            return trocarFragmento(new CategoriasFragment());
        });

        // O estado da sessao no Room decide o que aparece na tela.
        sessaoViewModel = new ViewModelProvider(this).get(SessaoViewModel.class);
        sessaoViewModel.getSessaoAtiva().observe(this, this::aplicarSessao);
    }

    private void aplicarSessao(@Nullable SessaoComUsuario sessao) {
        logado = sessao != null && sessao.usuario != null;
        invalidateOptionsMenu();
        if (logado) {
            mostrarConteudo(sessao.usuario);
        } else {
            mostrarLogin();
        }
    }

    private void mostrarConteudo(Usuario usuario) {
        setTitle(usuario.nome);
        toolbar.setLogo(usuario.foto == null ? null : ImagemUtil.avatar(getResources(),
                usuario.foto, getResources().getDimensionPixelSize(R.dimen.tamanho_avatar)));
        navegacao.setVisibility(View.VISIBLE);

        // So redireciona quem vem do login; rotacao ou edicao de perfil mantem a aba atual.
        if (fragmentoAtual() == null || fragmentoAtual() instanceof LoginFragment) {
            navegacao.getMenu().findItem(R.id.aba_categorias).setChecked(true);
            trocarFragmento(new CategoriasFragment());
        }
    }

    private void mostrarLogin() {
        setTitle(R.string.app_name);
        toolbar.setLogo(null);
        navegacao.setVisibility(View.GONE);
        if (!(fragmentoAtual() instanceof LoginFragment)) {
            trocarFragmento(new LoginFragment());
        }
    }

    private Fragment fragmentoAtual() {
        return getSupportFragmentManager().findFragmentById(R.id.container_fragmento);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.acao_editar_perfil).setVisible(logado);
        menu.findItem(R.id.acao_sair).setVisible(logado);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.acao_editar_perfil) {
            Intent intent = new Intent(this, CadastroActivity.class);
            intent.putExtra(CadastroActivity.EXTRA_MODO_EDICAO, true);
            startActivity(intent);
            return true;
        } else if (id == R.id.acao_sair) {
            // Apaga a sessao no Room; o Observer volta para o LoginFragment.
            sessaoViewModel.logout();
            return true;
        } else if (id == R.id.acao_configuracoes) {
            abrirDialogoTema();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


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
