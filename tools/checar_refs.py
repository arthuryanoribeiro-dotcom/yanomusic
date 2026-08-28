# -*- coding: utf-8 -*-
"""Checagem estatica de recursos Android, sem precisar do SDK.

Confere que toda referencia usada no Java e nos XML existe de fato:
  @string/ @color/ @array/ @style/ @drawable/ @mipmap/ @menu/ @layout/ @id/
  R.string.* R.color.* R.array.* R.style.* R.drawable.* R.raw.* R.layout.*
  R.menu.* R.id.*
e que cada .java declara o package e a classe compativeis com seu caminho.

E a classe de erro que mais quebra build Android; rodar isto antes de abrir o
Android Studio evita quase todo "cannot find symbol" de recurso.

Uso:  python tools/checar_refs.py
"""
import os
import re
import sys
import glob
import xml.etree.ElementTree as ET

RAIZ = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..")
MAIN = os.path.join(RAIZ, "app", "src", "main")
RES = os.path.join(MAIN, "res")

TIPOS = ("string", "color", "array", "style", "drawable", "mipmap",
         "layout", "menu", "raw", "id")

# Nomes que vem do framework Android ou da biblioteca Material, nao do projeto.
EXTERNOS = {
    "appbar_scrolling_view_behavior", "transparent", "white", "black",
    "simple_spinner_item", "simple_spinner_dropdown_item", "home",
}

erros = []


def arquivos(padrao):
    return glob.glob(os.path.join(RES, padrao), recursive=True)


def ler(caminho):
    with open(caminho, encoding="utf-8") as f:
        return f.read()


# ---------------------------------------------------------------- definidos
definidos = {t: set() for t in TIPOS}

for values in arquivos("values*/*.xml"):
    for filho in ET.parse(values).getroot():
        nome = filho.get("name")
        if not nome:
            continue
        if filho.tag == "string":
            definidos["string"].add(nome)
        elif filho.tag == "color":
            definidos["color"].add(nome)
        elif filho.tag in ("string-array", "integer-array", "array"):
            definidos["array"].add(nome)
        elif filho.tag == "style":
            definidos["style"].add(nome)

# arquivos viram recursos pelo proprio nome
for pasta, tipo in [("drawable*", "drawable"), ("mipmap*", "mipmap"),
                    ("layout", "layout"), ("menu", "menu"), ("raw", "raw")]:
    for caminho in glob.glob(os.path.join(RES, pasta, "*")):
        if os.path.isfile(caminho):
            definidos[tipo].add(os.path.splitext(os.path.basename(caminho))[0])

# ids declarados com @+id/ em qualquer layout ou menu
for caminho in arquivos("layout/*.xml") + arquivos("menu/*.xml"):
    definidos["id"].update(re.findall(r'"@\+id/([A-Za-z0-9_]+)"', ler(caminho)))


# ------------------------------------------------------------- referenciados
# O (?<!android:) descarta @android:color/... e R.id.home do framework.
XML_REF = re.compile(r'"@(?!\+)(?<!android:)(' + "|".join(TIPOS) +
                     r')/([A-Za-z0-9_.]+)"')
JAVA_REF = re.compile(r'(?<!android\.)\bR\.(' + "|".join(TIPOS) +
                      r')\.([A-Za-z0-9_]+)\b')

alvos = (arquivos("**/*.xml") + [os.path.join(MAIN, "AndroidManifest.xml")]
         + glob.glob(os.path.join(MAIN, "java", "**", "*.java"), recursive=True))

usados = set()
for caminho in alvos:
    texto = ler(caminho)
    rel = os.path.relpath(caminho, RAIZ).replace("\\", "/")
    padrao = JAVA_REF if caminho.endswith(".java") else XML_REF
    for tipo, nome in padrao.findall(texto):
        usados.add((tipo, nome))
        # No Java a classe R troca "." por "_": o estilo Theme.GuiaInstrumentos
        # vira R.style.Theme_GuiaInstrumentos. Normaliza os dois lados.
        conhecidos = definidos[tipo]
        if caminho.endswith(".java"):
            conhecidos = {d.replace(".", "_") for d in conhecidos}
        if nome in conhecidos or nome in EXTERNOS:
            continue
        if tipo == "style" and nome.startswith(
                ("Theme.Material3", "Widget.", "TextAppearance.")):
            continue
        erros.append("%s: @%s/%s nao existe" % (rel, tipo, nome))

# ------------------------------------------------- pacote x caminho do .java
for caminho in glob.glob(os.path.join(MAIN, "java", "**", "*.java"), recursive=True):
    texto = ler(caminho)
    rel = os.path.relpath(caminho, RAIZ).replace("\\", "/")
    esperado = os.path.dirname(os.path.relpath(
        caminho, os.path.join(MAIN, "java"))).replace(os.sep, ".")
    pacote = re.search(r"^package\s+([\w.]+);", texto, re.M)
    if not pacote or pacote.group(1) != esperado:
        erros.append("%s: package errado (esperado %s)" % (rel, esperado))
    classe = os.path.splitext(os.path.basename(caminho))[0]
    if not re.search(r"\b(class|interface|enum)\s+%s\b" % classe, texto):
        erros.append("%s: nao declara a classe %s" % (rel, classe))

# ------------------------------------------------------------------ resumo
print("Recursos definidos: " +
      ", ".join("%s=%d" % (t, len(v)) for t, v in sorted(definidos.items())))
print("Referencias verificadas: %d" % len(usados))

orfaos = sorted(n for t in ("raw", "drawable") for n in definidos[t]
                if (t, n) not in usados and not n.startswith(("ic_", "fundo_")))
if orfaos:
    print("Aviso: recursos nunca referenciados: " + ", ".join(orfaos))

if erros:
    print("\nFALHOU com %d problema(s):" % len(erros))
    for e in erros:
        print("  - " + e)
    sys.exit(1)

print("\nOK: nenhuma referencia quebrada.")
