package com.anand.glyphgiftoy.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.anand.glyphgiftoy.R;
import com.anand.glyphgiftoy.models.GlyphRule;
import java.util.List;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.ViewHolder> {

    private List<GlyphRule> rules;
    private OnDeleteClickListener deleteListener;
    private final String[] animNames = {
            "Pulse", "Spinner", "Matrix Rain", "Heartbeat", "Pacman", "Space Invader",
            "3D Tunnel", "3D Cube", "3D Sphere", "Equalizer", "Snake", "Rocket",
            "Clock", "Stars", "Fire", "Border Runner", "Bouncing Ball", "Expanding Rings",
            "Gradient Disc"
    };

    public interface OnDeleteClickListener {
        void onDeleteClick(GlyphRule rule);
    }

    public RuleAdapter(List<GlyphRule> rules, OnDeleteClickListener deleteListener) {
        this.rules = rules;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GlyphRule rule = rules.get(position);
        holder.appName.setText(rule.getAppName());
        holder.ruleDetails.setText(animNames[rule.getAnimationIndex()] + " (" + rule.getDurationSec() + "s)");

        try {
            Drawable icon = holder.itemView.getContext().getPackageManager().getApplicationIcon(rule.getPackageName());
            holder.appIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            holder.appIcon.setImageResource(android.R.drawable.sym_def_app_icon);
        }

        holder.deleteBtn.setOnClickListener(v -> deleteListener.onDeleteClick(rule));
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    public void updateRules(List<GlyphRule> newRules) {
        this.rules = newRules;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName, ruleDetails;
        ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            ruleDetails = itemView.findViewById(R.id.ruleDetails);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}