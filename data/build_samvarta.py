# -*- coding: utf-8 -*-
"""Build data/samvarta.json + compressed images from the Saṃvarta Stavaḥ PDF.

Source: /home/sasha/.openclaw/media/inbound/samvarta_stavah---47215fcd-13e0-4d25-9945-079f02a4b032.pdf
Text: Sthaneshwar Timalsina, © Vimarsha Foundation 2021.

Structure:
  p3   credits/title
  p5-11  preface (intro paragraphs)
  p12  opening verse (unnumbered)
  p13  MORNING header   | verses 1-9   (one per page, p15-31)
  p33  MIDDAY header    | verses 10-27 (two per page, p34-50)
  p53  EVENING header   | verses 28-37 (two per page, p55-63)
  p65  MIDNIGHT header  | verses 38-46 (p66,68,70,72,74,76,78)
  p32/52/64 unnumbered transition verses between sections
  p80  colophon + closing verse
Images: photographs on facing pages; junk 90-byte placeholders excluded.
"""
import json, re, os
import pymupdf

PDF = '/home/sasha/.openclaw/media/inbound/samvarta_stavah---47215fcd-13e0-4d25-9945-079f02a4b032.pdf'
IMG_SRC = '/home/sasha/projects/sivabodha-app/extract/samvarta'
ASSET_DIR = '/home/sasha/projects/sivabodha-app/app/src/main/assets'
OUT = '/home/sasha/projects/sivabodha-app/data/samvarta.json'

doc = pymupdf.open(PDF)

# ---------- text helpers ----------
def page_lines(pnum):  # pnum = 1-based pdf page
    d = doc[pnum - 1].get_text("dict")
    out = []
    for block in d["blocks"]:
        if block["type"] != 0:
            continue
        for line in block["lines"]:
            y = round(line["bbox"][1], 1)
            text = ""
            size = 0.0
            fonts = set()
            for s in line["spans"]:
                text += s["text"]
                fonts.add(s["font"])
                size = max(size, s["size"])
            out.append({"y": y, "text": text, "fonts": fonts, "size": size})
    return out

def classify(line):
    fonts = line["fonts"]
    t = line["text"].strip()
    if not t:
        return "blank"
    if any(f.startswith("NotoSansDevanagari") for f in fonts):
        return "deva"
    # Quintessential: section headers (words, ~24-32pt) vs drop caps (single letter, ~45pt)
    if any(f.startswith("Quintessential") for f in fonts):
        if len(t) <= 2 and line["size"] > 30:
            return "dropcap"
        return "header"
    if any(f.startswith("OpenSans-Italic") or f.startswith("NotoSansBengali") or f.startswith("NotoSans-Italic") for f in fonts):
        return "translit"
    if any(f.startswith("OpenSans-Light") for f in fonts):
        return "english"
    if any(f.startswith("OpenSans-Regular") for f in fonts):
        return "roman_num"
    return "other"

# ---------- text cleanup ----------
MATRA_DOUBLES = [
    ('ाा', 'ा'), ('ेे', 'े'), ('ोो', 'ो'), ('ूू', 'ू'), ('ृृ', 'ृ'),
    ('ैै', 'ै'), ('ंं', 'ं'), ('ीी', 'ी'), ('ुु', 'ु'), ('ौौ', 'ौ'),
    ('ँँ', 'ँ'), ('ःः', 'ः'), ('््', '्'),
]

def clean_deva(s):
    s = s.replace('\u200b', '')
    for a, b in MATRA_DOUBLES:
        s = s.replace(a, b)
    # consonant + virāma + vowel-sign  ->  consonant + vowel-sign  (e.g. ध््औ→धौ, भ्ऐ→भै)
    s = re.sub(r'([क-ह])्([ा-ौ])', r'\1\2', s)
    s = s.replace('|', '।')
    # danda spacing (but keep ॥N॥ markers tight)
    s = s.replace('।', '। ')
    s = re.sub(r'॥\s*([०-९]+)\s*॥', r'॥\1॥', s)
    s = re.sub(r'\s+', ' ', s)
    return s.strip()

