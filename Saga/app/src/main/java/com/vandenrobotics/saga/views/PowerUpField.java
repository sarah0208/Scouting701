package com.vandenrobotics.saga.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.vandenrobotics.saga2018.R;


/**
 * Created by sarah on 1/7/2018.
 */

public class PowerUpField extends View {
    static Paint paint;

    Bitmap keyField;

    private long then;

    Canvas keyCanvas;
    Context contextThis;

    int switchTopRight;
    int switchTopLeft;
    int switchBottomRight;
    int switchBottomLeft;
    int scaleTop;
    int scaleBottom;
    int exchange;

    void init(AttributeSet attributeSet, Context context) {
        paint = new Paint();
        paint.setColor(Color.BLACK);

        contextThis = context;


        switchTopRight = Color.argb(255, 255, 0, 0);
        switchTopLeft = Color.argb(255, 255, 128, 0);           // Gear spot color easy reference
        switchBottomRight = Color.argb(255, 0, 128, 64);
        switchBottomLeft = Color.argb(255, 128, 64, 0);

        scaleTop = Color.argb(255, 128, 0, 128);
        scaleBottom = Color.argb(255, 255, 128, 255);
        exchange = Color.argb(255, 0, 0, 255);



    }

    public PowerUpField(Context context) {
        super(context);

        init(null, context);
    }

    public PowerUpField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs, context);
    }

    public PowerUpField(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs, context);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            keyField = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.fieldmask);
            keyField = Bitmap.createScaledBitmap(keyField, right, bottom, false);

            keyCanvas = new Canvas(keyField);

        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                then = System.currentTimeMillis();
                return true;
            case MotionEvent.ACTION_UP:
                return onTouch(x, y);
        }

        return false;
    }

    private boolean onTouch(int x, int y) {
        int i = keyField.getPixel(x, y);

        if (i == Color.WHITE) {
            return false;
        } else if (i == switchTopLeft) {                                                                        // Gear spot color
            //openGearSpot(x, y);
            Log.d("Top left switch", x+"/"+y);
        } else if (i == switchTopRight) {
            Log.d("Top right switch", x+"/"+y);
        } else if (i == switchBottomLeft) {
            Log.d("Bottom left switch", x+"/"+y);
        } else if (i == switchBottomRight) {
            Log.d("Bottom right switch", x+"/"+y);
        } else if (i == scaleTop) {
            Log.d("Top scale", x+"/"+y);
        } else if (i == scaleBottom) {
            Log.d("Bottom scale", x+"/"+y);
        }

        return false;
    }
}
