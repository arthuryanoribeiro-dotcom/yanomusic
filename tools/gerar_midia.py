# -*- coding: utf-8 -*-
"""Gera a midia ORIGINAL do app (res/raw/*.mp3, res/drawable/*.png, mipmap/ic_launcher).

Nada aqui e baixado ou copiado: os audios sao sintetizados por DSP (numpy) e as
imagens sao desenhadas por primitivas geometricas (PIL). Isso atende a regra do
enunciado que proibe usar os arquivos fornecidos em aula.

Uso:  python tools/gerar_midia.py
"""
import os
import numpy as np
from PIL import Image, ImageDraw

SR = 44100
RES = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "app", "src", "main", "res")
RAW = os.path.join(RES, "raw")
DRW = os.path.join(RES, "drawable")
rng = np.random.default_rng(20260901)

# --------------------------------------------------------------------------
# Sintese de audio
# --------------------------------------------------------------------------

def env(n, atk=0.01, dec=0.25, sus=0.55, rel=0.35):
    """Envelope ADSR simples, normalizado para n amostras."""
    a, d, r = int(n * atk), int(n * dec), int(n * rel)
    s = max(1, n - a - d - r)
    return np.concatenate([
        np.linspace(0, 1, max(1, a)),
        np.linspace(1, sus, max(1, d)),
        np.full(s, sus),
        np.linspace(sus, 0, max(1, r)),
    ])[:n]


def karplus(freq, dur, decay=0.996, bright=0.5):
    """Karplus-Strong: corda dedilhada/friccionada. Atualiza a linha de atraso
    em blocos de um periodo, equivalente ao loop amostra-a-amostra e ordens de
    grandeza mais rapido em numpy."""
    n = int(SR * dur)
    N = max(2, int(SR / freq))
    buf = rng.uniform(-1, 1, N)
    out = np.empty(n)
    pos = 0
    while pos < n:
        k = min(N, n - pos)
        out[pos:pos + k] = buf[:k]
        buf = decay * (bright * buf + (1 - bright) * np.roll(buf, -1))
        pos += k
    return out * env(n, atk=0.002, dec=0.4, sus=0.35, rel=0.5)


def sopro(freq, dur, harmonics, breath=0.03, vib=5.0, vib_depth=0.005):
    """Sintese aditiva com vibrato e ruido de sopro."""
    n = int(SR * dur)
    t = np.arange(n) / SR
    f = freq * (1 + vib_depth * np.sin(2 * np.pi * vib * t))
    ph = 2 * np.pi * np.cumsum(f) / SR
    y = np.zeros(n)
    for k, amp in enumerate(harmonics, start=1):
        y += amp * np.sin(k * ph)
    y += breath * rng.normal(0, 1, n) * np.exp(-3 * t)
    return y * env(n, atk=0.08, dec=0.15, sus=0.8, rel=0.25)


def percussao(freq, dur, ruido=0.6, parciais=(1.0, 2.76, 5.40), amort=9.0):
    """Burst de ruido + parciais inarmonicas com decaimento exponencial."""
    n = int(SR * dur)
    t = np.arange(n) / SR
    y = ruido * rng.normal(0, 1, n) * np.exp(-amort * 2.5 * t)
    for i, p in enumerate(parciais):
        y += (0.8 / (i + 1)) * np.sin(2 * np.pi * freq * p * t) * np.exp(-amort * (1 + 0.6 * i) * t)
    return y


def nota(semitom, base=220.0):
    return base * (2 ** (semitom / 12.0))


def montar(voz, base, sequencia, total=4.0, **kw):
    """Toca uma sequencia de (semitom, inicio, duracao) numa trilha de `total` s."""
    trilha = np.zeros(int(SR * total))
    for semitom, inicio, dur in sequencia:
        som = voz(nota(semitom, base), dur, **kw)
        i = int(SR * inicio)
        fim = min(len(trilha), i + len(som))
        trilha[i:fim] += som[:fim - i]
    pico = np.max(np.abs(trilha))
    return trilha / pico * 0.89 if pico > 0 else trilha


