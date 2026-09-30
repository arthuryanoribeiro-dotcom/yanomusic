package com.avaliacao.guiainstrumentos;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class DetalheActivity extends BaseActivity {

    public static final String EXTRA_INSTRUMENTO = "extra_instrumento";
    public static final String EXTRA_CATEGORIA = "extra_categoria";

    private MediaPlayer tocador;
    private TextView status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Dados que vieram pelo Intent.putExtra dos Fragmentos 2 e 3.
        Instrumento instrumento =
                (Instrumento) getIntent().getSerializableExtra(EXTRA_INSTRUMENTO);
        int categoriaResId = getIntent().getIntExtra(EXTRA_CATEGORIA,
                R.string.categoria_cordas);

        if (instrumento == null) {
            finish();
            return;
        }

        setTitle(instrumento.getNomeResId());

        ImageView imagem = findViewById(R.id.imagem_destaque);
        TextView titulo = findViewById(R.id.detalhe_titulo);
        TextView categoria = findViewById(R.id.detalhe_categoria);
        TextView texto = findViewById(R.id.detalhe_texto);
        TextView ficha = findViewById(R.id.detalhe_ficha);
        status = findViewById(R.id.detalhe_status_player);

        imagem.setImageResource(instrumento.getImagemResId());
        titulo.setText(instrumento.getNomeResId());
        categoria.setText(categoriaResId);
        texto.setText(instrumento.getDetalheResId());
        ficha.setText(instrumento.getFichaResId());
        status.setText(R.string.status_parado);

        // MediaPlayer criado a partir do arquivo .mp3 em res/raw.
        tocador = MediaPlayer.create(this, instrumento.getSomResId());
        if (tocador != null) {
            tocador.setOnCompletionListener(mp -> status.setText(R.string.status_parado));
        }

        Button botaoTocar = findViewById(R.id.botao_tocar);
        Button botaoPausar = findViewById(R.id.botao_pausar);
        Button botaoParar = findViewById(R.id.botao_parar);
        Button botaoEncerrar = findViewById(R.id.botao_encerrar);

        botaoTocar.setOnClickListener(v -> tocar());
        botaoPausar.setOnClickListener(v -> pausar());
        botaoParar.setOnClickListener(v -> parar());

        // Item 3.4: encerra a Activity e devolve o usuario a Activity Principal.
        botaoEncerrar.setOnClickListener(v -> finish());
    }

    private void tocar() {
        if (tocador != null && !tocador.isPlaying()) {
            tocador.start();
            status.setText(R.string.status_tocando);
        }
    }

    private void pausar() {
        if (tocador != null && tocador.isPlaying()) {
            tocador.pause();
            status.setText(R.string.status_pausado);
        }
    }


    private void parar() {
        if (tocador != null) {
            if (tocador.isPlaying()) {
                tocador.pause();
            }
            tocador.seekTo(0);
            status.setText(R.string.status_parado);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        if (tocador != null) {
            if (tocador.isPlaying()) {
                tocador.stop();
            }
            tocador.release();
            tocador = null;
        }
        super.onDestroy();
    }
}