def clean_translit(s):
    s = s.replace('\u200b', '')
    s = re.sub(r'॥\s*(\d+)\s*॥', r'॥\1॥', s)
    s = re.sub(r'\s+', ' ', s)
    return s.strip()

def clean_english(s):
    s = s.replace('\u200b', '')
    s = re.sub(r'\s+', ' ', s)
    return s.strip()

# known text-layer errors (missing matra etc.)
DEVA_FIXES = {
    'निशानथोदयात्': 'निशानाथोदयात्',
}

# ---------- page extraction ----------
def extract_page(pnum):
    """Return (deva_raw_lines, translit_raw_lines, english_raw_lines, dropcap)."""
    lines = page_lines(pnum)
    deva, translit, engl, dropcap = [], [], [], ""
    for ln in lines:
        c = classify(ln)
        if c == "deva":
            deva.append(ln["text"].strip())
        elif c in ("translit", "roman_num"):
            t = ln["text"].strip()
            if t:
                translit.append(t)
        elif c == "english":
            t = ln["text"].strip()
            if t:
                engl.append(t)
        elif c == "dropcap":
            dropcap = ln["text"].strip()
    return deva, translit, engl, dropcap

def join_deva(lines):
    return clean_deva("".join(lines))

def join_translit(lines):
    out = ""
    for ln in lines:
        if out.endswith("-"):
            out = out[:-1] + ln          # hyphen line-break: join directly
        elif out:
            out += " " + ln
        else:
            out = ln
    return clean_translit(out)

def join_english(lines, dropcap):
    en = clean_english(" ".join(lines))
    if dropcap:
        if dropcap == "I" and not en.startswith("n "):
            en = "I " + en                # pronoun drop cap: "I bow..."
        elif dropcap == "I":
            en = "I" + en                 # first letter of "In the temple..."
        elif en[:1].isupper():
            en = dropcap + " " + en       # vocative drop cap: "O You..."
        else:
            en = dropcap + en             # missing first letter: "V" + "ictorious"
    return en

def split_by_markers(text, marker_re):
    """Split a page's devanagari/translit into per-verse segments, markers included."""
    segs = []
    start = 0
    for m in re.finditer(marker_re, text):
        segs.append(text[start:m.end()].strip())
        start = m.end()
    tail = text[start:].strip()
    if tail:
        if segs:
            segs[-1] += " " + tail
        else:
            segs.append(tail)
    return segs

# ---------- section/verse page maps ----------
SECTIONS = [
    {"start": 13, "name": "प्रातःसवनम्", "name_roman": "Prātaḥ-savana", "subtitle_en": "Morning Pressing (of Soma)", "verses": (1, 9)},
    {"start": 33, "name": "माध्यन्दिनसवनम्", "name_roman": "Mādhyaṃdina-savana", "subtitle_en": "Midday Pressing (of Soma)", "verses": (10, 27)},
    {"start": 53, "name": "सायंसवनम्", "name_roman": "Sāyaṃ-savana", "subtitle_en": "Evening Pressing (of Soma)", "verses": (28, 37)},
    {"start": 65, "name": "निशीथसवनम्", "name_roman": "Niśītha-savana", "subtitle_en": "Midnight Pressing (of Soma)", "verses": (38, 46)},
]

VERSE_PAGE = {
    1: 15, 2: 17, 3: 19, 4: 21, 5: 23, 6: 25, 7: 27, 8: 29, 9: 31,
    10: 34, 11: 34, 12: 36, 13: 36, 14: 38, 15: 38, 16: 40, 17: 40,
    18: 42, 19: 42, 20: 44, 21: 44, 22: 46, 23: 46, 24: 48, 25: 48,
    26: 50, 27: 50, 28: 55, 29: 55, 30: 57, 31: 57, 32: 59, 33: 59,
    34: 61, 35: 61, 36: 63, 37: 63, 38: 66, 39: 68, 40: 68, 41: 70,
    42: 70, 43: 72, 44: 74, 45: 76, 46: 78,
}

