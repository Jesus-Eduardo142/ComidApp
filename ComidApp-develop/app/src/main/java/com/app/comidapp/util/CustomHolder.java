package com.app.comidapp.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.comidapp.R;

public class CustomHolder {

    TextView name;
    ImageView profile;

    public CustomHolder(View v) {
        name = v.findViewById(R.id.name);
        profile = v.findViewById(R.id.profile);
    }
}
