package com.avaliacao.guiainstrumentos;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.function.Consumer;


// Unica porta de entrada para o Room: os ViewModels nao conhecem DAOs.
// Leituras devolvem LiveData; escritas rodam no executor e avisam o
// resultado pelo Consumer (chamado fora da thread principal).
public class Repositorio {

    private final AppDatabase banco;
    private final Executor executor = AppDatabase.EXECUTOR;

    public Repositorio(Context contexto) {
        banco = AppDatabase.getInstancia(contexto);
    }

    // ------------------------------------------------------------ dominio

    public LiveData<List<Categoria>> getCategorias() {
        return banco.categoriaDao().getTodas();
    }

    public LiveData<CategoriaComInstrumentos> getCategoriaComInstrumentos(long categoriaId) {
        return banco.categoriaDao().getComInstrumentos(categoriaId);
    }

    public LiveData<InstrumentoComCategoria> getInstrumento(long instrumentoId) {
        return banco.instrumentoDao().getDetalhe(instrumentoId);
    }

    // ------------------------------------------------------ sessao/usuario

    public LiveData<SessaoComUsuario> getSessaoAtiva() {
        return banco.sessaoDao().getSessaoAtiva();
    }

    public void login(String email, String senha, Consumer<Boolean> resultado) {
        executor.execute(() -> {
            Usuario usuario = banco.usuarioDao().buscarPorEmail(normalizar(email));
            boolean valido = usuario != null
                    && SenhaUtil.verificar(senha, usuario.salt, usuario.senhaHash);
            if (valido) {
                iniciarSessao(usuario.id);
            }
            resultado.accept(valido);
        });
    }

    public void logout() {
        executor.execute(() -> banco.sessaoDao().apagarTodas());
    }

    public void cadastrar(String nome, String email, String senha, byte[] foto,
                          Consumer<Boolean> resultado) {
        executor.execute(() -> {
            if (emailEmUsoPorOutro(email, null)) {
                resultado.accept(false);
                return;
            }
            Usuario usuario = new Usuario();
            preencher(usuario, nome, email, foto);
            definirSenha(usuario, senha);
            banco.usuarioDao().inserir(usuario);
            resultado.accept(true);
        });
    }

    // novaSenha vazia mantem a senha atual.
    public void atualizarPerfil(Usuario usuario, String nome, String email, String novaSenha,
                                byte[] foto, Consumer<Boolean> resultado) {
        executor.execute(() -> {
            if (emailEmUsoPorOutro(email, usuario)) {
                resultado.accept(false);
                return;
            }
            preencher(usuario, nome, email, foto);
            if (!novaSenha.isEmpty()) {
                definirSenha(usuario, novaSenha);
            }
            banco.usuarioDao().atualizar(usuario);
            resultado.accept(true);
        });
    }

    private void iniciarSessao(long usuarioId) {
        banco.runInTransaction(() -> {
            banco.sessaoDao().apagarTodas();
            banco.sessaoDao().inserir(new Sessao(usuarioId, System.currentTimeMillis()));
        });
    }

    private boolean emailEmUsoPorOutro(String email, Usuario proprio) {
        Usuario dono = banco.usuarioDao().buscarPorEmail(normalizar(email));
        return dono != null && (proprio == null || dono.id != proprio.id);
    }

    private void preencher(Usuario usuario, String nome, String email, byte[] foto) {
        usuario.nome = nome;
        usuario.email = normalizar(email);
        usuario.foto = foto;
    }

    // Criptografa antes de persistir: o banco nunca ve a senha em texto.
    private void definirSenha(Usuario usuario, String senha) {
        usuario.salt = SenhaUtil.gerarSalt();
        usuario.senhaHash = SenhaUtil.hash(senha, usuario.salt);
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
