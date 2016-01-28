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
package com.txusballesteros.demo;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.txusballesteros.CrazyImageView;
import com.txusballesteros.demo.instrumentation.SnapshotBuilder;

public class MainActivity extends Activity {
    private CrazyImageView crazyImageView;
    private ViewGroup crazyLayout;
    private ViewGroup dataLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        crazyImageView = (CrazyImageView)findViewById(R.id.crazyImageView);
        dataLayout = (ViewGroup)findViewById(R.id.dataLayout);
        crazyLayout = (ViewGroup)findViewById(R.id.crazyLayout);
        findViewById(R.id.takeSnapshot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSnapshot();
            }
        });
        findViewById(R.id.reveal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crazyImageView.revealBackground();
            }
        });
    }

    private void performSnapshot() {
        Drawable snapshot = SnapshotBuilder.getInstance().takeSnapshot(dataLayout);
        crazyImageView.setForegroundDrawable(snapshot);
        crazyImageView.setBackgroundDrawable(R.drawable.android_land);
        dataLayout.setVisibility(View.GONE);
        crazyLayout.setVisibility(View.VISIBLE);
    }
}
