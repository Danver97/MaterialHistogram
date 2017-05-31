# MaterialHistogram
A great customizable histogram for Android apps.
Note: this is a preview and it's not ready yet. If you are tring to use this repo, it could eventually not work. I'm still new in GitHub :D

## Installation
```
compile 'com.danver97:MaterialHistogram'
```
## Usage

Define 'app' namespace on root view in your layout
```
xmlns:app="http://schemas.android.com/apk/res-auto"
```
Include this material histogram library in your layout
```xml
<com.danver.materialhistogram.MaterialHistogram
                            android:layout_width="dimension"
                            android:layout_height="dimension"
                            app:bars_padding="dimension"
                            app:bars_thickness="dimension"
                            app:bars_thickness_adaptive="boolean"
                            app:bars_corner="dimension"
                            app:axis_show="boolean"
                            app:chart_alignment="center|left|right"
                            app:chart_show_scale="boolean"/>
```

![materialhistogram](https://cloud.githubusercontent.com/assets/28715404/26629481/e95eef5a-4602-11e7-83c5-a033aff04eeb.png)

The ```xml app:bars_thickness_adaptive="boolean"``` attribute make the ```app:bars_thickness="dimension"``` attribute to be ignored
and the chart will automatically adapt the bars thickness in order to fit the specified ```android:layout_width="dimension"``` dimension.
