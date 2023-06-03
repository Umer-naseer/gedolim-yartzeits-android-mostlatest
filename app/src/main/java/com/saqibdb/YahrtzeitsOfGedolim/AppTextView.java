package com.saqibdb.YahrtzeitsOfGedolim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.util.AttributeSet;

public class AppTextView extends androidx.appcompat.widget.AppCompatTextView {

    public Paint paint;
    public boolean addStrike = false;

    public AppTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) init(attrs);
    }

    public AppTextView(Context context) {
        super(context);
    }

    private void init(AttributeSet attrs) {
        //TypedArray attrsArray = getContext().obtainStyledAttributes(attrs, R.styleable.apptext);

        try {
            int style = 1;//attrsArray.getInteger(R.styleable.apptext_textStyle, 5);

            String fontFamily;
            switch (style) {
                case 0:
                    fontFamily = "Thin";
                    break;
                case 1:
                    fontFamily = "Light";
                    break;
                case 2:
                    fontFamily = "Regular";
                    break;
                case 3:
                    fontFamily = "Bold";
                    break;
                case 4:
                    fontFamily = "Semibold";
                    break;
                default:
                    fontFamily = "Regular";
                    break;
            }

           // String fontName = getContext().getString(R.string.font_name) + "-" + fontFamily;
            //setTypeface(TypefaceProvider.getTypeFace(getContext(), fontName));
            setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(getResources().getDisplayMetrics().density * 1);

        } finally {
            //attrsArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (addStrike) {
            canvas.drawLine(0, getHeight() / 2, getWidth(),
                    getHeight() / 2, paint);
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null) {
            super.setText(text, type);

        }
    }

    public void setHTMLText(String value) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            setText(value != null ? Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY) : "");
        } else {
            setText(value != null ? Html.fromHtml(value) : "");
        }
    }
}