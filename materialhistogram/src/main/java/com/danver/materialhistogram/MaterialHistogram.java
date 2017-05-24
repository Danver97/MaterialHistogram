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
    private boolean mChartOrientation;
    private boolean mTargetLimitShow;
    private int mTargetLimitValue;
    private int mBarSpacing = convertDpToPixel(8);
    private boolean mAxisShow;

    public final static int CHART_CENTER = 0;
    public final static int CHART_LEFT = 1;
    public final static int CHART_RIGHT = 2;

    float maxValue=0;

    private RectF mBarShape = new RectF();
    private Paint mBarsPaint;
    private Paint mLinePaint;
    private ArrayList<Integer> valuesInt = new ArrayList<>(0);
    private ArrayList<Float> valuesFloat;
    private boolean valuesType;

    public MaterialHistogram(Context context, AttributeSet attrs){
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MaterialHistogram,
                0, 0);
        /*
        valuesInt.add(0);
        valuesInt.add(6);
        valuesInt.add(10);
        valuesInt.add(5);
        valuesInt.add(4);
        */

        try {
            mBarNumber = a.getInteger(R.styleable.MaterialHistogram_bars_number, 5);
            mBarColor = a.getColor(R.styleable.MaterialHistogram_bars_color, 0xFFFF8650);
            mBarCorner = a.getDimensionPixelSize(R.styleable.MaterialHistogram_bars_corner, convertDpToPixel(2f));
            mBarAdaptiveThickness = a.getBoolean(R.styleable.MaterialHistogram_bars_thickness_adaptive, false);
            mBarThickness = a.getDimensionPixelSize(R.styleable.MaterialHistogram_bars_thickness, convertDpToPixel(16f));
            mBarPadding = a.getDimensionPixelSize(R.styleable.MaterialHistogram_bars_padding, convertDpToPixel(2f));
            mChartAlignment = a.getInteger(R.styleable.MaterialHistogram_chart_alignment, 0);
            mAxisShow = a.getBoolean(R.styleable.MaterialHistogram_axis_show, true);
            mChartShowScale =a.getBoolean(R.styleable.MaterialHistogram_chart_show_scale,true);
            //TODO: implement this
            mTargetLimitShow = a.getBoolean(R.styleable.MaterialHistogram_target_limit_show, false);
            mTargetLimitValue =a.getInteger(R.styleable.MaterialHistogram_target_limit_value, 15);
            if(a.getInteger(R.styleable.MaterialHistogram_chart_orientation, 0) == 0){
                mChartOrientation = true;
            } else {
                mChartOrientation = false;
            }

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
        if (valuesInt.size()!=0){
            mBarNumber=valuesInt.size();
        }
        else{
            for(int i=0; i<5; i++){
                valuesInt.add(1);
            }
            Log.w("histogram", "add def elem");
            mBarNumber=valuesInt.size();
        }
        int spacing = 0;
        if (mBarAdaptiveThickness){
            mBarPadding = barPadding();
            mBarThickness = mBarPadding*8;
            spacing = spacingToCenter();
        } else {

            switch (mChartAlignment){
                case CHART_CENTER:
                    spacing = spacingToCenter();
                    break;
                case CHART_LEFT:
                    spacing = mBarSpacing;
                    break;
                case CHART_RIGHT:
                    spacing = spacingToCenter()*2 - mBarSpacing;
                    break;
            }
        }

        if(mChartShowScale){
            drawScale(maxValue, canvas);
        }

        for(int i = 0; i<mBarNumber; i++){

            float left = spacing + ((mBarThickness + mBarPadding) * i);
            float right = spacing + mBarThickness + ((mBarThickness + mBarPadding) * i);

            mBarShape.set(left, fromValueToPixelHeight(valuesInt.get(i), maxValue), right, mHeight+mBarCorner);
            canvas.drawRoundRect(mBarShape, mBarCorner, mBarCorner, mBarsPaint);
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

    private int spacingToCenter(){
        return (int) ((mWidth - mBarThickness*mBarNumber - mBarPadding*(mBarNumber-1))/2);
    }

    private float fromValueToPixelHeight(float value, float maxValue){
        if (value<=0){
            return mHeight - convertDpToPixel(3f);
        } else {
            float coeff = (float) value/maxValue;
            return (float) mHeight - coeff*mHeight;
        }
    }

    private int scaleFactor(float maxValue){
        int x [] = {100, 50, 20, 10, 5, 1};
        boolean ended=false;
        int i=0;
        while (!ended){
            if (maxValue/x[i]>3){
                ended=true;
            }
            i++;
        }
        return x[i];
    }

    private void drawScale(float maxValue, Canvas canvas){
        int scaleFactor = scaleFactor(maxValue);
        int levelNumber = (int) (maxValue/scaleFactor);
        int y;
        for (int i=1; i<=levelNumber; i++){
            y = (int) fromValueToPixelHeight(scaleFactor*i, maxValue);
            canvas.drawLine(0.0f, y, mWidth, y, mLinePaint);
        }

    }

    private int barThickness(){
        return ((mWidth-mBarSpacing) - (mBarNumber - 1)*mBarPadding)/(mBarNumber + 1);
    }

    private int barPadding(){
        return (mWidth-2*mBarSpacing)/(mBarNumber*9-1);
    }

    public void setValuesInteger(int array[]){
        int lengh = Array.getLength(array);
        valuesInt = new ArrayList<Integer>(lengh);
        for(int i=0; i<lengh; i++){
            valuesInt.add(array[i]);
            if (array[i]>maxValue){
                maxValue=array[i];
            }
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

    public void setValuesInteger(ArrayList<Integer> arrayList){
        int lengh = arrayList.size();
        valuesInt = new ArrayList<Integer>(lengh);
        for (int i=0; i<lengh; i++){
            valuesInt.add(arrayList.get(i));
            if (maxValue < valuesInt.get(i)) {
                maxValue = valuesInt.get(i);
            }

        }

        //(lengh+1) is for a little spacing before and after the first and second bar
        /*if(!mBarAdaptiveThickness) {
            mWidth = (lengh + 1) * mBarThickness + (lengh - 1) * mBarPadding;
            invalidate();
            requestLayout();
        }*/
        updateLayout();

    }

    /*
    public void setValuesFloat(float array[]){
        int lengh = Array.getLength(array);
        valuesFloat = new ArrayList<Float>(lengh);
        for(int i=0; i<lengh; i++){
            valuesFloat.add(array[i]);
            if (array[i]>maxValue){
                maxValue=array[i];
            }
        }
        //(lengh+1) is for a little spacing before anc after the first and second bar
        if(!mBarAdaptiveThickness) {
            mWidth = (lengh + 1) * mBarThickness + (lengh - 1) * mBarPadding;
            invalidate();
            requestLayout();
        }
    }

    public void setValueFloat(ArrayList<Float> arrayList){
        valuesFloat = new ArrayList<Float>(arrayList.size());
    }
    */

    private void updateLayout(){

        if(!mBarAdaptiveThickness) {
            if(desiredWidth > 0){
                mWidth = desiredWidth;
            } else {
                int lengh = valuesInt.size();
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

    public void setBarAdptiveThickness(boolean status){
        mBarAdaptiveThickness = status;
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

    public ArrayList<Integer> getValuesInteger() {
        return valuesInt;
    }

    /*
    public ArrayList<Float> getValuesFloat() {
        return valuesFloat;
    }
    */

    public float getMaxValue() {
        return maxValue;
    }

    public int getBarNumber() {
        return mBarNumber;
    }
}
