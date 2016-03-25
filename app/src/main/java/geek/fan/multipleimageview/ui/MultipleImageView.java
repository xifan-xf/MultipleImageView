package geek.fan.multipleimageview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.util.Collection;
import java.util.Collections;

import geek.fan.multipleimageview.R;

/**
 * Created by fan on 16/3/16.
 */
public class MultipleImageView extends View {

    private String[] mImageUrls;
    private Bitmap[] mBitmaps;
    @SuppressWarnings("unchecked")
    private Target<Bitmap>[] mTargets = new Target[0]; // save a null check in setImageUrls
    private RectF[] mDrawRects;

    private Bitmap mPlaceHolder;
    private int mHorizontalSpace = 10;
    private int mVerticalSpace = 10;
    private int mRadius = 0;
    private int mMaxImageWidth = 0;
    private int mImageWidth = 0;
    private int mMinImageWidth = 0;
    private Matrix matrix = new Matrix();
    final Paint paint = new Paint();
    private OnClickItemListener onClickItemListener;
    private MotionEvent mEventDown;
    private int mDown;


    public MultipleImageView(Context context) {
        this(context, null);
    }

    public MultipleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultipleImageView);
            try {
                for (int i = 0; i < a.getIndexCount(); i++) {
                    int attr = a.getIndex(i);
                    switch (attr) {
                        case R.styleable.MultipleImageView_mivHorizontalSpace:
                            mHorizontalSpace = a.getDimensionPixelSize(attr, mHorizontalSpace);
                            break;
                        case R.styleable.MultipleImageView_mivVerticalSpace:
                            mVerticalSpace = a.getDimensionPixelSize(attr, mVerticalSpace);
                            break;
                        case R.styleable.MultipleImageView_mivRadius:
                            mRadius = a.getDimensionPixelSize(attr, mRadius);
                            break;
                    }
                }
            } finally {
                a.recycle();
            }
        }

        paint.setAntiAlias(true);
        mPlaceHolder = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_photo);
        setImageUrls(null); // initialize fields
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        for (int i = 0; i < mImageUrls.length; i++) {
            loadBitmap(i, mImageUrls[i]);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mImageUrls.length != 0) {
            int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

            mMaxImageWidth = width - getPaddingLeft() - getPaddingRight();
            mImageWidth = (width - mHorizontalSpace - getPaddingLeft() - getPaddingRight()) / 2;
            mMinImageWidth = (width - mHorizontalSpace - getPaddingLeft() - getPaddingRight()) / 3;
            switch (mImageUrls.length) {
                case 1:
                    height = mMaxImageWidth + getPaddingTop() + getPaddingBottom();
                    break;
                case 2:
                    height = mImageWidth + getPaddingTop() + getPaddingBottom();
                    break;
                case 3:
                    height = mMaxImageWidth + mVerticalSpace + mImageWidth + getPaddingTop() + getPaddingBottom();
                    break;
                case 4:
                    height = mImageWidth * 2 + mVerticalSpace + getPaddingTop() + getPaddingBottom();
                    break;
                case 5:
                    height = mImageWidth + mMinImageWidth + mVerticalSpace + getPaddingTop() + getPaddingBottom();
                    break;
                case 6:
                    height = mMinImageWidth * 2 + mVerticalSpace + getPaddingTop() + getPaddingBottom();
                    break;
                case 7:
                    height = mMinImageWidth + mImageWidth * 2 + mVerticalSpace * 2 + getPaddingTop() + getPaddingBottom();
                    break;
                case 8:
                    height = mMinImageWidth * 2 + mImageWidth + mVerticalSpace * 2 + getPaddingTop() + getPaddingBottom();
                    break;
                case 9:
                    height = mMinImageWidth * 3 + mVerticalSpace * 2 + getPaddingBottom() + getPaddingTop();
                    break;
            }
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mBitmaps.length) {
            case 1:
                drawBitmap(canvas, 0, 0, mMaxImageWidth, 0, 0);
                break;
            case 2:
                for (int column = 0; column < 2; column++) {
                    drawBitmap(canvas, 0, column, mImageWidth, 0, column);
                }
                break;
            case 3:
                drawBitmap(canvas, 0, 0, mMaxImageWidth, 0, 0);
                drawBitmap(canvas, 1, 0, mImageWidth, mMaxImageWidth, 1);
                drawBitmap(canvas, 1, 1, mImageWidth, mMaxImageWidth, 2);
                break;
            case 4:
                for (int row = 0; row < 2; row++) {
                    for (int column = 0; column < 2; column++) {
                        drawBitmap(canvas, row, column, mImageWidth, row * mImageWidth, row * 2 + column);
                    }
                }
                break;
            case 5:
                for (int column = 0; column < 2; column++) {
                    drawBitmap(canvas, 0, column, mImageWidth, 0, column + 1);
                }
                for (int column = 0; column < 3; column++) {
                    drawBitmap(canvas, 1, column, mMinImageWidth, mImageWidth, 2 + column);
                }
                break;
            case 6:
                for (int row = 0; row < 2; row++) {
                    for (int column = 0; column < 3; column++) {
                        drawBitmap(canvas, row, column, mMinImageWidth, row * mMinImageWidth, row * 3 + column);
                    }
                }
                break;
            case 7:
                for (int column = 0; column < 2; column++) {
                    drawBitmap(canvas, 0, column, mImageWidth, 0, column);
                }
                for (int column = 0; column < 3; column++) {
                    drawBitmap(canvas, 1, column, mMinImageWidth, mImageWidth, 2 + column);
                }
                for (int column = 0; column < 2; column++) {
                    drawBitmap(canvas, 2, column, mImageWidth, mImageWidth + mMinImageWidth, 5 + column);
                }
                break;
            case 8:
                for (int column = 0; column < 2; column++) {
                    drawBitmap(canvas, 0, column, mImageWidth, 0, column);
                }
                for (int row = 1; row < 3; row++) {
                    for (int column = 0; column < 3; column++) {
                        drawBitmap(canvas, row, column, mMinImageWidth, mImageWidth + mMinImageWidth * (row - 1), 2 + (row - 1) * 3 + column);
                    }
                }
                break;
            case 9:
                for (int row = 0; row < 3; row++) {
                    for (int column = 0; column < 3; column++) {
                        drawBitmap(canvas, row, column, mMinImageWidth, mMinImageWidth * row, row * 3 + column);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void drawBitmap(Canvas canvas, int row, int column, int imageWidth, int perImageWidth, int i) {
        Bitmap bitmap = mBitmaps[i];
        if (bitmap == null) {
            bitmap = mPlaceHolder;
        }

        float left = getPaddingLeft() + column * mHorizontalSpace + column * imageWidth;
        float top = getPaddingTop() + row * mVerticalSpace + perImageWidth;

        float scale;
        float dx = 0, dy = 0;

        int dwidth = bitmap.getWidth();
        int dheight = bitmap.getHeight();
        int vwidth = imageWidth;
        int vheight = imageWidth;
        if (dwidth * vheight > vwidth * dheight) {
            scale = (float) vheight / (float) dheight;
            dx = (vwidth - dwidth * scale) * 0.5f;//center after scale
        } else {
            scale = (float) vwidth / (float) dwidth;
            dy = (vheight - dheight * scale) * 0.5f;
        }

        matrix.setScale(scale, scale);
        matrix.postTranslate(left + Math.round(dx), top + Math.round(dy));

        BitmapShader mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapShader.setLocalMatrix(matrix);
        paint.setShader(mBitmapShader);
        // TODO drawRectList[i] should be the same as rectF most of the time,
        // try to prevent allocation by checking the array first
        RectF rectF = new RectF(left, top, left + imageWidth, top + imageWidth);
        canvas.drawRoundRect(rectF, mRadius, mRadius, paint);
        mDrawRects[i] = rectF;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isClickItem = false;
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mEventDown = MotionEvent.obtain(event);
                mDown = getClickItem(mEventDown);
                isClickItem = mDown > -1;
                break;
            case MotionEvent.ACTION_UP:
                if (mEventDown != null) {
                    float distance = (float) Math.sqrt(Math.pow((event.getX() - mEventDown.getX()), 2) + Math.pow((event.getY() - mEventDown.getY()), 2));
                    if (distance < ViewConfiguration.getTouchSlop()) {
                        int iUp = getClickItem(event);
                        if (mDown == iUp && iUp > -1) {
                            isClickItem = true;
                            if (onClickItemListener != null) {
                                onClickItemListener.onClick(iUp);
                            }
                        }
                    }
                }
                break;
        }
        return isClickItem || super.onTouchEvent(event);
    }

    private int getClickItem(MotionEvent event) {
        for (int i = 0; i < mDrawRects.length; i++) {
            if (mDrawRects[i] != null && mDrawRects[i].contains(event.getX(), event.getY())) {
                return i;
            }
        }
        return -1;
    }


    public interface OnClickItemListener {
        void onClick(int i);
    }

    private void loadBitmap(final int i, final String url) {
        Glide
                .with(getContext())
                .load(url)
                .asBitmap()
                .dontAnimate()
                .dontTransform()
                .override(mImageWidth, mImageWidth)
                .into(mTargets[i]);
    }

    public void setImageUrls(Collection<String> imageUrls) {
        // clean up outdated stuff
        for (Target target : mTargets) {
            Glide.clear(target); // clears mBitmaps[i] as well
        }
        // re-initialize internal state
        if (imageUrls == null) imageUrls = Collections.emptyList();
        int newSize = imageUrls.size();
        if (newSize > 9) {
            throw new IllegalArgumentException(
                    MultipleImageView.class.getSimpleName() + " only supports 1-9 images.");
        }
        mImageUrls = imageUrls.toArray(new String[newSize]);
        mBitmaps = new Bitmap[newSize];
        mDrawRects = new RectF[newSize];
        //noinspection unchecked
        mTargets = new Target[newSize];
        for (int i = 0; i < newSize; i++) {
            mTargets[i] = new PositionTarget(i);
        }
        requestLayout();
    }


    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    private void refresh(int pos) {
        switch (mBitmaps.length) {
            case 1:
                invalidate();
                break;
            case 2:
                setImageRect(0, pos, mImageWidth, 0);
                break;
            case 3:
                if (pos == 0) {
                    setImageRect(0, 0, mMaxImageWidth, 0);
                } else {
                    setImageRect(1, pos - 1, mImageWidth, mMaxImageWidth);
                }
                break;
            case 4:
                setImageRect(pos / 2, pos % 2, mImageWidth, pos / 2 * mImageWidth);
                break;
            case 5:
                if (pos == 0 || pos == 1) {
                    setImageRect(0, pos, mImageWidth, 0);
                } else {
                    setImageRect(1, pos - 2, mMinImageWidth, mImageWidth);
                }
                break;
            case 6:
                setImageRect(pos / 3, pos % 3, mMinImageWidth, pos / 3 * mMinImageWidth);
                break;
            case 7:
                if (pos == 0 || pos == 1) {
                    setImageRect(0, pos, mImageWidth, 0);
                } else if (pos == 5 || pos == 6) {
                    setImageRect(2, pos - 5, mImageWidth, mImageWidth + mMinImageWidth);
                } else {
                    setImageRect(1, pos - 2, mMinImageWidth, mImageWidth);
                }
                break;
            case 8:
                if (pos == 0 || pos == 1) {
                    setImageRect(0, pos, mImageWidth, 0);
                } else {
                    setImageRect(pos / 3, pos % 3, mMinImageWidth, pos / 3 * mMinImageWidth + mImageWidth);
                }
                break;
            case 9:
                setImageRect(pos / 3, pos % 3, mMinImageWidth, pos / 3 * mMinImageWidth);
                break;
        }
    }

    private void setImageRect(int row, int column, int imageWidth, int perImageWidth) {
        int left = getPaddingLeft() + column * mHorizontalSpace + column * imageWidth;
        int top = getPaddingTop() + row * mVerticalSpace + perImageWidth;
        invalidate(left, top, left + imageWidth, top + imageWidth);
    }

    private class PositionTarget extends SimpleTarget<Bitmap> {
        private final int i;

        PositionTarget(int i) {
            this.i = i;
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            mBitmaps[i] = resource;
            refresh(i);
        }

        @Override
        public void onLoadCleared(Drawable placeholder) {
            super.onLoadCleared(placeholder);
            mBitmaps[i] = null;
            refresh(i);
        }
    }
}
