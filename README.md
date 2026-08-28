# Guia de Instrumentos

Avaliação 1 de Programação Mobile — aplicativo Android nativo em **Java**, com uma Activity
principal hospedando **3 Fragmentos**, comunicação reativa por **ViewModel + LiveData**,
**Spinner / ListView / GridView**, **MediaPlayer** e uma **Activity Extra** aberta por Intent.

Tema escolhido: **Sugestão 2 — Categoria de Instrumentos Musicais** (Cordas, Sopro, Percussão).

---

## Como abrir e rodar

1. Abra o **Android Studio** → *Open* → selecione a pasta deste repositório.
2. Aguarde o *Gradle Sync* (ele baixa o AGP 8.5.2 e as dependências AndroidX na primeira vez).
3. Conecte o celular Android com **Depuração USB** ligada e clique em *Run*.

> O `local.properties`, que aponta para o SDK, **não** vai versionado — o Android Studio cria
> esse arquivo sozinho no primeiro Sync.

Requisitos: Android Studio Ladybug ou mais novo, JDK 17 (já embutido no Android Studio),
aparelho ou emulador com **Android 7.0 (API 24)** ou superior.

---

## Onde está cada requisito do enunciado

Esta tabela serve de roteiro na hora de gravar o vídeo: ela mapeia cada exigência ao arquivo.

| Requisito do PDF | Onde está |
|---|---|
| Uma Activity principal, Basic Views Activity **sem FAB** | [MainActivity.java](app/src/main/java/com/avaliacao/guiainstrumentos/MainActivity.java) · [activity_main.xml](app/src/main/res/layout/activity_main.xml) |
| Navegação por **Bottom Navigation**, 3 Fragmentos na mesma Activity | [MainActivity.java:47](app/src/main/java/com/avaliacao/guiainstrumentos/MainActivity.java:47) e o método `trocarFragmento` |
| **Fragmento 1** com Spinner | [CategoriasFragment.java](app/src/main/java/com/avaliacao/guiainstrumentos/CategoriasFragment.java) · opções vindas de [arrays.xml](app/src/main/res/values/arrays.xml) por `createFromResource` ([:48](app/src/main/java/com/avaliacao/guiainstrumentos/CategoriasFragment.java:48)) |
| **Fragmento 2** com ListView e **adaptador personalizado** (imagem + título + descrição) | [ListaFragment.java](app/src/main/java/com/avaliacao/guiainstrumentos/ListaFragment.java) · [InstrumentoAdapter.java](app/src/main/java/com/avaliacao/guiainstrumentos/InstrumentoAdapter.java) · View personalizada em [item_lista.xml](app/src/main/res/layout/item_lista.xml) |
| **Fragmento 3** com GridView e **BaseAdapter** | [GaleriaFragment.java](app/src/main/java/com/avaliacao/guiainstrumentos/GaleriaFragment.java) · [GaleriaAdapter.java:18](app/src/main/java/com/avaliacao/guiainstrumentos/GaleriaAdapter.java:18) |
| **ViewModel + LiveData** ligando o Spinner aos Fragmentos 2 e 3 | [InstrumentoViewModel.java:32](app/src/main/java/com/avaliacao/guiainstrumentos/InstrumentoViewModel.java:32) — `MediatorLiveData.addSource` |
| Escopo compartilhado, sem acoplamento entre os fragmentos | `new ViewModelProvider(requireActivity())` em [CategoriasFragment.java:41](app/src/main/java/com/avaliacao/guiainstrumentos/CategoriasFragment.java:41), [ListaFragment.java:44](app/src/main/java/com/avaliacao/guiainstrumentos/ListaFragment.java:44) e [GaleriaFragment.java:39](app/src/main/java/com/avaliacao/guiainstrumentos/GaleriaFragment.java:39) |
| **Intent + putExtra** para a Activity Extra | [ListaFragment.java:55](app/src/main/java/com/avaliacao/guiainstrumentos/ListaFragment.java:55) e [GaleriaFragment.java:50](app/src/main/java/com/avaliacao/guiainstrumentos/GaleriaFragment.java:50) |
| **Activity Extra** com imagem em destaque, textos e player | [DetalheActivity.java](app/src/main/java/com/avaliacao/guiainstrumentos/DetalheActivity.java) · [activity_detalhe.xml](app/src/main/res/layout/activity_detalhe.xml) |
| **MediaPlayer** lendo de `res/raw` | [DetalheActivity.java:69](app/src/main/java/com/avaliacao/guiainstrumentos/DetalheActivity.java:69) |
| Botão **"Encerrar"** tira a Activity da pilha | [DetalheActivity.java:84](app/src/main/java/com/avaliacao/guiainstrumentos/DetalheActivity.java:84) |
| `stop()` + `release()` no **`onDestroy()`** | [DetalheActivity.java:128](app/src/main/java/com/avaliacao/guiainstrumentos/DetalheActivity.java:128) |
| Menu **Settings** na Toolbar: Modo Claro/Noturno + cor de destaque | [MainActivity.java](app/src/main/java/com/avaliacao/guiainstrumentos/MainActivity.java) (`abrirDialogoTema` e `abrirDialogoCor`) · [menu_main.xml](app/src/main/res/menu/menu_main.xml) · [Prefs.java](app/src/main/java/com/avaliacao/guiainstrumentos/Prefs.java) |
| Tema aplicado antes de inflar o layout | [BaseActivity.java:18](app/src/main/java/com/avaliacao/guiainstrumentos/BaseActivity.java:18) |
| Strings centralizadas | [strings.xml](app/src/main/res/values/strings.xml) — 117 strings, **nenhum literal de texto no Java nem nos layouts** |
| Cores centralizadas | [colors.xml](app/src/main/res/values/colors.xml) · [themes.xml](app/src/main/res/values/themes.xml) · [values-night/themes.xml](app/src/main/res/values-night/themes.xml) |
| Arrays centralizados | [arrays.xml](app/src/main/res/values/arrays.xml) |
| Sons em `res/raw` | 21 arquivos `.mp3` em [res/raw](app/src/main/res/raw) |
| Imagens em `res/drawable` | 21 arquivos `.png` em [res/drawable](app/src/main/res/drawable) |

