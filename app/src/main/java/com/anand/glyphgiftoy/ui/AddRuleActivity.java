package com.anand.glyphgiftoy.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.anand.glyphgiftoy.R;
import com.anand.glyphgiftoy.data.CustomAnimationManager;
import com.anand.glyphgiftoy.data.RuleManager;
import com.anand.glyphgiftoy.models.CustomAnimation;
import com.anand.glyphgiftoy.models.GlyphRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddRuleActivity extends AppCompatActivity {

    private Spinner appSpinner, animSpinner;
    private SeekBar durationSeekBar, brightnessSeekBar;
    private TextView durationLabel, brightnessLabel;
    private List<AppInfo> installedApps;
    private List<AnimationOption> animationOptions;

    private final String[] animNames = {
            "Pulse", "Spinner", "Matrix Rain", "Heartbeat", "Pacman", "Space Invader",
            "3D Tunnel", "3D Cube", "3D Sphere", "Equalizer", "Snake", "Rocket",
            "Clock", "Stars", "Fire", "Border Runner", "Bouncing Ball", "Expanding Rings",
            "Gradient Disc"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rule);

        appSpinner = findViewById(R.id.appSpinner);
        animSpinner = findViewById(R.id.animSpinner);
        durationSeekBar = findViewById(R.id.durationSeekBar);
        brightnessSeekBar = findViewById(R.id.brightnessSeekBar);
        durationLabel = findViewById(R.id.durationLabel);
        brightnessLabel = findViewById(R.id.brightnessLabel);
        Button saveBtn = findViewById(R.id.saveBtn);

        loadApps();
        setupSpinners();
        setupSeekBars();

        saveBtn.setOnClickListener(v -> saveRule());
    }

    private void loadApps() {
        installedApps = new ArrayList<>();
        PackageManager pm = getPackageManager();
        // Use 0 instead of GET_META_DATA to speed up loading
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        for (ApplicationInfo app : apps) {
            // Include non-system apps and updated system apps (which users typically interact with)
            boolean isSystem = (app.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            boolean isUpdatedSystem = (app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
            
            if (!isSystem || isUpdatedSystem || app.packageName.equals("com.whatsapp") || app.packageName.contains("instagram")) {
                installedApps.add(new AppInfo(app.loadLabel(pm).toString(), app.packageName));
            }
        }
        Collections.sort(installedApps, (a, b) -> a.name.compareToIgnoreCase(b.name));
    }

    private void setupSpinners() {
        ArrayAdapter<AppInfo> appAdapter = new ArrayAdapter<AppInfo>(this, android.R.layout.simple_spinner_item, installedApps) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setText(getItem(position).name);
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setText(getItem(position).name);
                return view;
            }
        };
        appAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        appSpinner.setAdapter(appAdapter);

        animationOptions = new ArrayList<>();
        // Add built-in animations
        for (int i = 0; i < animNames.length; i++) {
            animationOptions.add(new AnimationOption(animNames[i], i, null));
        }
        // Add custom animations
        List<CustomAnimation> customAnims = CustomAnimationManager.getInstance(this).getAnimations();
        for (CustomAnimation anim : customAnims) {
            animationOptions.add(new AnimationOption(anim.getName() + " (Custom)", -1, anim.getId()));
        }

        ArrayAdapter<AnimationOption> animAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, animationOptions);
        animAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animSpinner.setAdapter(animAdapter);
    }

    private void setupSeekBars() {
        durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                durationLabel.setText("DURATION: " + progress + "S");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightnessLabel.setText("BRIGHTNESS: " + progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void saveRule() {
        AppInfo selectedApp = (AppInfo) appSpinner.getSelectedItem();
        AnimationOption selectedAnim = (AnimationOption) animSpinner.getSelectedItem();
        int duration = durationSeekBar.getProgress();
        int brightness = brightnessSeekBar.getProgress();

        if (duration == 0) duration = 1;

        GlyphRule rule = new GlyphRule(
                selectedApp.packageName,
                selectedApp.name,
                selectedAnim.index,
                selectedAnim.customId,
                duration,
                brightness,
                100
        );
        RuleManager.getInstance(this).addRule(rule);

        Toast.makeText(this, "Rule added for " + selectedApp.name, Toast.LENGTH_SHORT).show();
        finish();
    }

    private static class AnimationOption {
        String name;
        int index;
        String customId;

        AnimationOption(String name, int index, String customId) {
            this.name = name;
            this.index = index;
            this.customId = customId;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class AppInfo {
        String name;
        String packageName;

        AppInfo(String name, String packageName) {
            this.name = name;
            this.packageName = packageName;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}