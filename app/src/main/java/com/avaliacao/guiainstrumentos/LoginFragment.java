package com.avaliacao.guiainstrumentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


// Nao navega sozinho: ao logar, o Room grava a sessao e o Observer da
// MainActivity e quem troca para os fragmentos de conteudo.
public class LoginFragment extends Fragment {

    private SessaoViewModel viewModel;
    private EditText campoEmail;
    private EditText campoSenha;
    private TextView textoErro;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SessaoViewModel.class);

        campoEmail = view.findViewById(R.id.campo_email_login);
        campoSenha = view.findViewById(R.id.campo_senha_login);
        textoErro = view.findViewById(R.id.texto_erro_login);
        Button botaoEntrar = view.findViewById(R.id.botao_entrar);
        Button botaoCriarConta = view.findViewById(R.id.botao_criar_conta);

        botaoEntrar.setOnClickListener(v -> entrar());
        botaoCriarConta.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CadastroActivity.class)));

        viewModel.getErro().observe(getViewLifecycleOwner(), erro ->
                textoErro.setText(erro == null ? "" : getString(erro)));
    }

    private void entrar() {
        String email = campoEmail.getText().toString().trim();
        String senha = campoSenha.getText().toString();
        if (email.isEmpty() || senha.isEmpty()) {
            textoErro.setText(R.string.erro_campos_obrigatorios);
            return;
        }
        viewModel.login(email, senha);
    }
}
