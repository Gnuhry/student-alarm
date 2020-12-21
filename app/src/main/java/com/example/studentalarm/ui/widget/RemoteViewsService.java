package com.example.studentalarm.ui.widget;

import android.content.Intent;

import androidx.annotation.NonNull;

public class RemoteViewsService extends android.widget.RemoteViewsService {
    @NonNull
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new com.example.studentalarm.ui.widget.RemoteViewsFactory(this.getApplicationContext());
    }
}
