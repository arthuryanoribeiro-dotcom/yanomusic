# Roteiro do vídeo — Avaliação 2 (máx. 10 min)

Antes de gravar: desinstale o app do emulador (banco zerado), confira se a câmera virtual do emulador está ativa (AVD Manager > Advanced > Camera: *VirtualScene* ou *Emulated*) e deixe o Android Studio aberto no projeto.

## 1. Abertura (0:00 – 0:30)
- Nome(s), disciplina, tema: **Guia de Instrumentos** evoluído da Avaliação 1.
- O que mudou: dados no **Room**, **login com sessão**, **cadastro/edição com câmera** e **senha criptografada**.

## 2. Estrutura do projeto (0:30 – 1:30)
- **3 Activities**: `MainActivity`, `CadastroActivity` (cadastro e edição), `DetalheActivity`.
- **4 Fragmentos**: `LoginFragment` (novo), `CategoriasFragment` (Spinner), `ListaFragment` (ListView), `GaleriaFragment` (GridView).
- Camadas: Entidades/DAOs → `AppDatabase` → `Repositorio` → ViewModels (`SessaoViewModel`, `InstrumentoViewModel`) → telas. A tela nunca acessa o DAO diretamente.

## 3. Banco de dados Room (1:30 – 3:30)
- `AppDatabase.java`: 4 entidades — `Usuario`, `Sessao`, `Categoria`, `Instrumento`.
- **1:N**: `Instrumento.java` tem `@ForeignKey` para `Categoria`; `CategoriaComInstrumentos.java` usa `@Embedded` + `@Relation`.
- **1:1**: `Sessao.java` usa `usuarioId` como chave primária **e** estrangeira (um usuário tem no máximo uma sessão); `SessaoComUsuario.java` com `@Relation`.
- **Mídias**:
  - `Usuario.foto` é `byte[]` (BLOB).
  - `Instrumento.imagemPath` / `audioPath` guardam **só a String** do caminho (`android.resource://.../drawable/img_violao`).
- `SeedDados.java`: popula o banco na primeira abertura (callback `onCreate`), substituindo as listas estáticas da Avaliação 1.

## 4. Criptografia de senha (3:30 – 4:15)
- `SenhaUtil.java`: salt aleatório (`SecureRandom`) + hash **SHA-256**.
- `Repositorio.definirSenha(...)` é chamado no cadastro e na alteração. No login, `SenhaUtil.verificar(...)` recalcula o hash e compara com o salvo.

## 5. Arquitetura reativa (4:15 – 5:30)
- `SessaoDao.getSessaoAtiva()` devolve `LiveData<SessaoComUsuario>`.
- `MainActivity.aplicarSessao(...)` é o **Observer**:
  - Com sessão: nome e avatar na Toolbar, BottomNavigation visível, vai para os fragmentos de conteúdo.
  - Sem sessão: esconde a navegação e mostra o `LoginFragment`.
- O `LoginFragment` **não navega sozinho**: ele só grava a sessão, e o Observer reage.
- `InstrumentoViewModel` compartilhado: o Spinner troca a categoria, e Lista e Galeria recebem os dados do Room via `MediatorLiveData`.
- Adapters (`InstrumentoAdapter`, `GaleriaAdapter`) leem os campos da entidade e carregam a imagem com `setImageURI(Uri.parse(imagemPath))`.

## 6. Demonstração no emulador (5:30 – 9:15)
1. Abrir o app: só a tela de login, **sem** a navegação inferior e sem Editar/Sair no menu.
2. **Criar conta**: tirar foto com a câmera, preencher nome/e-mail/senha e salvar.
3. Login com **senha errada** (mensagem de erro) e depois com a senha certa: a navegação é liberada, com avatar e nome na Toolbar.
4. Spinner: trocar de Cordas para Sopro e mostrar a Lista e a Galeria filtradas.
5. Abrir um instrumento, tocar, pausar e parar o áudio (MediaPlayer lendo o caminho salvo no Room).
6. Menu > **Editar perfil**: trocar a foto e o nome, salvar. A Toolbar atualiza na hora, sem reiniciar.
7. Fechar e reabrir o app: continua logado (sessão no Room).
8. Menu > **Sair**: volta ao login e a navegação some.
9. (Opcional) **App Inspection > Database Inspector**: mostrar as 4 tabelas, o `senhaHash` (não é a senha), a `foto` em BLOB e os caminhos em String.

## 7. Encerramento (9:15 – 10:00)
- Recapitular os requisitos atendidos: Room com 4 tabelas e relações 1:N e 1:1, ViewModel + LiveData, login e logout com sessão, câmera, criptografia e mídias como caminho.
