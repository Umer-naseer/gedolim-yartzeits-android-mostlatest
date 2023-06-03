package com.saqibdb.YahrtzeitsOfGedolim;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import android.widget.TextView;

public class DeleteEventDialog extends Dialog {
    public TextView title, message, ok;

    public DeleteEventDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_delete);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ok = findViewById(R.id.dialogOk);
    }

}
