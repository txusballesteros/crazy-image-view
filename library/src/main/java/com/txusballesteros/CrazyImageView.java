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
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CrazyImageView extends View {
    private final static int DEFAULT_NUM_OF_COLUMNS = 7;
    private final static int DEFAULT_NUM_OF_ROWS = 15;
    private final static float RECT_PADDING_IN_DP = 1;
    private int numOfColumns = DEFAULT_NUM_OF_COLUMNS;
    private int numOfRows = DEFAULT_NUM_OF_ROWS;
    private float rectPadding = dp2px(RECT_PADDING_IN_DP);
    private List<RectController> rectControllers = new ArrayList<>();

    public CrazyImageView(Context context) {
        super(context);
        initializeView();
    }

    public CrazyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    public CrazyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CrazyImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeView();
    }

    private void initializeView() {
        setClickable(true);
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
            calculateAreas(width, height);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            for (int pointer = 0; pointer < event.getPointerCount(); pointer++) {
                float eventX = event.getX(pointer);
                float eventY = event.getY(pointer);
                for (RectController controller : rectControllers) {
                    if (controller.contains(eventX, eventY)) {
                        controller.flip(RectController.FLIP_LEFT_TO_RIGHT);
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void calculateAreas(float viewWidth, float viewHeight) {
        rectControllers.clear();
        float totalHorizontalPadding = rectPadding * (numOfColumns - 1);
        float totalVerticalPadding = rectPadding * (numOfRows - 1);
        float rectWidth = ((viewWidth - totalHorizontalPadding) / numOfColumns);
        float rectHeight = ((viewHeight - totalVerticalPadding) / numOfRows);
        float currentX;
        float currentY;
        for (int row = 0; row < numOfRows; row++) {
            currentY = (rectHeight * row) + (rectPadding * row);
            for (int column = 0; column < numOfColumns; column++) {
                currentX = (rectWidth * column) + (rectPadding * column);
                float left = currentX;
                float top = currentY;
                float bottom = currentY + rectHeight;
                float right = currentX + rectWidth;
                rectControllers.add(new RectController(this, new RectF(left, top, right, bottom)));
            }
        }
    }

    private float dp2px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                                         getResources().getDisplayMetrics());
    }
}
