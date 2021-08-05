package com.misiojab.mj.mjound;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.Arrays;

public class BezierView extends View {

    //Paint paint;
    Path path = new Path();
    Paint pathPaint = new Paint();
    Path borderPath = new Path();
    Paint borderPathPaint = new Paint();
    //int anc0X, anc0Y, anc1X, anc1Y;
    int[] xx = new int[5];
    int[] yy = new int[5];
    int[] x1;
    int[] y1;
    boolean draw;
    //int[] xx = new int[]{165, 349, 533, 717, 901};
    //int[] yy = new int[]{882, 934, 934, 934, 882};

    public ArrayList<PointF> points = new ArrayList<>();
    public ArrayList<PointF> conPoint1 = new ArrayList<>();
    public ArrayList<PointF> conPoint2 = new ArrayList<>();

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context);
        init();
    }

    public BezierView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        borderPathPaint.setColor(Color.WHITE);
        borderPathPaint.setStyle(Paint.Style.STROKE);
        borderPathPaint.setStrokeWidth(4f);

        if (SavedData.readString(SavedData.X_CORD, getContext()) != null
                && SavedData.readString(SavedData.Y_CORD, getContext()) != null){
            xx = equalizerStringToArray(SavedData.readString(SavedData.X_CORD, getContext()));
            yy = equalizerStringToArray(SavedData.readString(SavedData.Y_CORD, getContext()));

            if (xx != null && yy != null){
                try {
                    calculatePoints();
                    calculateConnectionPoints();

                } catch (Exception e){

                }

            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        Log.e("Rysowanie", "no rysuje tutaj");
        update();
        drawCurve(canvas);

        try {

            //canvas.drawPath(path, pathPaint);
            //canvas.drawPath(borderPath, borderPathPaint);
        } catch (Exception e){

        }

    }

    private void calculatePoints(){
        points = new ArrayList<>();

        for (int i = 0; i < xx.length; i++){
            points.add(i, new PointF(xx[i], yy[i]));
        }
    }

    private void calculateConnectionPoints(){
        try {
            conPoint1 = new ArrayList<>();
            conPoint2 = new ArrayList<>();
            for (int i = 1; i < points.size(); i++){
                conPoint1.add(new PointF((points.get(i).x + points.get(i - 1).x)/2, points.get(i - 1).y));
                conPoint2.add(new PointF((points.get(i).x + points.get(i - 1).x) / 2, points.get(i).y));
            }
        } catch (Exception e) {

        }
    }

    public void drawCurve(Canvas canvas){
        try {
            if (points.isEmpty() && conPoint1.isEmpty() && conPoint2.isEmpty()) return;

            path.reset();
            path.moveTo(points.get(0).x, points.get(0).y);

            for (int i = 1; i < points.size(); i++){
                path.cubicTo(
                        conPoint1.get(i - 1).x, conPoint1.get(i - 1).y,
                        conPoint2.get(i - 1).x, conPoint2.get(i - 1).y,
                        points.get(i).x, points.get(i).y
                );
            }
            borderPath.set(path);
            canvas.drawPath(borderPath, borderPathPaint);


        } catch (Exception e){

        }

    }

    public int[] equalizerStringToArray (String string){
        if (string != ""){
            String[] stringArray = string.split(";");
            int[] equalizerIntArray = new int[stringArray.length];
            try {
                for (int i = 0; i < stringArray.length; i++){
                    equalizerIntArray[i] = Integer.parseInt(stringArray[i]);
                }
            } catch (Exception e){

            }


            return equalizerIntArray;
        } else {
            return null;
        }

    }

    public void update (){
        if (!Arrays.equals(yy, equalizerStringToArray(SavedData.readString(SavedData.Y_CORD, getContext())))) {
            if (SavedData.readString(SavedData.X_CORD, getContext()) != null
                    && SavedData.readString(SavedData.Y_CORD, getContext()) != null){
                xx = equalizerStringToArray(SavedData.readString(SavedData.X_CORD, getContext()));
                yy = equalizerStringToArray(SavedData.readString(SavedData.Y_CORD, getContext()));

                if (xx != null && yy != null){
                    try {
                        calculatePoints();
                        calculateConnectionPoints();
                        invalidate();
                    } catch (Exception e){

                    }

                }
            }
        }
    }

}