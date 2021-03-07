package org.myoralvillage.cashcalculatormodule.views.listeners;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.myoralvillage.cashcalculatormodule.util.TwoFingerSwipeDetector;

/**
 * A listener class for receiving gesture detection on the view, <code>CountingTableView</code>.
 *
 * @author Alexander Yang
 * @author Hamza Mahfooz
 * @author Peter Panagiotis Roubatsis
 * @author Rahul Vaish
 * @see org.myoralvillage.cashcalculatormodule.views.CountingTableView
 */
public abstract class SwipeListener implements View.OnTouchListener {
    /**
     * Variable used to detect the gestures performed.
     *
     * @see GestureDetector
     */
    private GestureDetector gestureDetector;
    private TwoFingerSwipeDetector twoFingerSwipeDetector = new TwoFingerSwipeDetector();

    /**
     * Constructs a new <code>SwipeListener</code> with the specified context for the gesture detector
     * to monitor.
     *
     * @param c the context of this application.
     */
    public SwipeListener(Context c) {
        gestureDetector = new GestureDetector(c, new GestureListener());
    }

    /**
     * Called when a touch event is dispatched to the view, <code>CountingTableView</code>. This
     * allows listeners to get a chance to respond before the target view.
     *
     * @param view the view the touch event has been passed to.
     * @param event The MotionEvent object containing full information about the event.
     * @return true if the listener has consumed the event; false otherwise.
     *
     * REF : https://stackoverflow.com/a/62678103/243709
     */

    final int NONE = 0;
    final int SWIPE = 1;
    float p1StartX = 0, p1StopX = 0;
    float p2StartX = 0, p2StopX = 0;
    int mode = NONE;
    float startX, stopX;
    // We will only detect a swipe if the difference is at least 100 pixels
    final int DOUBLE_SWIPE_THRESHOLD = 50;
    float p1Diff, p2Diff;

    public boolean onTouch(final View view, final MotionEvent event) {
        if(event.getPointerCount() == 2){


            switch (event.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_POINTER_DOWN:
                    // This happens when you touch the screen with two fingers
                    mode = SWIPE;
                    // event.getY(1) is for the second finger
                    p1StartX = event.getX(0);
                    p2StartX = event.getX(1);
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    // This happens when you release the second finger
                    mode = NONE;
                    p1Diff = p1StartX - p1StopX;
                    p2Diff = p2StartX - p2StopX;

                    // this is to make sure that fingers go in same direction and
                    // swipe have certain length to consider it a swipe
                    if(Math.abs(p1Diff) > DOUBLE_SWIPE_THRESHOLD
                            && Math.abs(p2Diff) >DOUBLE_SWIPE_THRESHOLD &&
                            ( (p1Diff>0 && p2Diff>0) || (p1Diff < 0 && p2Diff<0) ))
                    {
                        if(p1StartX > p1StopX)
                        {
                            // Swipe left
                            swipeRightTwoFingers();
                        }
                        else
                        {
                            //Swipe right, do nothing
                        }
                    }
                    mode = NONE;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if(mode == SWIPE)
                    {
                        p1StopX = event.getY(0);
                        p2StopX = event.getY(1);
                    }
                    break;
            }
            return true;
        }

        return gestureDetector.onTouchEvent(event);
    }

    /**
     * The gesture listener class for gesture detection.
     *
     * @see android.view.GestureDetector.SimpleOnGestureListener
     */
    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        /**
         * A constant variable for the minimum distance a moving gesture should be in order to
         * consider it as a swipe.
         */
        private static final int THRESHOLD = 50;

        /**
         * A constant variable for the minimum velocity of a moving gesture in order for it to be
         * considered as a swipe.
         */
        private static final int THRESHOLD_VELOCITY = 50;

        /**
         * Notified when a long press occurs with the initial on down MotionEvent that triggered it.
         *
         * @param e the initial on down motion event that started the long press event.
         */
        @Override
        public void onLongPress(MotionEvent e) {
            longPress(e.getX(), e.getY());
        }

        /**
         * Notified when a tap occurs with the down MotionEvent that triggered it. This will be
         * triggered immediately for every down event. All other events should be preceded by this.
         *
         * @param e the down motion event.
         *
         * @return true if this function is invoked.
         */
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        /**
         * Notified of a fling event when it occurs with the initial on down MotionEvent and the
         * matching up MotionEvent. The calculated velocity is supplied along the x and y axis in
         * pixels per second.
         *
         * @param downEvent the first down motion event that started the fling.
         * @param moveEvent the move motion event that triggered the current onFling.
         * @param velocityX The velocity of this fling measured in pixels per second along the x axis.
         * @param velocityY The velocity of this fling measured in pixels per second along the y axis.
         * @return true if the event is consumed; false otherwise.
         */
        @Override
        public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = moveEvent.getY() - downEvent.getY();
                float diffX = moveEvent.getX() - downEvent.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    //Swipe Left or Right
                    if (Math.abs(diffX) > THRESHOLD && Math.abs(velocityX) > THRESHOLD_VELOCITY) {
                        if (diffX > 0) {
                            //Swipe Right
                            swipeRight();
                        } else {
                            //Swipe Left
                            swipeLeft();
                        }
                        result = true;
                    }
                }
                else {
                    //Swipe up or down
                    if (Math.abs(diffY) > THRESHOLD && Math.abs(velocityY) > THRESHOLD_VELOCITY) {
                        if (diffY > 0) {
                            //Swipe down
                            swipeDown();
                        } else {
                            //Swipe up
                            swipeUp();
                        }
                        result = true;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * Invoked when a gesture detection indicating a swipe up on the view.
     */
    public abstract void swipeUp();

    /**
     * Invoked when a gesture detection indicating a swipe down on the view.
     */
    public abstract void swipeDown();

    /**
     * Invoked when a gesture detection indicating a swipe left on the view.
     */
    public abstract void swipeLeft();

    /**
     * Invoked when a gesture detection indicating a swipe right on the view.
     */
    public abstract void swipeRight();

    /**
     * Invoked when a gesture detection indicating a swipe right on the view with two fingers.
     */
    public abstract void swipeRightTwoFingers();

    /**
     * Invoked when a gesture detection indicating a long press on the view.
     *
     * @param x the x coordinate of this event.
     * @param y the y coordinate of this event.
     */
    public abstract void longPress(float x, float y);
}
