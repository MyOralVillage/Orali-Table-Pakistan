package org.myoralvillage.cashcalculatormodule.util;

/**
 * Referenced from : https://gist.github.com/kopyaethu/2eded4114f43e83fd85794b7aa07d576#file-twofingerswipegesture-java
 */

import android.util.Log;
import android.view.MotionEvent;

public class TwoFingerSwipeDetector {

    private static final int NONE = 0;
    private static final int SWIPE = 1;
    private int mode = NONE;
    private float startX, stopX;
    // We will only detect a swipe if the difference is at least 100 pixels
    private static final int THRESHOLD = 50;

    public boolean detectTwoFingerSwipe(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            // MotionEvent.ACTION_DOWN means one finger.
            // MotionEvent.ACTION_POINTER_DOWN is two fingers.
            case MotionEvent.ACTION_POINTER_DOWN:
                // This happens when you touch the screen with two fingers
                mode = SWIPE;
                // You can also use event.getY(1) or the average of the two
                startX = event.getX(0);

                break;
            case MotionEvent.ACTION_POINTER_UP:

                // This happens when you release the second finger
                mode = NONE;
                if (Math.abs(startX - stopX) > THRESHOLD) {

                    if (startX > stopX) {

                        // Swipe left.
                        Log.e("Swipe", "LEFT");
                    } else {

                        // Swipe right.
                        Log.e("Swipe", "RIGHT");
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:

                if (mode == SWIPE) {

                    stopX = event.getX(0);
                }

                break;
        }
        return true;
    }
}
