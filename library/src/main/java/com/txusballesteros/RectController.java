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

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.LinearInterpolator;

class RectController {
    final static int FLIP_LEFT_TO_RIGHT = 1;
    final static int FLIP_RIGHT_TO_LEFT = 2;
    final static int FLIP_TOP_TO_BOTTOM = 3;
    final static int FLIP_BOTTOM_TO_TOP = 4;
    private final static int DEFAULT_ANIMATION_DURATION_IN_MS = 300;
    private final static int DEFAULT_FOREGROUND_COLOR = 0xffFF5900;
    private int foregroundColor = DEFAULT_FOREGROUND_COLOR;
    private Paint foregroundPaint;
    private final CrazyImageView owner;
    private final RectF area;
    private boolean flipInProgress = false;
    private float horizontalFlipValue = -1f;
    private float verticalFlipValue = -1f;

    public RectController(CrazyImageView owner, RectF area) {
        this.owner = owner;
        this.area = area;
        initializePaints();
    }

    private void initializePaints() {
        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setStyle(Paint.Style.FILL);
        foregroundPaint.setColor(foregroundColor);
    }

    boolean contains(float x, float y) {
        return area.contains(x, y);
    }

    void flip(int direction) {
        if (!flipInProgress) {
            switch (direction) {
                case FLIP_LEFT_TO_RIGHT:
                    performLeftToRightFlip();
                    break;
                case FLIP_RIGHT_TO_LEFT:
                    performLeftToRightFlip();
                    break;
                case FLIP_TOP_TO_BOTTOM:
                    performTopToBottomFlip();
                    break;
                case FLIP_BOTTOM_TO_TOP:
                    performTopToBottomFlip();
                    break;
            }
        }
    }

    private void performLeftToRightFlip() {
        final ValueAnimator animator = getDefaultAnimator();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                horizontalFlipValue = (float)animation.getAnimatedValue();
                owner.invalidate((int)area.left, (int)area.top, (int)area.right, (int)area.bottom);
            }
        });
        animator.start();
    }

    private void performTopToBottomFlip() {
        final ValueAnimator animator = getDefaultAnimator();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                verticalFlipValue = (float)animation.getAnimatedValue();
                owner.invalidate((int)area.left, (int)area.top, (int)area.right, (int)area.bottom);
            }
        });
        animator.start();
    }

    private ValueAnimator getDefaultAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(-1f, 1f);
        animator.setDuration(DEFAULT_ANIMATION_DURATION_IN_MS);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                flipInProgress = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                flipInProgress = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
        return animator;
    }

    void onDraw(Canvas canvas) {
        canvas.save();
        canvas.clipRect(area);
        float width = area.width() * Math.abs(horizontalFlipValue);
        float height = area.height() * Math.abs(verticalFlipValue);
        float left = (area.centerX() - (width / 2));
        float right = (area.centerX() + (width / 2));
        float top = (area.centerY() - (height / 2));
        float bottom = (area.centerY() + (height / 2));
        canvas.drawRect(left, top, right, bottom, foregroundPaint);
        canvas.restore();
    }
}
