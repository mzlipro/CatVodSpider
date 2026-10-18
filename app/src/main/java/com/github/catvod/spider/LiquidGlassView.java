package com.github.catvod.spider;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.RuntimeShader;
import android.graphics.Shader;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 液态玻璃（Liquid Glass）效果容器 —— AGSL 版
 *
 * 实现折射、色散、轮廓光、玻璃底色、深浅色双模式、表面光泽、内阴影、边缘描边。
 * API 33+ 使用 AGSL RuntimeShader（GPU 单 pass），低版本 Canvas 兜底。
 */
public class LiquidGlassView extends FrameLayout {

    private static final String TAG = "LiquidGlassView";

    // ====================== AGSL Shader ======================

    private static final String GLASS_SHADER =
        "uniform shader background;" +
        "uniform vec2  uResolution;" +
        "uniform float uRadius;" +
        "uniform float uStrengthUV;" +
        "uniform float uDispersionStrength;" +
        "uniform float uHighlightStrength;" +
        "uniform vec2  uRefractionCenter;" +
        "uniform float uRefractionRadiusScale;" +
        "uniform float uHighlightWidth;" +
        "uniform float uGlassAlpha;" +
        "uniform vec4 uGlassTint;" +
        "uniform float uTime;" +
        "uniform float uBreathing;" +
        "uniform vec4 uHighlightColor;" +
        "uniform float uShimmer;" +
        "uniform float uInnerShadow;" +
        "uniform float uEdgeWidth;" +
        "uniform vec4 uEdgeColor;" +
        "\n" +
        "const float PI = 3.14159265359;\n" +
        "\n" +
        "vec2 displacement(vec2 uv) {\n" +
        "    vec2 halfSize = uResolution * 0.5 - uRadius;\n" +
        "    vec2 d = uv - uResolution * 0.5;\n" +
        "    vec2 q = abs(d) - halfSize;\n" +
        "    float outside = length(max(q, 0.0));\n" +
        "    float inside = min(max(q.x, q.y), 0.0);\n" +
        "    float sdf = outside + inside;\n" +
        "    vec2 base = sign(d) * (1.0 - smoothstep(-uRadius, 0.0, sdf));\n" +
        "    return clamp(base, -1.0, 1.0);\n" +
        "}\n" +
        "\n" +
        "float highlightFactor(vec2 uv, vec2 normal, out bool lit) {\n" +
        "    float normalLen = length(normal);\n" +
        "    lit = false;\n" +
        "    if (normalLen < 1.0 - uHighlightStrength) {\n" +
        "        return 0.0;\n" +
        "    }\n" +
        "    lit = true;\n" +
        "    float g = normal.y * 0.5 + 0.5;\n" +
        "    float span = uHighlightWidth;\n" +
        "    float leftCenter  = 191.0 / 255.0;\n" +
        "    float rightCenter = 1.0 - leftCenter;\n" +
        "    float opacity = 0.0;\n" +
        "    if (normal.x > 0.0) {\n" +
        "        opacity = clamp(1.0 - abs(g - leftCenter) / span, 0.0, 1.0);\n" +
        "    } else if (normal.x < 0.0) {\n" +
        "        opacity = clamp(1.0 - abs(g - rightCenter) / span, 0.0, 1.0);\n" +
        "    }\n" +
        "    opacity *= (1.0 + uBreathing * sin(uTime * 2.0));\n" +
        "    return clamp(opacity, 0.0, 1.0);\n" +
        "}\n" +
        "\n" +
        "vec4 main(vec2 fragCoord) {\n" +
        "    vec2 uv = fragCoord / uResolution;\n" +
        "    vec2 px = fragCoord;\n" +
        "    vec2 normal = displacement(px);\n" +
        "    if (uRefractionRadiusScale != 1.0) {\n" +
        "        vec2 local = (px - uRefractionCenter) / uRefractionRadiusScale;\n" +
        "        normal = displacement(uRefractionCenter + local);\n" +
        "    }\n" +
        "    float aspect = uResolution.x / max(uResolution.y, 1.0);\n" +
        "    vec2 offsetUV = normal * uStrengthUV * vec2(1.0, -aspect);\n" +
        "    vec2 baseUV = px + offsetUV * uResolution.x;\n" +
        "    vec4 bgColor = background.eval(baseUV);\n" +
        "    vec4 glassColor = mix(bgColor, uGlassTint, uGlassAlpha);\n" +
        "    if (uDispersionStrength > 0.0) {\n" +
        "        vec4 rC = background.eval(baseUV + offsetUV * uDispersionStrength * uResolution.x);\n" +
        "        vec4 bC = background.eval(baseUV - offsetUV * uDispersionStrength * uResolution.x);\n" +
        "        vec3 dColor = vec3(rC.r, bgColor.g, bC.b);\n" +
        "        glassColor.rgb = mix(glassColor.rgb, dColor, uGlassAlpha);\n" +
        "    }\n" +
        "    bool lit;\n" +
        "    float hl = highlightFactor(px, normal, lit);\n" +
        "    if (lit) {\n" +
        "        glassColor = mix(glassColor, uHighlightColor, clamp(hl, 0.0, 1.0));\n" +
        "    }\n" +
        "    if (uShimmer > 0.0) {\n" +
        "        float ang = PI * 0.28;\n" +
        "        vec2 dir = vec2(cos(ang), sin(ang));\n" +
        "        float t = dot(uv - 0.5, dir) + 0.5;\n" +
        "        float sweep = smoothstep(0.30, 0.42, t) * (1.0 - smoothstep(0.42, 0.55, t));\n" +
        "        glassColor.rgb += uShimmer * sweep * 0.35;\n" +
        "    }\n" +
        "    if (uInnerShadow > 0.0) {\n" +
        "        vec2 halfSizeIS = uResolution * 0.5 - uRadius;\n" +
        "        vec2 dIS = px - uResolution * 0.5;\n" +
        "        vec2 qIS = abs(dIS) - halfSizeIS;\n" +
        "        float outsideIS = length(max(qIS, 0.0));\n" +
        "        float insideIS = min(max(qIS.x, qIS.y), 0.0);\n" +
        "        float sdfIS = outsideIS + insideIS;\n" +
        "        float sh = 1.0 - smoothstep(-uRadius, 0.0, sdfIS);\n" +
        "        glassColor.rgb *= (1.0 - uInnerShadow * sh * 0.45);\n" +
        "    }\n" +
        "    if (uEdgeWidth > 0.0) {\n" +
        "        vec2 halfSizeE = uResolution * 0.5 - uRadius;\n" +
        "        vec2 dE = px - uResolution * 0.5;\n" +
        "        vec2 qE = abs(dE) - halfSizeE;\n" +
        "        float outsideE = length(max(qE, 0.0));\n" +
        "        float insideE = min(max(qE.x, qE.y), 0.0);\n" +
        "        float sdfE = outsideE + insideE;\n" +
        "        float edge = 1.0 - smoothstep(0.0, max(uEdgeWidth, 1.0), abs(sdfE));\n" +
        "        glassColor = mix(glassColor, uEdgeColor, edge * uEdgeColor.a);\n" +
        "    }\n" +
        "    glassColor.a *= uGlassAlpha;\n" +
        "    return vec4(glassColor.rgb * glassColor.a, glassColor.a);\n" +
        "}\n";