SPLIT_MARKERS = {
    34: "of your own form.",
    36: "sixfold bliss.",
    38: "pristine rays of consciousness.",
    40: "is reflexivity.",
    42: "self-reflexivity.",
    44: "varied forms.",
    46: "gesture of boons.",
    48: "and Ratiśekhara",
    50: "Kālasaṃkarṣiṇī.",
    55: "form of sentience.",
    57: "destroyer of duality.",
    59: "as well as ambrosia].",
    61: "cycle of creation.",
    63: "Lord Samvarta!",
    68: "your recognition.",
    70: "phonemes and mantras.",
}

def split_english(pnum, english):
    marker = SPLIT_MARKERS.get(pnum)
    if marker is None:
        return english, ""
    idx = english.find(marker)
    if idx == -1:
        raise SystemExit(f"SPLIT MARKER NOT FOUND on p{pnum}: {marker!r} in {english[:150]!r}")
    cut = idx + len(marker)
    first = english[:cut]
    second = english[cut:].lstrip()
    if second.startswith(","):
        second = second[1:].lstrip()
    if second and second[0].islower():
        second = second[0].upper() + second[1:]
    return first, second

# ---------- images ----------
def list_images():
    imgs = {}
    for f in os.listdir(IMG_SRC):
        m = re.match(r'img_p(\d+)_(\d+)_(\d+)\.png$', f)
        if not m:
            continue
        p, idx, y = int(m.group(1)), int(m.group(2)), int(m.group(3))
        sz = os.path.getsize(os.path.join(IMG_SRC, f))
        if sz < 5000:
            continue
        imgs.setdefault(p, []).append((idx, y, sz, f))
    for p in imgs:
        imgs[p].sort()
    return imgs

def compress_png(src, dst_jpg, max_w=1400, quality=82):
    pix = pymupdf.Pixmap(src)
    if pix.width > max_w:
        scale = max_w / pix.width
        pix = pymupdf.Pixmap(pix, int(pix.width * scale), int(pix.height * scale))
    if pix.alpha:
        pix = pymupdf.Pixmap(pix, 0)
    data = pix.tobytes("jpeg", jpg_quality=quality)
    with open(dst_jpg, "wb") as f:
        f.write(data)
    return os.path.getsize(dst_jpg)

IMGS = list_images()

# ---------- build verses ----------
def build_verses():
    """Extract all 46 numbered verses from their pages."""
    verses = {}
    by_page = {}
    for n in range(1, 47):
        by_page.setdefault(VERSE_PAGE[n], []).append(n)

    for p, ns in sorted(by_page.items()):
        deva_lines, translit_lines, engl_lines, dropcap = extract_page(p)
        deva_full = join_deva(deva_lines)
        translit_full = join_translit(translit_lines)
        english_full = join_english(engl_lines, dropcap)

        deva_segs = split_by_markers(deva_full, r'॥[०-९]+॥')
        tr_segs = split_by_markers(translit_full, r'॥\s*\d+\s*॥')

        if len(ns) == 1:
            en_segs = [english_full]
        else:
            en_segs = list(split_english(p, english_full))
            if len(en_segs) == 1:
                en_segs.append("")
        for i, n in enumerate(ns):
            verses[n] = {
                "n": n,
                "devanagari": DEVA_FIXES.get(deva_segs[i], deva_segs[i]) if i < len(deva_segs) else "",
                "transliteration": tr_segs[i] if i < len(tr_segs) else "",
                "translation": en_segs[i] if i < len(en_segs) else "",
            }
    return verses

