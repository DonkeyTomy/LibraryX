package com.tomy.lib.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

import com.tomy.lib.ui.R;

import java.util.ArrayList;

/**
 * Created by benz on 2016/10/19.
 */

public class BigPttButton extends RelativeLayout {

    public interface OnPressActionListener {
        /**
         * 完全按下
         */
        void onPressDownFull();

        void onKeyUp();

        void onCancel();
    }

    class CircleFrameLayer extends View {
        Paint bgPaint = new Paint();
        Paint dottedLinePaint = new Paint();
        boolean showRotatedAni;
        int disableColor    = BigPttButton.this.getResources().getColor(R.color.ptt_disable);
        int normalColor     = BigPttButton.this.getResources().getColor(R.color.ptt_normal);
        int talkingColor    = BigPttButton.this.getResources().getColor(R.color.ptt_talking);
        int requestingColor = BigPttButton.this.getResources().getColor(R.color.ptt_requesting);
        int dottedLineWidth;

        public CircleFrameLayer(Context context) {
            super(context);

            dottedLinePaint.setAntiAlias(true);
            dottedLinePaint.setStyle(Paint.Style.STROKE);
            dottedLinePaint.setColor(requestingColor);

            bgPaint.setAntiAlias(true);
            bgPaint.setColor(normalColor);
            bgPaint.setStyle(Paint.Style.STROKE);

            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    getViewTreeObserver().removeOnPreDrawListener(this);
                    /**
                     * 31/886 通过量取zello得到的一个比例
                     */
                    dottedLineWidth = getWidth() * 31 / 886;
                    DashPathEffect effects = new DashPathEffect(new float[]{dottedLineWidth, dottedLineWidth/* * 2*/}, 1);
                    dottedLinePaint.setPathEffect(effects);
                    dottedLinePaint.setStrokeWidth(dottedLineWidth);
                    bgPaint.setStrokeWidth(dottedLineWidth);
                    return false;
                }
            });
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int r = getWidth();
            if (showRotatedAni) {
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, (r - dottedLineWidth) * 9 / 20, dottedLinePaint);
            } else {
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, (r - dottedLineWidth) * 9 / 20, bgPaint);
            }
        }

        public void onStateChanged(int state) {
            showRotatedAni = false;
            Animation ani = getAnimation();
            if (ani != null) {
                ani.cancel();
            }
            clearAnimation();
            switch (state) {
                case STATE_DISABLE:
                case STATE_ERROR:
                    bgPaint.setColor(disableColor);
                    break;
                case STATE_IDLE:
                    bgPaint.setColor(normalColor);
                    if (mIsDisableKeyUpAnimator) {
                        doFloatUpAnimation();
                    }
                    break;
                case STATE_REQUEST:
//                    bgPaint.setColor(requestingColor);
                    showRotatedAni = true;
                    doRotateAni();
                    break;
                case STATE_SPEAKING:
                case STATE_LISTENING:
                    bgPaint.setColor(talkingColor);
                    break;
            }
            invalidate();
        }

        private void doRotateAni() {
            Animation ani = getAnimation();
            if (ani == null) {
                ani = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                ani.setInterpolator(new LinearInterpolator());
                ani.setDuration(10000);
                ani.setRepeatCount(-1);
            }
            startAnimation(ani);
        }
    }

    private static final int LONG_PRESS_TIME = 200;

    /**
     * 按钮的各个状态
     */
    public static final int STATE_IDLE = 0;
    public static final int STATE_REQUEST = 1;
    //I
    public static final int STATE_SPEAKING = 2;
    //Remote
    public static final int STATE_LISTENING = 3;
    public static final int STATE_DISABLE = 4;
    public static final int STATE_ERROR = 5;

    private float mCurrentScaleX;
    private float mCurrentScaleY;
    private float mCurrentTranslationZ = 50;
    private ScaleXUpdateListener mScaleXUpdateListener;
    private ScaleYUpdateListener mScaleYUpdateListener;
    private TranslationZUpdateListener mTranslationZUpdateListener;
    private ArrayList<Animator> mAnimators = new ArrayList<>(3);

    private float mInitialValue = 0.98f;
    private FrameShapeDrawable mCircleBackground;
    private CircleFrameLayer mFloatCircleFrameLayer;
    private Bitmap mCenterIconBitmap;
    private Bitmap mNormalIconBitmap;
    private Bitmap mPressIconBitmap;
    private Paint mIconPaint;
    private OnPressActionListener mOnPressAction;

    public BigPttButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setClickable(true);
        setFocusable(true);

        mScaleXUpdateListener = new ScaleXUpdateListener();
        mScaleYUpdateListener = new ScaleYUpdateListener();
        mTranslationZUpdateListener = new TranslationZUpdateListener();

