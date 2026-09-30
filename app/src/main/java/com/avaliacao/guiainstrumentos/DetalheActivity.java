package com.avaliacao.guiainstrumentos;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;


public class DetalheActivity extends BaseActivity {

    public static final String EXTRA_INSTRUMENTO_ID = "extra_instrumento_id";
    private static final long SEM_ID = -1;

    private MediaPlayer tocador;
    private ImageView imagem;
    private TextView titulo;
    private TextView categoria;
    private TextView texto;
    private TextView ficha;
    private TextView status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Id que veio pelo Intent.putExtra dos Fragmentos 2 e 3.
        long instrumentoId = getIntent().getLongExtra(EXTRA_INSTRUMENTO_ID, SEM_ID);
        if (instrumentoId == SEM_ID) {
            finish();
            return;
        }

        imagem = findViewById(R.id.imagem_destaque);
        titulo = findViewById(R.id.detalhe_titulo);
        categoria = findViewById(R.id.detalhe_categoria);
        texto = findViewById(R.id.detalhe_texto);
        ficha = findViewById(R.id.detalhe_ficha);
        status = findViewById(R.id.detalhe_status_player);
        status.setText(R.string.status_parado);

        Button botaoTocar = findViewById(R.id.botao_tocar);
        Button botaoPausar = findViewById(R.id.botao_pausar);
        Button botaoParar = findViewById(R.id.botao_parar);
        Button botaoEncerrar = findViewById(R.id.botao_encerrar);

        botaoTocar.setOnClickListener(v -> tocar());
        botaoPausar.setOnClickListener(v -> pausar());
        botaoParar.setOnClickListener(v -> parar());

        // Item 3.4: encerra a Activity e devolve o usuario a Activity Principal.
        botaoEncerrar.setOnClickListener(v -> finish());

        InstrumentoViewModel viewModel =
                new ViewModelProvider(this).get(InstrumentoViewModel.class);
        viewModel.getInstrumento(instrumentoId).observe(this, this::exibir);
    }

    private void exibir(InstrumentoComCategoria detalhe) {
        if (detalhe == null) {
            finish();
            return;
        }
        Instrumento instrumento = detalhe.instrumento;

        setTitle(instrumento.nome);
        imagem.setImageURI(Uri.parse(instrumento.imagemPath));
        titulo.setText(instrumento.nome);
        categoria.setText(detalhe.categoria.nome);
        texto.setText(instrumento.detalhe);
        ficha.setText(instrumento.ficha);

        if (tocador == null) {
            prepararTocador(instrumento.audioPath);
        }
    }

    // MediaPlayer criado a partir do caminho do audio gravado no Room.
    private void prepararTocador(String audioPath) {
        tocador = MediaPlayer.create(this, Uri.parse(audioPath));
        if (tocador != null) {
            tocador.setOnCompletionListener(mp -> status.setText(R.string.status_parado));
        }
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
