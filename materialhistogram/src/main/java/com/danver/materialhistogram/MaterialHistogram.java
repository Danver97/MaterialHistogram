package com.danver.materialhistogram;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Christian on 19/05/2017.
 */

public class MaterialHistogram extends View {
    private int mWidth = 400;
    private static final int STANDARD_WIDTH = 400;
    private int desiredWidth = -1;
    private int mHeight = 200;
    private static final int STANDARD_HEIGHT = 200;
    private int desiredHeight;

    private int mBarNumber;
    private int mBarColor;
    private float mBarCorner;
    private boolean mBarAdaptiveThickness;
    private int mBarThickness;
    private int mBarPadding;
    private int mChartAlignment;
    private boolean mChartShowScale;
    private boolean mChartShowAverage;
    private boolean mChartOrientation;
    private boolean mTargetLimitShow;
    private int mTargetLimitValue;
    private int mBarSpacing = convertDpToPixel(8);
    private boolean mAxisShow;
    private int mAverageColor;

    public final static int CHART_ALIGNEMT_CENTER = 0;
    public final static int CHART_ALIGNEMT_LEFT = 1;
    public final static int CHART_ALIGNEMT_RIGHT = 2;

    private float maxValue=0;
    private float averageValue = 0;

    private RectF mBarShape = new RectF();
    private Paint mBarsPaint;
    private Paint mLinePaint;
    private Paint mScaleLevelsPaint;
    private Paint mAverageValuePaint;
    private ArrayList values;

    public MaterialHistogram(Context context, AttributeSet attrs){
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MaterialHistogram,
                0, 0);