    private static final String FLAT_BG_SHADER =
        "uniform half4 uColor;" +
        "vec4 main(vec2 fragCoord) { return uColor; }";

    // ====================== 状态字段 ======================

    private boolean mDarkMode = false;

    private float mRefractionStrength = 0.03f;
    private float mDispersionStrength = 0.03f;
    private float mHighlightStrength  = 0.10f;
    private float mGlassAlpha = 0.55f;
    private int   mTintColor = 0xFFFFFFFF;
    private float mCornerRadius = 24f;

    private boolean mInteractiveRefraction = false;
    private float mRefractionCenterX = 0.5f, mRefractionCenterY = 0.5f;
    private float mRefractionRadiusScale = 1.0f;

    private boolean mBreathing = false;
    private long mStartTime = System.currentTimeMillis() / 1000L;
    private float mBreathPhase = 0f;
    private ValueAnimator mBreathAnimator;

    private Bitmap mBackgroundBitmap;
    private Shader mBackgroundShader;
    private boolean mDirtySampling = true;

    private final List<Ripple> mRipples = new ArrayList<>();
    private static final int MAX_RIPPLES = 3;

    private final Paint mTintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mEdgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mInnerShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mShimmerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mRipplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mShaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private RuntimeShader mGlassShader;
    private RuntimeShader mFlatBgShader;
    private boolean mAgslAvailable = false;

