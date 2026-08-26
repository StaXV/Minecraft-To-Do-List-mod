"""Generate the Memo mod's PNG assets (icon + star sprites)."""

import math
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


def make_stars():
    def star_points(cx, cy, outer, inner):
        points = []
        for i in range(10):
            angle = math.radians(-90 + i * 36)
            radius = outer if i % 2 == 0 else inner
            points.append((cx + radius * math.cos(angle), cy + radius * math.sin(angle)))
        return points

    size = 16
    points = star_points(8, 8, 7, 3)
    unlit = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    ImageDraw.Draw(unlit).polygon(points, fill=(255, 255, 255, 255))
    save(unlit, "textures/gui/sprites/memo/star.png")

    lit = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    ImageDraw.Draw(lit).polygon(points, fill=(255, 140, 0, 255))
    save(lit, "textures/gui/sprites/memo/star_filled.png")


if __name__ == "__main__":
    save(make_icon(), "icon.png")
    make_stars()
    print("done")
