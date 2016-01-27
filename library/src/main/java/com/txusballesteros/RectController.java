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
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.LinearInterpolator;

class RectController {
    final static int FLIP_HORIZONTAL = 1;
    final static int FLIP_VERTICAL = 2;
    private final static int DEFAULT_ANIMATION_DURATION_IN_MS = 300;
    private final CrazyImageView owner;
    private final RectF area;
    private final Bitmap foregroundBitmap;
    private final Bitmap backgroundBitmap;
    private boolean flipInProgress = false;
    private float horizontalFlipValue = -1f;
    private float verticalFlipValue = -1f;

    public RectController(CrazyImageView owner, RectF area, Bitmap foregroundBitmap, Bitmap backgroundBitmap) {
        this.owner = owner;
        this.area = area;
        this.foregroundBitmap = foregroundBitmap;
        this.backgroundBitmap = backgroundBitmap;
    }

    boolean contains(float x, float y) {
        return area.contains(x, y);
    }

    void flip(int direction) {
        if (!flipInProgress) {
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
                verticalFlipValue = (float) animation.getAnimatedValue();
                owner.invalidate((int) area.left, (int) area.top, (int) area.right, (int) area.bottom);
            }
        });
        animator.start();
    }

    private ValueAnimator getDefaultAnimator() {
        float startValue = -1f;
        float finalValue = 1f;
        if (horizontalFlipValue == 1f || verticalFlipValue == 1f) {
            startValue = 1f;
            finalValue = -1f;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(startValue, finalValue);
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
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        return animator;
    }

    void onDraw(Canvas canvas) {
        canvas.save();
        canvas.clipRect(area);
        float width = area.width() * Math.abs(horizontalFlipValue);
        float height = area.height() * Math.abs(verticalFlipValue);
        float left = (area.centerX() - (width / 2));
        float top = (area.centerY() - (height / 2));
        Bitmap currentBitmap;
        if (horizontalFlipValue < 0f && verticalFlipValue < 0f) {
            currentBitmap = buildBitmap(foregroundBitmap);
//            currentBitmap = foregroundBitmap;
        } else {
            currentBitmap = buildBitmap(backgroundBitmap);
//            currentBitmap = backgroundBitmap;
        }
        if (currentBitmap != null) {
            canvas.drawBitmap(currentBitmap, left, top, null);
        }
        canvas.restore();
    }

    private Bitmap buildBitmap(Bitmap source) {
        Bitmap result = null;
        float width = source.getWidth() * Math.abs(horizontalFlipValue);
        float height = source.getHeight() * Math.abs(verticalFlipValue);
        float x = ((source.getWidth() - width) / 2);
        float y = ((source.getHeight() - height) / 2);
        if (width > 0 && height > 0) {
            result = Bitmap.createBitmap(source, (int) x, (int) y, (int) width, (int) height);
        }
        return result;
    }
}