def gravar_mp3(caminho, sinal):
    import lameenc
    enc = lameenc.Encoder()
    enc.set_bit_rate(128)
    enc.set_in_sample_rate(SR)
    enc.set_channels(1)
    enc.set_quality(2)
    pcm = (np.clip(sinal, -1, 1) * 32767).astype("<i2").tobytes()
    with open(caminho, "wb") as f:
        f.write(enc.encode(pcm))
        f.write(enc.flush())


ARPEJO = [(0, 0.0, 1.4), (4, 0.55, 1.4), (7, 1.1, 1.5), (12, 1.65, 1.7), (7, 2.5, 1.4)]
SUSTENIDO = [(0, 0.05, 1.1), (5, 1.15, 1.0), (7, 2.1, 1.5)]
BATIDA = [(0, 0.0, 0.7), (0, 0.55, 0.5), (0, 1.05, 0.7), (0, 1.75, 0.5), (0, 2.25, 1.1)]

# nome -> (familia, frequencia base em Hz, parametros do timbre)
VOZES = {
    "violao":         ("corda", 165.0, dict(decay=0.9965, bright=0.55)),
    "violino":        ("corda", 392.0, dict(decay=0.9988, bright=0.32)),
    "harpa":          ("corda", 262.0, dict(decay=0.9975, bright=0.70)),
    "viola_caipira":  ("corda", 220.0, dict(decay=0.9958, bright=0.62)),
    "cavaquinho":     ("corda", 330.0, dict(decay=0.9940, bright=0.72)),
    "contrabaixo":    ("corda", 82.0,  dict(decay=0.9990, bright=0.25)),
    "bandolim":       ("corda", 294.0, dict(decay=0.9948, bright=0.68)),

    "flauta":         ("sopro", 523.0, dict(harmonics=(1.0, 0.12, 0.05), breath=0.06, vib=5.5)),
    "trompete":       ("sopro", 349.0, dict(harmonics=(1.0, 0.7, 0.45, 0.3, 0.18), breath=0.02, vib=4.5)),
    "clarinete":      ("sopro", 233.0, dict(harmonics=(1.0, 0.02, 0.55, 0.03, 0.3), breath=0.03, vib=4.0)),
    "saxofone":       ("sopro", 277.0, dict(harmonics=(1.0, 0.55, 0.5, 0.28, 0.22), breath=0.05, vib=5.0)),
    "trombone":       ("sopro", 146.0, dict(harmonics=(1.0, 0.75, 0.5, 0.35, 0.2), breath=0.02, vib=3.5)),
    "tuba":           ("sopro", 87.0,  dict(harmonics=(1.0, 0.6, 0.3, 0.15), breath=0.02, vib=3.0)),
    "gaita":          ("sopro", 440.0, dict(harmonics=(1.0, 0.4, 0.35, 0.15), breath=0.08, vib=6.5)),

    "bateria":        ("perc", 90.0,   dict(ruido=0.85, parciais=(1.0, 1.9, 3.2), amort=11.0)),
    "pandeiro":       ("perc", 400.0,  dict(ruido=1.0, parciais=(1.0, 3.7, 6.1), amort=16.0)),
    "xilofone":       ("perc", 880.0,  dict(ruido=0.10, parciais=(1.0, 3.01, 6.02), amort=7.0)),
    "atabaque":       ("perc", 110.0,  dict(ruido=0.45, parciais=(1.0, 2.1, 3.4), amort=8.0)),
    "triangulo":      ("perc", 1760.0, dict(ruido=0.15, parciais=(1.0, 2.76, 5.4, 8.9), amort=2.2)),
    "agogo":          ("perc", 740.0,  dict(ruido=0.12, parciais=(1.0, 2.4, 4.5), amort=4.5)),
    "tambor":         ("perc", 140.0,  dict(ruido=0.7, parciais=(1.0, 1.6, 2.9), amort=10.0)),
}

VOZ_FN = {"corda": karplus, "sopro": sopro, "perc": percussao}
SEQ = {"corda": ARPEJO, "sopro": SUSTENIDO, "perc": BATIDA}

# --------------------------------------------------------------------------
# Desenho das imagens
# --------------------------------------------------------------------------