    private float mGlassInset = 0f;
    private boolean mRefractionEnabled = true;

    // ====================== 构造 & 初始化 ======================

    public LiquidGlassView(Context context) {
        super(context);
        init();
    }

    
    private void init() {
        setWillNotDraw(false);
        setClipChildren(false);
        setFocusable(false);

        setClipToOutline(true);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), dp(mCornerRadius));
            }
        });

        mTintPaint.setColor(mTintColor);
        mTintPaint.setAlpha((int) (mGlassAlpha * 255));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                mGlassShader = new RuntimeShader(GLASS_SHADER);
                mFlatBgShader = new RuntimeShader(FLAT_BG_SHADER);
                mAgslAvailable = true;
                Log.i(TAG, "AGSL RuntimeShader 初始化成功");
            } catch (Throwable t) {
                mAgslAvailable = false;
                Log.w(TAG, "AGSL 不可用，回退到 Canvas 路径: " + t.getMessage());
            }
        } else {
            mAgslAvailable = false;
            Log.i(TAG, "API < 33，使用 Canvas 路径");
        }
    }

    // ====================== 绘制 ======================

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mGlassInset = 0f;              // ← 改为 0，消除灰色方形框
        mDirtySampling = true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        try {
            if (mAgslAvailable && mRefractionEnabled) {
                drawWithShader(canvas, w, h);
            } else {
                drawWithCanvas(canvas, w, h);
            }
            drawRipples(canvas);
        } catch (Throwable t) {
            Log.e(TAG, "绘制失败，兜底纯色: " + t.getMessage());
            DanmakuSpider.log("LiquidGlassView 绘制异常: " + t.getMessage());  // ← 加这行
            canvas.drawColor(mDarkMode ? 0xCC1A1A2E : 0xB3FFFFFF);
        }

        super.onDraw(canvas);
    }

    // -------------------- AGSL 主路径 --------------------

    private void drawWithShader(Canvas canvas, int w, int h) {
        float t = (System.currentTimeMillis() / 1000.0f - mStartTime) + mBreathPhase;
        Shader bgShader = getBackgroundShader(w, h);

        mGlassShader.setFloatUniform("uResolution", (float) w, (float) h);
        mGlassShader.setFloatUniform("uRadius", dp(mCornerRadius));
        mGlassShader.setFloatUniform("uStrengthUV", mRefractionStrength);
        mGlassShader.setFloatUniform("uDispersionStrength", mDispersionStrength);
        mGlassShader.setFloatUniform("uHighlightStrength", mHighlightStrength);
        mGlassShader.setFloatUniform("uRefractionCenter",
                mRefractionCenterX * w, mRefractionCenterY * h);
        mGlassShader.setFloatUniform("uRefractionRadiusScale", mRefractionRadiusScale);
        mGlassShader.setFloatUniform("uHighlightWidth", 50f / 255f);
        mGlassShader.setFloatUniform("uGlassAlpha", mGlassAlpha);
        // 用 setFloatUniform 传 RGBA 分量，避免 half4/vec4 类型问题
        int tintColor = mDarkMode ? 0xFF1A1A2E : mTintColor;
        mGlassShader.setFloatUniform("uGlassTint",
                ((tintColor >> 16) & 0xFF) / 255f,
                ((tintColor >> 8) & 0xFF) / 255f,
                (tintColor & 0xFF) / 255f,
                ((tintColor >>> 24) & 0xFF) / 255f);
        mGlassShader.setFloatUniform("uTime", t);
        mGlassShader.setFloatUniform("uBreathing", mBreathing ? 0.35f : 0f);
        int hlColor = mDarkMode ? 0x6600E5FF : 0xDDFFFFFF;
        mGlassShader.setFloatUniform("uHighlightColor",
                ((hlColor >> 16) & 0xFF) / 255f,
                ((hlColor >> 8) & 0xFF) / 255f,
                (hlColor & 0xFF) / 255f,
                ((hlColor >>> 24) & 0xFF) / 255f);
        mGlassShader.setFloatUniform("uShimmer", 0.15f);
        mGlassShader.setFloatUniform("uInnerShadow", 0f);
        mGlassShader.setFloatUniform("uEdgeWidth", 0f);
        int edgeColor = mDarkMode ? 0x5500E5FF : 0x66FFFFFF;
        mGlassShader.setFloatUniform("uEdgeColor",
                ((edgeColor >> 16) & 0xFF) / 255f,
                ((edgeColor >> 8) & 0xFF) / 255f,
                (edgeColor & 0xFF) / 255f,
                ((edgeColor >>> 24) & 0xFF) / 255f);

        if (bgShader == null) {
            // 生成渐变背景
            LinearGradient gradient = new LinearGradient(0, 0, w, h,
                    mDarkMode ?
                        new int[]{0xFF1A1A2E, 0xFF0D0D1A, 0xFF1A1A2E} :
                        new int[]{0xFFDDE3EA, 0xFFB8C2CC, 0xFFDDE3EA},
                    null, Shader.TileMode.CLAMP);
            mShaderPaint.setShader(gradient);
            canvas.drawRect(0, 0, w, h, mShaderPaint);
            bgShader = gradient;
        }
        mGlassShader.setInputShader("background", bgShader);

        mShaderPaint.setShader(mGlassShader);
        canvas.drawRect(0, 0, w, h, mShaderPaint);
    }

    private Shader getBackgroundShader(int w, int h) {
        if (mBackgroundBitmap != null && !mBackgroundBitmap.isRecycled()) {
            if (mDirtySampling) {
                mBackgroundShader = new BitmapShader(mBackgroundBitmap,
                        Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                mDirtySampling = false;
            }
            return mBackgroundShader;
        }
        // 用渐变代替纯色，让折射效果更自然
        LinearGradient gradient;
        if (mDarkMode) {
            gradient = new LinearGradient(0, 0, w, h,
                    new int[]{0xFF1A1A2E, 0xFF14141F, 0xFF0D0D1A, 0xFF1A1A2E},
                    null, Shader.TileMode.CLAMP);
        } else {
            gradient = new LinearGradient(0, 0, w, h,
                    new int[]{0xFFDDE3EA, 0xFFC5CDD6, 0xFFB8C2CC, 0xFFDDE3EA},
                    null, Shader.TileMode.CLAMP);
        }
        return gradient;  // ← 直接返回渐变shader
    }

    // -------------------- Canvas 兜底路径 --------------------

    private void drawWithCanvas(Canvas canvas, int w, int h) {
        RectF rect = new RectF(mGlassInset, mGlassInset, w - mGlassInset, h - mGlassInset);
        float rx = dp(mCornerRadius);
        float ry = dp(mCornerRadius);

        drawBlurredBackground(canvas, w, h);

        mTintPaint.setColor(mDarkMode ? 0xFF1A1A2E : mTintColor);
        mTintPaint.setAlpha((int) (mGlassAlpha * 255));
        canvas.drawRoundRect(rect, rx, ry, mTintPaint);

        drawRefractionGradient(canvas, rect, rx, ry);

        if (mDispersionStrength > 0f) {
            drawDispersionLayers(canvas, rect, rx, ry);
        }

        drawEdgeGlow(canvas, rect, rx, ry);
        drawTopHighlight(canvas, rect);
        drawInnerShadow(canvas, rect, rx, ry);
    }

    private void drawBlurredBackground(Canvas canvas, int w, int h) {
        canvas.drawColor(mDarkMode ? 0xCC0D0D1A : 0xCCB0BEC5);
    }

    private void drawRefractionGradient(Canvas canvas, RectF rect, float rx, float ry) {
        LinearGradient gradient;
        if (mDarkMode) {
            gradient = new LinearGradient(
                    rect.left, rect.top, rect.right * 0.3f, rect.bottom,
                    new int[]{0x33FFFFFF, 0x18FFFFFF, 0x00000000, 0x22000000, 0x44000000},
                    new float[]{0f, 0.15f, 0.45f, 0.75f, 1f},
                    Shader.TileMode.CLAMP);
        } else {
            gradient = new LinearGradient(
                    rect.left, rect.top, rect.right * 0.3f, rect.bottom,
                    new int[]{0x44FFFFFF, 0x22FFFFFF, 0x00000000, 0x11000000, 0x33000000},
                    new float[]{0f, 0.15f, 0.45f, 0.75f, 1f},
                    Shader.TileMode.CLAMP);
        }
        mGradientPaint.setShader(gradient);
        canvas.drawRoundRect(rect, rx, ry, mGradientPaint);
    }

    private void drawDispersionLayers(Canvas canvas, RectF rect, float rx, float ry) {
        int shift = (int) (mDispersionStrength * 40);
        Paint red = new Paint(Paint.ANTI_ALIAS_FLAG);
        red.setColor(0x22FF0000);
        Paint blue = new Paint(Paint.ANTI_ALIAS_FLAG);
        blue.setColor(0x220000FF);
        RectF rRect = new RectF(rect.left + shift, rect.top, rect.right + shift, rect.bottom);
        RectF bRect = new RectF(rect.left - shift, rect.top, rect.right - shift, rect.bottom);
        canvas.drawRoundRect(rRect, rx, ry, red);
        canvas.drawRoundRect(bRect, rx, ry, blue);
    }

    private void drawTopHighlight(Canvas canvas, RectF rect) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(2));
        p.setColor(mDarkMode ? 0x6600E5FF : 0xAAFFFFFF);
        RectF topArc = new RectF(rect.left + dp(4), rect.top + dp(1),
                rect.right - dp(4), rect.top + dp(8));
        canvas.drawRoundRect(topArc, dp(6), dp(6), p);
    }

    private void drawInnerShadow(Canvas canvas, RectF rect, float rx, float ry) {
        LinearGradient shadow = new LinearGradient(
                rect.left, rect.top, rect.left, rect.top + dp(20),
                0x22000000, 0x00000000, Shader.TileMode.CLAMP);
        mInnerShadowPaint.setShader(shadow);
        canvas.drawRoundRect(rect, rx, ry, mInnerShadowPaint);
    }

    private void drawEdgeGlow(Canvas canvas, RectF rect, float rx, float ry) {
        LinearGradient edge = new LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                new int[]{0xCCFFFFFF, 0x00000000, 0x22FFFFFF, 0xCCFFFFFF},
                new float[]{0f, 0.35f, 0.65f, 1f},
                Shader.TileMode.CLAMP);
        mEdgePaint.setShader(edge);
        mEdgePaint.setStyle(Paint.Style.STROKE);
        mEdgePaint.setStrokeWidth(dp(2));
        canvas.drawRoundRect(rect, rx, ry, mEdgePaint);
    }

    // -------------------- 涟漪 --------------------

    private void drawRipples(Canvas canvas) {
        if (mRipples.isEmpty()) return;
        long now = System.currentTimeMillis();
        Iterator<Ripple> it = mRipples.iterator();
        while (it.hasNext()) {
            Ripple r = it.next();
            float elapsed = (now - r.startTime) / 1000f;
            if (elapsed > r.duration) {
                it.remove();
                continue;
            }
            float progress = elapsed / r.duration;
            float radius = r.maxRadius * progress;
            float alpha = (1f - progress) * 0.5f;
            mRipplePaint.reset();
            mRipplePaint.setAntiAlias(true);
            mRipplePaint.setStyle(Paint.Style.STROKE);
            mRipplePaint.setStrokeWidth(dp(2) * (1f - progress));
            int color = mDarkMode
                    ? Color.argb((int)(alpha * 255), 0x00, 0xE5, 0xFF)
                    : Color.argb((int)(alpha * 255), 255, 255, 255);
            mRipplePaint.setColor(color);
            canvas.drawCircle(r.x, r.y, radius, mRipplePaint);
        }
        invalidate();
    }

    private static class Ripple {
        final float x, y;
        final long startTime;
        final float maxRadius;
        final float duration;
        Ripple(float x, float y, long start, float maxRadius, float duration) {
            this.x = x;
            this.y = y;
            this.startTime = start;
            this.maxRadius = maxRadius;
            this.duration = duration;
        }
    }

    // ====================== 交互 ======================

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (mInteractiveRefraction && getWidth() > 0 && getHeight() > 0) {
                    setRefractionCenter(event.getX() / getWidth(), event.getY() / getHeight());
                }
                break;
            case MotionEvent.ACTION_UP:
                addRipple(event.getX(), event.getY());
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void addRipple(float x, float y) {
        if (mRipples.size() >= MAX_RIPPLES) {
            mRipples.remove(0);
        }
        float maxR = (float) Math.hypot(getWidth(), getHeight()) * 0.5f;
        mRipples.add(new Ripple(x, y, System.currentTimeMillis(), maxR, 0.8f));
        invalidate();
    }

    // ====================== 动画 ======================

    public void startBreathing() {
        if (mBreathing) return;
        mBreathing = true;
        mStartTime = System.currentTimeMillis() / 1000L;
        if (mBreathAnimator == null) {
            mBreathAnimator = ValueAnimator.ofFloat(0, 1);
            mBreathAnimator.setDuration(1000);
            mBreathAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mBreathAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mBreathAnimator.addUpdateListener(animation -> {
                mBreathPhase = (float) animation.getAnimatedValue();
                invalidate();
            });
        }
        mBreathAnimator.start();
    }

    public void stopBreathing() {
        mBreathing = false;
        if (mBreathAnimator != null) {
            mBreathAnimator.cancel();
        }
        invalidate();
    }

    // ====================== 参数设置 ======================

    /**
     * 当前使用的渲染路径
     */
    public String getRenderMode() {
        return mAgslAvailable && mRefractionEnabled ? "AGSL (GPU)" : "Canvas (兜底)";
    }

    /**
     * 是否使用 AGSL
     */
    public boolean isAgslAvailable() {
        return mAgslAvailable;
    }
    
    /** 深色模式开关 */
    public void setDarkMode(boolean dark) {
        mDarkMode = dark;
        mDirtySampling = true;
        invalidate();
    }

    /** 折射强度 */
    public void setRefractionStrength(float v) {
        mRefractionStrength = clamp(v, 0f, 0.2f);
        invalidate();
    }

    /** 色散强度 */
    public void setDispersionStrength(float v) {
        mDispersionStrength = clamp(v, 0f, 0.2f);
        invalidate();
    }

    /** 轮廓光强度 */
    public void setHighlightStrength(float v) {
        mHighlightStrength = clamp(v, 0f, 0.5f);
        invalidate();
    }

    /** 玻璃不透明度 */
    public void setGlassAlpha(float alpha) {
        mGlassAlpha = clamp(alpha, 0.1f, 0.95f);
        invalidate();
    }

    /** 玻璃染色 */
    public void setTintColor(int color) {
        mTintColor = color;
        invalidate();
    }

    /** 圆角半径（dp） */
    public void setCornerRadius(float radiusDp) {
        mCornerRadius = clamp(radiusDp, 0f, 128f);
        invalidateOutline();
        invalidate();
    }

    /** 启用/禁用折射 */
    public void setRefractionEnabled(boolean enabled) {
        mRefractionEnabled = enabled;
        invalidate();
    }

    /** 动态折射源 */
    public void setInteractiveRefraction(boolean enabled) {
        mInteractiveRefraction = enabled;
        if (!enabled) {
            setRefractionCenter(0.5f, 0.5f);
        }
    }

    /** 设置折射中心（归一化坐标 0..1） */
    public void setRefractionCenter(float nx, float ny) {
        mRefractionCenterX = clamp(nx, 0f, 1f);
        mRefractionCenterY = clamp(ny, 0f, 1f);
        invalidate();
    }

    /** 注入背景 Bitmap */
    public void setBackgroundBitmap(Bitmap bitmap) {
        mBackgroundBitmap = bitmap;
        mBackgroundShader = null;
        mDirtySampling = true;
        invalidate();
    }

    /** 刷新背景采样 */
    public void updateBackgroundSampling() {
        mDirtySampling = true;
        invalidate();
    }

    // ====================== 兼容旧接口 ======================

    /** 兼容旧接口：setBlurRadius → 映射到折射强度 */
    public void setBlurRadius(float radius) {
        // 旧版 18f → 新版折射强度约 0.036
        setRefractionStrength(radius / 500f);
    }

    /** 兼容旧接口：updateBlur → 仅刷新 */
    public void updateBlur() {
        mDirtySampling = true;
        invalidate();
    }

    // ====================== 生命周期 ======================

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopBreathing();
    }

    // ====================== 工具 ======================

    private static float clamp(float v, float lo, float hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private float dp(float v) {
        return v * getContext().getResources().getDisplayMetrics().density;
    }
}