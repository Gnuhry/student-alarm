package com.example.studentalarm.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.ViewGroup;

import com.example.studentalarm.R;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;

import androidx.annotation.Nullable;

public class OwnShowcaseView extends ShowcaseView {

    private Activity activity;

    public OwnShowcaseView(Context context) {
        super(context, false);
    }

    protected OwnShowcaseView(Activity activity, Target target, String title, String subtitle, @Nullable OnClickListener listener) {
        super(activity, false);
        this.activity = activity;
        this.setOnTouchListener((view, motionEvent) -> {
            this.hide();
            if (listener != null)
                listener.onClick(this);
            this.performClick();
            return true;
        });
        this.hideButton();
        this.setStyle(R.style.CustomShowcaseTheme);
        this.setHideOnTouchOutside(true);
        this.setTarget(target);
        this.setContentTitle(title);
        this.setContentText(subtitle);
        this.setOnClickListener(listener);
        this.setTag(false);
        this.setTitleTextAlignment(Layout.Alignment.ALIGN_CENTER);
        this.setDetailTextAlignment(Layout.Alignment.ALIGN_CENTER);

    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void show() {
        ((ViewGroup) activity.findViewById(android.R.id.content)).addView(this);
        super.show();
    }
}
