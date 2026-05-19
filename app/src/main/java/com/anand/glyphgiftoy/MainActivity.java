package com.anand.glyphgiftoy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> permissionLauncher;

    private TextView currentAnimText;

    private final String[] animNames = {
            "Pulse",
            "Spinner",
            "Matrix Rain",
            "Heartbeat",
            "Pacman",
            "Space Invader",
            "3D Tunnel",
            "3D Cube",
            "3D Sphere",
            "Equalizer",
            "Snake",
            "Rocket",
            "Clock",
            "Stars",
            "Fire",
            "Border Runner",
            "Bouncing Ball",
            "Expanding Rings"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        currentAnimText = findViewById(R.id.currentAnimText);

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        startGlyph();
                    } else {
                        Toast.makeText(
                                this,
                                "Notification permission needed",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        findViewById(R.id.startBtn)
                .setOnClickListener(v -> requestAndStart());

        findViewById(R.id.stopBtn)
                .setOnClickListener(v -> stopGlyph());

        for (int i = 0; i < 18; i++) {

            int id = getResources().getIdentifier(
                    "btn" + i,
                    "id",
                    getPackageName()
            );

            int finalI = i;

            findViewById(id).setOnClickListener(v -> {

                selectAnimation(finalI);

                currentAnimText.setText(
                        "Current Animation: " + animNames[finalI]
                );
            });
        }
    }

    private void requestAndStart() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                permissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                );

                return;
            }
        }

        startGlyph();
    }

    private void startGlyph() {

        Intent i = new Intent(
                this,
                GifGlyphToyService.class
        );

        startForegroundService(i);

        Toast.makeText(
                this,
                "Glyph Matrix Started!",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void selectAnimation(int index) {

        GifGlyphToyService.selectedAnimation = index;

        Intent i = new Intent(
                this,
                GifGlyphToyService.class
        );

        startService(i);
    }

    private void stopGlyph() {

        Intent i = new Intent(
                this,
                GifGlyphToyService.class
        );

        i.setAction(GifGlyphToyService.ACTION_STOP);

        startService(i);

        Toast.makeText(
                this,
                "Glyph Matrix Stopped",
                Toast.LENGTH_SHORT
        ).show();
    }
}