# (fundo escuro, fundo medio, tom claro, destaque) - espelha res/values/colors.xml
PALETA = {
    "corda": ((38, 24, 12), (122, 63, 18), (240, 187, 92), (255, 236, 199)),
    "sopro": ((6, 32, 38), (13, 92, 104), (86, 205, 214), (222, 250, 252)),
    "perc":  ((30, 12, 40), (92, 36, 122), (206, 124, 233), (247, 228, 255)),
}
W = 512


def fundo(familia):
    escuro, medio, _, _ = PALETA[familia]
    grad = np.linspace(0, 1, W)[:, None]
    cor = np.array(escuro) + (np.array(medio) - np.array(escuro)) * grad
    img = Image.fromarray(np.repeat(cor[:, None, :], W, axis=1).astype(np.uint8), "RGB")
    d = ImageDraw.Draw(img, "RGBA")
    d.ellipse([-120, 300, 300, 700], fill=(255, 255, 255, 16))
    d.ellipse([280, -140, 660, 240], fill=(255, 255, 255, 12))
    return img, d


def violao_like(d, c1, c2, corpo=(150, 200, 362, 460), largura=1.0, casas=6, arco=False):
    """Caixa (duas elipses sobrepostas) + braco + boca + cordas.

    `arco=True` troca a boca redonda por duas aberturas em "f" e adiciona o
    arco, que e o que distingue visualmente violino/contrabaixo do violao."""
    x0, y0, x1, y1 = corpo
    cx = (x0 + x1) / 2
    lg = (x1 - x0) * largura / 2
    alto = (y1 - y0)
    d.ellipse([cx - lg, y0, cx + lg, y0 + alto * 0.62], fill=c1)
    d.ellipse([cx - lg * 1.18, y0 + alto * 0.32, cx + lg * 1.18, y1], fill=c1)
    if arco:
        for lado in (-1, 1):
            fx = cx + lado * lg * 0.52
            d.line([(fx, y0 + alto * 0.44), (fx - lado * 10, y0 + alto * 0.62),
                    (fx, y0 + alto * 0.80)], fill=(20, 12, 8), width=9, joint="curve")
            d.ellipse([fx - 11, y0 + alto * 0.40, fx + 7, y0 + alto * 0.48], fill=(20, 12, 8))
            d.ellipse([fx - 7, y0 + alto * 0.76, fx + 11, y0 + alto * 0.84], fill=(20, 12, 8))
        d.line([(x1 + 46, y0 - 30), (x0 - 30, y1 + 20)], fill=(246, 240, 230), width=7)
        d.line([(x1 + 40, y0 - 22), (x0 - 24, y1 + 28)], fill=c2, width=5)
    else:
        d.ellipse([cx - lg * 0.30, y0 + alto * 0.46, cx + lg * 0.30, y1 - alto * 0.30],
                  fill=(20, 12, 8))
    d.rectangle([cx - 16, 62, cx + 16, y0 + alto * 0.28], fill=c2)
    d.rounded_rectangle([cx - 30, 30, cx + 30, 96], 12, fill=c1)
    for i in range(4):
        x = cx - 12 + i * 8
        d.line([(x, 60), (x, y1 - alto * 0.22)], fill=(250, 250, 250), width=2)
    for i in range(casas):
        y = 96 + i * ((y0 + alto * 0.28 - 96) / casas)
        d.line([(cx - 16, y), (cx + 16, y)], fill=(230, 220, 200), width=2)


def harpa(d, c1, c2):
    d.polygon([(110, 452), (150, 90), (402, 452)], fill=c1)
    d.polygon([(140, 430), (168, 132), (360, 430)], fill=(20, 12, 8))
    for i in range(11):
        k = i / 10
        x = 168 + (356 - 168) * k
        y = 132 + (428 - 132) * k
        d.line([(x, y), (150 + (398 - 150) * k, 448)], fill=c2, width=2)
    d.line([(104, 452), (408, 452)], fill=c2, width=18)


