package com.misiojab.mj.mjound;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class BezierView extends View {

    Paint paint;
    Path path;
    int anc0X, anc0Y, anc1X, anc1Y;

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BezierView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint = new Paint();
        path = new Path();



        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(255, 1, 1));


        path.moveTo(10, this.getWidth()/2);
/*
        int[] points = equalizerStringToArray(SavedData.readString(SavedData.EQUALIZERVALUES, getContext()));

        anc1X = (points[0] + points[2])/2;
        anc1Y = points[1];
        anc0Y = (points[0] + points[2])/2;
        anc0X = points[3];

        path.moveTo(points[0], points[1]);
        paint.setAntiAlias(true);

 */
        //path.cubicTo(anc0X, anc0Y, anc1X, anc1Y, points[2], points[3]);

        path.cubicTo(anc0X, anc0Y, anc1X, anc1Y, 300, 300);


        canvas.drawPath(path, paint);

    }

    public float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public int[] equalizerStringToArray (String string){
        String[] stringArray = string.split(";");
        int[] equalizerIntArray = new int[stringArray.length];

        for (int i = 0; i < stringArray.length; i++){
            equalizerIntArray[i] = Integer.parseInt(stringArray[i]);
        }

        return equalizerIntArray;
    }

}