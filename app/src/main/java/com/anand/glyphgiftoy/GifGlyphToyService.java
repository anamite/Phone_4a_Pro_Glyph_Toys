package com.anand.glyphgiftoy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.anand.glyphgiftoy.data.CustomAnimationManager;
import com.anand.glyphgiftoy.models.CustomAnimation;
import com.nothing.ketchum.Glyph;
import com.nothing.ketchum.GlyphException;
import com.nothing.ketchum.GlyphMatrixFrame;
import com.nothing.ketchum.GlyphMatrixManager;
import com.nothing.ketchum.GlyphMatrixObject;


public class GifGlyphToyService extends Service {

    public static int selectedAnimation = 0;
    public static CustomAnimation activeCustomAnimation = null;
    public static int musicSyncAnimationIndex = 0;
    public static float musicSyncSensitivity = 1.0f;
    public static float musicSyncIntensity = 1.0f;
    public static int currentMode = 0;
    public static boolean timerRunning = false;

    public static final int MODE_ANIMATION = 0;
    public static final int MODE_MUSIC_SYNC = 1;
    public static final int MODE_TIMER = 2;

    public static final String ACTION_NEXT = "NEXT_ANIM";
    public static final String ACTION_STOP = "STOP";
    public static final String ACTION_TRIGGER_OVERRIDE = "TRIGGER_OVERRIDE";
    public static final String ACTION_SET_MODE = "SET_MODE";
    public static final String ACTION_SET_CUSTOM_ANIM = "SET_CUSTOM_ANIM";

    public static final String EXTRA_ANIM_INDEX = "extra_anim_index";
    public static final String EXTRA_DURATION_SEC = "extra_duration_sec";
    public static final String EXTRA_BRIGHTNESS = "extra_brightness";
    public static final String EXTRA_SCALE = "extra_scale";
    public static final String EXTRA_CUSTOM_ANIM_ID = "extra_custom_anim_id";
    public static final String EXTRA_MODE = "extra_mode";
    public static final String EXTRA_MUSIC_ANIM_INDEX = "extra_music_anim_index";
    public static final String EXTRA_MUSIC_SENSITIVITY = "extra_music_sensitivity";
    public static final String EXTRA_MUSIC_INTENSITY = "extra_music_intensity";

    private static final String CHANNEL_ID = "glyph_channel";
    private static final int TOTAL_ANIMS = 19;

    private GlyphMatrixManager mGM;
    private Handler mHandler;
    private boolean mRunning = false;
    private AnimationGenerator mAnimGen = new AnimationGenerator();

    private int mTick = 0;
    private int mCurrentAnim = 0;
    private int mOverrideAnim = -1;
    private long mOverrideEndTime = 0;
    private int mOverrideBrightness = 255;
    private int mOverrideScale = 100;
    private CustomAnimation mOverrideCustomAnim = null;

