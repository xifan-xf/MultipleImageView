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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import geek.fan.multipleimageview.R;


/**
 * Created by liaolan on 16/1/7.
 */
public class PhotoWallView extends View {

    private ArrayList<String> imageUrls = new ArrayList<>();
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private Bitmap placeHolder;
    private int horizontalSpace = 10;
    private int verticalSpace = 10;
    private int radius = 0;
    private int columns = 4;
    private int rows = 1;
    private int imageWidth = 0;
    private Matrix matrix = new Matrix();
    final Paint paint = new Paint();
    private boolean isLoading = false;
    private OnClickItemListener onClickItemListener;


    public PhotoWallView(Context context) {
        this(context, null);
    }

    public PhotoWallView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoWallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PhotoWallView);
            for (int i = 0; i < a.getIndexCount(); i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case R.styleable.PhotoWallView_pwvHorizontalSpace:
                        horizontalSpace = a.getDimensionPixelSize(attr, horizontalSpace);
                        break;
                    case R.styleable.PhotoWallView_pwvVerticalSpace:
                        verticalSpace = a.getDimensionPixelSize(attr, verticalSpace);
                        break;
                    case R.styleable.PhotoWallView_pwvRadius:
                        radius = a.getDimensionPixelSize(attr, radius);
                        break;
                    case R.styleable.PhotoWallView_pwvColumns:
                        columns = a.getInt(attr, columns);
                        break;
                }
            }
        }

        paint.setAntiAlias(true);
        placeHolder = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_photo);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!imageUrls.isEmpty()) {
            int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            imageWidth = (width - (columns - 1) * horizontalSpace - getPaddingLeft() - getPaddingRight()) / columns;
            rows = (int) Math.ceil(imageUrls.size() * 1f / columns);
            int height = imageWidth * rows + (rows - 1) * verticalSpace + getPaddingTop() + getPaddingBottom();
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!isLoading) {
            loadBitmap(0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!imageUrls.isEmpty()) {
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    int i = row * columns + column;

                    Bitmap bitmap;
                    if (i >= imageUrls.size()) {
                        break;
                    } else if (i < bitmaps.size()) {
                        bitmap = bitmaps.get(i);
                    } else {
                        bitmap = placeHolder;
                    }

                    float left = getPaddingLeft() + column * horizontalSpace + column * imageWidth;
                    float top = getPaddingTop() + row * verticalSpace + row * imageWidth;
                    float scale;
                    float dx = 0, dy = 0;

                    int dwidth = bitmap.getWidth();
                    int dheight = bitmap.getHeight();
                    int vwidth = imageWidth;
                    int vheight = imageWidth;
                    if (dwidth * vheight > vwidth * dheight) {
                        scale = (float) vheight / (float) dheight;
                        dx = (vwidth - dwidth * scale) * 0.5f;
                    } else {
                        scale = (float) vwidth / (float) dwidth;
                        dy = (vheight - dheight * scale) * 0.5f;
                    }

                    matrix.setScale(scale, scale);
                    matrix.postTranslate(left + Math.round(dx), top + Math.round(dy));

                    BitmapShader mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    mBitmapShader.setLocalMatrix(matrix);
                    paint.setShader(mBitmapShader);
                    RectF rectF = new RectF(left, top, left + imageWidth, top + imageWidth);
                    canvas.drawRoundRect(rectF, radius, radius, paint);
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Glide.clear(glideTarget);
        isLoading = false;
    }

    private MotionEvent eventDown;
    private int iDown;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isClickItem = false;
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                eventDown = MotionEvent.obtain(event);
                iDown = getClickItem(eventDown);
                isClickItem = iDown > -1;
                break;
            case MotionEvent.ACTION_UP:
                if (eventDown != null) {
                    float distance = (float) Math.sqrt(Math.pow((event.getX() - eventDown.getX()), 2) + Math.pow((event.getY() - eventDown.getY()), 2));

                    if (distance < ViewConfiguration.getTouchSlop()) {

                        int iUp = getClickItem(event);
                        if (iDown == iUp && iUp > -1) {
                            isClickItem = true;
                            if (onClickItemListener != null) {
                                onClickItemListener.onClick(iUp, imageUrls);
                            }
                        }
                    }
                }
                break;
        }
        return isClickItem ? true : super.onTouchEvent(event);
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    private int getClickItem(MotionEvent event) {
        int i = -1;
        float result = (event.getX() - getPaddingLeft() * 1f) / (imageWidth + horizontalSpace);
        if (result < 0 || result >= columns) {
            return i;
        }
        int column = (int) result;

        result = (event.getY() - getPaddingTop() * 1f) / (imageWidth + verticalSpace);
        if (result < 0 || result >= rows) {
            return i;
        }
        int row = (int) result;

        i = row * columns + column;
        return i < imageUrls.size() ? i : -1;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls.clear();
        this.imageUrls.addAll(imageUrls);
        bitmaps.clear();
        isLoading = false;
        Glide.clear(glideTarget);
        requestLayout();
    }

    private void loadBitmap(final int i) {
        if (imageUrls.isEmpty()) {
            return;
        }
        isLoading = true;
        Log.d("loadBitmap",imageUrls.get(i));
        Glide.with(getContext()).load("http://img.hb.aicdn.com/ec6581a5006dc39d633503650bdfd227523df3fe24421-gJnX2K_fw658").asBitmap().into(glideTarget);
    }

    private SimpleTarget<Bitmap> glideTarget = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            bitmaps.add(resource);
            invalidate();

            int bSize = bitmaps.size();
            if (bSize < imageUrls.size()) {
                loadBitmap(bSize);
            } else {
                isLoading = false;
            }
        }
    };

    public interface OnClickItemListener {
        void onClick(int i, ArrayList<String> urls);
    }

}
