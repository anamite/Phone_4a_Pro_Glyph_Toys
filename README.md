# How to Add New Animations to GlyphGifToy

**Written for complete beginners — no prior Android experience needed.**

---

## Overview

The Glyph Matrix on your Nothing Phone (4a) Pro is a **25×25 grid of LEDs** — that's 625 tiny lights. Every animation works the same way:

> Your code draws a picture on a **25×25 pixel canvas** → that picture gets sent to the matrix → the LEDs light up.

Repeat this 20 times per second and you get a smooth animation.

---

## Where All the Code Lives

Open Android Studio and navigate to:

```
app/src/main/java/com/anand/glyphgiftoy/GifGlyphToyService.java
```

This is the **only file you need to edit** to add new animations.

---

## How the Animation System Works

The service keeps track of which animation is currently playing using one variable:

```java
private int mCurrentAnim = 0;
```

- `0` = Pulse (the default)
- `1` = Spinner
- `2` = Matrix Rain

When you tap a button, `mCurrentAnim` increases by 1 and wraps back to 0 after the last animation. The `startLoop()` method checks this number every 50ms and calls the matching animation function.

Here is the switch statement that decides which animation runs:

```java
switch (mCurrentAnim) {
    case 1:  frame = animSpinner(mTick); break;
    case 2:  frame = animRain();         break;
    default: frame = animPulse(mTick);   break;
}
```

**Adding a new animation = 3 steps:**

1. Write a new animation method
2. Add it to the switch statement
3. Update the total count

---

## Step-by-Step: Adding Your First Custom Animation

### Step 1 — Write the Animation Method

Every animation method must:
- Take no arguments (or `int tick` if it needs timing)
- Return a `Bitmap` object that is exactly **25×25 pixels**
- Draw something on that bitmap using black (`Color.BLACK`) and white (`Color.WHITE`)

Here is the simplest possible template — copy this and customize it:

```java
private Bitmap animMyCustom(int tick) {
    // Create a blank 25×25 black canvas
    Bitmap bmp = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(bmp);
    c.drawColor(Color.BLACK);   // fill background with black

    // Create a paint brush (white color)
    Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    p.setColor(Color.WHITE);

    // ★ YOUR DRAWING CODE GOES HERE ★

    return bmp;   // always return the bitmap at the end
}
```

### Step 2 — Add It to the Switch Statement

Find the `switch (mCurrentAnim)` block in `startLoop()` and add a new `case`. If you currently have 3 animations (cases 0, 1, 2), add `case 3`:

```java
switch (mCurrentAnim) {
    case 1:  frame = animSpinner(mTick); break;
    case 2:  frame = animRain();         break;
    case 3:  frame = animMyCustom(mTick); break;  // ← add this line
    default: frame = animPulse(mTick);   break;
}
```

### Step 3 — Update the Total Count

Find this line in `onStartCommand`:

```java
mCurrentAnim = (mCurrentAnim + 1) % 3;
```

Change `3` to `4` (or however many animations you now have total):

```java
mCurrentAnim = (mCurrentAnim + 1) % 4;
```

That's it. Hit ▶ Run and tap the button to cycle to your new animation.

---

## Drawing Reference — What You Can Draw

Everything is drawn on a coordinate grid where:
- Top-left corner = `(0, 0)`
- Bottom-right corner = `(24, 24)`
- Center = `(12, 12)`
- `SIZE` is the constant `25` already defined for you

### Draw a Dot / Filled Circle

```java
// Draw a circle at center, radius 5
c.drawCircle(12, 12, 5, p);
```

### Draw a Line

```java
// Line from top-left to bottom-right
c.drawLine(0, 0, 24, 24, p);
```

Set line thickness with:
```java
p.setStrokeWidth(2f);  // 2 pixels thick
```

### Draw a Rectangle

```java
// Rectangle from (2,2) to (22,22)
c.drawRect(2, 2, 22, 22, p);
```

For a hollow rectangle (just the border), add:
```java
p.setStyle(Paint.Style.STROKE);
p.setStrokeWidth(1f);
```

### Draw a Single Pixel

```java
// Light up the pixel at column 5, row 10
c.drawRect(5, 10, 6, 11, p);  // drawRect(x, y, x+1, y+1, p)
```

### Draw Text (A Letter or Number)

```java
p.setTextSize(10f);   // font size in pixels — keep small, canvas is only 25px!
c.drawText("A", 8, 16, p);   // text, x position, y position
```

---

## Making Things Move — Using `tick`

The `tick` variable increases by 1 every 50ms (about 20 times per second). Use it to make things animate over time.

### Make something move left-to-right

```java
private Bitmap animSlide(int tick) {
    Bitmap bmp = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(bmp);
    c.drawColor(Color.BLACK);
    Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    p.setColor(Color.WHITE);

    // x position cycles from 0 to 24 and back
    int x = tick % SIZE;
    c.drawCircle(x, 12, 3, p);   // dot slides across the middle

    return bmp;
}
```

### Make something blink

```java
// Blinks on for 10 ticks, off for 10 ticks
boolean isOn = (tick % 20) < 10;
if (isOn) {
    c.drawCircle(12, 12, 8, p);
}
```

