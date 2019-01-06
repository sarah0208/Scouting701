package com.vandenrobotics.saga.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

import com.vandenrobotics.saga2018.R;

public class EventListView extends ListView {

    private Context ctx;

    private static int indWidth = 40;
    private String[] sections;
    private float scaledWidth;
    private float indexBarX;
    private int indexSize;
    private String section;
    private Handler listHandler;

    public EventListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctx = context;

    }

    public EventListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;

    }

    public EventListView(Context context) {
        super(context);
        ctx = context;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(sections!=null) {
            scaledWidth = indWidth * getSizeInPixel(ctx);
            indexBarX = this.getWidth() - this.getPaddingRight() - scaledWidth;

            Paint p = new Paint();
            p.setColor(ResourcesCompat.getColor(getResources(), R.color.BackGroundGreen, null));
            p.setAlpha(200);

            canvas.drawRect(indexBarX, this.getPaddingTop(), indexBarX + scaledWidth,
                    this.getHeight() - this.getPaddingBottom(), p);

            indexSize = (this.getHeight() - this.getPaddingTop() - getPaddingBottom())
                    / sections.length;

            Paint textPaint = new Paint();
            textPaint.setColor(getResources().getColor(R.color.Gold));
            textPaint.setTextSize(scaledWidth / 3);

            for (int i = 0; i < sections.length; i++)
                canvas.drawText(sections[i].toUpperCase(),
                        indexBarX + textPaint.getTextSize() / 2, getPaddingTop()
                                + indexSize * (float) (i + 0.5), textPaint);
        }
    }

    private static float getSizeInPixel(Context ctx) {
        return ctx.getResources().getDisplayMetrics().density;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof SectionIndexer)
            sections = (String[]) ((SectionIndexer) adapter).getSections();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            float x = event.getX();

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE: {
                    if (x < indexBarX) {
                        return super.onTouchEvent(event);
                    } else {
                        float y = event.getY();
                        int currentPosition = (int) Math.floor(y / indexSize);
                        try {
                            section = sections[currentPosition];
                            this.setSelection(((SectionIndexer) getAdapter())
                                    .getPositionForSection(currentPosition));
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                            this.setSelection(((SectionIndexer) getAdapter())
                                    .getPositionForSection(0));
                        }
                        return true;
                    }
                }
                case MotionEvent.ACTION_UP: {
                    if (x < indexBarX) {
                        listHandler = new ListHandler();
                        listHandler.sendEmptyMessageDelayed(0, 30 * 1000);
                        return true;
                    } else {
                        // We touched the index bar
                        float y = event.getY() - this.getPaddingTop() - getPaddingBottom();
                        int currentPosition = (int) Math.floor(y / indexSize);
                        try {
                            section = sections[currentPosition];
                            this.setSelection(((SectionIndexer) getAdapter())
                                    .getPositionForSection(currentPosition));
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                            this.setSelection(((SectionIndexer) getAdapter())
                                    .getPositionForSection(0));
                        }

                        return true;
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        super.onTouchEvent(event);
        return true;
    }

    private class ListHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            EventListView.this.invalidate();
        }
    }
}
