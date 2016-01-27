Crazy ImageView
===============

Crazy Image View for Android is a true crazy experiment, offering to the user the opportunity to discover the image touching the screen. By the way with this repository you can learn how you can work with the Canvas API on Android and see some performance tricks.

![](assets/demo.gif)

# How to Use

Add the view to your layout.

```xml
<com.txusballesteros.CrazyImageView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:foregroundSrc="@drawable/foreground"
    app:backgroundSrc="@drawable/android"
    app:dividerSize="1dp"
    app:columns="15"
    app:rows="30" />
```