### Make something rotate

```java
// angle increases with tick → things spin
double angle = Math.toRadians(tick * 6);  // 6° per tick = full rotation in 60 ticks
float endX = 12 + (float)(Math.cos(angle) * 10);
float endY = 12 + (float)(Math.sin(angle) * 10);
c.drawLine(12, 12, endX, endY, p);
```

---

## 5 Ready-to-Use Animation Examples

Paste any of these directly into `GifGlyphToyService.java`, then follow Steps 2 & 3 above.

### Animation A — Bouncing Ball

```java
private Bitmap animBounce(int tick) {
    Bitmap bmp = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(bmp);
    c.drawColor(Color.BLACK);
    Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    p.setColor(Color.WHITE);

    // Ball bounces up and down using a sine wave
    float y = 12 + (float)(Math.sin(tick * 0.2) * 10);
    c.drawCircle(12, y, 3, p);

    return bmp;
}
```

### Animation B — Expanding Rings

```java
private Bitmap animRings(int tick) {
    Bitmap bmp = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(bmp);
    c.drawColor(Color.BLACK);
    Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    p.setColor(Color.WHITE);
    p.setStyle(Paint.Style.STROKE);
    p.setStrokeWidth(1f);

    // Three rings at different phases — they expand outward
    for (int i = 0; i < 3; i++) {
        float radius = ((tick * 0.3f + i * 4) % 13);
        if (radius > 1) c.drawCircle(12, 12, radius, p);
    }

    return bmp;
}
```

### Animation C — Diagonal Wipe

```java
private Bitmap animWipe(int tick) {
    Bitmap bmp = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(bmp);
    c.drawColor(Color.BLACK);
    Paint p = new Paint();
    p.setColor(Color.WHITE);

    // Fill pixels where col + row < tick (modulo 50)
    int threshold = tick % 50;
    for (int row = 0; row < SIZE; row++) {
        for (int col = 0; col < SIZE; col++) {
            if (col + row < threshold) {
                c.drawRect(col, row, col + 1, row + 1, p);
            }
        }
    }

    return bmp;
}
```

### Animation D — Random Twinkle

```java
private Bitmap animTwinkle(int tick) {
    Bitmap bmp = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(bmp);
    c.drawColor(Color.BLACK);
    Paint p = new Paint();
    p.setColor(Color.WHITE);

    // Each pixel has a 15% chance to be on, changes every frame
    for (int row = 0; row < SIZE; row++) {
        for (int col = 0; col < SIZE; col++) {
            if (Math.random() < 0.15) {
                c.drawRect(col, row, col + 1, row + 1, p);
            }
        }
    }

    return bmp;
}
```

### Animation E — Scrolling Border

```java
private Bitmap animBorder(int tick) {
    Bitmap bmp = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(bmp);
    c.drawColor(Color.BLACK);
    Paint p = new Paint();
    p.setColor(Color.WHITE);

    // Draw a pixel that travels around the border of the matrix
    int perimeter = (SIZE - 1) * 4;
    int pos = tick % perimeter;
    int x, y;

    if (pos < SIZE) {           // top edge: left → right
        x = pos; y = 0;
    } else if (pos < SIZE * 2 - 1) {  // right edge: top → bottom
        x = SIZE - 1; y = pos - (SIZE - 1);
    } else if (pos < SIZE * 3 - 2) {  // bottom edge: right → left
        x = SIZE - 1 - (pos - (SIZE * 2 - 2)); y = SIZE - 1;
    } else {                    // left edge: bottom → top
        x = 0; y = SIZE - 1 - (pos - (SIZE * 3 - 3));
    }

    p.setStyle(Paint.Style.FILL);
    c.drawCircle(x, y, 2, p);

    return bmp;
}
```

---

## Quick Checklist When Adding Any Animation

- [ ] Method returns a `Bitmap` object
- [ ] Bitmap is created with `SIZE, SIZE` (25×25)
- [ ] Background is filled with `c.drawColor(Color.BLACK)`
- [ ] New `case N:` added in the `switch` block
- [ ] The `% 3` in `onStartCommand` updated to `% N` (total animation count)
- [ ] Hit ▶ Run in Android Studio after every change

---

## Brightness Tip

The `setBrightness(255)` in `startLoop()` controls how bright the matrix is. Values range from `0` (off) to `255` (full brightness). You can lower it per animation if needed by using grey instead of pure white:

```java
// Instead of Color.WHITE, use a grey for dimmer pixels
p.setColor(Color.rgb(128, 128, 128));  // 50% brightness
```

---

## Speed Tip

The animation speed is controlled by this line at the bottom of `startLoop()`:

```java
mHandler.postDelayed(this, 50);  // 50ms = 20 frames per second
```

Change `50` to a smaller number for faster animations, or larger for slower ones:

| Value | Speed |
|-------|-------|
| `16` | ~60 fps (very fast) |
| `33` | ~30 fps (smooth) |
| `50` | ~20 fps (default) |
| `100` | ~10 fps (slow) |
| `500` | 2 fps (very slow) |

