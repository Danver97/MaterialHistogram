# MaterialHistogram
A great customizable histogram for Android apps.

Note: this is a preview and it's not ready yet.

## Installation
Download the zip, unzip it and import it in Android Studio. Then add the following lines to your app's grandle file:
```
compile project(':materialhistogram')
```
## Usage

Define 'app' namespace on root view in your layout:
```
xmlns:app="http://schemas.android.com/apk/res-auto"
```
Include this material histogram library in your layout:
```xml
<com.danver.materialhistogram.MaterialHistogram
                            android:layout_width="dimension"
                            android:layout_height="dimension"
                            app:bars_color="color"
                            app:bars_padding="dimension"
                            app:bars_thickness="dimension"
                            app:bars_thickness_adaptive="boolean"
                            app:bars_corner="dimension"
                            app:target_value="float"
                            app:target_color="color"
                            app:axis_show="boolean"
                            app:chart_alignment="center|left|right"
                            app:chart_show_scale="boolean"
                            app:chart_show_average="boolean"/>
```

![materialhistogram](https://cloud.githubusercontent.com/assets/28715404/26629481/e95eef5a-4602-11e7-83c5-a033aff04eeb.png)

The ```app:bars_thickness_adaptive="boolean"``` attribute set on ```"true"``` make the ```app:bars_thickness="dimension"``` attribute to be ignored and the chart will automatically adapt the bars thickness in order to fit the specified ```android:layout_width="dimension"``` dimension.

![materialhistogram-2](https://cloud.githubusercontent.com/assets/28715404/26695775/6252f37a-470c-11e7-959a-bc64699c6222.png)

Actually, the related method is supposed to adjust both thickness and padding in order to have a thickness:padding ratio of 8:1 (because it looks better). In the examble above only thickness have been changed.

```app:target_value="float"``` attribute represent, while showing some stats, a value compared to which the values displayed have to stay below or above. ```app:chart_show_average="boolean"``` allows to display a line representing the average of the values displayed on the histogram.

## Public fields and methods

```java
  //Public fields
  
  public final static int CHART_ALIGNEMT_CENTER = 0;
  public final static int CHART_ALIGNEMT_LEFT = 1;
  public final static int CHART_ALIGNEMT_RIGHT = 2;
  
  //Setters
  
  public void setValues(int array[]);
  public void setValues(float array[]);
  public void setValues(ArrayList<? extends Number> arrayList);
  
  public void setBarColor(@ColorInt int color);
  
  public void setBarCorner(float dp);
  public void setBarThickness(float dp);
  public void setBarPadding(float dp);
  public void setBarAdaptiveThickness(boolean status);
  
  public void setShowAverage(boolean status);
  public void setAverageColor(@ColorInt int color);
  
  public void setTargetValue(float value);
  public void setTargetValueColor(@ColorInt int color);
  
  public void setChartAlignment(int alignment);
  
  public void setHeight(int dpHeight);
  public void setWidth(int dpWidth);
  
  //Getters
  
  public int getBarColor();
  
  public ArrayList getValues();
  public float getMaxValue();
  
  public int getBarNumber();
  
  public int getChartAlignment();
  
  public boolean getBarAdaptiveThicknessStatus();
  public int getBarThicknessDp();
  public int getBarPaddingDp();
  public int getBarCornerDp();
  
  public float getAverageValue();
  public boolean getShowAverageStatus();
  public int getAverageColor();
  
  public float getTargetValue();
  public int getTargetValueColor();

```

## Next

- Add orientation settings
