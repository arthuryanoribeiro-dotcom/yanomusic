package com.avaliacao.guiainstrumentos;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {Usuario.class, Sessao.class, Categoria.class, Instrumento.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String NOME_ARQUIVO = "guia_instrumentos.db";

    // Room proibe consultas na thread principal; toda escrita passa por aqui.
    static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private static volatile AppDatabase instancia;

    public abstract UsuarioDao usuarioDao();

    public abstract SessaoDao sessaoDao();

    public abstract CategoriaDao categoriaDao();

    public abstract InstrumentoDao instrumentoDao();

    public static AppDatabase getInstancia(Context contexto) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    instancia = criar(contexto.getApplicationContext());
                }
            }
        }
        return instancia;
    }

    private static AppDatabase criar(Context contexto) {
        return Room.databaseBuilder(contexto, AppDatabase.class, NOME_ARQUIVO)
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase banco) {
                        // So na primeira abertura: popula as tabelas do tema.
                        EXECUTOR.execute(() ->
                                SeedDados.popular(contexto, getInstancia(contexto)));
                    }
                })
                .build();
    }
}
