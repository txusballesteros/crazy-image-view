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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;

public class ImageCroppingController {
    private static class LazySingleton {
        protected static final ImageCroppingController INSTANCE = new ImageCroppingController();
    }

    public static ImageCroppingController getInstance() {
        return LazySingleton.INSTANCE;
    }

    public Bitmap centerCrop(@NonNull Drawable source, int width, int height) {
        Bitmap result;
        if (source instanceof BitmapDrawable) {
            result = ThumbnailUtils
                    .extractThumbnail(((BitmapDrawable)source).getBitmap(), width, height);
        } else {
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            source.setBounds(0, 0, width, height);
            source.draw(canvas);
        }
        return result;
    }
}
