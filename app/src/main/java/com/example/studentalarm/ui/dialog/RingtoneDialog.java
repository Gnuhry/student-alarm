package com.example.studentalarm.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.studentalarm.R;
import com.example.studentalarm.Ringtone;
import com.example.studentalarm.save.PreferenceKeys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

public class RingtoneDialog extends Dialog {

    public final static int REQUEST_CODE = 3;
    @Nullable
    private static MediaPlayer mediaPlayer;
    @NonNull
    private final Activity activity;
    @NonNull
    private final Context context;
    private final String ringtoneOld;
    private RadioGroup rg;
    private boolean save = false;


    public RingtoneDialog(@NonNull Context context, @NonNull Activity activity) {
        super(context);
        this.activity = activity;
        this.context = context;
        ringtoneOld = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.RINGTONE, PreferenceKeys.DEFAULT_RINGTONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ringtone);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        String selectedRingtone = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.RINGTONE, PreferenceKeys.DEFAULT_RINGTONE);
        rg = findViewById(R.id.rGRingTone);
        for (String ringtone : activity.getResources().getStringArray(R.array.ringtone)) {
            RadioButton rb = new RadioButton(context);
            rb.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rb.setText(ringtone);
            rg.addView(rb);
            if (selectedRingtone.equals(ringtone))
                rb.setChecked(true);
        }
        RadioButton rb = new RadioButton(context);
        rg.addView(rb);
        if (selectedRingtone.startsWith("|"))
            rb.setChecked(true);
        String custom = activity.getString(R.string.custom);
        if (selectedRingtone.startsWith("|")) {
            String[] split = selectedRingtone.split("/");
            custom += " " + split[split.length - 1];
        }
        rb.setText(custom);
        rb.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rb.setOnClickListener(view -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                if (mediaPlayer != null)
                    mediaPlayer.stop();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");

                activity.startActivityForResult(intent, REQUEST_CODE);
            } else
                Toast.makeText(context, activity.getString(R.string.not_supported_in_your_android_version), Toast.LENGTH_LONG).show();
        });

        rg.setOnCheckedChangeListener((radioGroup, i) -> {
            if (mediaPlayer != null)
                mediaPlayer.stop();
            String text = ((RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();

            String selectedRingtoneHelp = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.RINGTONE, PreferenceKeys.DEFAULT_RINGTONE);
            String customHelp = activity.getString(R.string.custom);
            if (selectedRingtoneHelp.startsWith("|"))
                customHelp += " " + Uri.parse(selectedRingtone.substring(1)).getLastPathSegment();
            ((RadioButton) rg.getChildAt(rg.getChildCount() - 1)).setText(customHelp);
            mediaPlayer = Ringtone.getConstantRingtone(text, getContext(), false);
            if (mediaPlayer != null) {
                mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            }
        });
        findViewById(R.id.txVCancel).setOnClickListener(view -> dismiss());
        findViewById(R.id.txVSave).setOnClickListener(view -> {
            save = true;
            String text = ((RadioButton) rg.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
            String customHelp = activity.getString(R.string.custom);
            if (!text.startsWith(customHelp))
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PreferenceKeys.RINGTONE, text).apply();
            dismiss();
        });

    }

    @Override
    public void cancel() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        if (!save && ((RadioButton) rg.getChildAt(rg.getChildCount() - 1)).isChecked())
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PreferenceKeys.RINGTONE, ringtoneOld).apply();
    }

    /**
     * set result Intent
     *
     * @param intent   intent from the result
     * @param activity activity of app
     */
    public static void setResultIntent(@Nullable Intent intent, @NonNull Activity activity) {
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
                MediaPlayer mediaPlayerHelp = MediaPlayer.create(activity, Uri.parse(uri.toString()));
                if (mediaPlayerHelp != null) {
                    PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(PreferenceKeys.RINGTONE, "|" + uri.toString()).apply();
                    mediaPlayer = mediaPlayerHelp;
                    mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                } else
                    Toast.makeText(activity, R.string.not_a_song_file, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

