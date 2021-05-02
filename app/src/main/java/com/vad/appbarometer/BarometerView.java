package com.vad.appbarometer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class BarometerView extends View {

    private Paint mPaint;
    private RectF rectF;

    private final static int START_ANGLE = 135;
    private final static int END_ANGLE = 270;


    public BarometerView(Context context) {
        super(context);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.teal_700));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(15);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = getWidth()/2;
        float cy = getHeight()/2;

        float radius = getHeight()/2-40;
        rectF = new RectF();

        rectF.set(
                cx-radius,
                cx-radius,
                cx+radius,
                cx+radius
        );

        ///canvas.drawCircle(cx, cy, radius, mPaint);
        canvas.drawArc(
                rectF,
                START_ANGLE,
                END_ANGLE,
                false,
                mPaint
        );

        for (int i = 0; i < 280; i+=10) {
            onDrawLine(canvas, radius, i);
        }
    }

    private void onDrawLine(Canvas canvas, float rad, int step){
        float stopX = (float) (getWidth()/2+(rad-20)*Math.cos(Math.toRadians(START_ANGLE+step)));
        float stopY = (float) (getHeight()/2+(rad-20)*Math.sin(Math.toRadians(START_ANGLE+step)));

        float startX = (float) (getHeight()/2+(rad-60)*Math.cos(Math.toRadians(START_ANGLE+step)));;
        float startY = (float) (getHeight()/2+(rad-60)*Math.sin(Math.toRadians(START_ANGLE+step)));;
        canvas.drawLine(startX, startY, stopX, stopY, mPaint);
    }
}