---

## Sobre a mídia: por que ela é original

O enunciado proíbe usar os áudios e imagens fornecidos em aula. Aqui **nenhum arquivo foi baixado
ou copiado de lugar nenhum** — os 21 MP3 e os 21 PNG são gerados por código, pelo script
[tools/gerar_midia.py](tools/gerar_midia.py):

- **Áudio:** síntese por DSP com `numpy`, um algoritmo por família — *Karplus-Strong* para as
  cordas, síntese aditiva com vibrato e ruído de sopro para os sopros, e burst de ruído com
  parciais inarmônicas para a percussão. Codificação em MP3 via `lameenc`.
- **Imagem:** ilustrações vetoriais planas desenhadas com primitivas do `PIL` (elipses, polígonos
  e linhas), sobre fundo em degradê na cor da família.

Para regerar tudo do zero:

```bash
pip install numpy pillow lameenc && python tools/gerar_midia.py
```

---

## Verificação estática

Como o projeto foi escrito fora do Android Studio, há um script que confere se toda referência a
recurso (`@string/`, `@color/`, `@drawable/`, `@id/`, `R.raw.*`, …) realmente existe — é o erro que
mais quebra build Android:

```bash
python tools/checar_refs.py
```

Ele também valida que cada `.java` declara `package` e classe compatíveis com o caminho do arquivo.
Estado atual: **209 referências verificadas, nenhuma quebrada**.

> Atenção: essa checagem **não substitui a compilação**. O primeiro `build` real acontece quando
> você abrir o projeto no Android Studio.

---

## Estrutura

```
app/src/main/
├─ AndroidManifest.xml          MainActivity (LAUNCHER) + DetalheActivity
├─ java/com/avaliacao/guiainstrumentos/
│  ├─ MainActivity.java         Activity principal, bottom nav, menu Settings
│  ├─ BaseActivity.java         aplica tema/cor salvos antes do setContentView
│  ├─ DetalheActivity.java      Activity Extra: player + detalhes
│  ├─ InstrumentoViewModel.java ViewModel + LiveData (comunicação reativa)
│  ├─ CategoriasFragment.java   Fragmento 1 — Spinner
│  ├─ ListaFragment.java        Fragmento 2 — ListView
│  ├─ GaleriaFragment.java      Fragmento 3 — GridView
│  ├─ InstrumentoAdapter.java   adaptador personalizado da ListView
│  ├─ GaleriaAdapter.java       BaseAdapter da GridView
│  ├─ Instrumento.java          modelo (Serializable, viaja na Intent)
│  ├─ Repositorio.java          dados das 3 categorias
│  └─ Prefs.java                SharedPreferences do tema e da cor
└─ res/
   ├─ layout/     7 layouts
   ├─ menu/       2 menus
   ├─ drawable/   21 PNG + 4 ícones vetoriais
   ├─ raw/        21 MP3
   ├─ values/     strings, colors, arrays, themes
   └─ values-night/  themes do Modo Noturno
```

---

## Entrega

Falta ainda, e depende de você:

1. Gravar o vídeo de **5 a 10 minutos** com o app rodando em **celular Android físico** —
   siga o [ROTEIRO_VIDEO.md](ROTEIRO_VIDEO.md).
2. Publicar no YouTube com visibilidade **"Não listado"**.
3. Enviar o link do vídeo junto com os **nomes completos** dos integrantes do grupo.
