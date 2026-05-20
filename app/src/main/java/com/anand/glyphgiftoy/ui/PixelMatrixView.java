package com.anand.glyphgiftoy.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PixelMatrixView extends View {
    private static final int GRID_SIZE = 13;
    private Bitmap currentFrame;
    private Paint pixelPaint;
    private Paint backgroundPaint;
    private Path clipPath;
    private RectF pixelRect;

    public PixelMatrixView(Context context) {
        super(context);
        init();
    }

    public PixelMatrixView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        pixelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.parseColor("#0A0A0A"));
        clipPath = new Path();
        pixelRect = new RectF();
    }

    public void setFrame(Bitmap bitmap) {
        this.currentFrame = bitmap;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        clipPath.reset();
        float size = Math.min(w, h);
        float left = (w - size) / 2f;
        float top = (h - size) / 2f;
        float cornerRadius = size * 0.05f;
        clipPath.addRoundRect(left, top, left + size, top + size, cornerRadius, cornerRadius, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float size = Math.min(width, height);
        float left = (width - size) / 2f;
        float top = (height - size) / 2f;

        canvas.save();
        canvas.clipPath(clipPath);
        canvas.drawRect(left, top, left + size, top + size, backgroundPaint);

        if (currentFrame != null) {
            float cellSize = size / GRID_SIZE;
            float pixelGap = cellSize * 0.15f;
            float actualPixelSize = cellSize - pixelGap;
            float cornerRadius = actualPixelSize * 0.2f;

            float offsetX = (width - size) / 2f;
            float offsetY = (height - size) / 2f;

            for (int y = 0; y < GRID_SIZE; y++) {
                for (int x = 0; x < GRID_SIZE; x++) {
                    int color = currentFrame.getPixel(x, y);
                    if (Color.alpha(color) > 0 && (Color.red(color) > 20 || Color.green(color) > 20 || Color.blue(color) > 20)) {
                        pixelPaint.setColor(color);
                        
                        float pLeft = offsetX + x * cellSize + pixelGap / 2f;
                        float pTop = offsetY + y * cellSize + pixelGap / 2f;
                        pixelRect.set(pLeft, pTop, pLeft + actualPixelSize, pTop + actualPixelSize);
                        
                        canvas.drawRoundRect(pixelRect, cornerRadius, cornerRadius, pixelPaint);
                    }
                }
            }
        }
        canvas.restore();
    }
}