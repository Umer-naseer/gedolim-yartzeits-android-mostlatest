package com.saqibdb.YahrtzeitsOfGedolim;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import android.widget.TextView;

public class AddEventDialog extends Dialog {
    public TextView title, message, ok;

    public AddEventDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        title = findViewById(R.id.dialogTitle);
        ok = findViewById(R.id.dialogOk);
        message = findViewById(R.id.dialogMsg);
    }

}