# ---------- intro (preface) ----------
def build_intro():
    paras = []
    cur = []
    for p in range(5, 12):
        for ln in page_lines(p):
            t = ln["text"].strip()
            if not t:
                continue
            c = classify(ln)
            if c in ("other", "header", "roman_num") or t == "P R E F A C E":
                continue
            leading = len(ln["text"]) - len(ln["text"].lstrip(" "))
            if leading >= 3 and cur:
                paras.append(clean_english(" ".join(cur)))
                cur = []
            cur.append(t)
    if cur:
        paras.append(clean_english(" ".join(cur)))
    return [p for p in paras if p and p != "P R E F A C E"]

# ---------- unnumbered verses (hand-cleaned deva/translit, English from PDF) ----------
def simple_verse(p, deva_override=None, translit_override=None):
    deva_lines, translit_lines, engl_lines, dropcap = extract_page(p)
    deva = join_deva(deva_lines)
    if deva_override:
        deva = deva_override
    for k, v in DEVA_FIXES.items():
        deva = deva.replace(k, v)
    translit = translit_override or join_translit(translit_lines)
    return {
        "n": 0,
        "devanagari": deva,
        "transliteration": translit,
        "translation": join_english(engl_lines, dropcap),
    }

OPENING = simple_verse(
    12,
    translit_override="somasūryamahāsandhau kiñcinmātrarajaḥsthitam । śuddhasattvātmakaṃ bālaṃ vande uṣasi bhairavam ॥",
)
TRANSITIONS = {
    0: simple_verse(
        32,
        translit_override="pracaṇḍe sthitimārtaṇḍe kaumāryaṅkasthabhairavam । praṇamāmīśvaraṃ haṃsaṃ sahasrārkasamaprabham ॥",
    ),
    1: simple_verse(
        52,
        translit_override="bhānāv astaṃgate śūnye niśānāthodayāt purā । prauḍhāṅkastham ahaṃ vande bhairavaṃ pīṭhanāyakam ॥",
    ),
    2: simple_verse(
        64,
        translit_override="parāhlādarasodrekajagadānandakāraṇam । vande somarasāpluṣṭabhairavaṃ bhayabhañjanam ॥",
    ),
}

def build_colophon():
    deva_lines, translit_lines, engl_lines, dropcap = extract_page(80)
    deva_full = join_deva(deva_lines)
    # colophon page: iti-line (deva+translit+english) then closing verse (deva+english)
    if "संवर्तस्तवः" in deva_full:
        iti_deva = "इति स्थानेश्वरमुखोद्गीर्णः संवर्तस्तवः।"
        verse_deva = deva_full.replace(iti_deva, "").strip()
        # drop stray duplicate text if any
        verse_deva = verse_deva.replace("संवर्तस्तवः।", "", 1)
    else:
        iti_deva, verse_deva = "", deva_full
    # the colophon's English lines use OpenSans-Regular (roman_num bucket in
    # extract_page), so grab them from the raw page here.
    iti_en = ""
    for ln in page_lines(80):
        t = ln["text"].strip()
        if t.startswith("Thus completes"):
            iti_en = clean_english(" ".join(
                x["text"].strip() for x in page_lines(80)
                if x["text"].strip().startswith(("Thus", "articulated"))))
            break
    verse_en = join_english(engl_lines, dropcap)
    if iti_en in verse_en:
        verse_en = verse_en.replace(iti_en, "").strip()
    iti_translit = "iti sthāneśvaramukhodgīrṇaḥ saṃvartastavaḥ ।"
    return {
        "note": iti_deva,
        "note_roman": iti_translit,
        "note_en": iti_en,
        "devanagari": verse_deva,
        "transliteration": "vakrāṅkanilayaṃ devaṃ mālinīmātṛkāvapuḥ । saṃvartaṃ satataṃ vande sṛṣṭyunmukhakuleśvaram ॥",
        "translation": verse_en,
    }

