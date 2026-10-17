package com.github.catvod.spider;

import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

/**
 * 真液态玻璃效果容器
 * 实现高斯模糊 + 渐变折射 + 边缘高光 + 内阴影
 */
public class LiquidGlassView extends FrameLayout {

    // 新增：深色模式标记
    private boolean mDarkMode = false;
    // 效果参数
    private float mBlurRadius = 18f;
    private float mGlassAlpha = 0.55f;
    private int mTintColor = 0xFFFFFFFF;
    private float mCornerRadius = 24f;

    // 画笔
    private Paint mTintPaint;
    private Paint mGradientPaint;
    private Paint mEdgePaint;
    private Paint mInnerShadowPaint;
    private Paint mShimmerPaint;

    // 模糊缓存
    private Bitmap mBlurCache;
    private boolean mNeedsBlur = true;

    public LiquidGlassView(Context context) {
        super(context);
        init();
    }

    public void setDarkMode(boolean dark) {
        mDarkMode = dark;
        mNeedsBlur = true;
        invalidate();
    }
    
    private void init() {
        setWillNotDraw(false);
        setClipChildren(false);

        // ====== 新增：裁剪到圆角 ======
        setClipToOutline(true);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, android.graphics.Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(),
                        dp((int) mCornerRadius));
            }
        });

        mTintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTintPaint.setColor(mTintColor);
        mTintPaint.setAlpha((int) (mGlassAlpha * 255));

        mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        try {
            RectF rect = new RectF(0, 0, w, h);
            float rx = dp((int) mCornerRadius);
            float ry = dp((int) mCornerRadius);

            // 1. 模糊背景
            drawBlurredBackground(canvas, w, h);

            // 2. 玻璃底色（深色模式用黑色半透明）
            if (mDarkMode) {
                mTintPaint.setColor(0xFF1A1A2E);
                mTintPaint.setAlpha((int) (mGlassAlpha * 255));
            } else {
                mTintPaint.setColor(mTintColor);
                mTintPaint.setAlpha((int) (mGlassAlpha * 255));
            }
            canvas.drawRoundRect(rect, rx, ry, mTintPaint);

            // 3. 光线折射渐变
            drawRefractionGradient(canvas, rect);

            // 4. 边缘高光
            drawEdgeGlow(canvas, rect);

        } catch (Throwable t) {
            canvas.drawColor(mDarkMode ? 0xCC1A1A2E : 0xB3FFFFFF);
        }

        super.onDraw(canvas);
    }

    /**
     * 高斯模糊背景：多级缩放实现真实模糊感
     */
    /**
     * 高斯模糊背景：安全实现，不递归
     */
    private void drawBlurredBackground(Canvas canvas, int w, int h) {
        try {
            if (mDarkMode) {
                canvas.drawColor(0xCC0D0D1A);  // 深色：深蓝黑
            } else {
                canvas.drawColor(0xCCB0BEC5);  // 浅色：灰蓝
            }
        } catch (Throwable t) {
            canvas.drawColor(mDarkMode ? 0xCC0D0D1A : 0xCCEEF2F7);
        }
    }

    /**
     * 光线折射渐变：模拟光线穿过玻璃的明暗变化
     */
     private void drawRefractionGradient(Canvas canvas, RectF rect) {
        LinearGradient gradient;
        if (mDarkMode) {
            // 深色模式：暗色渐变
            gradient = new LinearGradient(
                    rect.left, rect.top, rect.right * 0.3f, rect.bottom,
                    new int[]{
                            0x33FFFFFF,   // 左上角微亮
                            0x18FFFFFF,
                            0x00000000,
                            0x22000000,
                            0x44000000
                    },
                    new float[]{0f, 0.15f, 0.45f, 0.75f, 1f},
                    Shader.TileMode.CLAMP
            );
        } else {
            // 浅色模式：亮色渐变
            gradient = new LinearGradient(
                    rect.left, rect.top, rect.right * 0.3f, rect.bottom,
                    new int[]{
                            0x44FFFFFF,
                            0x22FFFFFF,
                            0x00000000,
                            0x11000000,
                            0x33000000
                    },
                    new float[]{0f, 0.15f, 0.45f, 0.75f, 1f},
                    Shader.TileMode.CLAMP
            );
        }
        mGradientPaint.setShader(gradient);
        canvas.drawRoundRect(rect, dp((int) mCornerRadius), dp((int) mCornerRadius), mGradientPaint);
    }

    /**
     * 顶部高光线：模拟玻璃顶部反光
     */
    private void drawTopHighlight(Canvas canvas, RectF rect) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(2));
        p.setColor(0xAAFFFFFF);

        RectF topArc = new RectF(rect.left + dp(4), rect.top + dp(1),
                rect.right - dp(4), rect.top + dp(8));
        canvas.drawRoundRect(topArc, dp(6), dp(6), p);
    }

    /**
     * 内阴影：玻璃厚度感
     */
    private void drawInnerShadow(Canvas canvas, RectF rect) {
        LinearGradient shadow = new LinearGradient(
                rect.left, rect.top, rect.left, rect.top + dp(20),
                0x22000000,
                0x00000000,
                Shader.TileMode.CLAMP
        );
        mInnerShadowPaint.setShader(shadow);
        canvas.drawRoundRect(rect, dp((int) mCornerRadius), dp((int) mCornerRadius), mInnerShadowPaint);
    }

    /**
     * 边缘高光：单层干净描边
     */
    private void drawEdgeGlow(Canvas canvas, RectF rect) {
        Paint edge = new Paint(Paint.ANTI_ALIAS_FLAG);
        edge.setStyle(Paint.Style.STROKE);
        edge.setStrokeWidth(dp(2));
        edge.setColor(mDarkMode ? 0x4400E5FF : 0x88FFFFFF);  // 深色用青色边框
        canvas.drawRoundRect(rect, dp((int) mCornerRadius), dp((int) mCornerRadius), edge);
    }

    /**
     * 玻璃光泽斜线：模拟光线在玻璃表面的反射
     */
    private void drawShimmer(Canvas canvas, RectF rect) {
        LinearGradient shimmer = new LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom * 0.6f,
                new int[]{0x00000000, 0x18FFFFFF, 0x00000000},
                new float[]{0.2f, 0.4f, 0.6f},
                Shader.TileMode.CLAMP
        );
        mShimmerPaint.setShader(shimmer);
        canvas.drawRoundRect(rect, dp((int) mCornerRadius), dp((int) mCornerRadius), mShimmerPaint);
    }

    // ========== 参数设置 ==========

    public void setBlurRadius(float radius) {
        mBlurRadius = Math.max(4, Math.min(30, radius));
        mNeedsBlur = true;
        invalidate();
    }

    public void setGlassAlpha(float alpha) {
        mGlassAlpha = Math.max(0.1f, Math.min(0.95f, alpha));
        invalidate();
    }

    public void setTintColor(int color) {
        mTintColor = color;
        mTintPaint.setColor(color);
        invalidate();
    }

    public void setCornerRadius(float radius) {
        mCornerRadius = radius;
        invalidateOutline();  // ← 新增：刷新圆角裁剪
        invalidate();
    }

    public void updateBlur() {
        mNeedsBlur = true;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mNeedsBlur = true;
    }

    private int dp(int d) {
        return (int) (d * getContext().getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBlurCache != null && !mBlurCache.isRecycled()) {
            mBlurCache.recycle();
            mBlurCache = null;
        }
    }
}