# Add Rotating Gradient Disc Animation

This plan adds a new animation called "Gradient Disc" to the GlyphGifToy application. The animation features a circular disc with a sweep gradient (white to black) that rotates over time, creating a "tail" effect suitable for the circular glyph components on Nothing Phone devices.

## User Review Required

> [!NOTE]
> The rotation speed is set to 15 degrees per tick (approx. 300 degrees per second). This can be adjusted if it's too fast or slow.
> The gradient uses White to Black, which translates to Full Brightness to Off on the Glyph interface.

## Proposed Changes

### Animation Service

#### [GifGlyphToyService.java](file:///C:/Users/anand/AndroidStudioProjects/GlyphGifToy/app/src/main/java/com/anand/glyphgiftoy/GifGlyphToyService.java)

- Increment `TOTAL_ANIMS` to 19.
- Add imports for `android.graphics.Matrix` and `android.graphics.SweepGradient`.
- Implement `animGradientDisc(int tick)` method using `SweepGradient` and `Matrix` for rotation.
- Update `startLoop()` switch statement to include `case 18`.

```java
    private Bitmap animGradientDisc(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        float cx = SIZE / 2f;
        float cy = SIZE / 2f;

        int[] colors = {Color.WHITE, Color.BLACK};
        float[] positions = {0f, 1f};
        SweepGradient gradient = new SweepGradient(cx, cy, colors, positions);

        Matrix matrix = new Matrix();
        matrix.setRotate(tick * 15f, cx, cy);
        gradient.setLocalMatrix(matrix);

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setShader(gradient);
        c.drawCircle(cx, cy, SIZE / 2f, p);

        return bmp;
    }
```

---

### UI Components

Update the animation names list in three places to include "Gradient Disc".

#### [MainActivity.java](file:///C:/Users/anand/AndroidStudioProjects/GlyphGifToy/app/src/main/java/com/anand/glyphgiftoy/MainActivity.java)

- Add "Gradient Disc" to `animNames` array.
- Update the loop in `onCreateView` to use `animNames.length` instead of the hardcoded `18`.

#### [AddRuleActivity.java](file:///C:/Users/anand/AndroidStudioProjects/GlyphGifToy/app/src/main/java/com/anand/glyphgiftoy/ui/AddRuleActivity.java)

- Add "Gradient Disc" to `animNames` array.

#### [RuleAdapter.java](file:///C:/Users/anand/AndroidStudioProjects/GlyphGifToy/app/src/main/java/com/anand/glyphgiftoy/ui/RuleAdapter.java)

- Add "Gradient Disc" to `animNames` array.

## Verification Plan

### Automated Tests
- I will use `render_compose_preview` (if possible for non-compose views, or just rely on manual) to verify the UI changes.
- Since this is a standard Android app, I will perform a build to ensure no syntax errors.

### Manual Verification
1. **Build and Run**: Deploy the app to a device or emulator.
2. **Check UI**: Verify that "Gradient Disc" appears in the animation grid in the "Live View" tab.
3. **Check Rule Creation**: Verify that "Gradient Disc" appears in the "Add Rule" screen spinner.
4. **Visual Verification**: Select "Gradient Disc" and verify (if possible via logs or UI state if the Glyph Matrix SDK has a preview mode) that it renders correctly.
    - *Note*: Since the actual Glyph hardware is not present, I will check the `GlyphMatrixObject` creation in logs if I add debug logging.
