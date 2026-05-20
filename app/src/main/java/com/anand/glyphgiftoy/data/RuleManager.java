package com.anand.glyphgiftoy.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.anand.glyphgiftoy.models.GlyphRule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RuleManager {
    private static final String PREF_NAME = "glyph_rules";
    private static final String KEY_RULES = "rules";
    private static RuleManager instance;
    private SharedPreferences prefs;
    private Gson gson;

    private RuleManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized RuleManager getInstance(Context context) {
        if (instance == null) {
            instance = new RuleManager(context);
        }
        return instance;
    }

    public List<GlyphRule> getRules() {
        String json = prefs.getString(KEY_RULES, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<GlyphRule>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveRules(List<GlyphRule> rules) {
        String json = gson.toJson(rules);
        prefs.edit().putString(KEY_RULES, json).apply();
    }

    public void addRule(GlyphRule rule) {
        List<GlyphRule> rules = getRules();
        rules.add(rule);
        saveRules(rules);
    }

    public void deleteRule(String ruleId) {
        List<GlyphRule> rules = getRules();
        rules.removeIf(r -> r.getId().equals(ruleId));
        saveRules(rules);
    }

    public GlyphRule getRuleForPackage(String packageName) {
        for (GlyphRule rule : getRules()) {
            if (rule.getPackageName().equals(packageName) && rule.isEnabled()) {
                return rule;
            }
        }
        return null;
    }
}