//            mCircleBackground = new FrameShapeDrawable();

        setScaleX(mInitialValue);
        setScaleY(mInitialValue);
        mCurrentScaleX = mCurrentScaleY = mInitialValue;

//        mCircleBackground.setShape(new OvalShape());
//        mCircleBackground.getPaint().setColor(Color.parseColor("#1977c2"));
//        setBackgroundDrawable(mCircleBackground);

        mFloatCircleFrameLayer = new CircleFrameLayer(context);
        addView(mFloatCircleFrameLayer, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mIconPaint = new Paint();
        mIconPaint.setAntiAlias(true);
        mNormalIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home_but_talk);
        mPressIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.big_talk_bt_icon_press);
        mCenterIconBitmap = mNormalIconBitmap;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawBitmap(mCenterIconBitmap, mIconX, mIconY, mIconPaint);
        canvas.drawBitmap(mCenterIconBitmap,
                (getWidth() - mCenterIconBitmap.getWidth()) / 2,
                (getHeight() - mCenterIconBitmap.getHeight()) / 2 - 1, mIconPaint);
    }

    float lastX = 0, lastY = 0;
    boolean isOnLongClick = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            lastX = event.getX();
            lastY = event.getY();
            requestDisallowInterceptTouchEvent(true);
            doFloatDownAnimation();
            isOnLongClick = false;
            postDelayed(longClickRun, LONG_PRESS_TIME);
        } else if (action == MotionEvent.ACTION_UP) {
            requestDisallowInterceptTouchEvent(false);
            if (!mIsDisableKeyUpAnimator) {
                doFloatUpAnimation();
            }
            if (mOnPressAction != null) {
                if (isOnLongClick) {
                    mOnPressAction.onKeyUp();
                } else {
                    mOnPressAction.onCancel();
                }
            }
            getHandler().removeCallbacks(longClickRun);
            isOnLongClick = false;
        } else if (action == MotionEvent.ACTION_OUTSIDE || action == MotionEvent.ACTION_CANCEL) {
            if (mOnPressAction != null) {
                mOnPressAction.onCancel();
            }
            getHandler().removeCallbacks(longClickRun);
        }/* else if (action == MotionEvent.ACTION_MOVE) {
            if (event.getEventTime() - event.getDownTime() > LONG_PRESS_TIME
                    && !isOnLongClick) {
                if (mOnPressAction != null) {
                    mOnPressAction.onPressDownFull();
                }
                isOnLongClick = true;
            }
        }*/
        return true;
    }

    private Runnable longClickRun = new Runnable() {
        @Override
        public void run() {
            if (mOnPressAction != null) {
                mOnPressAction.onPressDownFull();
            }
            isOnLongClick = true;
        }
    };

    public void setOnPressActionListener(OnPressActionListener l) {
        mOnPressAction = l;
    }

    public void onStateChanged(int state) {
        switch (state) {
            case STATE_DISABLE:
            case STATE_ERROR:
            case STATE_IDLE:
            case STATE_REQUEST:
            case STATE_SPEAKING:
                mCenterIconBitmap = mNormalIconBitmap;
                break;
            case STATE_LISTENING:
                mCenterIconBitmap = mPressIconBitmap;
                break;
        }
        mFloatCircleFrameLayer.onStateChanged(state);
    }

    private boolean mIsDisableKeyUpAnimator = false;

    public void setDisableKeyUpAnimator(boolean isDisable) {
        this.mIsDisableKeyUpAnimator = isDisable;
    }

    private void doFloatUpAnimation() {
        for (Animator a : mAnimators) {
            a.cancel();
        }
        mAnimators.clear();

        ObjectAnimator xScaleAnim = ObjectAnimator.ofFloat(this, "scaleX", mCurrentScaleX, 1.0f);
        ObjectAnimator yScaleAnim = ObjectAnimator.ofFloat(this, "scaleY", mCurrentScaleY, 1.0f);
        xScaleAnim.setDuration(100);
        yScaleAnim.setDuration(100);
        xScaleAnim.addUpdateListener(mScaleXUpdateListener);
        yScaleAnim.addUpdateListener(mScaleYUpdateListener);

        ObjectAnimator upAnim = ObjectAnimator.ofFloat(this, "translationZ", mCurrentTranslationZ, 50);
        upAnim.setInterpolator(new DecelerateInterpolator());
        upAnim.setDuration(100);
//        upAnim.addUpdateListener(mTranslationZUpdateListener);

        mAnimators.add(xScaleAnim);
        mAnimators.add(yScaleAnim);
        mAnimators.add(upAnim);
        AnimatorSet aniSet = new AnimatorSet();
        aniSet.playTogether(mAnimators);
        aniSet.start();
    }

    private void doFloatDownAnimation() {
        for (Animator a : mAnimators) {
            a.cancel();
        }
        mAnimators.clear();

        ObjectAnimator xScaleAnim = ObjectAnimator.ofFloat(this, "scaleX", mCurrentScaleX, mInitialValue);
        ObjectAnimator yScaleAnim = ObjectAnimator.ofFloat(this, "scaleY", mCurrentScaleY, mInitialValue);
        xScaleAnim.setDuration(100);
        yScaleAnim.setDuration(100);
        xScaleAnim.addUpdateListener(mScaleXUpdateListener);
        yScaleAnim.addUpdateListener(mScaleYUpdateListener);

        ObjectAnimator upAnim = ObjectAnimator.ofFloat(this, "translationZ", mCurrentTranslationZ, 0);
        upAnim.setDuration(100);
//        upAnim.addUpdateListener(mTranslationZUpdateListener);

        mAnimators.add(xScaleAnim);
        mAnimators.add(yScaleAnim);
        mAnimators.add(upAnim);
        AnimatorSet aniSet = new AnimatorSet();
        aniSet.playTogether(mAnimators);
        aniSet.start();
    }

    class ScaleXUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mCurrentScaleX = (Float) animation.getAnimatedValue("scaleX");
//            mCurrentScaleX = animation.getAnimatedFraction();
        }
    }

    class ScaleYUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mCurrentScaleY = (Float) animation.getAnimatedValue("scaleY");
//            mCurrentScaleY = animation.getAnimatedFraction();

        }
    }

    class TranslationZUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        int currentDrawableId = 0;

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float current = (Float) animation.getAnimatedValue("translationZ");
            if (current < 25) {
                if (currentDrawableId != R.drawable.big_talk_bt_icon_press) {
                    mCenterIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.big_talk_bt_icon_press);
                    currentDrawableId = R.drawable.big_talk_bt_icon_press;
                    invalidate();
                }
            } else {
                if (currentDrawableId != R.drawable.big_talk_bt_icon) {
                    mCenterIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.big_talk_bt_icon);
                    currentDrawableId = R.drawable.big_talk_bt_icon;
                    invalidate();
                }
            }
            mCurrentTranslationZ = current;
//            Log.e(TAG, "Y fraction:::" + current);
        }
    }
}
