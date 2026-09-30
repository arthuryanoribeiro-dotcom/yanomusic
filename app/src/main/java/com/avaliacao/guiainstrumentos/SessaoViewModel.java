package com.avaliacao.guiainstrumentos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


// Sessao, login/logout e cadastro/edicao do usuario, sempre via Room.
public class SessaoViewModel extends AndroidViewModel {

    private final Repositorio repositorio;
    private final LiveData<SessaoComUsuario> sessaoAtiva;

    // Id do texto de erro a exibir (null = sem erro).
    private final MutableLiveData<Integer> erro = new MutableLiveData<>();
    private final MutableLiveData<Boolean> salvo = new MutableLiveData<>(false);

    public SessaoViewModel(@NonNull Application aplicacao) {
        super(aplicacao);
        repositorio = new Repositorio(aplicacao);
        sessaoAtiva = repositorio.getSessaoAtiva();
    }

    public LiveData<SessaoComUsuario> getSessaoAtiva() {
        return sessaoAtiva;
    }

    public LiveData<Integer> getErro() {
        return erro;
    }

    public LiveData<Boolean> getSalvo() {
        return salvo;
    }

    public void login(String email, String senha) {
        repositorio.login(email, senha, valido ->
                erro.postValue(valido ? null : R.string.erro_login_invalido));
    }

    public void logout() {
        repositorio.logout();
    }

    public void cadastrar(String nome, String email, String senha, byte[] foto) {
        repositorio.cadastrar(nome, email, senha, foto, this::aoSalvar);
    }

    public void atualizarPerfil(Usuario usuario, String nome, String email,
                                String novaSenha, byte[] foto) {
        repositorio.atualizarPerfil(usuario, nome, email, novaSenha, foto, this::aoSalvar);
    }

    private void aoSalvar(boolean sucesso) {
        if (sucesso) {
            salvo.postValue(true);
        } else {
            erro.postValue(R.string.erro_email_em_uso);
        }
    }
}
