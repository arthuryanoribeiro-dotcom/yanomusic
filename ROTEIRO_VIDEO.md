# Roteiro do vídeo — 5 a 10 minutos

O item 5 do enunciado pede um vídeo mostrando o app rodando **em celular Android físico** e
explicando **onde cada componente está no código Java**. Este roteiro cobre exatamente os pontos
que o professor listou, em ordem, com tempo sugerido.

Antes de gravar: abra o Android Studio com o projeto, deixe o celular conectado e o app instalado,
e deixe abertas as abas dos arquivos citados abaixo — assim você só alterna, não procura.

---

## 0. Abertura — 20 s

Diga o nome completo de todos os integrantes do grupo e o tema escolhido:
*Sugestão 2 — Categoria de Instrumentos Musicais*.

---

## 1. App rodando no celular físico — 2 min

Mostre a tela do aparelho (espelhamento ou câmera). Percorra, nesta ordem:

1. **Aba Categorias** (Fragmento 1): abra o Spinner e escolha **Sopro**.
2. **Aba Instrumentos** (Fragmento 2): a lista **já está** com Flauta, Trompete e Clarinete.
   Diga em voz alta que ninguém recarregou nada — a lista mudou sozinha quando o Spinner mudou.
3. **Aba Galeria** (Fragmento 3): a grade também já mostra Saxofone, Trombone, Tuba e Gaita.
4. Volte em Categorias, troque para **Percussão** e mostre as duas abas mudando de novo.
   *Esse vai e volta é a prova visual do ViewModel funcionando.*
5. Toque num item da **lista** → abre a Activity Extra. Aperte **Tocar**: o áudio sai.
   Teste **Pausar** e **Parar**.
6. Aperte **Encerrar** → volta para a principal e **o som para**.
7. Entre de novo, toque o áudio e saia pelo **botão voltar do aparelho** — o som também para.
   Diga que os dois caminhos passam pelo mesmo `onDestroy()`.
8. Toque num item da **grade** → mesma Activity Extra, agora com a ficha técnica.
9. Menu **⋮ → Configurações**: troque para **Modo Noturno**. Abra de novo e troque a
   **Cor de destaque** para Turquesa ou Roxo.

---

## 2. Explicação do código — 4 a 6 min

> A tabela do [README.md](README.md) tem o arquivo e a linha de cada item. Siga a ordem abaixo.

### 2.1 Estrutura: uma Activity, três Fragmentos — 1 min

- `MainActivity.java`: mostre que ela estende `BaseActivity` e que o `setOnItemSelectedListener`
  do `BottomNavigationView` chama `trocarFragmento(...)`.
- `trocarFragmento` faz `replace(R.id.container_fragmento, fragmento)` — **é sempre a mesma
  Activity**, só o conteúdo do `FrameLayout` muda. Nenhum fragmento é Activity separada.
- Abra `activity_main.xml` e aponte a `MaterialToolbar`, o `FrameLayout` e o
  `BottomNavigationView`. Comente que o FAB do template foi removido.

### 2.2 ViewModel com LiveData — 1 min 30 s (é o requisito de pesquisa, capriche)

Abra `InstrumentoViewModel.java` e explique o fluxo em três frases:

- Existe **um** `MutableLiveData<Integer> categoria`.
- `lista` e `galeria` são `MediatorLiveData` que fazem `addSource(categoria, ...)`: quando a
  categoria muda, elas se reescrevem sozinhas com os dados do `Repositorio`.
- Os três fragmentos pegam o ViewModel com `new ViewModelProvider(requireActivity())` — o escopo
  é a **Activity**, então os três compartilham a mesma instância.

Depois mostre as duas pontas:

- `CategoriasFragment.java`: no `onItemSelected` do Spinner ele **só** faz
  `viewModel.setCategoria(posicao)`. Ele não conhece os outros fragmentos.
- `ListaFragment.java` e `GaleriaFragment.java`: eles **só** fazem `.observe(...)`.
  Também não conhecem ninguém.

