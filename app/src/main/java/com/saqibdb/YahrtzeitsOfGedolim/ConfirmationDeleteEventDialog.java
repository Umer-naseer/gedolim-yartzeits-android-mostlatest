package com.saqibdb.YahrtzeitsOfGedolim;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ConfirmationDeleteEventDialog extends Dialog {
    public TextView title, message, dialogCancel, dialogDelete;

    public ConfirmationDeleteEventDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.confirmation_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCancel = findViewById(R.id.dialogCancel);
        dialogDelete = findViewById(R.id.dialogDelete);

    }

}
