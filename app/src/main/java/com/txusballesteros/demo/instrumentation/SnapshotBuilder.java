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
package com.txusballesteros.demo.instrumentation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;

public class SnapshotBuilder {
    private static class LazySingleton {
        protected static final SnapshotBuilder INSTANCE = new SnapshotBuilder();
    }

    public static SnapshotBuilder getInstance() {
        return LazySingleton.INSTANCE;
    }

    public Drawable takeSnapshot(@NonNull View view) {
        BitmapDrawable result;
        Bitmap snapshotBitmap = Bitmap.createBitmap(view.getWidth(),
                                                    view.getHeight(),
                                                    Bitmap.Config.ARGB_8888);
        Canvas snapshotCanvas = new Canvas(snapshotBitmap);
        view.draw(snapshotCanvas);
        result = new BitmapDrawable(view.getContext().getResources(), snapshotBitmap);
        return result;
    }
}
