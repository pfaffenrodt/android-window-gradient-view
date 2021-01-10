# Gradient Window Overlay

This is a proof of concept. I would not recommend doing any kind of Layouts with fading.
But if you have to, just keep in mind, that it costs RAM memory. nowdays it may not be an issue anymore.
Memory costs: Bitmaps are required of screen sizes or sizes of your fade element

## Fading Window background

![sample](/sample.png)

```xml
<de.pfaffenrodt.gradientwindowoverlay.GradientWindowOverlayView
        android:layout_width="match_parent"
        android:layout_height="160dp"
        android:gravity="top" />
```


## FadeoutLayout
cutout the content instead of overdrawing it.
define fading values with in DP

```xml
  <de.pfaffenrodt.gradientwindowoverlay.FadeoutLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/fade_layout"
        app:fadeTop="@dimen/gradient_size"
        app:fadeBottom="@dimen/gradient_size"
        app:fadeRight="@dimen/gradient_size"
        app:fadeLeft="@dimen/gradient_size"
        >
        <View/>
    </de.pfaffenrodt.gradientwindowoverlay.FadeoutLayout>
```