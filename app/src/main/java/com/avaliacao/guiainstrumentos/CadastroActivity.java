package com.avaliacao.guiainstrumentos;

import android.content.ActivityNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;


// Mesma Activity para os dois modos: cadastro (padrao) e edicao do perfil logado.
public class CadastroActivity extends BaseActivity {

    public static final String EXTRA_MODO_EDICAO = "extra_modo_edicao";
    private static final String ESTADO_FOTO = "estado_foto";

    private SessaoViewModel viewModel;
    private boolean modoEdicao;
    private Usuario usuarioLogado;
    private byte[] foto;

    private ImageView imagemFoto;
    private EditText campoNome;
    private EditText campoEmail;
    private EditText campoSenha;
    private EditText campoConfirmarSenha;
    private TextView textoErro;

    // Camera externa do sistema: devolve uma miniatura em Bitmap.
    private final ActivityResultLauncher<Void> camera = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(), this::aoCapturarFoto);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        modoEdicao = getIntent().getBooleanExtra(EXTRA_MODO_EDICAO, false);

        TextView titulo = findViewById(R.id.titulo_cadastro);
        TextInputLayout layoutSenha = findViewById(R.id.layout_senha);
        imagemFoto = findViewById(R.id.imagem_foto_perfil);
        campoNome = findViewById(R.id.campo_nome);
        campoEmail = findViewById(R.id.campo_email);
        campoSenha = findViewById(R.id.campo_senha);
        campoConfirmarSenha = findViewById(R.id.campo_confirmar_senha);
        textoErro = findViewById(R.id.texto_erro_cadastro);
        Button botaoTirarFoto = findViewById(R.id.botao_tirar_foto);
        Button botaoSalvar = findViewById(R.id.botao_salvar);

        titulo.setText(modoEdicao ? R.string.titulo_editar_perfil : R.string.titulo_cadastro);
        if (modoEdicao) {
            layoutSenha.setHelperText(getString(R.string.ajuda_senha_edicao));
        }

        botaoTirarFoto.setOnClickListener(v -> abrirCamera());
        botaoSalvar.setOnClickListener(v -> salvar());

        if (savedInstanceState != null) {
            mostrarFoto(savedInstanceState.getByteArray(ESTADO_FOTO));
        }

        viewModel = new ViewModelProvider(this).get(SessaoViewModel.class);
        viewModel.getErro().observe(this, erro -> {
            if (erro != null) {
                textoErro.setText(erro);
            }
        });
        viewModel.getSalvo().observe(this, salvo -> {
            if (salvo) {
                Toast.makeText(this, modoEdicao ? R.string.perfil_atualizado
                        : R.string.cadastro_concluido, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        if (modoEdicao) {
            observarUsuarioLogado(savedInstanceState == null);
        }
    }

    // No modo edicao, preenche o formulario com o perfil vindo do Room.
    private void observarUsuarioLogado(boolean preencherCampos) {
        viewModel.getSessaoAtiva().observe(this, sessao -> {
            if (sessao == null || sessao.usuario == null) {
                finish();
                return;
            }
            if (usuarioLogado == null && preencherCampos) {
                campoNome.setText(sessao.usuario.nome);
                campoEmail.setText(sessao.usuario.email);
                mostrarFoto(sessao.usuario.foto);
            }
            usuarioLogado = sessao.usuario;
        });
    }

    private void abrirCamera() {
        try {
            camera.launch(null);
        } catch (ActivityNotFoundException e) {
            textoErro.setText(R.string.erro_camera);
        }
    }

    private void aoCapturarFoto(Bitmap bitmap) {
        // null quando o usuario cancela a camera.
        if (bitmap != null) {
            mostrarFoto(ImagemUtil.paraBytes(bitmap));
        }
    }

    // A foto e sempre exibida a partir dos bytes, igual ao que vai para o banco.
    private void mostrarFoto(byte[] bytes) {
        foto = bytes;
        if (bytes != null) {
            imagemFoto.setImageBitmap(ImagemUtil.paraBitmap(bytes));
        }
    }

    private void salvar() {
        String nome = texto(campoNome);
        String email = texto(campoEmail);
        String senha = campoSenha.getText().toString();
        String confirmacao = campoConfirmarSenha.getText().toString();

        int erro = validar(nome, email, senha, confirmacao);
        if (erro != 0) {
            textoErro.setText(erro);
            return;
        }
        textoErro.setText("");

        if (modoEdicao) {
            if (usuarioLogado != null) {
                viewModel.atualizarPerfil(usuarioLogado, nome, email, senha, foto);
            }
        } else {
            viewModel.cadastrar(nome, email, senha, foto);
        }
    }

    // Devolve o id do texto de erro, ou 0 quando o formulario esta valido.
    private int validar(String nome, String email, String senha, String confirmacao) {
        if (nome.isEmpty() || email.isEmpty() || (!modoEdicao && senha.isEmpty())) {
            return R.string.erro_campos_obrigatorios;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return R.string.erro_email_invalido;
        }
        if (!senha.equals(confirmacao)) {
            return R.string.erro_senhas_diferentes;
        }
        if (foto == null) {
            return R.string.erro_foto_obrigatoria;
        }
        return 0;
    }

    private String texto(EditText campo) {
        return campo.getText().toString().trim();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putByteArray(ESTADO_FOTO, foto);
    }
}
