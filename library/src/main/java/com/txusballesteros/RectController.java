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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;

class RectController {
    final static int FLIP_HORIZONTAL = 1;
    final static int FLIP_VERTICAL = 2;
    private final static int DEFAULT_ANIMATION_DURATION_IN_MS = 300;
    private static final float ANIMATION_INITIAL_VALUE = -1f;
    private static final float ANIMATION_END_VALUE = 1f;
    private final CrazyImageView owner;
    private final RectF area;
    private final Bitmap foregroundBitmap;
    private final Bitmap backgroundBitmap;
    private boolean flipInProgress = false;
    private float horizontalFlipValue = ANIMATION_INITIAL_VALUE;
    private float verticalFlipValue = ANIMATION_INITIAL_VALUE;
    private int flipDirection = FLIP_HORIZONTAL;

    public RectController(CrazyImageView owner, RectF area, Bitmap foregroundBitmap, Bitmap backgroundBitmap) {
        this.owner = owner;
        this.area = area;
        this.foregroundBitmap = foregroundBitmap;
        this.backgroundBitmap = backgroundBitmap;
    }

    boolean contains(float x, float y) {
        return area.contains(x, y);
    }

    void reset() {
        horizontalFlipValue = ANIMATION_INITIAL_VALUE;
        verticalFlipValue = ANIMATION_INITIAL_VALUE;
    }

    void flip(int direction) {
        if (!flipInProgress) {
            flipDirection = direction;
            switch (direction) {
                case FLIP_HORIZONTAL:
                    performLeftToRightFlip();
                    break;
                case FLIP_VERTICAL:
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
                horizontalFlipValue = (float) animation.getAnimatedValue();
                owner.invalidate((int) area.left, (int) area.top, (int) area.right, (int) area.bottom);
            }
        });
        animator.start();
    }

    private void performTopToBottomFlip() {
        final ValueAnimator animator = getDefaultAnimator();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                verticalFlipValue = (float) animation.getAnimatedValue();
                owner.invalidate((int) area.left, (int) area.top, (int) area.right, (int) area.bottom);
            }
        });
        animator.start();
    }

    private ValueAnimator getDefaultAnimator() {
        float startValue = ANIMATION_INITIAL_VALUE;
        float finalValue = ANIMATION_END_VALUE;
        if (horizontalFlipValue == ANIMATION_END_VALUE || verticalFlipValue == ANIMATION_END_VALUE) {
            startValue = ANIMATION_END_VALUE;
            finalValue = ANIMATION_INITIAL_VALUE;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(startValue, finalValue);
        animator.setDuration(DEFAULT_ANIMATION_DURATION_IN_MS);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (flipDirection == FLIP_VERTICAL) {
                    horizontalFlipValue = ANIMATION_INITIAL_VALUE;
                } else {
                    verticalFlipValue = ANIMATION_INITIAL_VALUE;
                }
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
        float width = area.width() * Math.abs(horizontalFlipValue);
        float height = area.height() * Math.abs(verticalFlipValue);
        float left = (area.centerX() - (width / 2));
        float right = (area.centerX() + (width / 2));
        float top = (area.centerY() - (height / 2));
        float bottom = (area.centerY() + (height / 2));
        Bitmap currentBitmap;
        if ((flipDirection == FLIP_HORIZONTAL && horizontalFlipValue < 0f) ||
            (flipDirection == FLIP_VERTICAL && verticalFlipValue < 0f)) {
            currentBitmap = foregroundBitmap;
        } else {
            currentBitmap = backgroundBitmap;
        }
        canvas.save();
        canvas.clipRect(new RectF(left, top, right, bottom));
        canvas.drawBitmap(currentBitmap, left, top, null);
        canvas.restore();
    }
}
