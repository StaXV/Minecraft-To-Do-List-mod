"""Generate the Memo mod's PNG icon."""

from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent


def rounded_rect(size: int, radius: int, fill: tuple[int, int, int, int], outline=None, outline_w=1):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((outline_w, outline_w, size - 1 - outline_w, size - 1 - outline_w),
                        radius=radius, fill=fill,
                        outline=outline, width=outline_w)
    return img


def save(img: Image.Image, rel: str):
    out = ROOT / "src" / "client" / "resources" / "assets" / "memo" / rel
    out.parent.mkdir(parents=True, exist_ok=True)
    img.save(out)
    print("wrote", out.relative_to(ROOT))


def make_icon():
    S = 128
    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    # Square (slightly rounded) dark background so the yellow check is clearly visible.
    bg = Image.new("RGBA", (S, S), (30, 30, 44, 255))
    mask = Image.new("L", (S, S), 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, S - 1, S - 1), radius=16, fill=255)
    bg.putalpha(mask)
    img.alpha_composite(bg)

    d = ImageDraw.Draw(img)
    # Upright, bold yellow checkmark.
    d.line([(30, 64), (56, 90), (100, 36)], fill=(255, 212, 0, 255), width=16, joint="curve")
    return img


if __name__ == "__main__":
    save(make_icon(), "icon.png")
    print("done")
