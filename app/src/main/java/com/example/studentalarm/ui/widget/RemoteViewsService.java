package com.example.studentalarm.ui.widget;

import android.content.Intent;

public class RemoteViewsService extends android.widget.RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new com.example.studentalarm.ui.widget.RemoteViewsFactory(this.getApplicationContext());
    }
}
