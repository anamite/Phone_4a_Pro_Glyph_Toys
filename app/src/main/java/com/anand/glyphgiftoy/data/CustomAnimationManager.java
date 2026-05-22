package com.anand.glyphgiftoy.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.anand.glyphgiftoy.models.CustomAnimation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CustomAnimationManager {
    private static final String PREF_NAME = "custom_animations";
    private static final String KEY_ANIMS = "animations";
    private static CustomAnimationManager instance;
    private SharedPreferences prefs;
    private Gson gson;

    private CustomAnimationManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized CustomAnimationManager getInstance(Context context) {
        if (instance == null) {
            instance = new CustomAnimationManager(context);
        }
        return instance;
    }

    public List<CustomAnimation> getAnimations() {
        String json = prefs.getString(KEY_ANIMS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<CustomAnimation>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public CustomAnimation getAnimation(String id) {
        for (CustomAnimation anim : getAnimations()) {
            if (anim.getId().equals(id)) {
                return anim;
            }
        }
        return null;
    }

    public void saveAnimations(List<CustomAnimation> animations) {
        String json = gson.toJson(animations);
        prefs.edit().putString(KEY_ANIMS, json).apply();
    }

    public void addAnimation(CustomAnimation anim) {
        if (anim == null) return;
        anim.ensureId();
        List<CustomAnimation> animations = getAnimations();
        animations.add(anim);
        saveAnimations(animations);
    }

    public void deleteAnimation(String id) {
        List<CustomAnimation> animations = getAnimations();
        animations.removeIf(a -> a.getId().equals(id));
        saveAnimations(animations);
    }
}
