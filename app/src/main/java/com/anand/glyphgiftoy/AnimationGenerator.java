package com.anand.glyphgiftoy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SweepGradient;

public class AnimationGenerator {
    public static final int SIZE = 13;
    private int[][] rainGrid = new int[SIZE][SIZE];
    private final float[] smoothedHeights = new float[9];
    private static final float SMOOTHING_FACTOR = 0.25f;
    private static final float DECAY_FACTOR = 0.05f;

    public Bitmap getFrame(int animIndex, int tick) {
        switch (animIndex) {
            case 1: return animSpinner(tick);
            case 2: return animRain();
            case 3: return animHeartbeat(tick);
            case 4: return animPacman(tick);
            case 5: return animInvader(tick);
            case 6: return animTunnel(tick);
            case 7: return animCube3D(tick);
            case 8: return animSphere(tick);
            case 9: return animEqualizer(tick);
            case 10: return animSnake(tick);
            case 11: return animRocket(tick);
            case 12: return animClock(tick);
            case 13: return animStars(tick);
            case 14: return animFire(tick);
            case 15: return animBorder(tick);
            case 16: return animBounce(tick);
            case 17: return animRings(tick);
            case 18: return animGradientDisc(tick);
            default: return animPulse(tick);
        }
    }

    private Bitmap createBitmap() {
        Bitmap bmp = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.BLACK);
        return bmp;
    }

    private Paint whitePaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.WHITE);
        return p;
    }

    private Bitmap animPulse(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        float phase = (float)(Math.sin(tick * 0.15)) * 0.5f + 0.5f;
        c.drawCircle(SIZE / 2f, SIZE / 2f, 1.5f + phase * 4.5f, p);
        return bmp;
    }

    private Bitmap animSpinner(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        p.setStrokeWidth(1.2f);
        float cx = SIZE / 2f;
        float cy = SIZE / 2f;
        double a = Math.toRadians(tick * 10);
        c.drawLine(cx, cy, cx + (float)(Math.cos(a) * 6), cy + (float)(Math.sin(a) * 6), p);
        c.drawCircle(cx, cy, 1.2f, p);
        return bmp;
    }

    private Bitmap animRain() {
        for (int r = SIZE - 1; r > 0; r--) rainGrid[r] = rainGrid[r - 1].clone();
        rainGrid[0] = new int[SIZE];
        for (int col = 0; col < SIZE; col++) {
            rainGrid[0][col] = (Math.random() < 0.25) ? 255 : 0;
        }
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        for (int r = 0; r < SIZE; r++) {
            for (int col = 0; col < SIZE; col++) {
                if (rainGrid[r][col] > 0) {
                    c.drawRect(col, r, col + 1, r + 1, p);
                }
            }
        }
        return bmp;
    }

    private Bitmap animHeartbeat(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        float scale = 3.5f + (float)(Math.sin(tick * 0.25) * 0.8f);
        float center = SIZE / 2f;
        for (int y = -6; y <= 6; y++) {
            for (int x = -6; x <= 6; x++) {
                float nx = x / scale;
                float ny = y / scale;
                float eq = (float)Math.pow(nx * nx + ny * ny - 1, 3) - nx * nx * ny * ny * ny;
                if (eq <= 0) {
                    c.drawRect(center + x, center + y, center + x + 1, center + y + 1, p);
                }
            }
        }
        return bmp;
    }

    private Bitmap animPacman(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        int x = (tick % (SIZE + 6)) - 3;
        boolean mouth = (tick % 2 == 0);
        float cy = SIZE / 2f;
        for (int yy = -3; yy <= 3; yy++) {
            for (int xx = -3; xx <= 3; xx++) {
                float d = (float)Math.hypot(xx, yy);
                if (d < 3.2f) {
                    if (!(mouth && xx > 0 && Math.abs(yy) < 1.2)) {
                        c.drawRect(x + xx, cy + yy, x + xx + 1, cy + yy + 1, p);
                    }
                }
            }
        }
        c.drawCircle(SIZE - 1.5f, cy, 0.7f, p);
        return bmp;
    }

    private Bitmap animInvader(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        int shift = tick % 2;
        // Scaled down invader for 13x13
        int[][] pts = {
                {5,3},{6,3},{7,3},
                {4,4},{5,4},{6,4},{7,4},{8,4},
                {4,5},{6,5},{8,5},
                {4,6},{5,6},{6,6},{7,6},{8,6},
                {5-shift,7},{7+shift,7}
        };
        for (int[] pt : pts) {
            c.drawRect(pt[0], pt[1], pt[0]+1, pt[1]+1, p);
        }
        return bmp;
    }

    private Bitmap animTunnel(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        p.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < 4; i++) {
            int s = (tick + i * 3) % 12;
            int min = s / 2;
            int max = SIZE - min;
            c.drawRect(min, min, max, max, p);
        }
        return bmp;
    }

    private Bitmap animCube3D(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        p.setStyle(Paint.Style.STROKE);
        int offset = (int)(Math.sin(tick * 0.2) * 2);
        c.drawRect(2,4,8,10,p);
        c.drawRect(4 + offset, 2, 10 + offset, 8, p);
        c.drawLine(2,4,4+offset,2,p);
        c.drawLine(8,4,10+offset,2,p);
        c.drawLine(2,10,4+offset,8,p);
        c.drawLine(8,10,10+offset,8,p);
        return bmp;
    }

    private Bitmap animSphere(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        float center = SIZE / 2f;
        for (int y = -5; y <= 5; y++) {
            for (int x = -5; x <= 5; x++) {
                double d = Math.hypot(x, y);
                if (d < 5) {
                    int b = (int)(128 + Math.sin((x + tick) * 0.4) * 127);
                    p.setColor(Color.rgb(b,b,b));
                    c.drawRect(center + x, center + y, center + x + 1, center + y + 1, p);
                }
            }
        }
        return bmp;
    }

    private Bitmap animEqualizer(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        int numBars = 3;
        int barWidth = 3;
        int spacing = 1;
        for (int i = 0; i < numBars; i++) {
            int h = (int)(Math.abs(Math.sin(tick * 0.15 + i)) * (SIZE - 2));
            c.drawRect(1 + i * (barWidth + spacing), SIZE - h, 1 + i * (barWidth + spacing) + barWidth, SIZE, p);
        }
        return bmp;
    }

    private Bitmap animSnake(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        float cy = SIZE / 2f;
        for (int i = 0; i < 8; i++) {
            int x = (tick + i) % SIZE;
            int y = (int)(cy + (int)(Math.sin((tick + i) * 0.6) * 3));
            int b = 255 - i * 25;
            p.setColor(Color.rgb(b,b,b));
            c.drawCircle(x,y,1.0f,p);
        }
        return bmp;
    }

    private Bitmap animRocket(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        int y = SIZE - (tick % (SIZE + 5));
        float cx = SIZE / 2f;
        c.drawCircle(cx,y,1.2f,p);
        c.drawLine(cx,y+1,cx,y+3,p);
        if (tick % 2 == 0) {
            p.setColor(Color.rgb(180,180,180));
            c.drawCircle(cx,y+4,1.2f,p);
        }
        return bmp;
    }

    private Bitmap animClock(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        p.setStyle(Paint.Style.STROKE);
        float cx = SIZE / 2f;
        float cy = SIZE / 2f;
        c.drawCircle(cx,cy,5.5f,p);
        double a = Math.toRadians((tick * 8) % 360);
        c.drawLine(cx, cy, cx + (float)(Math.cos(a) * 4.5), cy + (float)(Math.sin(a) * 4.5), p);
        return bmp;
    }

    private Bitmap animStars(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        for (int i = 0; i < 15; i++) {
            if (Math.random() < 0.2) {
                int x = (int)(Math.random() * SIZE);
                int y = (int)(Math.random() * SIZE);
                c.drawRect(x,y,x+1,y+1,p);
            }
        }
        return bmp;
    }

    private Bitmap animFire(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                double v = Math.sin(x * 0.6 + tick * 0.3) + Math.sin(y * 0.5 + tick * 0.4);
                if (v > 0.8) {
                    int b = (int)(180 + v * 35);
                    p.setColor(Color.rgb(b,b,b));
                    c.drawRect(x,y,x+1,y+1,p);
                }
            }
        }
        return bmp;
    }

    private Bitmap animBorder(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        int perimeter = (SIZE - 1) * 4;
        int pos = tick % perimeter;
        int x,y;
        if (pos < SIZE) {
            x = pos; y = 0;
        } else if (pos < SIZE * 2 - 1) {
            x = SIZE - 1; y = pos - (SIZE - 1);
        } else if (pos < SIZE * 3 - 2) {
            x = SIZE - 1 - (pos - (SIZE * 2 - 2)); y = SIZE - 1;
        } else {
            x = 0; y = SIZE - 1 - (pos - (SIZE * 3 - 3));
        }
        c.drawCircle(x,y,1.2f,p);
        return bmp;
    }

    private Bitmap animBounce(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        float cx = SIZE / 2f;
        float y = cx + (float)(Math.sin(tick * 0.25) * 4.5);
        c.drawCircle(cx,y,2,p);
        return bmp;
    }

    private Bitmap animRings(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        p.setStyle(Paint.Style.STROKE);
        float cx = SIZE / 2f;
        for (int i = 0; i < 3; i++) {
            float r = ((tick * 0.4f + i * 3) % 8);
            if (r > 0.5f) {
                c.drawCircle(cx, cx, r, p);
            }
        }
        return bmp;
    }

    private Bitmap animGradientDisc(int tick) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        float cx = SIZE / 2f;
        float cy = SIZE / 2f;
        int baseGray = Color.rgb(60, 60, 60);
        int[] colors = {baseGray, Color.WHITE, baseGray, Color.WHITE, baseGray};
        float[] positions = {0f, 0.25f, 0.5f, 0.75f, 1f};
        SweepGradient gradient = new SweepGradient(cx, cy, colors, positions);
        Matrix matrix = new Matrix();
        matrix.setRotate(tick * 7f, cx, cy);
        gradient.setLocalMatrix(matrix);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setShader(gradient);
        c.drawCircle(cx, cy, SIZE / 2f, p);
        p.setShader(null);
        p.setColor(Color.WHITE);
        p.setAlpha(200);
        c.drawCircle(cx, cy, 1.5f, p);
        p.setShader(null);
        p.setColor(Color.BLACK);
        p.setAlpha(255);
        p.setStrokeWidth(1.2f);
        c.drawLine(SIZE - 1, SIZE - 1, cx + 1, cy + 1, p);
        return bmp;
    }

    public Bitmap animMusicSync(byte[] fft, int animationIndex, int tick, float sensitivity, float intensityMultiplier) {
        Bitmap bmp = createBitmap();
        if (fft == null) return bmp;
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();

        int n = fft.length / 2;
        float[] magnitudes = new float[n];
        magnitudes[0] = Math.abs(fft[0]); // DC
        for (int k = 1; k < n; k++) {
            magnitudes[k] = (float) Math.hypot(fft[2 * k], fft[2 * k + 1]);
        }

        // Apply sensitivity
        for (int k = 0; k < n; k++) {
            magnitudes[k] *= sensitivity;
        }

        // Calculate average magnitude for overall intensity
        float avgMag = 0;
        for (float m : magnitudes) avgMag += m;
        avgMag /= n;
        float intensity = Math.min(1.0f, (avgMag / 15.0f) * intensityMultiplier);

        switch (animationIndex) {
            case 0: // Classic Bars
                renderBars(c, p, magnitudes, n);
                break;
            case 1: // 3D Sphere
                renderMusicSphere(c, p, magnitudes, n, tick, intensity);
                break;
            case 2: // Circular Ripple
                renderMusicRipple(c, p, magnitudes, n, tick, intensity);
                break;
            case 3: // Vortex / Spiral
                renderMusicVortex(c, p, magnitudes, n, tick, intensity);
                break;
            default:
                renderBars(c, p, magnitudes, n);
                break;
        }
        return bmp;
    }

    private void renderBars(Canvas c, Paint p, float[] magnitudes, int n) {
        int numBars = 9;
        int barWidth = 1;
        int spacing = 1;
        int startX = (SIZE - (numBars * barWidth + (numBars - 1) * spacing)) / 2;

        for (int i = 0; i < numBars; i++) {
            int startBin = (int) Math.pow(1.6, i + 1);
            int endBin = (int) Math.pow(1.6, i + 2);
            if (endBin > n) endBin = n;
            if (startBin >= n) startBin = n - 1;

            float maxMag = 0;
            for (int k = startBin; k < endBin; k++) {
                if (magnitudes[k] > maxMag) maxMag = magnitudes[k];
            }

            float targetHeight = (maxMag / 25.0f) * SIZE;
            if (targetHeight > SIZE) targetHeight = SIZE;

            if (targetHeight > smoothedHeights[i]) {
                smoothedHeights[i] = smoothedHeights[i] * (1 - SMOOTHING_FACTOR) + targetHeight * SMOOTHING_FACTOR;
            } else {
                smoothedHeights[i] -= DECAY_FACTOR * SIZE;
                if (smoothedHeights[i] < targetHeight) smoothedHeights[i] = targetHeight;
            }

            if (smoothedHeights[i] < 0) smoothedHeights[i] = 0;

            int h = (int) smoothedHeights[i];
            if (h < 1 && maxMag > 2) h = 1;

            c.drawRect(startX + i * (barWidth + spacing), SIZE - h, startX + i * (barWidth + spacing) + barWidth, SIZE, p);
        }
    }

    private void renderMusicSphere(Canvas c, Paint p, float[] magnitudes, int n, int tick, float intensity) {
        float center = SIZE / 2f;
        float radius = (SIZE / 2.5f) + (intensity * 2);
        
        // Bass influence on radius
        float bass = 0;
        for (int i=1; i<4 && i<n; i++) bass += magnitudes[i];
        bass /= 3.0f;
        radius += (bass / 30.0f) * 2;

        for (int y = -SIZE/2; y <= SIZE/2; y++) {
            for (int x = -SIZE/2; x <= SIZE/2; x++) {
                double dist = Math.hypot(x, y);
                if (dist < radius) {
                    // 3D Normal approximation for shading
                    double nz = Math.sqrt(radius * radius - dist * dist) / radius;
                    
                    // Light source from top-left-front
                    double shading = (x/radius * -0.3 + y/radius * -0.3 + nz * 0.8);
                    shading = Math.max(0, Math.min(1, shading));
                    
                    // Add some audio-reactive "texture"
                    double texture = Math.sin((dist - tick * 0.5) * 0.8) * 0.1 * intensity;
                    
                    int b = (int) ((shading + texture) * 255);
                    b = Math.max(0, Math.min(255, b));
                    
                    p.setColor(Color.rgb(b, b, b));
                    c.drawPoint(center + x, center + y, p);
                }
            }
        }
    }

    private void renderMusicRipple(Canvas c, Paint p, float[] magnitudes, int n, int tick, float intensity) {
        float center = SIZE / 2f;
        
        // Use different bands for different ripple properties
        float bass = magnitudes[1] / 20.0f;
        float mid = magnitudes[n/4] / 20.0f;
        
        for (int r = 0; r < SIZE; r++) {
            double ripple = Math.sin(r * 0.8 - tick * 0.4) * 0.5 + 0.5;
            int b = (int) (ripple * intensity * 255 * (1.0f - (float)r/SIZE));
            
            // Bass kicks brighten the whole thing
            b += (int)(bass * 50);
            b = Math.max(0, Math.min(255, b));
            
            p.setColor(Color.rgb(b, b, b));
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1.0f);
            c.drawCircle(center, center, r, p);
        }
    }

    private void renderMusicVortex(Canvas c, Paint p, float[] magnitudes, int n, int tick, float intensity) {
        float center = SIZE / 2f;
        int numArms = 3;
        
        for (int i = 0; i < 60; i++) {
            float angle = (float) (i * 0.2 + tick * 0.1);
            float dist = (float) (i * 0.2);
            
            for (int arm = 0; arm < numArms; arm++) {
                float currentAngle = angle + (float)(arm * 2 * Math.PI / numArms);
                float x = center + (float) Math.cos(currentAngle) * dist;
                float y = center + (float) Math.sin(currentAngle) * dist;
                
                // Audio reactivity: expand/contract or brighten
                float audioShift = (magnitudes[(i % (n-1)) + 1] / 30.0f);
                int b = (int) (intensity * 255 * (1.0f - dist/SIZE) + audioShift * 100);
                b = Math.max(0, Math.min(255, b));
                
                p.setColor(Color.rgb(b, b, b));
                p.setStyle(Paint.Style.FILL);
                c.drawRect(x, y, x + 1, y + 1, p);
            }
        }
    }

    public Bitmap animTimer(float progress) {
        Bitmap bmp = createBitmap();
        Canvas c = new Canvas(bmp);
        Paint p = whitePaint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1.0f);
        
        float cx = SIZE / 2f;
        float cy = SIZE / 2f;
        float radius = SIZE / 2f - 0.5f;

        // Draw a neat circular progress
        float angle = 360 * progress;
        c.drawArc(cx - radius, cy - radius, cx + radius, cy + radius, -90, angle, false, p);
        
        // Add a dot at the current position
        p.setStyle(Paint.Style.FILL);
        double rad = Math.toRadians(angle - 90);
        float dotX = cx + (float)Math.cos(rad) * radius;
        float dotY = cy + (float)Math.sin(rad) * radius;
        c.drawCircle(dotX, dotY, 1.0f, p);

        return bmp;
    }
}