**A frase-chave para falar:** *"o Fragmento 1 não chama o 2 nem o 3; ele escreve num ViewModel
compartilhado e os outros dois reagem — é isso que elimina o acoplamento direto."*

### 2.3 Spinner, ListView e GridView — 1 min

- `CategoriasFragment.java:48` — `ArrayAdapter.createFromResource(..., R.array.categorias, ...)`.
  Abra `res/values/arrays.xml` e mostre que as opções vêm do XML, não do código.
- `InstrumentoAdapter.java` — adaptador **personalizado**: mostre o `getView` inflando
  `R.layout.item_lista`. Abra `item_lista.xml` e aponte o `ImageView` + os dois `TextView`
  (imagem, título e descrição, como o enunciado exige). Cite o padrão *ViewHolder* (`Suporte`).
- `GaleriaAdapter.java` — mostre que ele `extends BaseAdapter` e os métodos obrigatórios
  (`getCount`, `getItem`, `getItemId`, `getView`).

### 2.4 Intent entre Activities — 1 min

- `ListaFragment.java:55` — o `setOnItemClickListener` monta a `Intent` e usa
  `putExtra(EXTRA_INSTRUMENTO, ...)`. Comente que `Instrumento` é `Serializable`, por isso o
  objeto inteiro viaja na Intent.
- `DetalheActivity.java` — o `getSerializableExtra` do outro lado e o preenchimento das views.
- `AndroidManifest.xml` — as duas Activities declaradas.

### 2.5 MediaPlayer e liberação de memória — 45 s

- `DetalheActivity.java:69` — `MediaPlayer.create(this, instrumento.getSomResId())`, lendo o
  `.mp3` de `res/raw`.
- Os três botões: `tocar()`, `pausar()`, `parar()`.
- `DetalheActivity.java:128` — **`onDestroy()` com `stop()` e `release()`**. Explique que colocar
  no `onDestroy` garante que qualquer forma de sair (botão Encerrar, botão voltar do aparelho ou
  seta da Toolbar) libera o player, e por isso o áudio nunca fica tocando em segundo plano.

### 2.6 Imagens, cores e strings — 45 s

- `res/drawable/` — as 21 imagens; `res/raw/` — os 21 áudios.
- `res/values/strings.xml` — role rápido e diga: **117 strings, nenhum texto escrito direto no
  Java ou no layout**. Mostre que até o `Repositorio.java` guarda `R.string.*`, não texto.
- `res/values/colors.xml` e `themes.xml` + `res/values-night/themes.xml` — a paleta própria e o
  tema noturno.
- `MainActivity.java` (`abrirDialogoTema` e `abrirDialogoCor`) e `Prefs.java` — como o menu
  Configurações salva a escolha e aplica com `AppCompatDelegate.setDefaultNightMode` e `setTheme`.
- `BaseActivity.java:18` — por que o tema precisa ser aplicado **antes** do `setContentView`.

### 2.7 Mídia original — 20 s (diferencial, vale citar)

Abra `tools/gerar_midia.py` e diga que os áudios e imagens não vieram da aula nem da internet:
foram **gerados por código**, com síntese Karplus-Strong para as cordas, síntese aditiva para os
sopros e ruído com envelope para a percussão, e as ilustrações desenhadas com PIL.

---

## 3. Fechamento — 20 s

Recapitule em uma frase: uma Activity, três Fragmentos, ViewModel com LiveData ligando o Spinner às
duas listagens, Intent levando o item para a Activity Extra, e MediaPlayer liberado no `onDestroy`.

---

## Depois de gravar

- [ ] Vídeo entre **5 e 10 minutos**
- [ ] Aparece o app rodando em **celular Android físico**
- [ ] Subir no YouTube com visibilidade **"Não listado"**
- [ ] Enviar o link + **nomes completos** de todos os integrantes (1 a 3 alunos)
- [ ] Apenas **um** integrante faz o envio