        try {
            mBarNumber = a.getInteger(R.styleable.MaterialHistogram_bars_number, 5);
            mBarColor = a.getColor(R.styleable.MaterialHistogram_bars_color, 0xFFFF8650);
            mBarCorner = a.getDimensionPixelSize(R.styleable.MaterialHistogram_bars_corner, convertDpToPixel(2f));
            mBarAdaptiveThickness = a.getBoolean(R.styleable.MaterialHistogram_bars_thickness_adaptive, false);
            mBarThickness = a.getDimensionPixelSize(R.styleable.MaterialHistogram_bars_thickness, convertDpToPixel(16f));
            mBarPadding = a.getDimensionPixelSize(R.styleable.MaterialHistogram_bars_padding, convertDpToPixel(2f));
            mChartAlignment = a.getInteger(R.styleable.MaterialHistogram_chart_alignment, 0);
            mAxisShow = a.getBoolean(R.styleable.MaterialHistogram_axis_show, true);
            mChartShowScale =a.getBoolean(R.styleable.MaterialHistogram_chart_show_scale,false);
            mChartShowAverage =a.getBoolean(R.styleable.MaterialHistogram_chart_show_average,false);
            mAverageColor = a.getColor(R.styleable.MaterialHistogram_average_color,0xFF3399FF);
            //TODO: implement this
            mTargetLimitShow = a.getBoolean(R.styleable.MaterialHistogram_target_limit_show, false);
            mTargetLimitValue =a.getInteger(R.styleable.MaterialHistogram_target_limit_value, 15);
            mChartOrientation = a.getInteger(R.styleable.MaterialHistogram_chart_orientation, 0) == 0;

        } finally {
            a.recycle();
        }
        initDraw();
    }

    private void initDraw() {
        mBarsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarsPaint.setColor(mBarColor);
        mBarsPaint.setStyle(Paint.Style.FILL);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(0xFFBDBDBD);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(convertDpToPixel(2.0f));

        mScaleLevelsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScaleLevelsPaint.setColor(0xFFBDBDBD);
        mScaleLevelsPaint.setStyle(Paint.Style.STROKE);
        mScaleLevelsPaint.setStrokeWidth(convertDpToPixel(1.0f));

        mAverageValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAverageValuePaint.setColor(mAverageColor);
        mAverageValuePaint.setStyle(Paint.Style.STROKE);
        mAverageValuePaint.setStrokeWidth(convertDpToPixel(1.0f));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = mWidth;
        int desiredHeight = mHeight;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            mWidth = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            mWidth = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            mWidth = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            mHeight = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            mHeight = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            mHeight = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas){
        if (values.size()!=0){
            mBarNumber= values.size();
        }
        else{
            for(int i=0; i<5; i++){
                values.add(1);
            }
            Log.w("histogram", "add def elem");
            mBarNumber= values.size();
        }
        int spacing = 0;
        if (mBarAdaptiveThickness){
            mBarPadding = barPadding();
            mBarThickness = mBarPadding*8;
            spacing = spacingToCenter();
        } else {

            switch (mChartAlignment){
                case CHART_ALIGNEMT_CENTER:
                    spacing = spacingToCenter();
                    break;
                case CHART_ALIGNEMT_LEFT:
                    spacing = mBarSpacing;
                    break;
                case CHART_ALIGNEMT_RIGHT:
                    spacing = spacingToCenter()*2 - mBarSpacing;
                    break;
            }
        }

        if(mChartShowScale&&mAxisShow){
            drawScale(maxValue, canvas, mScaleLevelsPaint);
        }

        for(int i = 0; i<mBarNumber; i++){

            float left = spacing + ((mBarThickness + mBarPadding) * i);
            float right = spacing + mBarThickness + ((mBarThickness + mBarPadding) * i);

            if (values.get(i) instanceof Float){
                mBarShape.set(left, fromValueToPixelHeight((Float) values.get(i), maxValue), right, mHeight+mBarCorner);
            } else {
                mBarShape.set(left, fromValueToPixelHeight((int) values.get(i), maxValue), right, mHeight + mBarCorner);
            }

            canvas.drawRoundRect(mBarShape, mBarCorner, mBarCorner, mBarsPaint);
        }

        if(mChartShowAverage){
            drawAverageValue(averageValue,canvas, mAverageValuePaint);
        }

        if (mAxisShow) {
            canvas.drawLine(0.0f, mHeight, mWidth, mHeight, mLinePaint);
        }

        super.onDraw(canvas);
    }

    private static int convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    private int converPixelToDp(int px) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private int spacingToCenter(){
        return (mWidth - mBarThickness*mBarNumber - mBarPadding*(mBarNumber-1))/2;
    }

    private float fromValueToPixelHeight(float value, float maxValue){
        if (value<=0){
            return mHeight - convertDpToPixel(2f);
        } else {
            float coeff = value /maxValue;
            return (float) mHeight - coeff*mHeight;
        }
    }

    private int scaleFactor(float maxValue){
        int x [] = {100, 50, 20, 10, 5, 1};
        int i=0;
        while (i < 6){
            if (maxValue/x[i]>3){
                return x[i];
                //Log.w("histogram", "maxValue: " + maxValue + " || x[i]=" + x[i]);
            }else {
                i++;
            }
        }
        return x[5];
    }

    private void drawScale(float maxValue, Canvas canvas, Paint scaleLevelsPaint){
        int scaleFactor = scaleFactor(maxValue);
        int levelNumber = (int) Math.floor(maxValue/scaleFactor);
        int y;
        for (int i=1; i<=levelNumber; i++){
            y = (int) fromValueToPixelHeight(scaleFactor*i, maxValue);
            canvas.drawLine(0.0f, y, mWidth, y, scaleLevelsPaint);
        }

    }

    private void drawAverageValue(float averageValue, Canvas canvas, Paint averageValuePaint){
        float avgHeight = fromValueToPixelHeight(averageValue, maxValue);
        Log.w("Average", "" + averageValue);
        canvas.drawLine(0.0f, avgHeight, mWidth, avgHeight, averageValuePaint);
    }

    private int barThickness(){
        return ((mWidth-mBarSpacing) - (mBarNumber - 1)*mBarPadding)/(mBarNumber + 1);
    }

    private int barPadding(){
        return (mWidth-2*mBarSpacing)/(mBarNumber*9-1);
    }

    public void setValues(int array[]){
        int lengh = Array.getLength(array);
        values = new ArrayList<>(lengh);
        for(int i=0; i<lengh; i++){
            values.add(array[i]);
            averageValue += array[i];
            if (array[i]>maxValue){
                maxValue=array[i];
            }
            averageValue /= lengh;
        }

        //(lengh+1) is for a little spacing before and after the first and second bar
        /*if(!mBarAdaptiveThickness) {
            Log.w("invalidated!!!", "invalidated!!!  " + mWidth);
            mWidth = (lengh + 1) * mBarThickness + (lengh - 1) * mBarPadding;
            Log.w("invalidated!!!", "invalidated!!!  " + mWidth);

            // il setAdaptive ha effetto sulla mWidth cambiata dopo il setValues:
            // quindi di fatto mWidth viene calcolata per le barre di larghezza fissa.
            // dunque l'inizializzazione globale di mWidth a 400 viene ignorata dopo
            // il setValues poichè il valore di mWidth viene ricalcolato nel setValues
            // e il valore standard di 400 viene perso e non riassegnato.
            // onDraw tiene quindi conto dei ricalcoli e non del valore standard E dei valori attesi

            //Morale: 400 deve essere riassegnato nel caso di un cambiamento di AdaptiveStatus

            invalidate();
            requestLayout();
        }*/
        updateLayout();
    }

    public void setValues(float array[]){
        int lengh = Array.getLength(array);
        values = new ArrayList<>(lengh);
        for(int i=0; i<lengh; i++){
            values.add(array[i]);
            averageValue += array[i];
            if (array[i]>maxValue){
                maxValue=array[i];
            }
        }
        averageValue /= lengh;
        updateLayout();
    }

    public void setValues(ArrayList<? extends Number> arrayList){
        int lengh = arrayList.size();
        values = new ArrayList<>(lengh);
        for(int i=0; i<lengh; i++){
            values.add(arrayList.get(i));
            averageValue += arrayList.get(i).floatValue();
            if (arrayList.get(i).floatValue()>maxValue){
                maxValue=arrayList.get(i).floatValue();
            }
        }
        averageValue /= lengh;
        updateLayout();
    }

    private void updateLayout(){

        if(!mBarAdaptiveThickness) {
            if(desiredWidth > 0){
                mWidth = desiredWidth;
            } else {
                int lengh = values.size();
                mWidth = (lengh) * mBarThickness + (lengh - 1) * mBarPadding + 2 * mBarSpacing;
            }
        } else {
            if(desiredWidth > 0){
                mWidth = desiredWidth;
            } else {
                mWidth = STANDARD_WIDTH;
            }
        }

        invalidate();
        requestLayout();
    }

    public void setBarColor(@ColorInt int color){
        mBarColor = color;
        mBarsPaint.setColor(mBarColor);
        updateLayout();
    }

    public void setBarCorner(float dp){
        mBarCorner = convertDpToPixel(dp);
        updateLayout();
    }

    public void setBarThickness(float dp){
        mBarThickness = convertDpToPixel(dp);
        updateLayout();
    }

    public void setBarPadding(float dp){
        mBarPadding = convertDpToPixel(dp);
        updateLayout();
    }

    public void setBarAdaptiveThickness(boolean status){
        mBarAdaptiveThickness = status;
        updateLayout();
    }

    public void setShowAverage(boolean status){
        mChartShowAverage = status;
        updateLayout();
    }

    public void setAverageColor(@ColorInt int color){
        mAverageColor = color;
        mAverageValuePaint.setColor(color);
        updateLayout();
    }

    public void setChartAlignment(int alignment){
        mChartAlignment = alignment;
        updateLayout();
    }

    public void setHeight(int dpHeight){
        mHeight = convertDpToPixel(dpHeight);
        updateLayout();
    }

    public void setWidth(int dpWidth){
        desiredWidth = convertDpToPixel(dpWidth);
        updateLayout();
    }

    ///////////
    //GETTERS//
    ///////////


    public int getBarColor() {
        return mBarColor;
    }

    public ArrayList getValues() {
        return values;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public int getBarNumber() {
        return mBarNumber;
    }

    public int getChartAlignment() {
        return mChartAlignment;
    }

    public boolean getBarAdaptiveThicknessStatus(){
        return mBarAdaptiveThickness;
    }

    public int getBarThicknessDp(){
        return converPixelToDp(mBarThickness);
    }

    public int getBarPaddingDp(){
        return converPixelToDp(mBarPadding);
    }

    public int getBarCornerDp(){
        return converPixelToDp((int) mBarCorner);
    }

    public float getAverageValue(){
        return averageValue;
    }

    public boolean getShowAverageStatus(){
        return mChartShowAverage;
    }

    public int getAverageColor(){
        return mAverageColor;
    }

}