def sopro_metal(d, c1, c2, tubo_y=250, sino=1.0, valvulas=3):
    """Tubo + campana conica + valvulas."""
    d.polygon([(300, tubo_y - 90 * sino), (452, tubo_y - 130 * sino),
               (452, tubo_y + 130 * sino), (300, tubo_y + 90 * sino)], fill=c1)
    d.ellipse([424, tubo_y - 132 * sino, 480, tubo_y + 132 * sino], fill=c2)
    d.rounded_rectangle([70, tubo_y - 22, 320, tubo_y + 22], 22, fill=c1)
    d.ellipse([48, tubo_y - 34, 104, tubo_y + 34], fill=c2)
    for i in range(valvulas):
        x = 150 + i * 52
        d.rounded_rectangle([x, tubo_y - 92, x + 24, tubo_y - 18], 10, fill=c2)
        d.ellipse([x - 4, tubo_y - 104, x + 28, tubo_y - 80], fill=(250, 245, 235))


def sopro_tubo(d, c1, c2, furos=7):
    d.rounded_rectangle([64, 226, 452, 288], 30, fill=c1)
    d.rounded_rectangle([64, 226, 132, 288], 30, fill=c2)
    d.ellipse([432, 214, 476, 300], fill=c2)
    for i in range(furos):
        x = 168 + i * 38
        d.ellipse([x, 244, x + 20, 264], fill=(24, 16, 10))


def gaita(d, c1, c2):
    d.rounded_rectangle([70, 200, 442, 312], 22, fill=c1)
    d.rounded_rectangle([70, 200, 442, 236], 18, fill=c2)
    d.rounded_rectangle([70, 276, 442, 312], 18, fill=c2)
    for i in range(10):
        x = 92 + i * 36
        d.rounded_rectangle([x, 246, x + 24, 268], 6, fill=(22, 30, 34))


def tambor(d, c1, c2, aro=True, jingles=False, pes=False):
    d.ellipse([96, 176, 416, 336], fill=c2)
    d.rectangle([96, 256, 416, 356], fill=c1)
    d.ellipse([96, 276, 416, 436], fill=c1)
    d.ellipse([120, 190, 392, 322], fill=(246, 240, 230))
    if aro:
        d.ellipse([96, 176, 416, 336], outline=c2, width=14)
    if jingles:
        for i in range(8):
            ang = np.pi * (i / 7.0)
            x = 256 + 168 * np.cos(ang)
            y = 256 - 88 * np.sin(ang)
            d.ellipse([x - 16, y - 16, x + 16, y + 16], fill=(250, 226, 150), outline=c1, width=3)
    if pes:
        for x in (140, 372):
            d.line([(x, 400), (x + (30 if x < 256 else -30), 476)], fill=c2, width=14)


def xilofone(d, c1, c2):
    # c1/c2 sao os dois tons claros da paleta: barras alternadas precisam
    # contrastar com o fundo, que ja usa o tom medio.
    for i in range(7):
        w = 250 - i * 22
        y = 130 + i * 36
        d.rounded_rectangle([256 - w / 2, y, 256 + w / 2, y + 26], 8, fill=c2 if i % 2 else c1)
    d.line([(150, 120), (150, 400)], fill=c1, width=10)
    d.line([(362, 120), (362, 400)], fill=c1, width=10)
    d.line([(400, 430), (466, 300)], fill=(246, 240, 230), width=10)
    d.ellipse([446, 276, 490, 320], fill=c2)


def triangulo(d, c1, c2):
    d.polygon([(256, 108), (452, 424), (60, 424)], outline=c2, width=26)
    d.line([(300, 420), (452, 424)], fill=c1, width=30)
    d.line([(256, 60), (256, 108)], fill=(246, 240, 230), width=8)
    d.line([(120, 200), (200, 330)], fill=(246, 240, 230), width=10)


def agogo(d, c1, c2):
    d.polygon([(150, 150), (110, 330), (214, 330), (196, 150)], fill=c2)
    d.polygon([(320, 190), (292, 336), (382, 336), (368, 190)], fill=c1)
    d.ellipse([104, 306, 220, 356], fill=c1)
    d.ellipse([288, 314, 388, 358], fill=c2)
    d.line([(173, 150), (250, 90), (344, 190)], fill=c1, width=16)
    d.line([(410, 120), (466, 250)], fill=(246, 240, 230), width=10)


