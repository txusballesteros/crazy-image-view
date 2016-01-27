package com.txusballesteros;

import android.view.MotionEvent;

public class TouchController {
    private final static int MINIMUN_TRANSLATION_IN_PX = 10;
    private final TouchEventListener listener;
    private float lastEventX;
    private float lastEventY;
    private int lastAction = RectController.FLIP_HORIZONTAL;

    public TouchController(TouchEventListener listener) {
        this.listener = listener;
    }

    public void onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastEventX = event.getX();
                lastEventY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                float currentX = event.getX();
                float currentY = event.getY();
                float differenceInX = Math.abs(lastEventX - currentX);
                float differenceInY = Math.abs(lastEventY - currentY);
                if (differenceInX >= MINIMUN_TRANSLATION_IN_PX) {
                    lastAction = RectController.FLIP_HORIZONTAL;
                    lastEventX = event.getX();
                }
                if (differenceInY >= MINIMUN_TRANSLATION_IN_PX) {
                    lastAction = RectController.FLIP_VERTICAL;
                    lastEventY = event.getY();
                }
                listener.onTouchCommand(event, lastAction);
                break;
        }
    }

    public interface TouchEventListener {
        void onTouchCommand(MotionEvent event, int action);
    }
}
