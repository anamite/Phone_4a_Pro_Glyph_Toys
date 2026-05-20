package com.anand.glyphgiftoy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.anand.glyphgiftoy.data.RuleManager;
import com.anand.glyphgiftoy.ui.AddRuleActivity;
import com.anand.glyphgiftoy.ui.PixelMatrixView;
import com.anand.glyphgiftoy.ui.RuleAdapter;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return new LiveViewFragment();
                    case 1: return new ModulesFragment();
                    default: return new RulesFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("LIVE VIEW"); break;
                case 1: tab.setText("MODULES"); break;
                case 2: tab.setText("GLYPH RULES"); break;
            }
        }).attach();
    }

    public static class ModulesFragment extends Fragment {
        private MaterialSwitch musicSyncSwitch;
        private ChipGroup musicAnimChipGroup;
        private View musicAdvancedSettings;
        private TextView musicAdvancedToggle;
        private com.google.android.material.slider.Slider musicSensitivitySlider;
        private com.google.android.material.slider.Slider musicIntensitySlider;
        private TextInputEditText timerMinInput;
        private com.google.android.material.textfield.TextInputEditText timerSecInput;
        private Button timerActionBtn;
        private ActivityResultLauncher<String> audioPermissionLauncher;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_modules, container, false);

            musicSyncSwitch = v.findViewById(R.id.musicSyncSwitch);
            musicAnimChipGroup = v.findViewById(R.id.musicAnimChipGroup);
            musicAdvancedSettings = v.findViewById(R.id.musicAdvancedSettings);
            musicAdvancedToggle = v.findViewById(R.id.musicAdvancedToggle);
            musicSensitivitySlider = v.findViewById(R.id.musicSensitivitySlider);
            musicIntensitySlider = v.findViewById(R.id.musicIntensitySlider);
            timerMinInput = v.findViewById(R.id.timerMinInput);
            timerSecInput = v.findViewById(R.id.timerSecInput);
            timerActionBtn = v.findViewById(R.id.timerActionBtn);

            audioPermissionLauncher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted) {
                            toggleMusicSync(true);
                        } else {
                            musicSyncSwitch.setChecked(false);
                            Toast.makeText(getContext(), "Audio permission required for Music Sync", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            musicSyncSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
                    } else {
                        toggleMusicSync(true);
                    }
                } else {
                    toggleMusicSync(false);
                }
            });

            musicAnimChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId != View.NO_ID) {
                    int index = 0;
                    if (checkedId == R.id.chipBars) index = 0;
                    else if (checkedId == R.id.chipSphere) index = 1;
                    else if (checkedId == R.id.chipRipple) index = 2;
                    else if (checkedId == R.id.chipVortex) index = 3;
                    
                    GifGlyphToyService.musicSyncAnimationIndex = index;
                    if (musicSyncSwitch.isChecked()) {
                        toggleMusicSync(true);
                    }
                }
            });

            musicAdvancedToggle.setOnClickListener(view -> {
                if (musicAdvancedSettings.getVisibility() == View.VISIBLE) {
                    musicAdvancedSettings.setVisibility(View.GONE);
                    musicAdvancedToggle.setText("Advanced Settings ▼");
                } else {
                    musicAdvancedSettings.setVisibility(View.VISIBLE);
                    musicAdvancedToggle.setText("Advanced Settings ▲");
                }
            });

            musicSensitivitySlider.addOnChangeListener((slider, value, fromUser) -> {
                GifGlyphToyService.musicSyncSensitivity = value;
                if (musicSyncSwitch.isChecked()) {
                    toggleMusicSync(true);
                }
            });

            musicIntensitySlider.addOnChangeListener((slider, value, fromUser) -> {
                GifGlyphToyService.musicSyncIntensity = value;
                if (musicSyncSwitch.isChecked()) {
                    toggleMusicSync(true);
                }
            });

            timerActionBtn.setOnClickListener(view -> {
                if (GifGlyphToyService.timerRunning) {
                    stopTimer();
                } else {
                    startTimer();
                }
            });

            return v;
        }

        private void toggleMusicSync(boolean enable) {
            Intent i = new Intent(getContext(), GifGlyphToyService.class);
            i.setAction(GifGlyphToyService.ACTION_SET_MODE);
            i.putExtra(GifGlyphToyService.EXTRA_MODE, enable ? GifGlyphToyService.MODE_MUSIC_SYNC : GifGlyphToyService.MODE_ANIMATION);
            i.putExtra(GifGlyphToyService.EXTRA_MUSIC_ANIM_INDEX, GifGlyphToyService.musicSyncAnimationIndex);
            i.putExtra(GifGlyphToyService.EXTRA_MUSIC_SENSITIVITY, GifGlyphToyService.musicSyncSensitivity);
            i.putExtra(GifGlyphToyService.EXTRA_MUSIC_INTENSITY, GifGlyphToyService.musicSyncIntensity);
            getContext().startService(i);
        }

        private void startTimer() {
            String minStr = timerMinInput.getText().toString();
            String secStr = timerSecInput.getText().toString();
            int mins = minStr.isEmpty() ? 0 : Integer.parseInt(minStr);
            int secs = secStr.isEmpty() ? 0 : Integer.parseInt(secStr);
            int totalSecs = mins * 60 + secs;

            if (totalSecs <= 0) {
                Toast.makeText(getContext(), "Please set a duration", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(getContext(), GifGlyphToyService.class);
            i.setAction(GifGlyphToyService.ACTION_SET_MODE);
            i.putExtra(GifGlyphToyService.EXTRA_MODE, GifGlyphToyService.MODE_TIMER);
            i.putExtra(GifGlyphToyService.EXTRA_DURATION_SEC, totalSecs);
            getContext().startService(i);
            
            updateTimerUI(true);
        }

        private void stopTimer() {
            Intent i = new Intent(getContext(), GifGlyphToyService.class);
            i.setAction(GifGlyphToyService.ACTION_SET_MODE);
            i.putExtra(GifGlyphToyService.EXTRA_MODE, GifGlyphToyService.MODE_ANIMATION);
            getContext().startService(i);
            
            updateTimerUI(false);
        }

        private void updateTimerUI(boolean running) {
            timerActionBtn.setText(running ? R.string.stop_timer : R.string.start_timer);
            timerMinInput.setEnabled(!running);
            timerSecInput.setEnabled(!running);
        }

        @Override
        public void onResume() {
            super.onResume();
            musicSyncSwitch.setChecked(GifGlyphToyService.currentMode == GifGlyphToyService.MODE_MUSIC_SYNC);
            
            int index = GifGlyphToyService.musicSyncAnimationIndex;
            if (index == 0) musicAnimChipGroup.check(R.id.chipBars);
            else if (index == 1) musicAnimChipGroup.check(R.id.chipSphere);
            else if (index == 2) musicAnimChipGroup.check(R.id.chipRipple);
            else if (index == 3) musicAnimChipGroup.check(R.id.chipVortex);

            musicSensitivitySlider.setValue(GifGlyphToyService.musicSyncSensitivity);
            musicIntensitySlider.setValue(GifGlyphToyService.musicSyncIntensity);

            updateTimerUI(GifGlyphToyService.timerRunning);
        }
    }

    public static class LiveViewFragment extends Fragment {
        private PixelMatrixView pixelMatrixView;
        private AnimationGenerator animGen = new AnimationGenerator();
        private Handler handler = new Handler(Looper.getMainLooper());
        private int tick = 0;
        private boolean isResumed = false;
        private RadioButton[] radioButtons;

        private final String[] animNames = {
                "Pulse", "Spinner", "Matrix Rain", "Heartbeat", "Pacman", "Space Invader",
                "3D Tunnel", "3D Cube", "3D Sphere", "Equalizer", "Snake", "Rocket",
                "Clock", "Stars", "Fire", "Border Runner", "Bouncing Ball", "Expanding Rings",
                "Gradient Disc"
        };
        private ActivityResultLauncher<String> permissionLauncher;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_live_view, container, false);
            pixelMatrixView = v.findViewById(R.id.pixelMatrixView);

            permissionLauncher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> { if (granted) startGlyph(); }
            );

            v.findViewById(R.id.startBtn).setOnClickListener(view -> requestAndStart());
            v.findViewById(R.id.stopBtn).setOnClickListener(view -> stopGlyph());

            ViewGroup list = v.findViewById(R.id.animList);
            radioButtons = new RadioButton[animNames.length];

            for (int i = 0; i < animNames.length; i++) {
                View itemView = inflater.inflate(R.layout.item_animation, list, false);
                TextView nameText = itemView.findViewById(R.id.animNameText);
                RadioButton rb = itemView.findViewById(R.id.animRadioButton);

                nameText.setText(animNames[i]);
                radioButtons[i] = rb;

                int index = i;
                itemView.setOnClickListener(view -> {
                    GifGlyphToyService.selectedAnimation = index;
                    updateRadioButtons(index);
                    startGlyph();
                });
                list.addView(itemView);
            }

            return v;
        }

        private void updateRadioButtons(int selectedIndex) {
            for (int i = 0; i < radioButtons.length; i++) {
                if (radioButtons[i] != null) {
                    radioButtons[i].setChecked(i == selectedIndex);
                }
            }
        }

        private Runnable animRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isResumed) return;
                pixelMatrixView.setFrame(animGen.getFrame(GifGlyphToyService.selectedAnimation, tick++));
                handler.postDelayed(this, 50);
            }
        };

        @Override
        public void onResume() {
            super.onResume();
            isResumed = true;
            updateRadioButtons(GifGlyphToyService.selectedAnimation);
            handler.post(animRunnable);
        }

        @Override
        public void onPause() {
            super.onPause();
            isResumed = false;
            handler.removeCallbacks(animRunnable);
        }

        private void requestAndStart() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    return;
                }
            }
            startGlyph();
        }

        private void startGlyph() {
            Intent i = new Intent(getContext(), GifGlyphToyService.class);
            getContext().startForegroundService(i);
        }

        private void stopGlyph() {
            Intent i = new Intent(getContext(), GifGlyphToyService.class);
            i.setAction(GifGlyphToyService.ACTION_STOP);
            getContext().startService(i);
        }
    }

    public static class RulesFragment extends Fragment {
        private RuleAdapter adapter;
        private Button permissionBtn;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_rules, container, false);
            RecyclerView rv = v.findViewById(R.id.rulesRecyclerView);
            permissionBtn = v.findViewById(R.id.permissionBtn);
            
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new RuleAdapter(RuleManager.getInstance(getContext()).getRules(), rule -> {
                RuleManager.getInstance(getContext()).deleteRule(rule.getId());
                refreshRules();
            });
            rv.setAdapter(adapter);

            v.findViewById(R.id.addRuleFab).setOnClickListener(view -> {
                startActivity(new Intent(getContext(), AddRuleActivity.class));
            });

            permissionBtn.setOnClickListener(view -> {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            });

            return v;
        }

        @Override
        public void onResume() {
            super.onResume();
            refreshRules();
            checkNotificationPermission();
        }

        private void refreshRules() {
            if (adapter != null) {
                adapter.updateRules(RuleManager.getInstance(getContext()).getRules());
            }
        }

        private void checkNotificationPermission() {
            if (getContext() == null) return;
            boolean enabled = NotificationManagerCompat.getEnabledListenerPackages(getContext())
                    .contains(getContext().getPackageName());
            permissionBtn.setVisibility(enabled ? View.GONE : View.VISIBLE);
        }
    }
}