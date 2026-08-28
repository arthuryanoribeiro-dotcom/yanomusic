package com.avaliacao.guiainstrumentos;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * ACTIVITY EXTRA (item 3 do enunciado).
 *
 * Recebe o Instrumento pela Intent, mostra imagem em destaque + textos
 * (historia e ficha tecnica) e controla o MediaPlayer.
 *
 * Encerramento: o botao "Encerrar" e o botao voltar do aparelho chamam
 * finish(), tirando esta Activity da pilha. Em QUALQUER um dos dois caminhos o
 * Android passa por onDestroy(), e e la que o MediaPlayer e parado e liberado
 * (item 3.5) - por isso o audio nunca continua tocando em segundo plano.
 */
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

    /** Para e volta ao inicio, deixando o player pronto para tocar de novo. */
    private void parar() {
        if (tocador != null) {
            if (tocador.isPlaying()) {
                tocador.pause();
            }
            tocador.seekTo(0);
            status.setText(R.string.status_parado);
        }
    }

    /** A seta da Toolbar encerra igual ao botao voltar (nao empilha outra tela). */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Item 3.5: stop() e release() acontecem AQUI, no onDestroy, que e o ponto
     * por onde toda forma de encerramento passa (botao Encerrar, botao voltar
     * do aparelho ou seta da Toolbar).
     */
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