DESENHOS = {
    "violao":        lambda d, p: violao_like(d, p[1], p[2]),
    "violino":       lambda d, p: violao_like(d, p[1], p[2], corpo=(176, 214, 336, 430), largura=0.9, casas=0, arco=True),
    "harpa":         lambda d, p: harpa(d, p[1], p[2]),
    "viola_caipira": lambda d, p: violao_like(d, p[1], p[2], corpo=(166, 216, 346, 452), largura=0.92, casas=8),
    "cavaquinho":    lambda d, p: violao_like(d, p[1], p[2], corpo=(190, 250, 322, 434), largura=0.85, casas=5),
    "contrabaixo":   lambda d, p: violao_like(d, p[1], p[2], corpo=(132, 176, 380, 472), largura=1.06, casas=0, arco=True),
    "bandolim":      lambda d, p: violao_like(d, p[1], p[2], corpo=(180, 238, 332, 448), largura=1.0, casas=7),

    "flauta":        lambda d, p: sopro_tubo(d, p[1], p[2], furos=8),
    "trompete":      lambda d, p: sopro_metal(d, p[1], p[2], sino=1.0, valvulas=3),
    "clarinete":     lambda d, p: sopro_tubo(d, p[1], p[2], furos=6),
    "saxofone":      lambda d, p: sopro_metal(d, p[1], p[2], tubo_y=270, sino=1.15, valvulas=4),
    "trombone":      lambda d, p: sopro_metal(d, p[1], p[2], tubo_y=240, sino=1.2, valvulas=0),
    "tuba":          lambda d, p: sopro_metal(d, p[1], p[2], tubo_y=280, sino=1.3, valvulas=4),
    "gaita":         lambda d, p: gaita(d, p[1], p[2]),

    "bateria":       lambda d, p: tambor(d, p[1], p[2], pes=True),
    "pandeiro":      lambda d, p: tambor(d, p[1], p[2], jingles=True),
    "xilofone":      lambda d, p: xilofone(d, p[2], p[3]),
    "atabaque":      lambda d, p: tambor(d, p[1], p[2], aro=False),
    "triangulo":     lambda d, p: triangulo(d, p[1], p[2]),
    "agogo":         lambda d, p: agogo(d, p[1], p[2]),
    "tambor":        lambda d, p: tambor(d, p[1], p[2]),
}


def icone_app():
    """ic_launcher: nota musical estilizada sobre disco colorido."""
    img = Image.new("RGB", (432, 432), (13, 92, 104))
    d = ImageDraw.Draw(img, "RGBA")
    d.ellipse([26, 26, 406, 406], fill=(240, 187, 92))
    d.ellipse([120, 240, 232, 336], fill=(38, 24, 12))
    d.ellipse([236, 190, 348, 286], fill=(38, 24, 12))
    d.rectangle([210, 96, 232, 300], fill=(38, 24, 12))
    d.rectangle([326, 46, 348, 250], fill=(38, 24, 12))
    d.polygon([(210, 96), (348, 46), (348, 104), (210, 154)], fill=(38, 24, 12))
    for dpi, tam in [("mdpi", 48), ("hdpi", 72), ("xhdpi", 96), ("xxhdpi", 144), ("xxxhdpi", 192)]:
        alvo = os.path.join(RES, "mipmap-" + dpi)
        os.makedirs(alvo, exist_ok=True)
        img.resize((tam, tam), Image.LANCZOS).save(os.path.join(alvo, "ic_launcher.png"))


def main():
    for p in (RAW, DRW):
        os.makedirs(p, exist_ok=True)
    for nome, (familia, base, kw) in VOZES.items():
        trilha = montar(VOZ_FN[familia], base, SEQ[familia], **kw)
        gravar_mp3(os.path.join(RAW, "som_%s.mp3" % nome), trilha)
        img, d = fundo(familia)
        DESENHOS[nome](d, PALETA[familia])
        img.save(os.path.join(DRW, "img_%s.png" % nome), optimize=True)
        print("  gerado: som_%s.mp3 + img_%s.png" % (nome, nome))
    icone_app()
    print("OK -> %d audios e %d imagens em res/" % (len(VOZES), len(VOZES)))


if __name__ == "__main__":
    main()