    private Visualizer mVisualizer;
    private byte[] mFFT;
    private long mTimerEndTime = 0;
    private int mTimerTotalDuration = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_STOP.equals(action)) {
                stopSelf();
                return START_NOT_STICKY;
            } else if (ACTION_NEXT.equals(action)) {
                mCurrentAnim = (mCurrentAnim + 1) % TOTAL_ANIMS;
                mTick = 0;
                return START_NOT_STICKY;
            } else if (ACTION_TRIGGER_OVERRIDE.equals(action)) {
                mOverrideAnim = intent.getIntExtra(EXTRA_ANIM_INDEX, 0);
                String customId = intent.getStringExtra(EXTRA_CUSTOM_ANIM_ID);
                if (customId != null) {
                    mOverrideCustomAnim = CustomAnimationManager.getInstance(this).getAnimation(customId);
                } else {
                    mOverrideCustomAnim = null;
                }
                int durationSec = intent.getIntExtra(EXTRA_DURATION_SEC, 3);
                mOverrideBrightness = intent.getIntExtra(EXTRA_BRIGHTNESS, 255);
                mOverrideScale = intent.getIntExtra(EXTRA_SCALE, 100);
                mOverrideEndTime = System.currentTimeMillis() + (durationSec * 1000L);
                mTick = 0; // Reset tick for the override animation
                return START_NOT_STICKY;
            } else if (ACTION_SET_MODE.equals(action)) {
                int mode = intent.getIntExtra(EXTRA_MODE, MODE_ANIMATION);
                setMode(mode, intent);
                return START_NOT_STICKY;
            } else if (ACTION_SET_CUSTOM_ANIM.equals(action)) {
                // The CustomAnimation object is usually set via the static field
                // but we trigger a tick reset here.
                mTick = 0;
                return START_NOT_STICKY;
            }
        }

        startForeground(1, buildNotification());
        initGlyph();

        return START_NOT_STICKY;
    }

    private void setMode(int mode, Intent intent) {
        currentMode = mode;
        if (intent != null && intent.hasExtra(EXTRA_MUSIC_ANIM_INDEX)) {
            musicSyncAnimationIndex = intent.getIntExtra(EXTRA_MUSIC_ANIM_INDEX, 0);
        }
        if (intent != null && intent.hasExtra(EXTRA_MUSIC_SENSITIVITY)) {
            musicSyncSensitivity = intent.getFloatExtra(EXTRA_MUSIC_SENSITIVITY, 1.0f);
        }
        if (intent != null && intent.hasExtra(EXTRA_MUSIC_INTENSITY)) {
            musicSyncIntensity = intent.getFloatExtra(EXTRA_MUSIC_INTENSITY, 1.0f);
        }
        stopVisualizer();
        timerRunning = false;

        if (mode == MODE_MUSIC_SYNC) {
            startVisualizer();
        } else if (mode == MODE_TIMER) {
            int durationSec = intent.getIntExtra(EXTRA_DURATION_SEC, 30);
            mTimerTotalDuration = durationSec;
            mTimerEndTime = System.currentTimeMillis() + (durationSec * 1000L);
            timerRunning = true;
        }
    }

    private void startVisualizer() {
        try {
            mVisualizer = new Visualizer(0);
            int captureSize = Visualizer.getCaptureSizeRange()[1];
            mVisualizer.setCaptureSize(captureSize);
            mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                    mFFT = fft;
                }
            }, Visualizer.getMaxCaptureRate() / 2, false, true);
            mVisualizer.setEnabled(true);
            Log.d("GifGlyphToyService", "Visualizer initialized successfully with capture size: " + captureSize);
        } catch (Exception e) {
            Log.e("GifGlyphToyService", "Error starting visualizer", e);
            mVisualizer = null;
        }
    }

    private void stopVisualizer() {
        if (mVisualizer != null) {
            mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
        }
        mFFT = null;
    }

    @Override
    public void onDestroy() {
        mRunning = false;
        stopVisualizer();

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

                Bitmap frame = null;

                int animToPlay;
                int brightness = 255;
                int scale = 100;

                if (System.currentTimeMillis() < mOverrideEndTime) {
                    brightness = mOverrideBrightness;
                    scale = mOverrideScale;
                    if (mOverrideCustomAnim != null) {
                        frame = mAnimGen.renderCustom(mOverrideCustomAnim, mTick);
                    } else {
                        animToPlay = mOverrideAnim;
                        frame = mAnimGen.getFrame(animToPlay, mTick);
                    }
                } else if (currentMode == MODE_MUSIC_SYNC) {
                    frame = mAnimGen.animMusicSync(mFFT, musicSyncAnimationIndex, mTick, musicSyncSensitivity, musicSyncIntensity);
                } else if (currentMode == MODE_TIMER) {
                    long remaining = mTimerEndTime - System.currentTimeMillis();
                    if (remaining <= 0) {
                        timerRunning = false;
                        currentMode = MODE_ANIMATION;
                        frame = mAnimGen.getFrame(selectedAnimation, mTick);
                    } else {
                        float progress = 1.0f - ((float) remaining / (mTimerTotalDuration * 1000f));
                        frame = mAnimGen.animTimer(progress);
                    }
                } else if (activeCustomAnimation != null) {
                    frame = mAnimGen.renderCustom(activeCustomAnimation, mTick);
                } else {
                    animToPlay = selectedAnimation;
                    frame = mAnimGen.getFrame(animToPlay, mTick);
                }

                if (frame == null) {
                    frame = Bitmap.createBitmap(AnimationGenerator.SIZE, AnimationGenerator.SIZE, Bitmap.Config.ARGB_8888);
                }

                GlyphMatrixObject obj = new GlyphMatrixObject.Builder()
                        .setImageSource(frame)
                        .setPosition(0, 0)
                        .setScale(scale)
                        .setBrightness(brightness)
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

                mHandler.postDelayed(this, 30);
            }
        });
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