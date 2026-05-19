package com.anand.glyphgiftoy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.nothing.ketchum.Glyph;
import com.nothing.ketchum.GlyphException;
import com.nothing.ketchum.GlyphMatrixFrame;
import com.nothing.ketchum.GlyphMatrixManager;
import com.nothing.ketchum.GlyphMatrixObject;


public class GifGlyphToyService extends Service {

    public static int selectedAnimation = 0;

    public static final String ACTION_NEXT = "NEXT_ANIM";
    public static final String ACTION_STOP = "STOP";

    private static final String CHANNEL_ID = "glyph_channel";
    private static final int SIZE = 25;
    private static final int TOTAL_ANIMS = 18;

    private GlyphMatrixManager mGM;
    private Handler mHandler;
    private boolean mRunning = false;

    private int mTick = 0;
    private int mCurrentAnim = 0;

    private int[][] rainGrid = new int[SIZE][SIZE];

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (intent != null && ACTION_NEXT.equals(intent.getAction())) {
            mCurrentAnim = (mCurrentAnim + 1) % TOTAL_ANIMS;
            mTick = 0;
            return START_NOT_STICKY;
        }

        startForeground(1, buildNotification());
        initGlyph();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mRunning = false;

        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);

        if (mGM != null) {
            mGM.turnOff();
            mGM.unInit();
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initGlyph() {

        mHandler = new Handler(Looper.getMainLooper());

        mGM = GlyphMatrixManager.getInstance(getApplicationContext());

        mGM.init(new GlyphMatrixManager.Callback() {

            @Override
            public void onServiceConnected(ComponentName name) {

                mGM.register(Glyph.DEVICE_23112);

                mRunning = true;

                startLoop();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mRunning = false;
            }
        });
    }

    private void startLoop() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (!mRunning || mGM == null)
                    return;

                Bitmap frame;

                mCurrentAnim = selectedAnimation;

                switch (mCurrentAnim) {

                    case 1: frame = animSpinner(mTick); break;
                    case 2: frame = animRain(); break;
                    case 3: frame = animHeartbeat(mTick); break;
                    case 4: frame = animPacman(mTick); break;
                    case 5: frame = animInvader(mTick); break;
                    case 6: frame = animTunnel(mTick); break;
                    case 7: frame = animCube3D(mTick); break;
                    case 8: frame = animSphere(mTick); break;
                    case 9: frame = animEqualizer(mTick); break;
                    case 10: frame = animSnake(mTick); break;
                    case 11: frame = animRocket(mTick); break;
                    case 12: frame = animClock(mTick); break;
                    case 13: frame = animStars(mTick); break;
                    case 14: frame = animFire(mTick); break;
                    case 15: frame = animBorder(mTick); break;
                    case 16: frame = animBounce(mTick); break;
                    case 17: frame = animRings(mTick); break;

                    default:
                        frame = animPulse(mTick);
                        break;
                }

                GlyphMatrixObject obj = new GlyphMatrixObject.Builder()
                        .setImageSource(frame)
                        .setPosition(0, 0)
                        .setScale(100)
                        .setBrightness(255)
                        .build();

                GlyphMatrixFrame matrixFrame = new GlyphMatrixFrame.Builder()
                        .addTop(obj)
                        .build(getApplicationContext());

                try {
                    mGM.setMatrixFrame(matrixFrame);
                } catch (GlyphException e) {
                    e.printStackTrace();
                }

                mTick++;

                mHandler.postDelayed(this, 50);
            }
        });
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Bitmap createBitmap() {

        Bitmap bmp = Bitmap.createBitmap(
                SIZE,
                SIZE,
                Bitmap.Config.ARGB_8888
        );

        Canvas c = new Canvas(bmp);

        c.drawColor(Color.BLACK);

        return bmp;
    }

    private Paint whitePaint() {

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(Color.WHITE);

        return p;
    }

    // =========================================================
    // ANIMATION 0
    // =========================================================

    private Bitmap animPulse(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        float phase =
                (float)(Math.sin(tick * 0.15))
                        * 0.5f + 0.5f;

        c.drawCircle(
                SIZE / 2f,
                SIZE / 2f,
                3f + phase * 8f,
                p
        );

        return bmp;
    }

    // =========================================================
    // SPINNER
    // =========================================================

    private Bitmap animSpinner(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        p.setStrokeWidth(2f);

        float cx = SIZE / 2f;
        float cy = SIZE / 2f;

        double a = Math.toRadians(tick * 8);

        c.drawLine(
                cx,
                cy,
                cx + (float)(Math.cos(a) * 11),
                cy + (float)(Math.sin(a) * 11),
                p
        );

        c.drawCircle(cx, cy, 2f, p);

        return bmp;
    }

    // =========================================================
    // RAIN
    // =========================================================

    private Bitmap animRain() {

        for (int r = SIZE - 1; r > 0; r--)
            rainGrid[r] = rainGrid[r - 1].clone();

        rainGrid[0] = new int[SIZE];

        for (int col = 0; col < SIZE; col++) {
            rainGrid[0][col] =
                    (Math.random() < 0.3)
                            ? 255 : 0;
        }

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        for (int r = 0; r < SIZE; r++) {

            for (int col = 0; col < SIZE; col++) {

                if (rainGrid[r][col] > 0) {

                    c.drawRect(
                            col,
                            r,
                            col + 1,
                            r + 1,
                            p
                    );
                }
            }
        }

        return bmp;
    }

    // =========================================================
    // HEARTBEAT
    // =========================================================

    private Bitmap animHeartbeat(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        float scale =
                7f + (float)(Math.sin(tick * 0.25) * 2f);

        for (int y = -10; y <= 10; y++) {

            for (int x = -10; x <= 10; x++) {

                float nx = x / scale;
                float ny = y / scale;

                float eq =
                        (float)Math.pow(nx * nx + ny * ny - 1, 3)
                                - nx * nx * ny * ny * ny;

                if (eq <= 0) {

                    c.drawRect(
                            x + 12,
                            y + 12,
                            x + 13,
                            y + 13,
                            p
                    );
                }
            }
        }

        return bmp;
    }

    // =========================================================
    // PACMAN
    // =========================================================

    private Bitmap animPacman(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        int x = (tick % 16);

        boolean mouth = (tick % 2 == 0);

        for (int yy = -4; yy <= 4; yy++) {

            for (int xx = -4; xx <= 4; xx++) {

                float d = (float)Math.hypot(xx, yy);

                if (d < 4.5f) {

                    if (!(mouth && xx > 0 && Math.abs(yy) < 2)) {

                        c.drawRect(
                                x + xx,
                                12 + yy,
                                x + xx + 1,
                                12 + yy + 1,
                                p
                        );
                    }
                }
            }
        }

        c.drawCircle(22, 12, 1f, p);

        return bmp;
    }

    // =========================================================
    // INVADER
    // =========================================================

    private Bitmap animInvader(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        int shift = tick % 2;

        int[][] pts = {
                {10,4},{11,4},{13,4},{14,4},
                {9,5},{10,5},{11,5},{12,5},{13,5},{14,5},{15,5},
                {8,6},{10,6},{11,6},{12,6},{13,6},{14,6},{16,6},
                {8,7},{9,7},{11,7},{12,7},{13,7},{15,7},{16,7},
                {10-shift,8},{14+shift,8}
        };

        for (int[] pt : pts) {

            c.drawRect(
                    pt[0],
                    pt[1],
                    pt[0]+1,
                    pt[1]+1,
                    p
            );
        }

        return bmp;
    }

    // =========================================================
    // 3D TUNNEL
    // =========================================================

    private Bitmap animTunnel(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        p.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < 6; i++) {

            int s = (tick + i * 4) % 20;

            int min = s / 2;
            int max = SIZE - min;

            c.drawRect(
                    min,
                    min,
                    max,
                    max,
                    p
            );
        }

        return bmp;
    }

    // =========================================================
    // 3D CUBE
    // =========================================================

    private Bitmap animCube3D(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        p.setStyle(Paint.Style.STROKE);

        int offset =
                (int)(Math.sin(tick * 0.2) * 4);

        c.drawRect(5,5,15,15,p);

        c.drawRect(
                9 + offset,
                3,
                19 + offset,
                13,
                p
        );

        c.drawLine(5,5,9+offset,3,p);
        c.drawLine(15,5,19+offset,3,p);
        c.drawLine(5,15,9+offset,13,p);
        c.drawLine(15,15,19+offset,13,p);

        return bmp;
    }

    // =========================================================
    // SPHERE
    // =========================================================

    private Bitmap animSphere(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        for (int y = -10; y <= 10; y++) {

            for (int x = -10; x <= 10; x++) {

                double d = Math.hypot(x, y);

                if (d < 10) {

                    int b =
                            (int)(128 +
                                    Math.sin((x + tick) * 0.3) * 127);

                    p.setColor(Color.rgb(b,b,b));

                    c.drawRect(
                            x + 12,
                            y + 12,
                            x + 13,
                            y + 13,
                            p
                    );
                }
            }
        }

        return bmp;
    }

    // =========================================================
    // EQUALIZER
    // =========================================================

    private Bitmap animEqualizer(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        for (int i = 0; i < 5; i++) {

            int h =
                    (int)(
                            Math.abs(
                                    Math.sin(tick * 0.15 + i)
                            ) * 18
                    );

            c.drawRect(
                    3 + i * 4,
                    SIZE - h,
                    5 + i * 4,
                    SIZE,
                    p
            );
        }

        return bmp;
    }

    // =========================================================
    // SNAKE
    // =========================================================

    private Bitmap animSnake(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        for (int i = 0; i < 12; i++) {

            int x = (tick + i) % SIZE;

            int y =
                    12 +
                            (int)(
                                    Math.sin((tick + i) * 0.4) * 5
                            );

            int b = 255 - i * 18;

            p.setColor(Color.rgb(b,b,b));

            c.drawCircle(x,y,1.5f,p);
        }

        return bmp;
    }

    // =========================================================
    // ROCKET
    // =========================================================

    private Bitmap animRocket(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        int y = 24 - (tick % 30);

        c.drawCircle(12,y,2,p);

        c.drawLine(12,y+2,12,y+6,p);

        if (tick % 2 == 0) {

            p.setColor(Color.rgb(180,180,180));

            c.drawCircle(12,y+8,2,p);
        }

        return bmp;
    }

    // =========================================================
    // CLOCK
    // =========================================================

    private Bitmap animClock(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        p.setStyle(Paint.Style.STROKE);

        c.drawCircle(12,12,10,p);

        double a =
                Math.toRadians((tick * 6) % 360);

        c.drawLine(
                12,
                12,
                12 + (float)(Math.cos(a) * 8),
                12 + (float)(Math.sin(a) * 8),
                p
        );

        return bmp;
    }

    // =========================================================
    // STARS
    // =========================================================

    private Bitmap animStars(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        for (int i = 0; i < 40; i++) {

            if (Math.random() < 0.2) {

                int x = (int)(Math.random() * SIZE);
                int y = (int)(Math.random() * SIZE);

                c.drawRect(x,y,x+1,y+1,p);
            }
        }

        return bmp;
    }

    // =========================================================
    // FIRE
    // =========================================================

    private Bitmap animFire(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        for (int y = 0; y < SIZE; y++) {

            for (int x = 0; x < SIZE; x++) {

                double v =
                        Math.sin(x * 0.5 + tick * 0.2)
                                +
                                Math.sin(y * 0.4 + tick * 0.3);

                if (v > 0.7) {

                    int b = (int)(180 + v * 40);

                    p.setColor(Color.rgb(b,b,b));

                    c.drawRect(x,y,x+1,y+1,p);
                }
            }
        }

        return bmp;
    }

    // =========================================================
    // BORDER
    // =========================================================

    private Bitmap animBorder(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        int perimeter = (SIZE - 1) * 4;

        int pos = tick % perimeter;

        int x,y;

        if (pos < SIZE) {

            x = pos;
            y = 0;

        } else if (pos < SIZE * 2 - 1) {

            x = SIZE - 1;
            y = pos - (SIZE - 1);

        } else if (pos < SIZE * 3 - 2) {

            x = SIZE - 1 -
                    (pos - (SIZE * 2 - 2));

            y = SIZE - 1;

        } else {

            x = 0;

            y = SIZE - 1 -
                    (pos - (SIZE * 3 - 3));
        }

        c.drawCircle(x,y,2f,p);

        return bmp;
    }

    // =========================================================
    // BOUNCE
    // =========================================================

    private Bitmap animBounce(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        float y =
                12 +
                        (float)(
                                Math.sin(tick * 0.2) * 9
                        );

        c.drawCircle(12,y,3,p);

        return bmp;
    }

    // =========================================================
    // RINGS
    // =========================================================

    private Bitmap animRings(int tick) {

        Bitmap bmp = createBitmap();

        Canvas c = new Canvas(bmp);

        Paint p = whitePaint();

        p.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < 4; i++) {

            float r =
                    ((tick * 0.5f + i * 4) % 13);

            if (r > 1) {

                c.drawCircle(
                        12,
                        12,
                        r,
                        p
                );
            }
        }

        return bmp;
    }

    // =========================================================
    // NOTIFICATION
    // =========================================================

    private Notification buildNotification() {

        NotificationChannel ch =
                new NotificationChannel(
                        CHANNEL_ID,
                        "Glyph Toy",
                        NotificationManager.IMPORTANCE_LOW
                );

        ((NotificationManager)
                getSystemService(NOTIFICATION_SERVICE))
                .createNotificationChannel(ch);

        return new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Glyph Matrix Running")
                .setContentText("Tap to switch animations")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();
    }
}