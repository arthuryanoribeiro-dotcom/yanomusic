package com.avaliacao.guiainstrumentos;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Modo Claro / Noturno / Seguir o sistema.
        AppCompatDelegate.setDefaultNightMode(Prefs.getModoNoturno(this));
        // Cor de destaque (Ambar, Turquesa ou Roxo).
        setTheme(Prefs.getTemaDaCor(this));
        super.onCreate(savedInstanceState);
    }
}
