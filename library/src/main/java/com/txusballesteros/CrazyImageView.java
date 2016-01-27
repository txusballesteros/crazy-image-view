/*
 * Copyright Txus Ballesteros 2016 (@txusballesteros)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Txus Ballesteros <txus.ballesteros@gmail.com>
 */
package com.txusballesteros;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CrazyImageView extends View {
    private final static int DEFAULT_NUM_OF_COLUMNS = 15;
    private final static int DEFAULT_NUM_OF_ROWS = 30;
    private final static float DEFAULT_DIVIDER_SIZE_IN_DP = 1;
    private int numOfColumns = DEFAULT_NUM_OF_COLUMNS;
    private int numOfRows = DEFAULT_NUM_OF_ROWS;
    private float dividerSizeInPx = dp2px(DEFAULT_DIVIDER_SIZE_IN_DP);
    private Drawable foregroundDrawable;
    private Drawable backgroundDrawable;
    private Bitmap foregroundBitmap;
    private Bitmap backgroundBitmap;
    private List<RectController> rectControllers = new ArrayList<>();
    private TouchController touchController;

    public CrazyImageView(Context context) {
        super(context);
        initializeView();
    }

    public CrazyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributes(attrs);
        initializeView();
    }

    public CrazyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttributes(attrs);
        initializeView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CrazyImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readAttributes(attrs);
        initializeView();
    }

    public void revealBackground() {
        for (RectController controller : rectControllers) {
            controller.reset();
            controller.flip(RectController.FLIP_VERTICAL);
        }
    }

    private void readAttributes(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributes = getContext()
                    .getTheme().obtainStyledAttributes(attrs, R.styleable.CrazyImageView, 0, 0);
            dividerSizeInPx = attributes
                    .getDimension(R.styleable.CrazyImageView_dividerSize, dividerSizeInPx);
            numOfColumns = attributes.getInteger(R.styleable.CrazyImageView_columns, numOfColumns);
            numOfRows = attributes.getInteger(R.styleable.CrazyImageView_rows, numOfRows);
            foregroundDrawable = attributes.getDrawable(R.styleable.CrazyImageView_foregroundSrc);
            if (foregroundDrawable == null) {
                int defaultForegroundColor = getResources()
                        .getColor(R.color.default_foreground_color);
                foregroundDrawable = new ColorDrawable(defaultForegroundColor);
            }
            backgroundDrawable = attributes.getDrawable(R.styleable.CrazyImageView_backgroundSrc);
            if (backgroundDrawable == null) {
                int defaultForegroundColor = getResources()
                        .getColor(R.color.default_background_color);
                backgroundDrawable = new ColorDrawable(defaultForegroundColor);
            }
            attributes.recycle();
        }
    }

    private void initializeView() {
        setClickable(true);
        setFocusable(false);
        setFocusableInTouchMode(false);
        touchController = new TouchController(new TouchController.TouchEventListener() {
            @Override
            public void onTouchCommand(MotionEvent event, int action) {
                for (int pointer = 0; pointer < event.getPointerCount(); pointer++) {
                    float eventX = event.getX(pointer);
                    float eventY = event.getY(pointer);
                    for (RectController controller : rectControllers) {
                        if (controller.contains(eventX, eventY)) {
                            controller.flip(action);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(RectController controller : rectControllers) {
            controller.onDraw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        if (width > 0 && height > 0) {
            buildForegroundBitmap();
            buildBackgroundBitmap();
            calculateAreas(width, height);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchController.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        foregroundBitmap.recycle();
        foregroundBitmap = null;
        backgroundBitmap.recycle();
        backgroundBitmap = null;
        super.onDetachedFromWindow();
    }

    private void buildForegroundBitmap() {
        foregroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(foregroundBitmap);
        foregroundDrawable.setBounds(0, 0, getWidth(), getHeight());
        foregroundDrawable.draw(canvas);
    }

    private void buildBackgroundBitmap() {
        backgroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(backgroundBitmap);
        backgroundDrawable.setBounds(0, 0, getWidth(), getHeight());
        backgroundDrawable.draw(canvas);
    }

    private Bitmap getForegroundBitmapRectArea(float x, float y, float width, float height) {
        return Bitmap.createBitmap(foregroundBitmap, (int)x, (int)y, (int)width, (int)height);
    }

    private Bitmap getBackgroundBitmapRectArea(float x, float y, float width, float height) {
        return Bitmap.createBitmap(backgroundBitmap, (int)x, (int)y, (int)width, (int)height);
    }

    private void calculateAreas(float viewWidth, float viewHeight) {
        rectControllers.clear();
        float totalHorizontalPadding = dividerSizeInPx * (numOfColumns - 1);
        float totalVerticalPadding = dividerSizeInPx * (numOfRows - 1);
        float rectWidth = ((viewWidth - totalHorizontalPadding) / numOfColumns);
        float rectHeight = ((viewHeight - totalVerticalPadding) / numOfRows);
        float currentX;
        float currentY;
        for (int row = 0; row < numOfRows; row++) {
            currentY = (rectHeight * row) + (dividerSizeInPx * row);
            for (int column = 0; column < numOfColumns; column++) {
                currentX = (rectWidth * column) + (dividerSizeInPx * column);
                float left = currentX;
                float top = currentY;
                float bottom = currentY + rectHeight;
                float right = currentX + rectWidth;
                final RectF areaRect = new RectF(left, top, right, bottom);
                final Bitmap foregroundAreaBitmap
                        = getForegroundBitmapRectArea(areaRect.left,
                                                      areaRect.top,
                                                      areaRect.width(),
                                                      areaRect.height());
                final Bitmap backgroundAreaBitmap
                        = getBackgroundBitmapRectArea(areaRect.left,
                                                      areaRect.top,
                                                      areaRect.width(),
                                                      areaRect.height());
                rectControllers.add(new RectController(this, areaRect, foregroundAreaBitmap, backgroundAreaBitmap));
            }
        }
    }

    private float dp2px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                                         getResources().getDisplayMetrics());
    }
}