# ---------- main ----------
def main():
    verses = build_verses()
    colophon = build_colophon()

    sections = []
    for i, sec in enumerate(SECTIONS):
        v_start, v_end = sec["verses"]
        vs = [verses[n] for n in range(v_start, v_end + 1)]
        sections.append({
            "name": sec["name"],
            "name_roman": sec["name_roman"],
            "subtitle_en": sec["subtitle_en"],
            "image": f"samvarta_p{sec['start']}_0.jpg",
            "verses": vs,
            "closing": TRANSITIONS.get(i),
        })

    # images per verse: facing page = p-1 for odd verse pages, p+1 for even
    vp = {}
    for v, p in VERSE_PAGE.items():
        vp.setdefault(p, []).append(v)
    for p, ns in sorted(vp.items()):
        fp = p - 1 if p % 2 == 1 else p + 1
        imgs = IMGS.get(fp)
        if not imgs:
            continue
        if len(ns) == 1:
            pick = max(imgs, key=lambda t: t[2])
            verses[ns[0]]["image"] = f"samvarta_p{fp}_{pick[0]}.jpg"
        elif len(imgs) == 1:
            verses[ns[0]]["image"] = f"samvarta_p{fp}_{imgs[0][0]}.jpg"
            verses[ns[1]]["image"] = f"samvarta_p{fp}_{imgs[0][0]}.jpg"
        else:
            verses[ns[0]]["image"] = f"samvarta_p{fp}_{imgs[0][0]}.jpg"
            verses[ns[1]]["image"] = f"samvarta_p{fp}_{imgs[1][0]}.jpg"

    data = {
        "title": "संवर्तस्तवः",
        "title_roman": "Saṃvarta Stavaḥ",
        "subtitle": "Hymn to Saṃvarta Bhairava",
        "author": "Text and commentary by Śaivācārya Sthaneshwar Timalsina",
        "credits": "© Vimarsha Foundation, San Diego, 2021 · Photographs from Wikimedia Commons (CC-BY-SA)",
        "intro": build_intro(),
        "opening": OPENING,
        "sections": sections,
        "colophon": colophon,
    }

    with open(OUT, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=1)

    # copy + compress images (remove stale samvarta_*.jpg first)
    for old in os.listdir(ASSET_DIR):
        if old.startswith("samvarta_") and old.endswith(".jpg"):
            os.remove(os.path.join(ASSET_DIR, old))
    used = set()
    for sec in sections:
        used.add(sec["image"])
        for v in sec["verses"]:
            if v.get("image"):
                used.add(v["image"])
    copied = []
    for fp, imgs in IMGS.items():
        for idx, y, sz, fname in imgs:
            asset = f"samvarta_p{fp}_{idx}.jpg"
            if asset not in used:
                continue
            size = compress_png(os.path.join(IMG_SRC, fname), os.path.join(ASSET_DIR, asset))
            copied.append((asset, size))

    # verification output
    print(f"verses: {len(verses)} | sections: {len(sections)} | intro paras: {len(data['intro'])}")
    print("images copied:", len(copied), f"({sum(s for _,s in copied)//1024}KB)")
    missing = [n for n, v in verses.items() if not v["translation"] or not v["devanagari"]]
    print("missing fields:", missing if missing else "none")
    for n in [1, 10, 11, 38, 46]:
        v = verses[n]
        print(f"\nv{n}: {v['devanagari'][:60]}")
        print(f"  TR: {v['transliteration'][:60]}")
        print(f"  EN: {v['translation'][:80]}")
        print(f"  IMG: {v.get('image')}")
    print("\nopening:", OPENING["translation"][:80])
    print("colophon note:", colophon["note"], "|", colophon["note_en"][:60])
    print("colophon verse:", colophon["devanagari"][:60], "|", colophon["translation"][:80])

if __name__ == "__main__":
    main()
