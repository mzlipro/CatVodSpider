package com.github.catvod.spider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;   // ← 新增这一行
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.content.DialogInterface;
import android.widget.ScrollView;
import android.widget.HorizontalScrollView;   // ← 新增这一行
import android.widget.ListView;
import android.widget.TextView;
import com.github.catvod.spider.entity.DanmakuItem;
import com.github.catvod.spider.danmu.SharedPreferencesService;
import com.github.catvod.net.OkHttp;
import okhttp3.Response;

import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DanmakuUIHelper {

    // 是否强制所有弹窗使用液态玻璃效果（true=始终启用，false=跟随主题模式）
    private static final boolean FORCE_LIQUID_GLASS = true;
    // 定义统一的颜色常量 - 现代化配色方案
    private static final int PRIMARY_COLOR = 0xFF007AFF;        // 主色调蓝色 - 第一层(页签)
    private static final int PRIMARY_DARK = 0xFF0056B3;         // 深蓝色
    private static final int PRIMARY_LIGHT = 0xFF80BFFF;        // 浅蓝色
    private static final int SECONDARY_COLOR = 0xFFFF9500;      // 橙色 - 第二层(分组)
    private static final int SECONDARY_DARK = 0xFFCC7700;       // 深橙色
    private static final int SECONDARY_LIGHT = 0xFFFFB84D;      // 浅橙色
    private static final int TERTIARY_COLOR = 0xFF34C759;       // 绿色 - 第三层(结果)
    private static final int TERTIARY_DARK = 0xFF2D9D4F;        // 深绿色
    private static final int TERTIARY_LIGHT = 0xFF5ECA7C;       // 浅绿色
    private static final int ACCENT_COLOR = 0xFFFF9500;         // 橙色强调
    private static final int ACCENT_LIGHT = 0xFFFFB84D;         // 浅橙色
    private static final int SUCCESS_COLOR = 0xFF34C759;        // 绿色成功
    private static final int TEXT_PRIMARY = 0xFF1A1A1A;         // 深灰色主文本
    private static final int TEXT_SECONDARY = 0xFF666666;       // 中灰色副文本
    private static final int TEXT_TERTIARY = 0xFF999999;        // 浅灰色辅助文本
    private static final int BACKGROUND_LIGHT = 0xFFF8F9FA;     // 浅灰白背景
    private static final int BACKGROUND_WHITE = 0xFFFFFFFF;     // 纯白背景
    private static final int BORDER_COLOR = 0xFFE0E0E0;         // 边框色
    private static final int BORDER_LIGHT = 0xFFF0F0F0;         // 浅边框色
    private static final int FOCUS_HIGHLIGHT_COLOR = 0xFF80BFFF;// 焦点高亮色
    private static final int SHADOW_COLOR = 0x1A000000;         // 阴影色
    private static final int GRAY_INACTIVE = 0xFFBBBBBB;        // 灰色(未选中状态)

    // ========== 电视端焦点高亮专用颜色 ==========
    private static final int TV_FOCUS_BORDER_COLOR = 0xFF00D4FF;  // 高对比度青色边框 - 电视端焦点
    private static final int TV_FOCUS_BG_COLOR = 0xFF007AFF;      // 焦点背景色 - 蓝色
    private static final int TV_FOCUS_TEXT_COLOR = 0xFFFFFFFF;    // 焦点文字颜色 - 白色
    private static final int TV_FOCUS_GLOW_COLOR = 0xFF00D4FF;    // 发光效果颜色
    private static final int TV_INPUT_FOCUS_BG = 0xFFE3F2FD;      // 输入框焦点背景 - 浅蓝
    private static final int TV_INPUT_FOCUS_BORDER = 0xFF007AFF;  // 输入框焦点边框

    // ========== 深色透明主题颜色 (from DanmakuUIHelper3) ==========
    private static final int DARK_BG_PRIMARY =  0xCC000000;     // 主背景色 - 半透明黑色
    private static final int DARK_BG_SECONDARY = 0x33FFFFFF;   // 次级背景色 - 更透明
    private static final int DARK_BG_TERTIARY = 0x1AFFFFFF;    // 三级背景色 - 更透明
    private static final int DARK_TEXT_PRIMARY = 0xFFFFFFFF;   // 主文本色 - 白色
    private static final int DARK_TEXT_SECONDARY = 0xFFCCCCCC; // 次文本色 - 浅灰色
    private static final int DARK_TEXT_TERTIARY = 0xFF999999;  // 三级文本色 - 中灰色
    private static final int DARK_BORDER = 0x44FFFFFF;         // 边框色 - 浅白透明
    private static final int DARK_HIGHLIGHT = 0xFF007AFF;      // 高亮色 - 蓝色
    private static final int DARK_HIGHLIGHT_DARK = 0xFF0056B3; // 深高亮色 - 深蓝
    private static final int DARK_HIGHLIGHT_LIGHT = 0xFF80BFFF;// 浅高亮色 - 浅蓝
    private static final int DARK_INACTIVE = 0x55444444;       // 非激活状态 - 深灰半透明

    // 功能色（深色透明版本）
    private static final int DARK_PRIMARY_COLOR = 0x99007AFF;        // 页签蓝色
    private static final int DARK_PRIMARY_DARK = 0xCC0056B3;         // 深蓝色
    private static final int DARK_PRIMARY_LIGHT = 0xCC80BFFF;        // 浅蓝色
    private static final int DARK_SECONDARY_COLOR = 0xAAFF9500;      // 分组橙色 (translucent orange)
    private static final int DARK_SECONDARY_DARK = 0xCCCC7700;       // 深橙色 (darker translucent orange)
    private static final int DARK_SECONDARY_LIGHT = 0xCCFFB84D;      // 浅橙色
    private static final int DARK_TERTIARY_COLOR = 0xAA34C759;       // 结果绿色 (translucent green)
    private static final int DARK_TERTIARY_DARK = 0xCC2D9D4F;        // 深绿色 (darker translucent green)
    private static final int DARK_TERTIARY_LIGHT = 0x99BBBBBB;       // 浅绿色
    private static final int DARK_ACCENT_COLOR = 0xAAFF9500;         // 橙色强调
    private static final int DARK_ACCENT_LIGHT = 0xCCFFB84D;         // 浅橙色


    /**
     * 排序状态标记 (false=正序, true=倒序)
     */
    private static boolean isReversed;
    /**
     * 当前选中的标签索引
     */
    private static List<DanmakuItem> currentItems = new ArrayList<>();
    private static String currentSearchKeyword = "";
    private static final int SIMPLE_SOURCE_TAB_LIMIT = 3;
    private static final int SIMPLE_RESULT_COUNT_LIMIT = 36;


    // 显示配置对话框
    public static void showConfigDialog(Context ctx) {
        // 添加检查
        if (!(ctx instanceof Activity)) {
            DanmakuSpider.log("错误：Context不是Activity");
            return;
        }
        Activity activity = (Activity) ctx;
        if (activity.isFinishing() || activity.isDestroyed()) {
            DanmakuSpider.log("Activity已销毁或正在销毁，不显示配置对话框");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 在创建对话框前再次检查状态
                    if (activity.isFinishing() || activity.isDestroyed()) {
                        DanmakuSpider.log("Activity已销毁，不显示配置对话框");
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                    LinearLayout mainLayout = new LinearLayout(activity);
                    mainLayout.setOrientation(LinearLayout.VERTICAL);
                    mainLayout.setBackgroundColor(BACKGROUND_WHITE);
                    mainLayout.setPadding(dpToPx(activity, 24), dpToPx(activity, 20), dpToPx(activity, 24), dpToPx(activity, 20));

                    // 标题 - 增强视觉效果
                    TextView title = new TextView(activity);
                    title.setText("Leo弹幕配置");
                    title.setTextSize(24);
                    title.setTextColor(PRIMARY_COLOR);
                    title.setGravity(Gravity.CENTER);
                    title.setPadding(0, dpToPx(activity, 8), 0, dpToPx(activity, 20));
                    title.setTypeface(null, android.graphics.Typeface.BOLD);
                    mainLayout.addView(title);

                    // 副标题说明
                    TextView subtitle = new TextView(activity);
                    subtitle.setText("管理弹幕API源和时间偏移");
                    subtitle.setTextSize(13);
                    subtitle.setTextColor(TEXT_SECONDARY);
                    subtitle.setGravity(Gravity.CENTER);
                    subtitle.setPadding(0, 0, 0, dpToPx(activity, 16));
                    mainLayout.addView(subtitle);
                    
                    Button apiManagerBtn = createStyledButton(activity, "API源管理", TERTIARY_COLOR);
                    LinearLayout.LayoutParams apiManagerParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(activity, 42));
                    apiManagerParams.setMargins(0, 0, 0, dpToPx(activity, 12));
                    apiManagerBtn.setLayoutParams(apiManagerParams);
                    mainLayout.addView(apiManagerBtn);

                    // IP地址提示 - 改进样式
                    TextView ipInfo = new TextView(activity);
                    String ip = NetworkUtils.getLocalIpAddress();
//                ipInfo.setText("Web配置: http://" + ip + ":9810");
                    ipInfo.setTextSize(13);
                    ipInfo.setTextColor(ACCENT_COLOR);
                    ipInfo.setGravity(Gravity.CENTER);
                    ipInfo.setPadding(dpToPx(activity, 12), dpToPx(activity, 8), dpToPx(activity, 12), dpToPx(activity, 12));
                    ipInfo.setBackgroundColor(0xFFFFF8E1);
                    mainLayout.addView(ipInfo);

                    // API输入框容器 - 改进样式
                    LinearLayout inputContainer = new LinearLayout(activity);
                    inputContainer.setOrientation(LinearLayout.VERTICAL);
                    inputContainer.setBackgroundColor(BORDER_LIGHT);
                    inputContainer.setPadding(dpToPx(activity, 2), dpToPx(activity, 2), dpToPx(activity, 2), dpToPx(activity, 2));
                    LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    containerParams.setMargins(0, dpToPx(activity, 12), 0, dpToPx(activity, 12));
                    inputContainer.setLayoutParams(containerParams);

                    DanmakuConfig config = DanmakuConfigManager.getConfig(activity);

                    EditText apiInput = new EditText(activity);
                    apiInput.setText(TextUtils.join("\n", config.getApiUrlEntries()));
                    apiInput.setHint("每行一个API地址，可用 | 设置别名\n例如: https://example.com/danmu|公益源");
                    apiInput.setMinLines(3);
                    apiInput.setMaxLines(4);
                    apiInput.setBackgroundColor(BACKGROUND_WHITE);
                    apiInput.setTextColor(TEXT_PRIMARY);
                    apiInput.setTextSize(13);
                    apiInput.setPadding(dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12));
                    apiInput.setHintTextColor(TEXT_TERTIARY);

                    inputContainer.addView(apiInput);
                    mainLayout.addView(inputContainer);

                    TextView apiSummary = new TextView(activity);
                    apiSummary.setText(buildApiSourceSummary(config));
                    apiSummary.setTextSize(13);
                    apiSummary.setTextColor(TEXT_SECONDARY);
                    apiSummary.setPadding(0, 0, 0, dpToPx(activity, 8));
                    mainLayout.addView(apiSummary);

                    TextView offsetLabel = new TextView(activity);
                    offsetLabel.setText("弹幕时间偏移（秒，负数提前）");
                    offsetLabel.setTextSize(14);
                    offsetLabel.setTextColor(TEXT_PRIMARY);
                    offsetLabel.setPadding(0, dpToPx(activity, 4), 0, dpToPx(activity, 6));
                    mainLayout.addView(offsetLabel);

                    EditText offsetInput = new EditText(activity);
                    offsetInput.setHint("0 表示不偏移，例如 1.5 或 -0.8");
                    offsetInput.setText(DanmakuUtils.formatOffsetSeconds(config.getDanmakuTimeOffsetMs()));
                    offsetInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
                            android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL |
                            android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
                    offsetInput.setTextColor(TEXT_PRIMARY);
                    offsetInput.setTextSize(14);
                    offsetInput.setSingleLine(true);
                    offsetInput.setPadding(dpToPx(activity, 12), dpToPx(activity, 10), dpToPx(activity, 12), dpToPx(activity, 10));
                    offsetInput.setHintTextColor(TEXT_TERTIARY);
                    applyTVInputFocusEffect(offsetInput, false);
                    mainLayout.addView(offsetInput, new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    // 分割线 - 改进样式
                    View divider = new View(activity);
                    divider.setBackgroundColor(BORDER_LIGHT);
                    LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(activity, 1));
                    dividerParams.setMargins(0, dpToPx(activity, 16), 0, dpToPx(activity, 16));
                    divider.setLayoutParams(dividerParams);
                    mainLayout.addView(divider);

                    // 按钮布局 - 改进设计
                    LinearLayout btnLayout = new LinearLayout(activity);
                    btnLayout.setOrientation(LinearLayout.HORIZONTAL);
                    btnLayout.setGravity(Gravity.CENTER);

                    Button saveBtn = createStyledButton(activity, "保存", PRIMARY_COLOR);
                    Button lpConfigBtn = createStyledButton(activity, "布局", ACCENT_COLOR);
                    Button cancelBtn = createStyledButtonWithBorder(activity, "取消", PRIMARY_COLOR);

                    LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                            0, dpToPx(activity, 44), 1);
                    btnParams.setMargins(dpToPx(activity, 6), 0, dpToPx(activity, 6), 0);

                    saveBtn.setLayoutParams(btnParams);
                    lpConfigBtn.setLayoutParams(btnParams);
                    cancelBtn.setLayoutParams(btnParams);

                    btnLayout.addView(saveBtn);
                    btnLayout.addView(lpConfigBtn);
                    btnLayout.addView(cancelBtn);

                    mainLayout.addView(btnLayout);

                    // ====== 液态玻璃包装 ======
                    boolean isLiquidMode1 = FORCE_LIQUID_GLASS;
                    if (isLiquidMode1) {
                        LiquidGlassView liquidRoot = new LiquidGlassView(activity);
                        liquidRoot.setBlurRadius(18f);
                        DanmakuConfig glassCfg = DanmakuConfigManager.getConfig(activity);
                        liquidRoot.setDarkMode(config.isDarkMode());  // ← 加上
                        liquidRoot.setGlassAlpha(config.getLpLiquidAlpha());
                        liquidRoot.setCornerRadius(24f);
                        mainLayout.setBackgroundColor(Color.TRANSPARENT);
                        liquidRoot.addView(mainLayout, new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                        builder.setView(liquidRoot);
                    } else {
                        builder.setView(mainLayout);
                    }
                    AlertDialog dialog = builder.create();
                    if (isLiquidMode1) {
                        applyRealBlur(dialog);
                        if (dialog.getWindow() != null)
                            dialog.getWindow().setBackgroundDrawable(
                                    new android.graphics.drawable.ColorDrawable(0x00000000));
                    }
    
                    // ====== 修复：show 之后按屏幕比例设窗口尺寸（原来是 MATCH_PARENT×600dp 导致全屏）======
                    dialog.setOnShowListener(d -> {
                        android.view.Window window = dialog.getWindow();
                        if (window != null) {
                            android.view.WindowManager.LayoutParams params = window.getAttributes();
                            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;   // ← 与API源管理一致，不指定宽度
                            params.height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.78f);
                            window.setAttributes(params);
                        }
                    });
    
                    // 按钮事件
                    saveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String text = apiInput.getText().toString();
                            String[] lines = text.split("\n");
                            Set<String> newUrls = new LinkedHashSet<>();
                            for (String line : lines) {
                                String trimmed = line.trim();
                                String url = DanmakuApiSource.normalizeUrl(trimmed);
                                if (!TextUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"))) {
                                    newUrls.add(trimmed);
                                }
                            }

                            if (newUrls.isEmpty()) {
                                Utils.safeShowToast(activity, "请输入有效的API地址");
                                return;
                            }

                            int offsetMs;
                            try {
                                offsetMs = parseOffsetMs(offsetInput.getText().toString());
                            } catch (NumberFormatException e) {
                                Utils.safeShowToast(activity, "请输入有效的时间偏移");
                                return;
                            }

                            DanmakuConfig config = DanmakuConfigManager.getConfig( activity);
                            int oldOffsetMs = config.getDanmakuTimeOffsetMs();
                            config.setApiUrls(newUrls);
                            config.setDanmakuTimeOffsetMs(offsetMs);
                            DanmakuConfigManager.saveConfig(activity, config);

                            Utils.safeShowToast(activity, "配置已保存");

                            DanmakuSpider.log("已保存API地址: " + newUrls + "，弹幕时间偏移: " + DanmakuUtils.formatOffsetLabel(offsetMs));
                            if (oldOffsetMs != offsetMs) {
                                refreshCurrentDanmaku(activity, offsetMs);
                            }

                            dialog.dismiss();
                        }
                    });

                    lpConfigBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showLpConfigDialog(activity);
                            dialog.dismiss();
                        }
                    });

                    apiManagerBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showApiSourceManagerDialog(activity);
                            dialog.dismiss();
                        }
                    });

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    safeShowDialog(activity, dialog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void showCacheManagerDialog(Context ctx) {
        if (!(ctx instanceof Activity)) {
            DanmakuSpider.log("错误：Context不是Activity");
            return;
        }
        Activity activity = (Activity) ctx;
        if (activity.isFinishing() || activity.isDestroyed()) {
            DanmakuSpider.log("Activity已销毁或正在销毁，不显示缓存管理对话框");
            return;
        }

        activity.runOnUiThread(() -> {
            try {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    DanmakuSpider.log("Activity已销毁，不显示缓存管理对话框");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                LinearLayout mainLayout = new LinearLayout(activity);
                mainLayout.setOrientation(LinearLayout.VERTICAL);
                mainLayout.setBackgroundColor(BACKGROUND_WHITE);
                mainLayout.setPadding(dpToPx(activity, 20), dpToPx(activity, 18), dpToPx(activity, 20), dpToPx(activity, 18));

                TextView title = new TextView(activity);
                title.setText("缓存管理");
                title.setTextSize(24);
                title.setTextColor(PRIMARY_COLOR);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, android.graphics.Typeface.BOLD);
                title.setPadding(0, dpToPx(activity, 4), 0, dpToPx(activity, 8));
                mainLayout.addView(title);

                TextView summary = new TextView(activity);
                summary.setTextSize(13);
                summary.setTextColor(TEXT_SECONDARY);
                summary.setPadding(0, 0, 0, dpToPx(activity, 12));
                mainLayout.addView(summary);

                ScrollView scrollView = new ScrollView(activity);
                LinearLayout listLayout = new LinearLayout(activity);
                listLayout.setOrientation(LinearLayout.VERTICAL);
                scrollView.addView(listLayout);
                mainLayout.addView(scrollView, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

                LinearLayout bottomLayout = new LinearLayout(activity);
                bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
                bottomLayout.setGravity(Gravity.CENTER);
                bottomLayout.setPadding(0, dpToPx(activity, 14), 0, 0);

                Button refreshBtn = createStyledButton(activity, "刷新", TERTIARY_COLOR);
                Button clearBtn = createStyledButton(activity, "全部清空", ACCENT_COLOR);
                Button closeBtn = createStyledButtonWithBorder(activity, "关闭", PRIMARY_COLOR);

                LinearLayout.LayoutParams bottomBtnParams = new LinearLayout.LayoutParams(0, dpToPx(activity, 44), 1);
                bottomBtnParams.setMargins(dpToPx(activity, 6), 0, dpToPx(activity, 6), 0);
                bottomLayout.addView(refreshBtn, bottomBtnParams);
                bottomLayout.addView(clearBtn, bottomBtnParams);
                bottomLayout.addView(closeBtn, new LinearLayout.LayoutParams(0, dpToPx(activity, 44), 1));
                mainLayout.addView(bottomLayout);

                // ====== 液态玻璃包装 ======
                boolean isLiquidMode2 = FORCE_LIQUID_GLASS;
                if (isLiquidMode2) {
                    LiquidGlassView liquidRoot = new LiquidGlassView(activity);
                    liquidRoot.setBlurRadius(18f);
                    DanmakuConfig glassCfg = DanmakuConfigManager.getConfig(activity);
                    liquidRoot.setDarkMode(glassCfg.isDarkMode());
                    liquidRoot.setGlassAlpha(glassCfg.getLpLiquidAlpha());
                    liquidRoot.setCornerRadius(24f);
                    mainLayout.setBackgroundColor(Color.TRANSPARENT);
                    liquidRoot.addView(mainLayout, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    builder.setView(liquidRoot);
                } else {
                    builder.setView(mainLayout);
                }
                AlertDialog dialog = builder.create();
                if (isLiquidMode2) {
                    applyRealBlur(dialog);
                    if (dialog.getWindow() != null)
                        dialog.getWindow().setBackgroundDrawable(
                                new android.graphics.drawable.ColorDrawable(0x00000000));
                }

                final Runnable[] refresh = new Runnable[1];
                refresh[0] = new Runnable() {
                    @Override
                    public void run() {
                        DanmakuSpider.CacheStats stats = DanmakuSpider.getCacheStats(activity);
                        summary.setText(buildCacheSummary(stats));
                        renderCacheSections(activity, listLayout, refresh[0]);
                    }
                };

                refreshBtn.setOnClickListener(v -> refresh[0].run());
                clearBtn.setOnClickListener(v -> {
                    try {
                        DanmakuSpider.clearCache(activity);
                        Utils.safeShowToast(activity, "缓存已清空");
                        refresh[0].run();
                    } catch (Exception e) {
                        Utils.safeShowToast(activity, "清空失败");
                    }
                });
                closeBtn.setOnClickListener(v -> dialog.dismiss());

                refresh[0].run();
                safeShowDialog(activity, dialog);
            } catch (Exception e) {
                DanmakuSpider.log("显示缓存管理失败: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private static String buildCacheSummary(DanmakuSpider.CacheStats stats) {
        return "弹幕文件 " + stats.fileCount + " 个，"
                + "占用 " + formatCacheSize(stats.totalBytes) + "；"
                + "搜索关键词 " + stats.searchCacheCount + " 条；"
                + "运行时缓存 " + DanmakuScanner.getRuntimeCacheCount() + " 条";
    }

    private static String formatCacheSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format(Locale.getDefault(), "%.2f KB", bytes / 1024f);
        if (bytes < 1024L * 1024L * 1024L) return String.format(Locale.getDefault(), "%.2f MB", bytes / 1024f / 1024f);
        return String.format(Locale.getDefault(), "%.2f GB", bytes / 1024f / 1024f / 1024f);
    }

    private static void renderCacheSections(Activity activity, LinearLayout container, Runnable refresh) {
        container.removeAllViews();

        DanmakuSpider.CacheStats stats = DanmakuSpider.getCacheStats(activity);
        container.addView(createCacheInfoCard(activity, "缓存目录", stats.cachePath));

        List<DanmakuSpider.FileCacheEntry> fileEntries = DanmakuSpider.getFileCacheEntries(activity);
        container.addView(createCacheSectionHeader(activity,
                "弹幕文件缓存",
                fileEntries.isEmpty() ? "暂无缓存文件" : ("共 " + fileEntries.size() + " 个文件，" + formatCacheSize(stats.totalBytes)),
                "清空本组",
                v -> {
                    DanmakuSpider.clearFileCache(activity);
                    Utils.safeShowToast(activity, "弹幕文件缓存已清空");
                    refresh.run();
                }));
        if (fileEntries.isEmpty()) {
            container.addView(createCacheEmptyText(activity, "当前没有弹幕文件缓存"));
        } else {
            for (DanmakuSpider.FileCacheEntry entry : fileEntries) {
                String subtitle = entry.relativePath + "\n" + formatCacheSize(entry.bytes);
                container.addView(createCacheEntryView(activity, "文件", subtitle, "删除", v -> {
                    DanmakuSpider.removeFileCacheEntry(activity, entry.relativePath);
                    Utils.safeShowToast(activity, "已删除缓存文件");
                    refresh.run();
                }));
            }
        }

        List<SharedPreferencesService.SearchCacheEntry> searchEntries = SharedPreferencesService.getSearchKeywordCacheEntries(activity);
        container.addView(createCacheSectionHeader(activity,
                "搜索关键词缓存",
                searchEntries.isEmpty() ? "暂无搜索缓存" : ("共 " + searchEntries.size() + " 条"),
                "清空本组",
                v -> {
                    SharedPreferencesService.clearSearchKeywordCache(activity);
                    Utils.safeShowToast(activity, "搜索关键词缓存已清空");
                    refresh.run();
                }));
        if (searchEntries.isEmpty()) {
            container.addView(createCacheEmptyText(activity, "当前没有搜索关键词缓存"));
        } else {
            for (SharedPreferencesService.SearchCacheEntry entry : searchEntries) {
                String subtitle = "原关键词: " + safeText(entry.initialKeyword)
                        + "\n当前关键词: " + safeText(entry.manualKeyword);
                container.addView(createCacheEntryView(activity, "搜索缓存", subtitle, "删除", v -> {
                    SharedPreferencesService.removeSearchKeywordCache(activity, entry.rawKey);
                    Utils.safeShowToast(activity, "已删除搜索缓存");
                    refresh.run();
                }));
            }
        }

        List<String> runtimeKeys = DanmakuScanner.getRuntimeCacheKeys();
        container.addView(createCacheSectionHeader(activity,
                "运行时缓存",
                runtimeKeys.isEmpty() ? "暂无运行时缓存" : ("共 " + runtimeKeys.size() + " 条"),
                "清空本组",
                v -> {
                    DanmakuScanner.clearRuntimeCache();
                    Utils.safeShowToast(activity, "运行时缓存已清空");
                    refresh.run();
                }));
        if (runtimeKeys.isEmpty()) {
            container.addView(createCacheEmptyText(activity, "当前没有运行时缓存"));
        } else {
            for (String key : runtimeKeys) {
                container.addView(createCacheEntryView(activity, "运行时键", key, "删除", v -> {
                    DanmakuScanner.removeRuntimeCacheKey(key);
                    Utils.safeShowToast(activity, "已删除运行时缓存");
                    refresh.run();
                }));
            }
        }
    }

    private static View createCacheSectionHeader(Activity activity, String titleText, String summaryText, String actionText, View.OnClickListener action) {
        LinearLayout wrapper = new LinearLayout(activity);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setPadding(0, dpToPx(activity, 10), 0, dpToPx(activity, 6));

        TextView title = new TextView(activity);
        title.setText(titleText);
        title.setTextSize(16);
        title.setTextColor(PRIMARY_COLOR);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        wrapper.addView(title);

        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView summary = new TextView(activity);
        summary.setText(summaryText);
        summary.setTextSize(13);
        summary.setTextColor(TEXT_SECONDARY);
        LinearLayout.LayoutParams summaryParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        row.addView(summary, summaryParams);

        Button actionBtn = createStyledButton(activity, actionText, ACCENT_COLOR);
        row.addView(actionBtn, new LinearLayout.LayoutParams(dpToPx(activity, 92), dpToPx(activity, 38)));
        actionBtn.setOnClickListener(action);

        wrapper.addView(row);
        return wrapper;
    }

    private static View createCacheEntryView(Activity activity, String titleText, String bodyText, String actionText, View.OnClickListener action) {
        LinearLayout wrapper = new LinearLayout(activity);
        wrapper.setOrientation(LinearLayout.HORIZONTAL);
        wrapper.setGravity(Gravity.CENTER_VERTICAL);
        wrapper.setBackgroundColor(0xFFF7F7F7);
        wrapper.setPadding(dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12));

        LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapperParams.setMargins(0, 0, 0, dpToPx(activity, 8));
        wrapper.setLayoutParams(wrapperParams);

        LinearLayout textWrap = new LinearLayout(activity);
        textWrap.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        TextView title = new TextView(activity);
        title.setText(titleText);
        title.setTextSize(14);
        title.setTextColor(TEXT_PRIMARY);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        textWrap.addView(title);

        TextView body = new TextView(activity);
        body.setText(bodyText);
        body.setTextSize(12);
        body.setTextColor(TEXT_SECONDARY);
        body.setPadding(0, dpToPx(activity, 4), 0, 0);
        textWrap.addView(body);

        wrapper.addView(textWrap, textParams);

        Button actionBtn = createStyledButton(activity, actionText, ACCENT_COLOR);
        wrapper.addView(actionBtn, new LinearLayout.LayoutParams(dpToPx(activity, 76), dpToPx(activity, 38)));
        actionBtn.setOnClickListener(action);
        return wrapper;
    }

    private static View createCacheInfoCard(Activity activity, String titleText, String bodyText) {
        LinearLayout wrapper = new LinearLayout(activity);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setBackgroundColor(0xFFF7F7F7);
        wrapper.setPadding(dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dpToPx(activity, 10));
        wrapper.setLayoutParams(params);

        TextView title = new TextView(activity);
        title.setText(titleText);
        title.setTextSize(14);
        title.setTextColor(TEXT_PRIMARY);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        wrapper.addView(title);

        TextView body = new TextView(activity);
        body.setText(bodyText);
        body.setTextSize(12);
        body.setTextColor(TEXT_SECONDARY);
        body.setPadding(0, dpToPx(activity, 4), 0, 0);
        wrapper.addView(body);
        return wrapper;
    }

    private static TextView createCacheEmptyText(Activity activity, String text) {
        TextView empty = new TextView(activity);
        empty.setText(text);
        empty.setTextSize(13);
        empty.setTextColor(TEXT_TERTIARY);
        empty.setPadding(0, 0, 0, dpToPx(activity, 10));
        return empty;
    }

    private static String safeText(String value) {
        return TextUtils.isEmpty(value) ? "空" : value;
    }

    public static void showApiSourceManagerDialog(Context ctx) {
        if (!(ctx instanceof Activity)) {
            DanmakuSpider.log("错误：Context不是Activity");
            return;
        }
        Activity activity = (Activity) ctx;
        if (activity.isFinishing() || activity.isDestroyed()) {
            DanmakuSpider.log("Activity已销毁或正在销毁，不显示API源管理对话框");
            return;
        }

        activity.runOnUiThread(() -> {
            try {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    DanmakuSpider.log("Activity已销毁，不显示API源管理对话框");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                LinearLayout mainLayout = new LinearLayout(activity);
                mainLayout.setOrientation(LinearLayout.VERTICAL);
                mainLayout.setBackgroundColor(BACKGROUND_WHITE);
                mainLayout.setPadding(dpToPx(activity, 20), dpToPx(activity, 18), dpToPx(activity, 20), dpToPx(activity, 18));

                TextView title = new TextView(activity);
                title.setText("API源管理");
                title.setTextSize(24);
                title.setTextColor(PRIMARY_COLOR);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, android.graphics.Typeface.BOLD);
                title.setPadding(0, dpToPx(activity, 4), 0, dpToPx(activity, 8));
                mainLayout.addView(title);

                TextView summary = new TextView(activity);
                summary.setTextSize(13);
                summary.setTextColor(TEXT_SECONDARY);
                summary.setGravity(Gravity.CENTER);
                summary.setPadding(0, 0, 0, dpToPx(activity, 10));
                mainLayout.addView(summary);

                LinearLayout addLayout = new LinearLayout(activity);
                addLayout.setOrientation(LinearLayout.HORIZONTAL);
                addLayout.setGravity(Gravity.CENTER);
                addLayout.setPadding(0, 0, 0, dpToPx(activity, 10));

                EditText addInput = new EditText(activity);
                addInput.setHint("新增API地址，可写 URL|别名");
                addInput.setSingleLine(true);
                addInput.setTextSize(13);
                addInput.setTextColor(TEXT_PRIMARY);
                addInput.setHintTextColor(TEXT_TERTIARY);
                addInput.setPadding(dpToPx(activity, 12), dpToPx(activity, 10), dpToPx(activity, 12), dpToPx(activity, 10));
                applyTVInputFocusEffect(addInput, false);
                LinearLayout.LayoutParams addInputParams = new LinearLayout.LayoutParams(0, dpToPx(activity, 44), 1);
                addInputParams.setMargins(0, 0, dpToPx(activity, 8), 0);
                addLayout.addView(addInput, addInputParams);

                Button addBtn = createStyledButton(activity, "新增", PRIMARY_COLOR);
                addLayout.addView(addBtn, new LinearLayout.LayoutParams(dpToPx(activity, 76), dpToPx(activity, 44)));
                mainLayout.addView(addLayout);

                ScrollView scrollView = new ScrollView(activity);
                LinearLayout listLayout = new LinearLayout(activity);
                listLayout.setOrientation(LinearLayout.VERTICAL);
                scrollView.addView(listLayout);
                LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
                mainLayout.addView(scrollView, scrollParams);

                LinearLayout bottomLayout = new LinearLayout(activity);
                bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
                bottomLayout.setGravity(Gravity.CENTER);
                bottomLayout.setPadding(0, dpToPx(activity, 12), 0, 0);

                Button testAllBtn = createStyledButton(activity, "全部测试", TERTIARY_COLOR);
                Button closeBtn = createStyledButtonWithBorder(activity, "关闭", PRIMARY_COLOR);
                LinearLayout.LayoutParams bottomBtnParams = new LinearLayout.LayoutParams(0, dpToPx(activity, 44), 1);
                bottomBtnParams.setMargins(dpToPx(activity, 6), 0, dpToPx(activity, 6), 0);
                bottomLayout.addView(testAllBtn, bottomBtnParams);
                bottomLayout.addView(closeBtn, new LinearLayout.LayoutParams(0, dpToPx(activity, 44), 1));
                mainLayout.addView(bottomLayout);

                // ====== 液态玻璃包装 ======
                boolean isLiquidMode3 = FORCE_LIQUID_GLASS;
                if (isLiquidMode3) {
                    LiquidGlassView liquidRoot = new LiquidGlassView(activity);
                    liquidRoot.setBlurRadius(18f);
                    DanmakuConfig glassCfg = DanmakuConfigManager.getConfig(activity);
                    liquidRoot.setDarkMode(glassCfg.isDarkMode());
                    liquidRoot.setGlassAlpha(glassCfg.getLpLiquidAlpha());
                    liquidRoot.setCornerRadius(24f);
                    mainLayout.setBackgroundColor(Color.TRANSPARENT);
                    liquidRoot.addView(mainLayout, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    builder.setView(liquidRoot);
                } else {
                    builder.setView(mainLayout);
                }
                AlertDialog dialog = builder.create();
                if (isLiquidMode3) {
                    applyRealBlur(dialog);
                    if (dialog.getWindow() != null)
                        dialog.getWindow().setBackgroundDrawable(
                                new android.graphics.drawable.ColorDrawable(0x00000000));
                }

                final Runnable[] refresh = new Runnable[1];
                refresh[0] = new Runnable() {
                    @Override
                    public void run() {
                        DanmakuConfig latest = DanmakuConfigManager.getConfig(activity);
                        summary.setText(buildApiSourceSummary(latest));
                        renderApiSourceList(activity, listLayout, latest, refresh[0]);
                    }
                };

                addBtn.setOnClickListener(v -> {
                    String entry = addInput.getText().toString();
                    String url = DanmakuApiSource.parseEntry(entry).url;
                    if (TextUtils.isEmpty(url) || !(url.startsWith("http://") || url.startsWith("https://"))) {
                        Utils.safeShowToast(activity, "请输入有效的API地址");
                        return;
                    }

                    DanmakuConfig config = DanmakuConfigManager.getConfig(activity);
                    if (config.findApiSource(url) != null) {
                        Utils.safeShowToast(activity, "API源已存在");
                        return;
                    }
                    config.addApiUrls(java.util.Collections.singletonList(entry));
                    DanmakuConfigManager.saveConfig(activity, config);
                    addInput.setText("");
                    Utils.safeShowToast(activity, "API源已新增");
                    refresh[0].run();
                });

                testAllBtn.setOnClickListener(v -> testAllApiSources(activity, testAllBtn, refresh[0]));
                closeBtn.setOnClickListener(v -> dialog.dismiss());

                refresh[0].run();
                safeShowDialog(activity, dialog);
            } catch (Exception e) {
                DanmakuSpider.log("显示API源管理失败: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private static void renderApiSourceList(Activity activity, LinearLayout listLayout, DanmakuConfig config, Runnable refresh) {
        listLayout.removeAllViews();
        List<DanmakuApiSource> sources = config.getApiSources();
        if (sources.isEmpty()) {
            TextView empty = new TextView(activity);
            empty.setText("暂无API源");
            empty.setTextSize(14);
            empty.setTextColor(TEXT_TERTIARY);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, dpToPx(activity, 24), 0, dpToPx(activity, 24));
            listLayout.addView(empty);
            return;
        }

        for (int i = 0; i < sources.size(); i++) {
            DanmakuApiSource source = sources.get(i);
            final String sourceUrl = source.url;

            LinearLayout itemLayout = new LinearLayout(activity);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setPadding(dpToPx(activity, 12), dpToPx(activity, 10), dpToPx(activity, 12), dpToPx(activity, 10));
            itemLayout.setBackground(createRoundedBackgroundDrawable(BACKGROUND_LIGHT));
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemParams.setMargins(0, 0, 0, dpToPx(activity, 10));
            listLayout.addView(itemLayout, itemParams);

            TextView urlView = new TextView(activity);
            String sourceLabel = source.getDisplayName("源" + (i + 1));
            String sourceTitle = (i + 1) + ". " + sourceLabel;
            if (!TextUtils.isEmpty(source.name)) {
                sourceTitle += "\n" + sourceUrl;
            } else {
                sourceTitle += "  " + sourceUrl;
            }
            urlView.setText(sourceTitle);
            urlView.setTextSize(14);
            urlView.setTextColor(TEXT_PRIMARY);
            urlView.setSingleLine(false);
            itemLayout.addView(urlView);

            TextView statusView = new TextView(activity);
            statusView.setText(formatApiSourceStatus(source));
            statusView.setTextSize(12);
            statusView.setTextColor(source.enabled ? TEXT_SECONDARY : TEXT_TERTIARY);
            statusView.setPadding(0, dpToPx(activity, 6), 0, dpToPx(activity, 8));
            itemLayout.addView(statusView);

            LinearLayout actionLayout = new LinearLayout(activity);
            actionLayout.setOrientation(LinearLayout.HORIZONTAL);
            actionLayout.setGravity(Gravity.CENTER);

            Button toggleBtn = createStyledButton(activity, source.enabled ? "停用" : "启用",
                    source.enabled ? ACCENT_COLOR : TERTIARY_COLOR);
            Button aliasBtn = createStyledButtonWithBorder(activity, "别名", PRIMARY_COLOR);
            Button testBtn = createStyledButton(activity, "测试", PRIMARY_COLOR);
            Button deleteBtn = createStyledButtonWithBorder(activity, "删除", ACCENT_COLOR);

            actionLayout.addView(toggleBtn, createSourceButtonParams(activity));
            actionLayout.addView(aliasBtn, createSourceButtonParams(activity));
            actionLayout.addView(testBtn, createSourceButtonParams(activity));
            actionLayout.addView(deleteBtn, createSourceButtonParams(activity));
            itemLayout.addView(actionLayout);

            toggleBtn.setOnClickListener(v -> {
                DanmakuConfig latest = DanmakuConfigManager.getConfig(activity);
                DanmakuApiSource latestSource = latest.findApiSource(sourceUrl);
                if (latestSource != null) {
                    latestSource.enabled = !latestSource.enabled;
                    DanmakuConfigManager.saveConfig(activity, latest);
                    refresh.run();
                }
            });

            testBtn.setOnClickListener(v -> testApiSource(activity, sourceUrl, testBtn, refresh));
            aliasBtn.setOnClickListener(v -> showApiSourceAliasDialog(activity, sourceUrl, refresh));

            deleteBtn.setOnClickListener(v -> {
                DanmakuConfig latest = DanmakuConfigManager.getConfig(activity);
                latest.removeApiSource(sourceUrl);
                DanmakuConfigManager.saveConfig(activity, latest);
                Utils.safeShowToast(activity, "API源已删除");
                refresh.run();
            });
        }
    }

    private static void showApiSourceAliasDialog(Activity activity, String sourceUrl, Runnable refresh) {
        DanmakuConfig config = DanmakuConfigManager.getConfig(activity);
        DanmakuApiSource source = config.findApiSource(sourceUrl);
        if (source == null) {
            Utils.safeShowToast(activity, "API源不存在");
            return;
        }

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(activity, 20), dpToPx(activity, 16), dpToPx(activity, 20), dpToPx(activity, 8));

        TextView urlView = new TextView(activity);
        urlView.setText(source.url);
        urlView.setTextSize(12);
        urlView.setTextColor(TEXT_SECONDARY);
        urlView.setPadding(0, 0, 0, dpToPx(activity, 10));
        layout.addView(urlView);

        EditText aliasInput = new EditText(activity);
        aliasInput.setHint("别名，例如 公益源");
        aliasInput.setSingleLine(true);
        aliasInput.setText(DanmakuApiSource.normalizeName(source.name));
        aliasInput.setTextSize(14);
        aliasInput.setTextColor(TEXT_PRIMARY);
        aliasInput.setHintTextColor(TEXT_TERTIARY);
        aliasInput.setPadding(dpToPx(activity, 12), dpToPx(activity, 10), dpToPx(activity, 12), dpToPx(activity, 10));
        applyTVInputFocusEffect(aliasInput, false);
        layout.addView(aliasInput, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("设置API源别名")
                .setView(layout)
                .setPositiveButton("保存", null)
                .setNegativeButton("取消", null)
                .setNeutralButton("清空", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                DanmakuConfig latest = DanmakuConfigManager.getConfig(activity);
                latest.setApiSourceName(sourceUrl, aliasInput.getText().toString());
                DanmakuConfigManager.saveConfig(activity, latest);
                Utils.safeShowToast(activity, "别名已保存");
                refresh.run();
                dialog.dismiss();
            });

            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                DanmakuConfig latest = DanmakuConfigManager.getConfig(activity);
                latest.setApiSourceName(sourceUrl, "");
                DanmakuConfigManager.saveConfig(activity, latest);
                Utils.safeShowToast(activity, "别名已清空");
                refresh.run();
                dialog.dismiss();
            });
        });

        safeShowDialog(activity, dialog);
    }

    private static LinearLayout.LayoutParams createSourceButtonParams(Activity activity) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(activity, 38), 1);
        params.setMargins(dpToPx(activity, 3), 0, dpToPx(activity, 3), 0);
        return params;
    }

    private static void testApiSource(Activity activity, String sourceUrl, Button button, Runnable refresh) {
        button.setEnabled(false);
        button.setText("测试中");
        new Thread(() -> {
            LeoDanmakuService.ApiSourceTestResult result = LeoDanmakuService.testApiSource(sourceUrl);
            DanmakuConfigManager.recordApiSourceResult(activity.getApplicationContext(), sourceUrl,
                    result.success, result.latencyMs, result.message);
            DanmakuSpider.log("API源测试 " + (result.success ? "成功" : "失败") + ": " + sourceUrl + " - " + result.message);

            activity.runOnUiThread(() -> {
                if (!activity.isFinishing() && !activity.isDestroyed()) {
                    Utils.safeShowToast(activity, result.success ? "测试成功: " + result.latencyMs + "ms" : "测试失败: " + result.message);
                    refresh.run();
                }
            });
        }).start();
    }

    private static void testAllApiSources(Activity activity, Button button, Runnable refresh) {
        DanmakuConfig config = DanmakuConfigManager.getConfig(activity);
        List<DanmakuApiSource> sources = new ArrayList<>(config.getApiSources());
        if (sources.isEmpty()) {
            Utils.safeShowToast(activity, "暂无API源");
            return;
        }

        button.setEnabled(false);
        button.setText("测试中");
        new Thread(() -> {
            int successCount = 0;
            for (DanmakuApiSource source : sources) {
                LeoDanmakuService.ApiSourceTestResult result = LeoDanmakuService.testApiSource(source.url);
                if (result.success) successCount++;
                DanmakuConfigManager.recordApiSourceResult(activity.getApplicationContext(), source.url,
                        result.success, result.latencyMs, result.message);
            }
            final int finalSuccessCount = successCount;
            activity.runOnUiThread(() -> {
                if (!activity.isFinishing() && !activity.isDestroyed()) {
                    button.setEnabled(true);
                    button.setText("全部测试");
                    Utils.safeShowToast(activity, "测试完成: " + finalSuccessCount + "/" + sources.size() + " 可用");
                    refresh.run();
                }
            });
        }).start();
    }

    private static String buildApiSourceSummary(DanmakuConfig config) {
        if (config == null) return "未配置API源";
        List<DanmakuApiSource> sources = config.getApiSources();
        List<DanmakuApiSource> enabledSources = config.getEnabledApiSources();
        if (sources.isEmpty()) return "未配置API源";
        if (enabledSources.isEmpty()) return "已配置 " + sources.size() + " 个API源，当前全部停用";
        DanmakuApiSource first = enabledSources.get(0);
        String firstName = first.getDisplayName(shortenUrl(first.url));
        return "已启用 " + enabledSources.size() + "/" + sources.size() + "，例如：" + firstName;
    }

    private static String formatApiSourceStatus(DanmakuApiSource source) {
        String state = source.enabled ? "启用" : "停用";
        String latency = source.lastLatencyMs >= 0 ? source.lastLatencyMs + "ms" : "未测试";
        String successTime = source.lastSuccessTimeMs > 0 ? formatTime(source.lastSuccessTimeMs) : "未成功";
        String error = TextUtils.isEmpty(source.lastError) ? "" : " | " + source.lastError;
        return state + " | 延迟 " + latency + " | 最近成功 " + successTime + error;
    }

    private static String formatTime(long timeMs) {
        if (timeMs <= 0) return "未记录";
        return new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(new Date(timeMs));
    }

    private static String shortenUrl(String url) {
        if (TextUtils.isEmpty(url) || url.length() <= 42) return url;
        return url.substring(0, 39) + "...";
    }

    public static void showLpConfigDialog(Context ctx) {
        if (!(ctx instanceof Activity)) {
            DanmakuSpider.log("错误：Context不是Activity");
            return;
        }
        Activity activity = (Activity) ctx;
        if (activity.isFinishing() || activity.isDestroyed()) {
            DanmakuSpider.log("Activity已销毁或正在销毁，不显示配置对话框");
            return;
        }

        activity.runOnUiThread(() -> {
            try {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    DanmakuSpider.log("Activity已销毁，不显示配置对话框");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                LinearLayout mainLayout = new LinearLayout(activity);
                mainLayout.setOrientation(LinearLayout.VERTICAL);
                mainLayout.setBackgroundColor(BACKGROUND_WHITE);
                mainLayout.setPadding(dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12));

                TextView title = new TextView(activity);
                title.setText("布局配置");
                title.setTextSize(16);
                title.setTextColor(PRIMARY_COLOR);
                title.setGravity(Gravity.CENTER);
                title.setPadding(0, 0, 0, dpToPx(activity, 10));
                title.setTypeface(null, android.graphics.Typeface.BOLD);
                mainLayout.addView(title);

                DanmakuConfig config = DanmakuConfigManager.getConfig(activity);

                // ====== GridLayout 一行两个 ======
                GridLayout gridLayout = new GridLayout(activity);
                gridLayout.setColumnCount(2);
                gridLayout.setUseDefaultMargins(false);
                gridLayout.setPadding(0, 0, 0, 0);

                // 宽度
                gridLayout.addView(createLpGridItem(activity, "宽度:", "0.1 - 1.0",
                        String.valueOf(config.getLpWidth()), false));
                // 高度
                gridLayout.addView(createLpGridItem(activity, "高度:", "0.1 - 1.0",
                        String.valueOf(config.getLpHeight()), false));
                // 透明度
                gridLayout.addView(createLpGridItem(activity, "透明度:", "0.1 - 1.0",
                        String.valueOf(config.getLpAlpha()), false));
                // 液态透明度（新增）
                gridLayout.addView(createLpGridItem(activity, "液态透明度:", "0.1 - 1.0",
                        String.valueOf(config.getLpLiquidAlpha()), true));

                mainLayout.addView(gridLayout);

                // 获取引用用于保存
                EditText widthInput = (EditText) ((LinearLayout) gridLayout.getChildAt(0)).getChildAt(1);
                EditText heightInput = (EditText) ((LinearLayout) gridLayout.getChildAt(1)).getChildAt(1);
                EditText alphaInput = (EditText) ((LinearLayout) gridLayout.getChildAt(2)).getChildAt(1);
                EditText liquidAlphaInput = (EditText) ((LinearLayout) gridLayout.getChildAt(3)).getChildAt(1);

                // 按钮
                LinearLayout btnLayout = new LinearLayout(activity);
                btnLayout.setOrientation(LinearLayout.HORIZONTAL);
                btnLayout.setGravity(Gravity.CENTER);
                btnLayout.setPadding(0, dpToPx(activity, 10), 0, 0);

                Button saveBtn = createStyledButton(activity, "保存", PRIMARY_COLOR);
                Button cancelBtn = createStyledButtonWithBorder(activity, "取消", PRIMARY_COLOR);

                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                        0, dpToPx(activity, 44), 1);
                btnParams.setMargins(dpToPx(activity, 6), 0, dpToPx(activity, 6), 0);
                saveBtn.setLayoutParams(btnParams);
                cancelBtn.setLayoutParams(btnParams);

                btnLayout.addView(saveBtn);
                btnLayout.addView(cancelBtn);
                mainLayout.addView(btnLayout);

                // ====== 液态玻璃包装 ======
                boolean isLiquidMode4 = FORCE_LIQUID_GLASS;
                if (isLiquidMode4) {
                    LiquidGlassView liquidRoot = new LiquidGlassView(activity);
                    liquidRoot.setBlurRadius(18f);
                    liquidRoot.setDarkMode(DanmakuConfigManager.getConfig(activity).isDarkMode());  // ← 加上
                    liquidRoot.setGlassAlpha(config.getLpLiquidAlpha());  // ← 联动液态透明度
                    liquidRoot.setCornerRadius(24f);
                    mainLayout.setBackgroundColor(Color.TRANSPARENT);
                    liquidRoot.addView(mainLayout, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    builder.setView(liquidRoot);
                } else {
                    builder.setView(mainLayout);
                }
                AlertDialog dialog = builder.create();
                if (isLiquidMode4) {
                    applyRealBlur(dialog);
                    if (dialog.getWindow() != null)
                        dialog.getWindow().setBackgroundDrawable(
                                new android.graphics.drawable.ColorDrawable(0x00000000));
                }

                saveBtn.setOnClickListener(v -> {
                    try {
                        float width = Float.parseFloat(widthInput.getText().toString());
                        float height = Float.parseFloat(heightInput.getText().toString());
                        float alpha = Float.parseFloat(alphaInput.getText().toString());
                        float liquidAlpha = Float.parseFloat(liquidAlphaInput.getText().toString());

                        width = Math.max(0.1f, Math.min(1.0f, width));
                        height = Math.max(0.1f, Math.min(1.0f, height));
                        alpha = Math.max(0.1f, Math.min(1.0f, alpha));
                        liquidAlpha = Math.max(0.1f, Math.min(1.0f, liquidAlpha));

                        config.setLpWidth(width);
                        config.setLpHeight(height);
                        config.setLpAlpha(alpha);
                        config.setLpLiquidAlpha(liquidAlpha);   // ← 保存液态透明度
                        DanmakuConfigManager.saveConfig(activity, config);
                        Utils.safeShowToast(activity, "布局配置已保存");
                        dialog.dismiss();
                    } catch (NumberFormatException e) {
                        Utils.safeShowToast(activity, "请输入有效的数字");
                    }
                });

                cancelBtn.setOnClickListener(v -> dialog.dismiss());

                safeShowDialog(activity, dialog);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 布局配置网格项：标签 + 输入框
     */
    private static LinearLayout createLpGridItem(Activity activity, String label, String hint,
                                                  String value, boolean isLiquid) {
        LinearLayout item = new LinearLayout(activity);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(Gravity.CENTER_VERTICAL);

        GridLayout.LayoutParams itemParams = new GridLayout.LayoutParams();
        itemParams.height = dpToPx(activity, 48);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            itemParams.width = 0;
            itemParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        } else {
            itemParams.width = dpToPx(activity, 160);
        }
        int margin = dpToPx(activity, 3);
        itemParams.setMargins(margin, margin, margin, margin);
        item.setLayoutParams(itemParams);

        TextView labelView = new TextView(activity);
        labelView.setText(label);
        labelView.setTextSize(11);
        labelView.setTextColor(isLiquid ? 0xFFE65100 : TEXT_PRIMARY);  // 液态透明度用橙色标签
        labelView.setPadding(0, 0, dpToPx(activity, 4), 0);
        item.addView(labelView);

        EditText input = new EditText(activity);
        input.setHint(hint);
        input.setText(value);
        input.setTextSize(11);
        input.setSingleLine(true);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setTextColor(TEXT_PRIMARY);
        input.setHintTextColor(TEXT_TERTIARY);
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                0, dpToPx(activity, 38), 1);
        input.setLayoutParams(inputParams);
        item.addView(input);

        return item;
    }

    public static void showDanmakuOffsetDialog(Context ctx) {
        if (!(ctx instanceof Activity)) {
            DanmakuSpider.log("错误：Context不是Activity");
            return;
        }
        Activity activity = (Activity) ctx;
        if (activity.isFinishing() || activity.isDestroyed()) {
            DanmakuSpider.log("Activity已销毁或正在销毁，不显示配置对话框");
            return;
        }

        activity.runOnUiThread(() -> {
            try {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    DanmakuSpider.log("Activity已销毁，不显示配置对话框");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                LinearLayout mainLayout = new LinearLayout(activity);
                mainLayout.setOrientation(LinearLayout.VERTICAL);
                mainLayout.setBackgroundColor(BACKGROUND_WHITE);
                mainLayout.setPadding(dpToPx(activity, 24), dpToPx(activity, 20), dpToPx(activity, 24), dpToPx(activity, 20));

                TextView title = new TextView(activity);
                title.setText("弹幕时间偏移");
                title.setTextSize(24);
                title.setTextColor(PRIMARY_COLOR);
                title.setGravity(Gravity.CENTER);
                title.setPadding(0, dpToPx(activity, 8), 0, dpToPx(activity, 12));
                title.setTypeface(null, android.graphics.Typeface.BOLD);
                mainLayout.addView(title);

                TextView subtitle = new TextView(activity);
                subtitle.setText("正数延后，负数提前，单位秒");
                subtitle.setTextSize(13);
                subtitle.setTextColor(TEXT_SECONDARY);
                subtitle.setGravity(Gravity.CENTER);
                subtitle.setPadding(0, 0, 0, dpToPx(activity, 16));
                mainLayout.addView(subtitle);

                DanmakuConfig config = DanmakuConfigManager.getConfig(activity);

                EditText offsetInput = new EditText(activity);
                offsetInput.setHint("例如 1.5 或 -0.8");
                offsetInput.setText(DanmakuUtils.formatOffsetSeconds(config.getDanmakuTimeOffsetMs()));
                offsetInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
                        android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL |
                        android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
                offsetInput.setTextColor(TEXT_PRIMARY);
                offsetInput.setTextSize(16);
                offsetInput.setSingleLine(true);
                offsetInput.setGravity(Gravity.CENTER);
                offsetInput.setPadding(dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12));
                offsetInput.setHintTextColor(TEXT_TERTIARY);
                applyTVInputFocusEffect(offsetInput, false);
                mainLayout.addView(offsetInput, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                TextView current = new TextView(activity);
                current.setText("当前：" + DanmakuUtils.formatOffsetLabel(config.getDanmakuTimeOffsetMs()));
                current.setTextSize(13);
                current.setTextColor(TEXT_SECONDARY);
                current.setGravity(Gravity.CENTER);
                current.setPadding(0, dpToPx(activity, 12), 0, dpToPx(activity, 16));
                mainLayout.addView(current);

                LinearLayout btnLayout = new LinearLayout(activity);
                btnLayout.setOrientation(LinearLayout.HORIZONTAL);
                btnLayout.setGravity(Gravity.CENTER);

                Button saveBtn = createStyledButton(activity, "保存", PRIMARY_COLOR);
                Button resetBtn = createStyledButton(activity, "重置", ACCENT_COLOR);
                Button cancelBtn = createStyledButtonWithBorder(activity, "取消", PRIMARY_COLOR);

                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                        0, dpToPx(activity, 44), 1);
                btnParams.setMargins(dpToPx(activity, 6), 0, dpToPx(activity, 6), 0);

                saveBtn.setLayoutParams(btnParams);
                resetBtn.setLayoutParams(btnParams);
                cancelBtn.setLayoutParams(btnParams);

                btnLayout.addView(saveBtn);
                btnLayout.addView(resetBtn);
                btnLayout.addView(cancelBtn);
                mainLayout.addView(btnLayout);

                // ====== 液态玻璃包装 ======
                boolean isLiquidMode5 = FORCE_LIQUID_GLASS;
                if (isLiquidMode5) {
                    LiquidGlassView liquidRoot = new LiquidGlassView(activity);
                    liquidRoot.setBlurRadius(18f);
                    liquidRoot.setDarkMode(config.isDarkMode());  // ← 加上
                    liquidRoot.setGlassAlpha(config.getLpLiquidAlpha());
                    liquidRoot.setCornerRadius(24f);
                    mainLayout.setBackgroundColor(Color.TRANSPARENT);
                    liquidRoot.addView(mainLayout, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    builder.setView(liquidRoot);
                } else {
                    builder.setView(mainLayout);
                }
                AlertDialog dialog = builder.create();
                if (isLiquidMode5) {
                    applyRealBlur(dialog);
                    if (dialog.getWindow() != null)
                        dialog.getWindow().setBackgroundDrawable(
                                new android.graphics.drawable.ColorDrawable(0x00000000));
                }

                saveBtn.setOnClickListener(v -> {
                    try {
                        int offsetMs = parseOffsetMs(offsetInput.getText().toString());
                        int oldOffsetMs = config.getDanmakuTimeOffsetMs();
                        config.setDanmakuTimeOffsetMs(offsetMs);
                        DanmakuConfigManager.saveConfig(activity, config);
                        Utils.safeShowToast(activity, "弹幕时间偏移已保存：" + DanmakuUtils.formatOffsetLabel(offsetMs));
                        DanmakuSpider.log("弹幕时间偏移已保存：" + DanmakuUtils.formatOffsetLabel(offsetMs));
                        if (oldOffsetMs != offsetMs) {
                            refreshCurrentDanmaku(activity, offsetMs);
                        }
                        dialog.dismiss();
                    } catch (NumberFormatException e) {
                        Utils.safeShowToast(activity, "请输入有效的时间偏移");
                    }
                });

                resetBtn.setOnClickListener(v -> {
                    int oldOffsetMs = config.getDanmakuTimeOffsetMs();
                    config.setDanmakuTimeOffsetMs(0);
                    DanmakuConfigManager.saveConfig(activity, config);
                    Utils.safeShowToast(activity, "弹幕时间偏移已重置");
                    DanmakuSpider.log("弹幕时间偏移已重置");
                    if (oldOffsetMs != 0) {
                        refreshCurrentDanmaku(activity, 0);
                    }
                    dialog.dismiss();
                });

                cancelBtn.setOnClickListener(v -> dialog.dismiss());

                safeShowDialog(activity, dialog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void showProxyPortDialog(Context ctx) {
        if (!(ctx instanceof Activity)) {
            DanmakuSpider.log("错误：Context不是Activity");
            return;
        }
        Activity activity = (Activity) ctx;
        if (activity.isFinishing() || activity.isDestroyed()) {
            DanmakuSpider.log("Activity已销毁或正在销毁，不显示代理端口配置对话框");
            return;
        }

        activity.runOnUiThread(() -> {
            try {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    DanmakuSpider.log("Activity已销毁，不显示代理端口配置对话框");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                LinearLayout mainLayout = new LinearLayout(activity);
                mainLayout.setOrientation(LinearLayout.VERTICAL);
                mainLayout.setBackgroundColor(BACKGROUND_WHITE);
                mainLayout.setPadding(dpToPx(activity, 24), dpToPx(activity, 20), dpToPx(activity, 24), dpToPx(activity, 20));

                TextView title = new TextView(activity);
                title.setText("代理端口设置");
                title.setTextSize(24);
                title.setTextColor(PRIMARY_COLOR);
                title.setGravity(Gravity.CENTER);
                title.setPadding(0, dpToPx(activity, 8), 0, dpToPx(activity, 12));
                title.setTypeface(null, android.graphics.Typeface.BOLD);
                mainLayout.addView(title);

                TextView subtitle = new TextView(activity);
                subtitle.setText("对外代理入口端口，默认 5575");
                subtitle.setTextSize(13);
                subtitle.setTextColor(TEXT_SECONDARY);
                subtitle.setGravity(Gravity.CENTER);
                subtitle.setPadding(0, 0, 0, dpToPx(activity, 16));
                mainLayout.addView(subtitle);

                DanmakuConfig config = DanmakuConfigManager.getConfig(activity);

                EditText portInput = new EditText(activity);
                portInput.setHint(String.valueOf(DanmakuConfig.DEFAULT_PROXY_PORT));
                portInput.setText(String.valueOf(config.getProxyPort()));
                portInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                portInput.setTextColor(TEXT_PRIMARY);
                portInput.setTextSize(16);
                portInput.setSingleLine(true);
                portInput.setGravity(Gravity.CENTER);
                portInput.setPadding(dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12));
                portInput.setHintTextColor(TEXT_TERTIARY);
                applyTVInputFocusEffect(portInput, false);
                mainLayout.addView(portInput, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                TextView current = new TextView(activity);
                current.setText("当前：" + config.getProxyPort() + " | 内部端口：" + GoProxyManager.DEFAULT_BACKEND_PORT + "/" + ProxyManager.JAVA_BACKEND_PORT);
                current.setTextSize(13);
                current.setTextColor(TEXT_SECONDARY);
                current.setGravity(Gravity.CENTER);
                current.setPadding(0, dpToPx(activity, 12), 0, dpToPx(activity, 16));
                mainLayout.addView(current);

                LinearLayout btnLayout = new LinearLayout(activity);
                btnLayout.setOrientation(LinearLayout.HORIZONTAL);
                btnLayout.setGravity(Gravity.CENTER);

                Button saveBtn = createStyledButton(activity, "保存", PRIMARY_COLOR);
                Button resetBtn = createStyledButton(activity, "默认", ACCENT_COLOR);
                Button cancelBtn = createStyledButtonWithBorder(activity, "取消", PRIMARY_COLOR);

                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                        0, dpToPx(activity, 44), 1);
                btnParams.setMargins(dpToPx(activity, 6), 0, dpToPx(activity, 6), 0);

                saveBtn.setLayoutParams(btnParams);
                resetBtn.setLayoutParams(btnParams);
                cancelBtn.setLayoutParams(btnParams);

                btnLayout.addView(saveBtn);
                btnLayout.addView(resetBtn);
                btnLayout.addView(cancelBtn);
                mainLayout.addView(btnLayout);

                // ====== 液态玻璃包装 ======
                boolean isLiquidMode6 = FORCE_LIQUID_GLASS;
                if (isLiquidMode6) {
                    LiquidGlassView liquidRoot = new LiquidGlassView(activity);
                    liquidRoot.setBlurRadius(18f);
                    liquidRoot.setDarkMode(config.isDarkMode());  // ← 加上
                    liquidRoot.setGlassAlpha(config.getLpLiquidAlpha());
                    liquidRoot.setCornerRadius(24f);
                    mainLayout.setBackgroundColor(Color.TRANSPARENT);
                    liquidRoot.addView(mainLayout, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    builder.setView(liquidRoot);
                } else {
                    builder.setView(mainLayout);
                }
                AlertDialog dialog = builder.create();
                if (isLiquidMode6) {
                    applyRealBlur(dialog);
                    if (dialog.getWindow() != null)
                        dialog.getWindow().setBackgroundDrawable(
                                new android.graphics.drawable.ColorDrawable(0x00000000));
                }

                saveBtn.setOnClickListener(v -> {
                    try {
                        int port = parseProxyPort(portInput.getText().toString());
                        boolean applied = ProxyManager.updateProxyPort(activity.getApplicationContext(), port);
                        String msg = applied ? "代理端口已保存：" + port : "代理端口已保存，当前端口启用失败：" + port;
                        Utils.safeShowToast(activity, msg);
                        DanmakuSpider.log(msg);
                        dialog.dismiss();
                    } catch (NumberFormatException e) {
                        Utils.safeShowToast(activity, "请输入 1024-65535 的有效端口");
                    }
                });

                resetBtn.setOnClickListener(v -> {
                    int port = DanmakuConfig.DEFAULT_PROXY_PORT;
                    boolean applied = ProxyManager.updateProxyPort(activity.getApplicationContext(), port);
                    String msg = applied ? "代理端口已恢复默认：" + port : "代理端口已恢复默认，当前启用失败";
                    Utils.safeShowToast(activity, msg);
                    DanmakuSpider.log(msg);
                    dialog.dismiss();
                });

                cancelBtn.setOnClickListener(v -> dialog.dismiss());

                safeShowDialog(activity, dialog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void showDanmakuStyleDialog(Context ctx) {
        if (!(ctx instanceof Activity)) {
            DanmakuSpider.log("错误：Context不是Activity");
            return;
        }
        Activity activity = (Activity) ctx;
        if (activity.isFinishing() || activity.isDestroyed()) {
            DanmakuSpider.log("Activity已销毁或正在销毁，不显示配置对话框");
            return;
        }

        activity.runOnUiThread(() -> {
            try {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    DanmakuSpider.log("Activity已销毁，不显示配置对话框");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                LinearLayout mainLayout = new LinearLayout(activity);
                mainLayout.setOrientation(LinearLayout.VERTICAL);
                mainLayout.setBackgroundColor(BACKGROUND_WHITE);
                mainLayout.setPadding(dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12), dpToPx(activity, 12));

                TextView title = new TextView(activity);
                title.setText("弹幕交互模式");
                title.setTextSize(16);
                title.setTextColor(PRIMARY_COLOR);
                title.setGravity(Gravity.CENTER);
                title.setPadding(0, 0, 0, dpToPx(activity, 10));
                title.setTypeface(null, android.graphics.Typeface.BOLD);
                mainLayout.addView(title);

                DanmakuConfig config = DanmakuConfigManager.getConfig(activity);
                String[] styles = DanmakuConfig.STYLE_OPTIONS;
                String currentStyle = config.getDanmakuStyle();

                // ====== 一行两个 GridLayout ======
                GridLayout gridLayout = new GridLayout(activity);
                gridLayout.setColumnCount(2);
                gridLayout.setUseDefaultMargins(false);
                gridLayout.setPadding(0, 0, 0, 0);

                // ====== 用数组引用 dialog，解决 lambda 中引用未定义变量 ======
                final AlertDialog[] dialogRef = new AlertDialog[1];

                for (String style : styles) {
                    Button styleBtn = createStyledButton(activity, buildDanmakuStyleOptionText(style),
                            style.equals(currentStyle) ? PRIMARY_COLOR : GRAY_INACTIVE);
                    styleBtn.setTextSize(11);
                    styleBtn.setSingleLine(false);
                    styleBtn.setMaxLines(2);
                    styleBtn.setEllipsize(TextUtils.TruncateAt.END);
                    styleBtn.setGravity(Gravity.CENTER);

                    GridLayout.LayoutParams btnParams = new GridLayout.LayoutParams();
                    btnParams.height = dpToPx(activity, 48);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        btnParams.width = 0;
                        btnParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                        btnParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                    } else {
                        btnParams.width = dpToPx(activity, 150);
                    }
                    int margin = dpToPx(activity, 4);
                    btnParams.setMargins(margin, margin, margin, margin);
                    styleBtn.setLayoutParams(btnParams);
                    gridLayout.addView(styleBtn);

                    styleBtn.setOnClickListener(v -> {
                        config.setDanmakuStyle(style);
                        DanmakuConfigManager.saveConfig(activity, config);
                        Utils.safeShowToast(activity, "弹幕交互模式已切换为: " + config.getDanmakuStyleDisplayName());
                        if (dialogRef[0] != null) dialogRef[0].dismiss();   // ← 用 dialogRef
                    });
                }

                mainLayout.addView(gridLayout);

                // ====== 液态玻璃包装 ======
                boolean isLiquidMode7 = FORCE_LIQUID_GLASS;
                if (isLiquidMode7) {
                    LiquidGlassView liquidRoot = new LiquidGlassView(activity);
                    liquidRoot.setBlurRadius(18f);
                    DanmakuConfig glassCfg = DanmakuConfigManager.getConfig(activity);
                    liquidRoot.setDarkMode(config.isDarkMode());  // ← 加上
                    liquidRoot.setGlassAlpha(config.getLpLiquidAlpha());
                    liquidRoot.setCornerRadius(24f);
                    mainLayout.setBackgroundColor(Color.TRANSPARENT);
                    liquidRoot.addView(mainLayout, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    builder.setView(liquidRoot);
                } else {
                    builder.setView(mainLayout);
                }

                AlertDialog dialog = builder.create();
                dialogRef[0] = dialog;   // ← 赋值到数组

                if (isLiquidMode7) {
                    applyRealBlur(dialog);
                    if (dialog.getWindow() != null)
                        dialog.getWindow().setBackgroundDrawable(
                                new android.graphics.drawable.ColorDrawable(0x00000000));
                }

                safeShowDialog(activity, dialog);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static String buildDanmakuStyleOptionText(String style) {
        if (DanmakuConfig.STYLE_CLASSIC.equals(style)) return style + "（原模板一）";
        if (DanmakuConfig.STYLE_GRID.equals(style)) return style + "（原模板二）";
        if (DanmakuConfig.STYLE_DARK_GRID.equals(style)) return style + "（原模板三）";
        if (DanmakuConfig.STYLE_MODERN_PANEL.equals(style)) return style + "（原模板四）";
        if (DanmakuConfig.STYLE_MULTI_GRID.equals(style)) return style + "（新模板）";  // ← 新增多格
        if (DanmakuConfig.STYLE_LIQUID.equals(style)) return style + "（玻璃质感）";  // ← 新增液态
        return DanmakuConfig.normalizeDanmakuStyle(style);
    }


    private static int parseOffsetMs(String text) throws NumberFormatException {
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())) return 0;
        double seconds = Double.parseDouble(text.trim());
        if (seconds > 600) seconds = 600;
        if (seconds < -600) seconds = -600;
        return (int) Math.round(seconds * 1000);
    }

    private static int parseProxyPort(String text) throws NumberFormatException {
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())) {
            return DanmakuConfig.DEFAULT_PROXY_PORT;
        }
        int port = Integer.parseInt(text.trim());
        if (port < 1024 || port > 65535) throw new NumberFormatException("invalid port");
        if (port == GoProxyManager.DEFAULT_BACKEND_PORT || port == ProxyManager.JAVA_BACKEND_PORT) {
            throw new NumberFormatException("backend port");
        }
        return port;
    }

    private static void refreshCurrentDanmaku(Activity activity, int offsetMs) {
        DanmakuItem item = DanmakuManager.getLastDanmakuItem();
        if (item == null) {
            DanmakuSpider.log("弹幕时间偏移已更新；当前没有可重推的弹幕，下一次选择/自动推送时生效");
            return;
        }

        DanmakuSpider.log("弹幕时间偏移变更，刷新当前弹幕: " + item.getTitleWithEp() + " -> " + DanmakuUtils.formatOffsetLabel(offsetMs));
        LeoDanmakuService.pushDanmakuDirect(item, activity, false, true);
    }

    // 创建带边框的按钮 - 电视端优化版本
    private static Button createStyledButtonWithBorder(Activity activity, String text, int color) {
        Button button = new Button(activity);
        button.setText(text);
        button.setTextColor(color);
        button.setTextSize(14);
        button.setTypeface(null, android.graphics.Typeface.BOLD);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);

        // ====== 性能优化：两种状态的背景预先创建 ======
        final android.graphics.drawable.Drawable normalBg = createTVFocusableBorderDrawable(color, false);
        final android.graphics.drawable.Drawable focusBg = createTVFocusableBorderDrawable(TV_FOCUS_BORDER_COLOR, true);
        button.setBackground(normalBg);

        button.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                v.setBackground(hasFocus ? focusBg : normalBg);
                ((Button) v).setTextColor(hasFocus ? TV_FOCUS_TEXT_COLOR : color);
            }
        });

        return button;
    }

    // 创建实心按钮 - 电视端优化版本（焦点橙色）
    private static Button createStyledButton(Activity activity, String text, int backgroundColor) {
        Button button = new Button(activity);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(14);
        button.setTypeface(null, android.graphics.Typeface.BOLD);
        button.setFocusable(true);
        button.setFocusableInTouchMode(false);

        // ====== 性能优化：两种状态的背景预先创建，焦点切换不再分配对象 ======
        final android.graphics.drawable.Drawable normalBg = createTVFocusableSolidDrawable(backgroundColor, false);
        final android.graphics.drawable.Drawable focusBg = createTVFocusableSolidDrawable(backgroundColor, true);
        button.setBackground(normalBg);

        button.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                v.setBackground(hasFocus ? focusBg : normalBg);
                ((Button) v).setTextColor(hasFocus ? TV_FOCUS_TEXT_COLOR : Color.WHITE);
            }
        });

        return button;
    }

    // ========== 电视端焦点高亮专用Drawable方法 ==========

    /**
     * 创建电视端可聚焦的边框按钮背景
     * @param color 边框颜色
     * @param focused 是否为焦点状态
     */
    private static android.graphics.drawable.Drawable createTVFocusableBorderDrawable(int color, boolean focused) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        if (focused) {
            // 焦点状态：填充背景 + 加粗发光边框
            drawable.setColor(TV_FOCUS_BG_COLOR);
            drawable.setStroke(6, TV_FOCUS_BORDER_COLOR); // 6px加粗边框，高对比度
            drawable.setCornerRadius(12);
        } else {
            // 非焦点状态：透明背景 + 细边框
            drawable.setColor(Color.TRANSPARENT);
            drawable.setStroke(2, color);
            drawable.setCornerRadius(12);
        }
        return drawable;
    }

    /**
     * 创建电视端可聚焦的实心按钮背景
     * @param color 背景颜色
     * @param focused 是否为焦点状态
     */
    private static android.graphics.drawable.Drawable createTVFocusableSolidDrawable(int color, boolean focused) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(12);
        if (focused) {
            drawable.setStroke(6, 0xFFFF6B00);  // ← 改为橙色
            drawable.setColor(lightenColor(color, 0.15f));
        } else {
            drawable.setStroke(0, 0);
        }
        return drawable;
    }

    /**
     * 创建电视端可聚焦的透明按钮背景（用于深色主题）
     * @param color 背景颜色（带透明度）
     * @param focused 是否为焦点状态
     */
    private static android.graphics.drawable.Drawable createTVFocusableTransparentDrawable(int color, boolean focused) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(12);
        if (focused) {
            // 焦点状态：加粗发光边框
            drawable.setStroke(6, TV_FOCUS_BORDER_COLOR);
            // 背景色稍微增加不透明度，增强对比度
            drawable.setColor(lightenAlpha(color, 0.2f));
        } else {
            drawable.setStroke(0, 0);
        }
        return drawable;
    }

    /**
     * 增加颜色透明度（让透明颜色更不透明一点）
     */
    private static int lightenAlpha(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        // 增加alpha值（减少透明度）
        int newA = Math.min(255, (int) (a + (255 - a) * factor));
        return (newA << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * 创建电视端输入框背景
     * @param focused 是否为焦点状态
     * @param isDarkTheme 是否为深色主题
     */
    private static android.graphics.drawable.Drawable createTVInputDrawable(boolean focused, boolean isDarkTheme) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        if (focused) {
            // 焦点状态：明显的浅蓝背景 + 蓝色边框
            drawable.setColor(isDarkTheme ? 0x44007AFF : TV_INPUT_FOCUS_BG);
            drawable.setStroke(4, TV_INPUT_FOCUS_BORDER);
            drawable.setCornerRadius(8);
        } else {
            // 非焦点状态：普通背景
            drawable.setColor(isDarkTheme ? DARK_BG_SECONDARY : BACKGROUND_LIGHT);
            drawable.setStroke(2, isDarkTheme ? DARK_BORDER : BORDER_COLOR);
            drawable.setCornerRadius(8);
        }
        return drawable;
    }

    // 颜色提亮辅助方法
    private static int lightenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (int) Math.min(255, ((color >> 16) & 0xFF) + (255 - ((color >> 16) & 0xFF)) * factor);
        int g = (int) Math.min(255, ((color >> 8) & 0xFF) + (255 - ((color >> 8) & 0xFF)) * factor);
        int b = (int) Math.min(255, (color & 0xFF) + (255 - (color & 0xFF)) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    // 创建圆角背景 - 带阴影效果
    private static android.graphics.drawable.Drawable createRoundedBackgroundDrawable(int color) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(12); // 使用px单位的圆角半径
        drawable.setStroke(0, 0); // 无边框
        return drawable;
    }

    private static android.graphics.drawable.Drawable createRoundedStrokeBackgroundDrawable(int fillColor, int strokeColor, int strokeWidth) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setColor(fillColor);
        drawable.setCornerRadius(12);
        drawable.setStroke(strokeWidth, strokeColor);
        return drawable;
    }

    // 创建带边框的圆角背景
    private static android.graphics.drawable.Drawable createRoundedBorderDrawable(int color) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setColor(Color.TRANSPARENT);
        drawable.setStroke(2, color);
        drawable.setCornerRadius(12);
        return drawable;
    }

    // 创建带粗边框的焦点页签背景（用于电视端焦点高亮）
    private static android.graphics.drawable.Drawable createFocusedTabDrawable(int color) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setColor(color);
        drawable.setStroke(4, PRIMARY_COLOR); // 加粗外边框
        drawable.setCornerRadius(12);
        return drawable;
    }

    // 安全显示对话框的辅助方法
    private static void safeShowDialog(Activity activity, AlertDialog dialog) {
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed() && !dialog.isShowing()) {
            try {
                dialog.show();
            } catch (Exception e) {
                DanmakuSpider.log("显示对话框失败: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            DanmakuSpider.log("Activity已销毁或对话框已在显示，无法显示对话框");
        }
    }


    /**
     * 显示带标签页切换的日志对话框（弹幕日志 + Go代理日志）
     * @param ctx 上下文
     */
    public static void showUnifiedLogDialog(Context ctx) {
        if (!(ctx instanceof Activity)) {
            DanmakuSpider.log("错误：Context不是Activity");
            return;
        }
        Activity activity = (Activity) ctx;
        if (activity.isFinishing() || activity.isDestroyed()) {
            DanmakuSpider.log("Activity已销毁或正在销毁，不显示日志对话框");
            return;
        }

        activity.runOnUiThread(() -> {
            try {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    DanmakuSpider.log("Activity已销毁，不显示日志对话框");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                LinearLayout mainLayout = new LinearLayout(activity);
                mainLayout.setOrientation(LinearLayout.VERTICAL);
                mainLayout.setBackgroundColor(BACKGROUND_WHITE);
                mainLayout.setPadding(dpToPx(activity, 20), dpToPx(activity, 12), dpToPx(activity, 20), dpToPx(activity, 12));

                // 标题
                TextView title = new TextView(activity);
                title.setText("日志查看器");
                title.setTextSize(20);
                title.setTextColor(PRIMARY_COLOR);
                title.setGravity(Gravity.CENTER);
                title.setPadding(0, dpToPx(activity, 4), 0, dpToPx(activity, 8));
                title.setTypeface(null, android.graphics.Typeface.BOLD);
                mainLayout.addView(title);

                // 判断是否有代理
                final int activeProxyType = ProxyManager.getActiveProxyType();
                final boolean hasProxy = activeProxyType != ProxyManager.PROXY_TYPE_NONE;

                // 当前选中的页签索引（0=弹幕日志，1=代理日志）
                final int[] currentTab = {0};
                // 当前排序状态（false=正序，true=倒序）
                final boolean[] isReversed = {false};

                // 日志内容显示区域 - 使用权重自适应高度
                ScrollView scrollView = new ScrollView(activity);
                scrollView.setBackgroundColor(0xFFF5F5F5);
                LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
                scrollView.setLayoutParams(scrollParams);
                scrollView.setPadding(dpToPx(activity, 10), dpToPx(activity, 10), dpToPx(activity, 10), dpToPx(activity, 10));

                TextView logText = new TextView(activity);
                logText.setTextSize(11);
                logText.setTextColor(TEXT_PRIMARY);
                logText.setTypeface(android.graphics.Typeface.MONOSPACE);
                logText.setText(DanmakuSpider.getLogContent());
                scrollView.addView(logText);
                mainLayout.addView(scrollView);

                // 页签容器 - 放在日志区域下方
                LinearLayout tabContainer = new LinearLayout(activity);
                tabContainer.setOrientation(LinearLayout.HORIZONTAL);
                tabContainer.setGravity(Gravity.CENTER);
                tabContainer.setPadding(0, dpToPx(activity, 8), 0, dpToPx(activity, 8));

                // 弹幕日志页签按钮
                Button danmakuTabBtn = createTabButton(activity, "弹幕日志", true);
                LinearLayout.LayoutParams danmakuTabParams = new LinearLayout.LayoutParams(
                        0, dpToPx(activity, 36), 1);
                danmakuTabParams.setMargins(0, 0, dpToPx(activity, 4), 0);
                danmakuTabBtn.setLayoutParams(danmakuTabParams);

                // 代理日志页签按钮（仅当存在时显示）
                Button goProxyTabBtn = null;
                if (hasProxy) {
                    String tabLabel = ProxyManager.getProxyTypeShortName() + "代理日志";
                    goProxyTabBtn = createTabButton(activity, tabLabel, false);
                    LinearLayout.LayoutParams goProxyTabParams = new LinearLayout.LayoutParams(
                            0, dpToPx(activity, 36), 1);
                    goProxyTabParams.setMargins(dpToPx(activity, 4), 0, 0, 0);
                    goProxyTabBtn.setLayoutParams(goProxyTabParams);
                }

                tabContainer.addView(danmakuTabBtn);
                if (goProxyTabBtn != null) {
                    tabContainer.addView(goProxyTabBtn);
                }
                mainLayout.addView(tabContainer);

                // 按钮区域
                LinearLayout btnLayout = new LinearLayout(activity);
                btnLayout.setOrientation(LinearLayout.HORIZONTAL);
                btnLayout.setGravity(Gravity.CENTER);
                btnLayout.setPadding(0, dpToPx(activity, 4), 0, 0);

                Button sortButton = createStyledButtonWithBorder(activity, "排序: 正序", PRIMARY_COLOR);
                Button clearButton = createStyledButtonWithBorder(activity, "清空", PRIMARY_COLOR);
                Button closeButton = createStyledButtonWithBorder(activity, "关闭", PRIMARY_COLOR);

                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                        0, dpToPx(activity, 40), 1);
                btnParams.setMargins(dpToPx(activity, 4), 0, dpToPx(activity, 4), 0);

                sortButton.setLayoutParams(btnParams);
                clearButton.setLayoutParams(btnParams);
                closeButton.setLayoutParams(btnParams);

                btnLayout.addView(sortButton);
                btnLayout.addView(clearButton);
                btnLayout.addView(closeButton);
                mainLayout.addView(btnLayout);

                // ====== 液态玻璃包装 ======
                boolean isLiquidLog = FORCE_LIQUID_GLASS;
                if (isLiquidLog) {
                    LiquidGlassView liquidRoot = new LiquidGlassView(activity);
                    liquidRoot.setBlurRadius(18f);
                    DanmakuConfig glassCfg = DanmakuConfigManager.getConfig(activity);
                    liquidRoot.setDarkMode(glassCfg.isDarkMode());
                    liquidRoot.setGlassAlpha(glassCfg.getLpLiquidAlpha());
                    liquidRoot.setCornerRadius(24f);
                    mainLayout.setBackgroundColor(Color.TRANSPARENT);
                    liquidRoot.addView(mainLayout, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    builder.setView(liquidRoot);
                } else {
                    builder.setView(mainLayout);
                }
                AlertDialog dialog = builder.create();
                if (isLiquidLog) {
                    applyRealBlur(dialog);
                    if (dialog.getWindow() != null)
                        dialog.getWindow().setBackgroundDrawable(
                                new android.graphics.drawable.ColorDrawable(0x00000000));
                }

                // 设置对话框最大高度为屏幕高度的70%
                dialog.setOnShowListener(d -> {
                    android.view.Window window = dialog.getWindow();
                    if (window != null) {
                        android.view.WindowManager.LayoutParams params = window.getAttributes();
                        params.width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.90);
                        params.height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.85);
                        window.setAttributes(params);
                    }
                });

                // 页签切换逻辑
                final Button finalGoProxyTabBtn = goProxyTabBtn;
                danmakuTabBtn.setOnClickListener(v -> {
                    currentTab[0] = 0;
                    updateTabButtonState(danmakuTabBtn, true);
                    if (finalGoProxyTabBtn != null) {
                        updateTabButtonState(finalGoProxyTabBtn, false);
                    }
                    // 弹幕日志支持倒序
                    logText.setText(DanmakuSpider.getLogContent(isReversed[0]));
                    // 倒序时滚动到顶部，正序时滚动到底部
                    scrollView.post(() -> scrollView.fullScroll(isReversed[0] ? ScrollView.FOCUS_UP : ScrollView.FOCUS_DOWN));
                });

                if (goProxyTabBtn != null) {
                    final Button finalGoProxyTabBtnForClick = goProxyTabBtn;
                    goProxyTabBtn.setOnClickListener(v -> {
                        currentTab[0] = 1;
                        updateTabButtonState(danmakuTabBtn, false);
                        updateTabButtonState(finalGoProxyTabBtnForClick, true);
                        logText.setText(ProxyManager.getLogContent(isReversed[0]));
                        // 倒序时滚动到顶部，正序时滚动到底部
                        scrollView.post(() -> scrollView.fullScroll(isReversed[0] ? ScrollView.FOCUS_UP : ScrollView.FOCUS_DOWN));
                    });
                }

                // 排序按钮逻辑（弹幕日志和Go代理日志都支持倒序）
                sortButton.setOnClickListener(v -> {
                    isReversed[0] = !isReversed[0];
                    sortButton.setText(isReversed[0] ? "排序: 倒序" : "排序: 正序");

                    if (currentTab[0] == 0) {
                        logText.setText(DanmakuSpider.getLogContent(isReversed[0]));
                    } else {
                        logText.setText(ProxyManager.getLogContent(isReversed[0]));
                    }
                    // 倒序时滚动到顶部，正序时滚动到底部
                    scrollView.post(() -> scrollView.fullScroll(isReversed[0] ? ScrollView.FOCUS_UP : ScrollView.FOCUS_DOWN));
                });

                // 清空按钮逻辑
                clearButton.setOnClickListener(v -> {
                    if (currentTab[0] == 0) {
                        DanmakuSpider.clearLogs();
                        logText.setText(DanmakuSpider.getLogContent(isReversed[0]));
                    } else {
                        ProxyManager.clearLogs();
                        logText.setText(ProxyManager.getLogContent(isReversed[0]));
                    }
                });

                closeButton.setOnClickListener(v -> dialog.dismiss());

                safeShowDialog(activity, dialog);

                // 初始滚动到底部（正序时）
                scrollView.post(() -> scrollView.fullScroll(isReversed[0] ? ScrollView.FOCUS_UP : ScrollView.FOCUS_DOWN));

            } catch (Exception e) {
                DanmakuSpider.log("显示统一日志对话框异常: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // 创建页签按钮
    private static Button createTabButton(Activity activity, String text, boolean isActive) {
        Button button = new Button(activity);
        button.setText(text);
        button.setTextSize(14);
        button.setTypeface(null, android.graphics.Typeface.BOLD);
        // 设置焦点可用，支持电视端遥控器操作
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.setClickable(true);
        updateTabButtonState(button, isActive);

        // 添加焦点变化监听，电视端高亮效果
        button.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // 获得焦点时显示高亮边框 - 使用加粗边框而不是缩放，避免覆盖其他按钮
                ((Button) v).setBackground(createFocusedTabDrawable(PRIMARY_LIGHT));
                ((Button) v).setTextColor(Color.WHITE);
            } else {
                // 失去焦点时恢复原始状态
                // 需要根据当前激活状态恢复样式
                boolean isCurrentlyActive = v.getTag() != null && (Boolean) v.getTag();
                updateTabButtonState((Button) v, isCurrentlyActive);
            }
        });

        // 保存激活状态到tag，用于焦点恢复时判断
        button.setTag(isActive);

        return button;
    }

    // 更新页签按钮状态
    private static void updateTabButtonState(Button button, boolean isActive) {
        if (button == null) return;
        if (isActive) {
            button.setTextColor(Color.WHITE);
            button.setBackground(createRoundedBackgroundDrawable(PRIMARY_COLOR));
        } else {
            button.setTextColor(PRIMARY_COLOR);
            button.setBackground(createRoundedBorderDrawable(PRIMARY_COLOR));
        }
        // 更新tag保存当前激活状态，用于焦点恢复时判断
        button.setTag(isActive);
    }


    // 显示搜索对话框
    public static void showSearchDialog(Activity activity, EpisodeInfo episodeInfo) {
        // 检查Activity状态
        if (activity.isFinishing() || activity.isDestroyed()) {
            DanmakuSpider.log("Activity已销毁或正在销毁，不显示搜索对话框");
            return;
        }
    
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 在创建对话框前再次检查状态
                    if (activity.isFinishing() || activity.isDestroyed()) {
                        DanmakuSpider.log("Activity已销毁，不显示搜索对话框");
                        return;
                    }
    
                    DanmakuConfig config = DanmakuConfigManager.getConfig(activity);
                    boolean isTemplate3 = config.isDarkUI();
                    boolean isModernPanel = config.isModernPanelDanmakuStyle();
                    boolean isLiquid = config.isLiquidDanmakuStyle();
    
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    LinearLayout mainLayout = new LinearLayout(activity);
                    mainLayout.setOrientation(LinearLayout.VERTICAL);
                    mainLayout.setPadding(dpToPx(activity, 15), dpToPx(activity, 10), dpToPx(activity, 15), dpToPx(activity, 10));
    
                    TextView title = new TextView(activity);
                    title.setText("Leo弹幕搜索");
                    title.setTextSize(16);
                    title.setTextColor(isTemplate3 ? DARK_TEXT_PRIMARY : PRIMARY_COLOR);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, android.graphics.Typeface.BOLD);
                    title.setPadding(0, dpToPx(activity, 2), 0, dpToPx(activity, 5));
                    mainLayout.addView(title);
    
                    LinearLayout searchLayout = new LinearLayout(activity);
                    searchLayout.setOrientation(LinearLayout.HORIZONTAL);
                    searchLayout.setPadding(0, 0, 0, dpToPx(activity, 4));
                    searchLayout.setGravity(Gravity.CENTER_VERTICAL);
    
                    final EditText searchInput = new EditText(activity);
                    searchInput.setHint("输入关键词搜索弹幕...");
                    String initialKeyword = getStableSearchCacheKey(episodeInfo);
                    String cachedKeyword = SharedPreferencesService.getSearchKeywordCache(activity, initialKeyword);
                    searchInput.setText(cachedKeyword);
                    searchInput.setHintTextColor(isTemplate3 ? DARK_TEXT_TERTIARY : TEXT_TERTIARY);
                    boolean isDarkMode = config.isDarkMode();   // ← 提前读一次
                    if (isLiquid) {
                        searchInput.setBackground(createLiquidInputDrawable(isDarkMode, false));
                    } else {
                        searchInput.setBackgroundColor(isTemplate3 ? DARK_BG_SECONDARY : BACKGROUND_LIGHT);
                    }
                    searchInput.setPadding(dpToPx(activity, 12), dpToPx(activity, 10), dpToPx(activity, 12), dpToPx(activity, 10));
                    searchInput.setTextSize(14);
                    searchInput.setTextColor(isTemplate3 ? DARK_TEXT_PRIMARY : TEXT_PRIMARY);
                    LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(0, dpToPx(activity, 44), 1);
                    inputParams.setMargins(0, 0, dpToPx(activity, 4), 0);
                    searchInput.setLayoutParams(inputParams);
                    if (isLiquid) {
                        // 预创建两个状态的 Drawable，焦点切换零分配
                        final android.graphics.drawable.Drawable liqNormalBg = createLiquidInputDrawable(isDarkMode, false);
                        final android.graphics.drawable.Drawable liqFocusBg = createLiquidInputDrawable(isDarkMode, true);
                        searchInput.setOnFocusChangeListener((v, hasFocus) -> {
                            ((EditText) v).setBackground(hasFocus ? liqFocusBg : liqNormalBg);
                        });
                    }
    
                    Button reverseBtn;
                    if (isLiquid) {
                        reverseBtn = createLiquidGlassButton(activity, isReversed ? "倒序" : "升序", 0xFF4CAF50);
                    } else if (isTemplate3) {
                        reverseBtn = createDarkSolidButton(activity, isReversed ? "倒序" : "升序", isReversed ? DARK_ACCENT_COLOR : DARK_TERTIARY_LIGHT);
                    } else {
                        reverseBtn = createStyledButton(activity, isReversed ? "倒序" : "升序", isReversed ? ACCENT_COLOR : TERTIARY_LIGHT);
                    }
                    reverseBtn.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(activity, 50), dpToPx(activity, 44)));
                    reverseBtn.setTextSize(16);
    
                    Button searchBtn;
                    if (isLiquid) {
                        searchBtn = createLiquidGlassButton(activity, "搜索", 0xFF2196F3);
                    } else if (isTemplate3) {
                        searchBtn = createDarkSolidButton(activity, "搜索", DARK_BG_TERTIARY);
                    } else {
                        searchBtn = createStyledButton(activity, "搜索", PRIMARY_COLOR);
                    }
                    searchBtn.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(activity, 70), dpToPx(activity, 44)));
    
                    searchLayout.addView(searchInput);
                    searchLayout.addView(reverseBtn);
                    View separator = new View(activity);
                    LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(dpToPx(activity, 4), dpToPx(activity, 44));
                    separator.setLayoutParams(separatorParams);
                    searchLayout.addView(separator);
                    searchLayout.addView(searchBtn);
                    mainLayout.addView(searchLayout);
    
                    LinearLayout tabContainer = new LinearLayout(activity);
                    tabContainer.setOrientation(LinearLayout.HORIZONTAL);
                    tabContainer.setGravity(Gravity.CENTER_VERTICAL);
                    tabContainer.setPadding(dpToPx(activity, 4), dpToPx(activity, 4), dpToPx(activity, 4), dpToPx(activity, 4));
                    if (isLiquid) {
                        tabContainer.setBackgroundColor(0x33FFFFFF);
                    } else {
                        tabContainer.setBackgroundColor(isTemplate3 ? DARK_BG_TERTIARY : BACKGROUND_LIGHT);
                    }
                    LinearLayout.LayoutParams tabContainerParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, dpToPx(activity, 17));
                    tabContainer.setLayoutParams(tabContainerParams);
                    tabContainer.setVisibility(View.GONE);
    
                    HorizontalScrollView tabScroll = new HorizontalScrollView(activity);
                    tabScroll.setHorizontalScrollBarEnabled(false);
                    tabScroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
                    tabScroll.addView(tabContainer);
                    LinearLayout.LayoutParams tabScrollParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    tabScrollParams.setMargins(0, dpToPx(activity, 2), 0, dpToPx(activity, 8));
                    tabScroll.setLayoutParams(tabScrollParams);
                    mainLayout.addView(tabScroll);
    
                    // ====== 结果区 ListView（一行多列 + 视图回收，焦点天然稳定）======
                    ListView resultListView = new ListView(activity);
                    resultListView.setBackgroundColor((isTemplate3 || isLiquid) ? Color.TRANSPARENT : BACKGROUND_WHITE);
                    resultListView.setDivider(null);
                    resultListView.setDividerHeight(0);
                    resultListView.setCacheColorHint(Color.TRANSPARENT);
                    resultListView.setItemsCanFocus(true);
                    resultListView.setClipToPadding(false);
                    resultListView.setPadding(0, dpToPx(activity, 10), 0, 0);

                    final ResultListAdapter resultAdapter = new ResultListAdapter(activity);
                    DanmakuConfig rcCfg = DanmakuConfigManager.getConfig(activity);
                    boolean rcMulti = rcCfg.isMultiGridDanmakuStyle();
                    boolean rcLiquid = rcCfg.isLiquidDanmakuStyle();
                    boolean rcGrid = rcCfg.isGridDanmakuStyle() || rcMulti || rcLiquid;
                    DisplayMetrics rdm = activity.getResources().getDisplayMetrics();
                    final int gridColumns = !rcGrid ? 1
                            : (rcMulti || rcLiquid ? 3
                                    : Math.max(3, (int) (rdm.widthPixels / rdm.density) / 120));
                    resultAdapter.setGridColumns(gridColumns);
                    resultListView.setAdapter(resultAdapter);

                    LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    scrollParams.weight = 1;
                    resultListView.setLayoutParams(scrollParams);
                    mainLayout.addView(resultListView);
    
                    // ====== 所有子 View 添加完毕后，才决定用什么容器 setView ======
                        // 真液态玻璃模式
                    if (isLiquid) {
                        LiquidGlassView liquidRoot = new LiquidGlassView(activity);
                        liquidRoot.setBlurRadius(18f);
                        liquidRoot.setGlassAlpha(config.getLpLiquidAlpha());
                        liquidRoot.setCornerRadius(24f);
                        liquidRoot.setDarkMode(config.isDarkMode());
                        // ====== 加上这三行（测试用）======
                    //    liquidRoot.setRefractionStrength(0.15f);   // 折射加强5倍
                    //    liquidRoot.setDispersionStrength(0.12f);   // 色散加强4倍
                    //    liquidRoot.setHighlightStrength(0.25f);    // 轮廓光加强2.5倍
                        DanmakuSpider.log("液态玻璃渲染模式: " + liquidRoot.getRenderMode());


                        mainLayout.setBackgroundColor(Color.TRANSPARENT);
                        ViewGroup.LayoutParams wrapParams = new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);
                        liquidRoot.addView(mainLayout, wrapParams);
                        builder.setView(liquidRoot);
                    } else {
                        // ====== 非液态模式：必须设置背景色 + setView ======
                        mainLayout.setBackgroundColor(isTemplate3 ? DARK_BG_PRIMARY : BACKGROUND_WHITE);
                        builder.setView(mainLayout);
                    }

                    final AlertDialog dialog = builder.create();
                    resultAdapter.bindDialog(dialog);   // ← 结果按钮点击时要 dismiss
    
                    if (isTemplate3) {
                        dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
                    }
    
                    reverseBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isReversed = !isReversed;
                        if (isLiquid) {
                            // 液态模式：重新应用液态玻璃按钮样式
                            Button newBtn = createLiquidGlassButton(activity,
                                    isReversed ? "倒序" : "升序", 0xFF4CAF50);
                            reverseBtn.setBackground(newBtn.getBackground());
                        } else if (isTemplate3) {
                            reverseBtn.setBackground(createRoundedTransparentDrawable(isReversed ? DARK_ACCENT_COLOR : DARK_TERTIARY_LIGHT));
                        } else {
                            reverseBtn.setBackground(createRoundedBackgroundDrawable(isReversed ? ACCENT_COLOR : TERTIARY_LIGHT));
                        }
                            reverseBtn.setText(isReversed ? "倒序" : "升序");
                            showResultsForTab(resultListView, currentItems, activity, dialog);
                        }
                    });
    
                    searchBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String keyword = searchInput.getText().toString().trim();
                            if (TextUtils.isEmpty(keyword)) {
                                Utils.safeShowToast(activity, "请输入关键词");
                                return;
                            }
                            currentSearchKeyword = keyword;
    
                            String cacheKey = getStableSearchCacheKey(episodeInfo);
                            if (!keyword.equals(cacheKey)) {
                                SharedPreferencesService.saveSearchKeywordCache(activity, cacheKey, keyword);
                                DanmakuSpider.log("已保存新的搜索缓存: " + cacheKey + " -> " + keyword);
                            } else {
                                SharedPreferencesService.saveSearchKeywordCache(activity, cacheKey, "");
                                DanmakuSpider.log("已清空搜索缓存: " + cacheKey);
                            }
    
                            resultAdapter.showMessage("正在搜索: " + keyword);
                            tabContainer.removeAllViews();
                            tabContainer.setVisibility(View.GONE);
    
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    EpisodeInfo searchEpisodeInfo = new EpisodeInfo();
                                    List<String> names = new ArrayList<>();
                                    names.add(keyword);
                                    searchEpisodeInfo.setEpisodeNames(names);
                                    searchEpisodeInfo.setSearchCacheKey(getStableSearchCacheKey(episodeInfo));
                                    if (episodeInfo != null && !TextUtils.isEmpty(episodeInfo.getEpisodeNum())) {
                                        searchEpisodeInfo.setEpisodeNum(episodeInfo.getEpisodeNum());
                                    }
                                    List<DanmakuItem> results = LeoDanmakuService.manualSearch(searchEpisodeInfo, activity);
                                    if (isReversed) {
                                        java.util.Collections.reverse(results);
                                    }
    
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tabContainer.removeAllViews();
    
                                            if (results.isEmpty()) {
                                                tabContainer.setVisibility(View.GONE);
                                                resultAdapter.showMessage("未找到结果");
                                                return;
                                            }
    
                                            java.util.Map<String, List<DanmakuItem>> groupedResults = new java.util.LinkedHashMap<>();
                                            for (DanmakuItem item : results) {
                                                String groupName = isModernPanel ? buildSearchResultGroupName(activity, item)
                                                        : (item.from != null ? item.from : "默认");
                                                if (!groupedResults.containsKey(groupName)) {
                                                    groupedResults.put(groupName, new java.util.ArrayList<>());
                                                }
                                                groupedResults.get(groupName).add(item);
                                            }
    
                                            java.util.List<String> tabs = new java.util.ArrayList<>(groupedResults.keySet());
                                            java.util.Collections.sort(tabs);
    
                                            if (isModernPanel) {
                                                int selectedTabIndex = findInitialSearchTabIndex(tabs, groupedResults);
                                                renderSearchSourceSelector(tabContainer, resultListView, groupedResults, tabs,
                                                        selectedTabIndex, activity, dialog, isTemplate3);
                                            } else {
                                                renderLegacySearchSourceTabs(tabContainer, resultListView, groupedResults, tabs,
                                                        activity, dialog, isTemplate3);
                                            }
                                        }
                                    });
                                }
                            }).start();
                        }
                    });
    
                    safeShowDialog(activity, dialog);
    
                    // ====== 液态模式：开启窗口模糊 + 透明背景 ======
                    if (isLiquid) {
                        applyRealBlur(dialog);
                        if (dialog.getWindow() != null) {
                            dialog.getWindow().setBackgroundDrawable(
                                    new android.graphics.drawable.ColorDrawable(0x00000000));
                        }
                    }
    
                    android.view.WindowManager.LayoutParams lp = new android.view.WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels * config.getLpWidth());
                    lp.height = (int) (activity.getResources().getDisplayMetrics().heightPixels * config.getLpHeight());
                    lp.alpha = config.getLpAlpha();
                    dialog.getWindow().setAttributes(lp);
    
                    String keywordToSearch = SharedPreferencesService.getSearchKeywordCache(activity, initialKeyword);
                    if (!TextUtils.isEmpty(keywordToSearch)) {
                        searchBtn.performClick();
                    }
    
                } catch (Throwable e) {
                    DanmakuSpider.log("显示搜索对话框异常: " + e.getMessage());
                    e.printStackTrace();
                    Utils.safeShowToast(activity, "搜索框错误: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        });
    }

    // dp转px
    private static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private static String getStableSearchCacheKey(EpisodeInfo episodeInfo) {
        if (episodeInfo == null) return "";
        if (!TextUtils.isEmpty(episodeInfo.getSearchCacheKey())) {
            return episodeInfo.getSearchCacheKey();
        }
        if (episodeInfo.getEpisodeNames() != null && !episodeInfo.getEpisodeNames().isEmpty()) {
            return episodeInfo.getEpisodeNames().get(episodeInfo.getEpisodeNames().size() > 1 ? 1 : 0);
        }
        return "";
    }

    private static int findInitialSearchTabIndex(List<String> tabs, java.util.Map<String, List<DanmakuItem>> groupedResults) {
        if (tabs == null || tabs.isEmpty()) return 0;
        if (TextUtils.isEmpty(DanmakuManager.lastDanmakuUrl)) return 0;

        for (int i = 0; i < tabs.size(); i++) {
            List<DanmakuItem> items = groupedResults.get(tabs.get(i));
            if (items == null) continue;
            for (DanmakuItem item : items) {
                if (item != null && DanmakuManager.lastDanmakuUrl.equals(item.getDanmakuUrl())) {
                    return i;
                }
            }
        }
        return 0;
    }

    private static void renderLegacySearchSourceTabs(LinearLayout tabContainer,
                                                     ListView resultListView,
                                                     java.util.Map<String, List<DanmakuItem>> groupedResults,
                                                     List<String> tabs,
                                                     Activity activity,
                                                     AlertDialog dialog,
                                                     boolean isTemplate3) {
        tabContainer.removeAllViews();
        if (tabs == null || tabs.isEmpty()) {
            tabContainer.setVisibility(View.GONE);
            showResultsForTab(resultListView, new ArrayList<>(), activity, dialog);
            return;
        }

        tabContainer.setVisibility(View.VISIBLE);
        updateSearchSourceContainerHeight(tabContainer, activity, 48);
        int selectedIndex = findInitialSearchTabIndex(tabs, groupedResults);

        // ====== 性能优化：配置只读一次，闭包捕获 ======
        final boolean isLiquidTab = DanmakuConfigManager.getConfig(activity).isLiquidDanmakuStyle();
        final boolean isDarkTab = DanmakuConfigManager.getConfig(activity).isDarkMode();

        for (int i = 0; i < tabs.size(); i++) {
            String tabName = tabs.get(i);
            Button tabBtn;
            if (isLiquidTab) {
                tabBtn = createLiquidMenuButton(activity, tabName);
            } else if (isTemplate3) {
                tabBtn = createDarkSolidButton(activity, tabName, DARK_PRIMARY_COLOR);
            } else {
                tabBtn = createStyledButton(activity, tabName, PRIMARY_COLOR);
            }
            tabBtn.setTag(tabName);
            tabBtn.setPadding(dpToPx(activity, 15), dpToPx(activity, 10), dpToPx(activity, 15), dpToPx(activity, 10));

            LinearLayout.LayoutParams tabParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            tabParams.setMargins(dpToPx(activity, 4), 0, dpToPx(activity, 4), 0);
            tabBtn.setLayoutParams(tabParams);

            // 焦点监听：零配置读取（用闭包变量）
            tabBtn.setOnFocusChangeListener((v, hasFocus) -> {
                Object tag = v.getTag();
                boolean active = tag instanceof Boolean && (Boolean) tag;
                if (isLiquidTab) {
                    if (hasFocus) {
                        v.setBackground(createLiquidGlassDrawable(v.getContext(), 0xFFFF6B00, 3));
                        ((Button) v).setTextColor(0xFFE65100);
                    } else if (active) {
                        v.setBackground(createLiquidGlassDrawable(v.getContext(),
                                isDarkTab ? 0xFF00E5FF : 0xFF2196F3, 2));
                        ((Button) v).setTextColor(isDarkTab ? 0xFF00E5FF : 0xFF1565C0);
                    } else {
                        v.setBackground(createLiquidGlassDrawable(v.getContext(),
                                isDarkTab ? 0x33FFFFFF : 0x44FFFFFF, 1));
                        ((Button) v).setTextColor(isDarkTab ? 0xFFCCCCCC : 0xDD333333);
                    }
                    return;
                }
                if (isTemplate3) {
                    ((Button) v).setTextColor(hasFocus ? Color.WHITE : DARK_TEXT_PRIMARY);
                    v.setBackground(createRoundedTransparentDrawable(hasFocus ? 0xFFFF6B00 :
                            (active ? DARK_PRIMARY_COLOR : DARK_INACTIVE)));
                } else {
                    ((Button) v).setTextColor(Color.WHITE);
                    v.setBackground(createRoundedBackgroundDrawable(hasFocus ? 0xFFFF6B00 :
                            (active ? PRIMARY_COLOR : GRAY_INACTIVE)));
                }
            });

            final int tabIndex = i;
            tabBtn.setOnClickListener(v1 -> {
                for (int j = 0; j < tabContainer.getChildCount(); j++) {
                    updateLegacySourceTabState((Button) tabContainer.getChildAt(j), j == tabIndex,
                            isTemplate3, isLiquidTab, isDarkTab);
                }
                showResultsForTab(resultListView, groupedResults.get(tabName), activity, dialog);
            });

            tabContainer.addView(tabBtn);
            updateLegacySourceTabState(tabBtn, i == selectedIndex, isTemplate3, isLiquidTab, isDarkTab);
        }

        showResultsForTab(resultListView, groupedResults.get(tabs.get(selectedIndex)), activity, dialog);
    }

    // ====== 性能优化：接受 isLiquid/isDark 参数，不再 getConfig ======
    private static void updateLegacySourceTabState(Button button, boolean isActive, boolean isTemplate3,
                                                   boolean isLiquid, boolean isDark) {
        button.setTag(Boolean.valueOf(isActive));

        if (isLiquid) {
            if (button.hasFocus()) {
                button.setBackground(createLiquidGlassDrawable(button.getContext(), 0xFFFF6B00, 3));
                button.setTextColor(0xFFE65100);
            } else if (isActive) {
                button.setBackground(createLiquidGlassDrawable(button.getContext(),
                        isDark ? 0xFF00E5FF : 0xFF2196F3, 2));
                button.setTextColor(isDark ? 0xFF00E5FF : 0xFF1565C0);
            } else {
                button.setBackground(createLiquidGlassDrawable(button.getContext(),
                        isDark ? 0x33FFFFFF : 0x44FFFFFF, 1));
                button.setTextColor(isDark ? 0xFFCCCCCC : 0xDD333333);
            }
            return;
        }

        if (isTemplate3) {
            button.setBackground(createRoundedTransparentDrawable(isActive ? DARK_PRIMARY_COLOR : DARK_INACTIVE));
            button.setTextColor(DARK_TEXT_PRIMARY);
        } else {
            button.setBackground(createRoundedBackgroundDrawable(isActive ? PRIMARY_COLOR : GRAY_INACTIVE));
            button.setTextColor(Color.WHITE);
        }
    }

    private static void renderSearchSourceSelector(LinearLayout tabContainer,
                                                   ListView resultListView,
                                                   java.util.Map<String, List<DanmakuItem>> groupedResults,
                                                   List<String> tabs,
                                                   int selectedIndex,
                                                   Activity activity,
                                                   AlertDialog dialog,
                                                   boolean isTemplate3) {
        tabContainer.removeAllViews();
        if (tabs == null || tabs.isEmpty()) {
            tabContainer.setVisibility(View.GONE);
            showResultsForTab(resultListView, new ArrayList<>(), activity, dialog);
            return;
        }

        int selected = Math.max(0, Math.min(selectedIndex, tabs.size() - 1));
        if (tabs.size() == 1) {
            tabContainer.setVisibility(View.GONE);
            showResultsForTab(resultListView, groupedResults.get(tabs.get(selected)), activity, dialog);
            return;
        }

        tabContainer.setVisibility(View.VISIBLE);
        if (shouldUseSimpleSearchTabs(tabs, groupedResults)) {
            updateSearchSourceContainerHeight(tabContainer, activity, 48);
            renderSearchSourceTabs(tabContainer, resultListView, groupedResults, tabs,
                    selected, activity, dialog, isTemplate3);
        } else {
            updateSearchSourceContainerHeight(tabContainer, activity, 48);
            renderSearchSourcePager(tabContainer, resultListView, groupedResults, tabs,
                    selected, activity, dialog, isTemplate3);
        }
    }

    private static boolean shouldUseSimpleSearchTabs(List<String> tabs,
                                                     java.util.Map<String, List<DanmakuItem>> groupedResults) {
        return tabs != null
                && tabs.size() > 1
                && tabs.size() <= SIMPLE_SOURCE_TAB_LIMIT
                && getTotalSearchResultCount(tabs, groupedResults) <= SIMPLE_RESULT_COUNT_LIMIT;
    }

    private static int getTotalSearchResultCount(List<String> tabs,
                                                 java.util.Map<String, List<DanmakuItem>> groupedResults) {
        if (tabs == null || groupedResults == null) return 0;
        int count = 0;
        for (String tab : tabs) {
            List<DanmakuItem> items = groupedResults.get(tab);
            if (items != null) count += items.size();
        }
        return count;
    }

    private static void updateSearchSourceContainerHeight(LinearLayout tabContainer, Activity activity, int heightDp) {
        ViewGroup.LayoutParams params = tabContainer.getLayoutParams();
        if (params != null) {
            params.height = dpToPx(activity, heightDp);
            tabContainer.setLayoutParams(params);
        }
    }

    private static void renderSearchSourceTabs(LinearLayout tabContainer,
                                               ListView resultListView,
                                               java.util.Map<String, List<DanmakuItem>> groupedResults,
                                               List<String> tabs,
                                               int selectedIndex,
                                               Activity activity,
                                               AlertDialog dialog,
                                               boolean isTemplate3) {
        tabContainer.removeAllViews();
        int selected = Math.max(0, Math.min(selectedIndex, tabs.size() - 1));
        for (int i = 0; i < tabs.size(); i++) {
            String tabName = tabs.get(i);
            Button tabBtn = createSearchSourceTabButton(activity, tabName, i == selected, isTemplate3);
            LinearLayout.LayoutParams tabParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            tabParams.setMargins(dpToPx(activity, 4), 0, dpToPx(activity, 4), 0);
            tabBtn.setLayoutParams(tabParams);

            final int targetIndex = i;
            tabBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renderSearchSourceSelector(tabContainer, resultListView, groupedResults, tabs,
                            targetIndex, activity, dialog, isTemplate3);
                }
            });
            tabContainer.addView(tabBtn);
        }

        showResultsForTab(resultListView, groupedResults.get(tabs.get(selected)), activity, dialog);
    }

    private static Button createSearchSourceTabButton(Activity activity, String text, boolean isActive, boolean isTemplate3) {
        Button button = new Button(activity);
        button.setText(text);
        button.setTextSize(5); //来源字体大小
        button.setTypeface(null, android.graphics.Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setSingleLine(true);
        button.setEllipsize(TextUtils.TruncateAt.END);
        button.setAllCaps(false);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.setPadding(dpToPx(activity, 3), 0, dpToPx(activity, 3), 0); //来源边距
        button.setTag(Boolean.valueOf(isActive));
        updateSearchSourceTabStyle(button, isActive, isTemplate3, false);

        button.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Object tag = v.getTag();
                boolean active = tag instanceof Boolean && (Boolean) tag;
                updateSearchSourceTabStyle((Button) v, active, isTemplate3, hasFocus);
            }
        });
        return button;
    }

    private static void updateSearchSourceTabStyle(Button button, boolean isActive, boolean isTemplate3, boolean hasFocus) {
        int color;
        if (isTemplate3) {
            // 深色模式：焦点橙色，选中蓝色，未选灰色
            color = hasFocus ? 0xFFFF6B00 : (isActive ? DARK_PRIMARY_COLOR : DARK_INACTIVE);
            button.setTextColor(hasFocus ? Color.WHITE : DARK_TEXT_PRIMARY);
            button.setBackground(createRoundedTransparentDrawable(color));
        } else {
            // 浅色模式：焦点橙色，选中蓝色，未选灰色
            color = hasFocus ? 0xFFFF6B00 : (isActive ? PRIMARY_COLOR : GRAY_INACTIVE);
            button.setTextColor(Color.WHITE);
            button.setBackground(createRoundedBackgroundDrawable(color));
        }
    }

    private static void renderSearchSourcePager(LinearLayout tabContainer,
                                                ListView resultListView,
                                                java.util.Map<String, List<DanmakuItem>> groupedResults,
                                                List<String> tabs,
                                                int selectedIndex,
                                                Activity activity,
                                                AlertDialog dialog,
                                                boolean isTemplate3) {
        tabContainer.removeAllViews();
        if (tabs == null || tabs.isEmpty()) {
            showResultsForTab(resultListView, new ArrayList<>(), activity, dialog);
            return;
        }

        final int selected = Math.max(0, Math.min(selectedIndex, tabs.size() - 1));
        final String tabName = tabs.get(selected);
        List<DanmakuItem> selectedItems = groupedResults.get(tabName);
        boolean canCycle = tabs.size() > 1;

        final Runnable prevAction = new Runnable() {
            @Override
            public void run() {
                if (!canCycle) return;
                int target = selected > 0 ? selected - 1 : tabs.size() - 1;
                renderSearchSourceSelector(tabContainer, resultListView, groupedResults, tabs,
                        target, activity, dialog, isTemplate3);
            }
        };

        final Runnable nextAction = new Runnable() {
            @Override
            public void run() {
                if (!canCycle) return;
                int target = selected < tabs.size() - 1 ? selected + 1 : 0;
                renderSearchSourceSelector(tabContainer, resultListView, groupedResults, tabs,
                        target, activity, dialog, isTemplate3);
            }
        };

        final Runnable chooseAction = new Runnable() {
            @Override
            public void run() {
                showSearchSourceChooser(tabContainer, resultListView, groupedResults, tabs,
                        selected, activity, dialog, isTemplate3);
            }
        };

        Button prevBtn = createSearchPagerArrowButton(activity, "<", isTemplate3, canCycle);
        Button centerBtn = createSearchPagerCenterButton(activity, tabName,
                "来源 " + (selected + 1) + "/" + tabs.size(),
                isTemplate3);
        Button nextBtn = createSearchPagerArrowButton(activity, ">", isTemplate3, canCycle);

        prevBtn.setOnClickListener(v -> prevAction.run());
        nextBtn.setOnClickListener(v -> nextAction.run());
        attachSearchPagerNavigation(centerBtn, prevAction, nextAction, chooseAction, canCycle);

        LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(dpToPx(activity, 54), ViewGroup.LayoutParams.MATCH_PARENT);
        arrowParams.setMargins(dpToPx(activity, 4), 0, dpToPx(activity, 4), 0);
        LinearLayout.LayoutParams centerParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        centerParams.setMargins(dpToPx(activity, 4), 0, dpToPx(activity, 4), 0);

        tabContainer.addView(prevBtn, arrowParams);
        tabContainer.addView(centerBtn, centerParams);
        tabContainer.addView(nextBtn, arrowParams);

        showResultsForTab(resultListView, selectedItems, activity, dialog);
    }

    private static Button createSearchPagerArrowButton(Activity activity, String text, boolean isTemplate3, boolean enabled) {
        Button button = new Button(activity);
        button.setText(text);
        button.setTextSize(22);
        button.setTypeface(null, android.graphics.Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setAllCaps(false);
        button.setEnabled(enabled);
        button.setFocusable(enabled);
        button.setFocusableInTouchMode(enabled);
        button.setPadding(0, 0, 0, 0);

        int activeColor = isTemplate3 ? DARK_PRIMARY_COLOR : PRIMARY_COLOR;
        int inactiveColor = isTemplate3 ? DARK_INACTIVE : GRAY_INACTIVE;
        button.setTextColor(isTemplate3 ? DARK_TEXT_PRIMARY : Color.WHITE);
        button.setBackground(isTemplate3 ?
                createRoundedTransparentDrawable(enabled ? activeColor : inactiveColor) :
                createRoundedBackgroundDrawable(enabled ? activeColor : inactiveColor));
        button.setAlpha(enabled ? 1.0f : 0.55f);

        button.setOnFocusChangeListener((v, hasFocus) -> {
            if (!button.isEnabled()) return;
            int color = hasFocus ? (isTemplate3 ? DARK_SECONDARY_COLOR : PRIMARY_DARK) : activeColor;
            v.setBackground(isTemplate3 ? createRoundedTransparentDrawable(color) : createRoundedBackgroundDrawable(color));
        });

        return button;
    }

    private static Button createSearchPagerCenterButton(Activity activity, String title, String summary, boolean isTemplate3) {
        Button button = new Button(activity);
        button.setText(title + "  " + summary);
        button.setTextSize(14);
        button.setTypeface(null, android.graphics.Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setSingleLine(true);
        button.setMaxLines(1);
        button.setEllipsize(TextUtils.TruncateAt.END);
        button.setAllCaps(false);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.setPadding(dpToPx(activity, 12), 0, dpToPx(activity, 12), 0);
        button.setTextColor(isTemplate3 ? DARK_TEXT_PRIMARY : Color.WHITE);
        button.setBackground(isTemplate3 ?
                createRoundedTransparentDrawable(DARK_PRIMARY_COLOR) :
                createRoundedBackgroundDrawable(PRIMARY_COLOR));

        button.setOnFocusChangeListener((v, hasFocus) -> {
            int color = hasFocus ? (isTemplate3 ? DARK_SECONDARY_COLOR : PRIMARY_DARK) :
                    (isTemplate3 ? DARK_PRIMARY_COLOR : PRIMARY_COLOR);
            v.setBackground(isTemplate3 ? createRoundedTransparentDrawable(color) : createRoundedBackgroundDrawable(color));
        });

        return button;
    }

    private static void showSearchSourceChooser(LinearLayout tabContainer,
                                                ListView resultListView,
                                                java.util.Map<String, List<DanmakuItem>> groupedResults,
                                                List<String> tabs,
                                                int selectedIndex,
                                                Activity activity,
                                                AlertDialog parentDialog,
                                                boolean isTemplate3) {
        if (tabs == null || tabs.isEmpty()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LinearLayout mainLayout = new LinearLayout(activity);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(isTemplate3 ? DARK_BG_PRIMARY : BACKGROUND_WHITE);
        mainLayout.setPadding(dpToPx(activity, 14), dpToPx(activity, 12), dpToPx(activity, 14), dpToPx(activity, 12));

        TextView title = new TextView(activity);
        title.setText("选择来源（共 " + tabs.size() + " 个来源）");
        title.setTextSize(16);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(isTemplate3 ? DARK_TEXT_PRIMARY : PRIMARY_COLOR);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dpToPx(activity, 8));
        mainLayout.addView(title);

        ScrollView scrollView = new ScrollView(activity);
        GridLayout gridLayout = new GridLayout(activity);
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int screenWidthDp = (int) (displayMetrics.widthPixels / displayMetrics.density);
        int columns = screenWidthDp >= 1200 ? 4 : (screenWidthDp >= 760 ? 3 : (screenWidthDp >= 520 ? 2 : 1));
        gridLayout.setColumnCount(columns);
        gridLayout.setUseDefaultMargins(false);
        gridLayout.setPadding(0, 0, 0, 0);
        scrollView.addView(gridLayout);

        final AlertDialog chooserDialog = builder.setView(mainLayout).create();
        if (isTemplate3 && chooserDialog.getWindow() != null) {
            chooserDialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        }

        int selected = Math.max(0, Math.min(selectedIndex, tabs.size() - 1));
        for (int i = 0; i < tabs.size(); i++) {
            String tabName = tabs.get(i);
            Button sourceBtn = createSearchSourceChoiceButton(activity, tabName, i == selected, isTemplate3);

            GridLayout.LayoutParams sourceParams = new GridLayout.LayoutParams();
            sourceParams.height = dpToPx(activity, 42);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sourceParams.width = 0;
                sourceParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                sourceParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            } else {
                sourceParams.width = dpToPx(activity, 180);
            }
            int margin = dpToPx(activity, 4);
            sourceParams.setMargins(margin, margin, margin, margin);
            sourceBtn.setLayoutParams(sourceParams);

            final int targetIndex = i;
            sourceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooserDialog.dismiss();
                    renderSearchSourceSelector(tabContainer, resultListView, groupedResults, tabs,
                            targetIndex, activity, parentDialog, isTemplate3);
                }
            });
            gridLayout.addView(sourceBtn);
        }
        mainLayout.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        safeShowDialog(activity, chooserDialog);
        android.view.Window window = chooserDialog.getWindow();
        if (window != null) {
            android.view.WindowManager.LayoutParams lp = new android.view.WindowManager.LayoutParams();
            lp.copyFrom(window.getAttributes());
            lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.76f);
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
    }

    private static Button createSearchSourceChoiceButton(Activity activity,
                                                        String title,
                                                        boolean isActive,
                                                        boolean isTemplate3) {
        Button button = new Button(activity);
        button.setText(title);
        button.setTextSize(13);
        button.setTypeface(null, android.graphics.Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setSingleLine(true);
        button.setEllipsize(TextUtils.TruncateAt.END);
        button.setAllCaps(false);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.setPadding(dpToPx(activity, 8), 0, dpToPx(activity, 8), 0);
        button.setTag(Boolean.valueOf(isActive));
        updateSearchSourceChoiceStyle(button, isActive, isTemplate3, false);

        button.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Object tag = v.getTag();
                boolean active = tag instanceof Boolean && (Boolean) tag;
                updateSearchSourceChoiceStyle((Button) v, active, isTemplate3, hasFocus);
            }
        });
        return button;
    }

    private static void updateSearchSourceChoiceStyle(Button button, boolean isActive, boolean isTemplate3, boolean hasFocus) {
        int color;
        if (isTemplate3) {
            color = hasFocus ? 0xFFFF6B00 : (isActive ? DARK_PRIMARY_COLOR : DARK_BG_TERTIARY);
            button.setTextColor(hasFocus ? Color.WHITE : DARK_TEXT_PRIMARY);
            button.setBackground(createRoundedTransparentDrawable(color));
        } else {
            color = hasFocus ? 0xFFFF6B00 : (isActive ? PRIMARY_COLOR : 0xFFE8E8E8);
            button.setTextColor(hasFocus ? Color.WHITE : (isActive ? Color.WHITE : TEXT_PRIMARY));
            button.setBackground(createRoundedBackgroundDrawable(color));
        }
    }

    private static void attachSearchPagerNavigation(Button centerButton,
                                                    Runnable prevAction,
                                                    Runnable nextAction,
                                                    Runnable chooseAction,
                                                    boolean canCycle) {
        centerButton.setOnClickListener(v -> {
            if (chooseAction != null) chooseAction.run();
        });

        centerButton.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() != KeyEvent.ACTION_UP) return false;
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && canCycle) {
                prevAction.run();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && canCycle) {
                nextAction.run();
                return true;
            }
            return false;
        });

        final float[] downX = new float[1];
        centerButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                downX[0] = event.getX();
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                float deltaX = event.getX() - downX[0];
                int threshold = dpToPx(v.getContext(), 48);
                if (deltaX > threshold && canCycle) {
                    prevAction.run();
                } else if (deltaX < -threshold && canCycle) {
                    nextAction.run();
                } else {
                    v.performClick();
                }
                return true;
            }
            return true;
        });
    }

    /** 用户是否已开始操作（用于抑制自动 requestFocus 抢焦点） */
    private static volatile boolean sUserInteracted = false;
    
    // 为指定页签显示结果
    // 为指定页签显示结果（ListView 版：一行多列 + 视图回收）
    private static void showResultsForTab(ListView resultListView, List<DanmakuItem> items,
                                          Activity activity, AlertDialog dialog) {
        ResultListAdapter adapter = (ResultListAdapter) resultListView.getAdapter();
        if (adapter == null) return;
        adapter.bindDialog(dialog);

        currentItems = items;
        sUserInteracted = false;

        if (items == null || items.isEmpty()) {
            adapter.showMessage("该来源下无结果");
            return;
        }

        // 按 animeTitle 分组（保持出现顺序）
        java.util.Map<String, List<DanmakuItem>> animeGroups = new java.util.LinkedHashMap<>();
        for (DanmakuItem item : items) {
            String animeTitle = item.animeTitle != null ? item.animeTitle : item.title;
            if (!animeGroups.containsKey(animeTitle)) {
                animeGroups.put(animeTitle, new java.util.ArrayList<>());
            }
            animeGroups.get(animeTitle).add(item);
        }

        // 配置只读一次
        DanmakuConfig config = DanmakuConfigManager.getConfig(activity);
        boolean isGrid = config.isGridDanmakuStyle()
                || config.isMultiGridDanmakuStyle()
                || config.isLiquidDanmakuStyle();

        java.util.List<String> animeTitles = new java.util.ArrayList<>(animeGroups.keySet());
        sortAnimeTitlesByKeyword(animeTitles);

        // 倒序统一在提交前处理
        for (List<DanmakuItem> g : animeGroups.values()) {
            sortResults(g, isReversed);
        }

        java.util.Map<String, List<DanmakuItem>> ordered = new java.util.LinkedHashMap<>();
        for (String t : animeTitles) ordered.put(t, animeGroups.get(t));

        // 自动展开包含 lastDanmakuUrl 的组
        java.util.Set<String> autoExpand = new java.util.HashSet<>();
        String lastUrl = DanmakuManager.lastDanmakuUrl;
        if (!TextUtils.isEmpty(lastUrl)) {
            for (String t : animeTitles) {
                for (DanmakuItem it : ordered.get(t)) {
                    if (lastUrl.equals(it.getDanmakuUrl())) {
                        autoExpand.add(t);
                        break;
                    }
                }
            }
        }

        adapter.submit(ordered, autoExpand);

        // 滚动 + 焦点落到上次播放项
        int scrollPos = adapter.findPositionByUrl(lastUrl);
        if (scrollPos < 0 && !animeTitles.isEmpty()) scrollPos = 0;
        final int finalPos = scrollPos;
        if (finalPos >= 0) {
            resultListView.post(() -> {
                resultListView.setSelection(finalPos);
                resultListView.postDelayed(() -> {
                    try {
                        View v = resultListView.getChildAt(
                                finalPos - resultListView.getFirstVisiblePosition());
                        if (v instanceof ViewGroup && ((ViewGroup) v).getChildCount() > 0) {
                            View target = ((ViewGroup) v).getChildAt(0);
                            if (!sUserInteracted) target.requestFocus();
                        }
                    } catch (Throwable ignored) {}
                }, 250);
            });
        }
    }

    private static String buildGroupHeaderTitle(String title, int count, boolean expanded) {
        return (expanded ? "[-] " : "[+] ") + title + " (" + count + "集)";
    }

    private static void applyGroupHeaderStyle(Button button, boolean active, boolean isTemplate3, boolean isLiquid, boolean focused) {
        Context ctx = button.getContext();

        // ====== 液态模式分支（不再 getConfig）======
        if (isLiquid) {
            boolean isDarkL = isTemplate3;   // 深色状态就是 isTemplate3（isDarkUI）
            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
            bg.setCornerRadius(dpToPx(ctx, 12));
            if (focused) {
                if (isDarkL) {
                    bg.setColors(new int[]{0xCC4E342E, 0x99FF9800});
                    bg.setStroke(dpToPx(ctx, 3), 0xFFFF6B00);
                    button.setTextColor(0xFFFFB74D);
                } else {
                    bg.setColors(new int[]{0xCCFFE0B2, 0x99FF9800});
                    bg.setStroke(dpToPx(ctx, 3), 0xFFFF6B00);
                    button.setTextColor(0xFFE65100);
                }
            } else if (active) {
                if (isDarkL) {
                    bg.setColors(new int[]{0x881A237E, 0x553F51B5});
                    bg.setStroke(dpToPx(ctx, 2), 0xFF00E5FF);
                    button.setTextColor(0xFF80DEEA);
                } else {
                    bg.setColors(new int[]{0x88E3F2FD, 0x5590CAF9});
                    bg.setStroke(dpToPx(ctx, 2), 0xFF2196F3);
                    button.setTextColor(0xFF1565C0);
                }
            } else {
                if (isDarkL) {
                    bg.setColors(new int[]{0x66303040, 0x44303040});
                    bg.setStroke(dpToPx(ctx, 2), 0x66FFFFFF);
                    button.setTextColor(0xDDCCCCCC);
                } else {
                    bg.setColors(new int[]{0x66FFFFFF, 0x44FFFFFF});
                    bg.setStroke(dpToPx(ctx, 2), 0x99AAAAAA);
                    button.setTextColor(0xDD222222);
                }
            }
            button.setBackground(bg);
            return;
        }

        // ====== 原有逻辑不变 ======
        if (isTemplate3) {
            button.setTextColor(DARK_TEXT_PRIMARY);
            int fillColor = focused || active ? 0x33007AFF : DARK_BG_SECONDARY;
            int strokeColor = focused ? DARK_HIGHLIGHT_LIGHT : (active ? DARK_HIGHLIGHT : DARK_BORDER);
            int strokeWidth = focused || active ? 4 : 2;
            button.setBackground(createRoundedStrokeBackgroundDrawable(fillColor, strokeColor, strokeWidth));
        } else {
            button.setTextColor(TEXT_PRIMARY);
            int fillColor = focused || active ? 0xFFEAF2FF : 0xFFE8E8E8;
            int strokeColor = focused ? PRIMARY_DARK : (active ? PRIMARY_COLOR : 0xFFD6D6D6);
            int strokeWidth = focused || active ? 4 : 2;
            button.setBackground(createRoundedStrokeBackgroundDrawable(fillColor, strokeColor, strokeWidth));
        }
    }

    private static void sortResults(List<DanmakuItem> results, boolean reversed) {
        // 不做自定义排序，保持接口返回的原始顺序
        // 倒序时仅反转列表
        if (reversed) {
            java.util.Collections.reverse(results);
        }
    }

    private static void sortAnimeTitlesByKeyword(List<String> titles) {
        final String keyword = normalizeSearchText(currentSearchKeyword);
        if (TextUtils.isEmpty(keyword)) {
            java.util.Collections.sort(titles);
            return;
        }
        java.util.Collections.sort(titles, new java.util.Comparator<String>() {
            @Override
            public int compare(String left, String right) {
                double leftScore = calculateTitleMatchScore(left, keyword);
                double rightScore = calculateTitleMatchScore(right, keyword);
                int scoreCompare = Double.compare(rightScore, leftScore);
                if (scoreCompare != 0) return scoreCompare;

                int lengthCompare = Integer.compare(safeLength(left), safeLength(right));
                if (lengthCompare != 0) return lengthCompare;

                String leftText = left != null ? left : "";
                String rightText = right != null ? right : "";
                return leftText.compareTo(rightText);
            }
        });
    }

    private static double calculateTitleMatchScore(String title, String normalizedKeyword) {
        String normalizedTitle = normalizeSearchText(title);
        if (TextUtils.isEmpty(normalizedTitle) || TextUtils.isEmpty(normalizedKeyword)) return 0.0;
        if (normalizedTitle.equals(normalizedKeyword)) return 3.0;
        if (normalizedTitle.contains(normalizedKeyword)) return 2.0 + normalizedKeyword.length() / (double) normalizedTitle.length();
        if (normalizedKeyword.contains(normalizedTitle)) return 1.8 + normalizedTitle.length() / (double) normalizedKeyword.length();
        return calculateSimilarity(normalizedTitle, normalizedKeyword);
    }

    private static String normalizeSearchText(String text) {
        if (TextUtils.isEmpty(text)) return "";
        return text.replaceAll("(?i)\\s*from\\s*.*$", "")
                .replaceAll("[\\[【].*?[\\]】]", "")
                .replaceAll("\\(\\d{4}\\)", "")
                .replaceAll("\\s+", "")
                .trim()
                .toLowerCase();
    }

    private static double calculateSimilarity(String left, String right) {
        if (left == null) left = "";
        if (right == null) right = "";
        String longer = left;
        String shorter = right;
        if (left.length() < right.length()) {
            longer = right;
            shorter = left;
        }
        int longerLength = longer.length();
        if (longerLength == 0) return 1.0;
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    private static int editDistance(String left, String right) {
        left = left.toLowerCase();
        right = right.toLowerCase();
        int[] costs = new int[right.length() + 1];
        for (int i = 0; i <= left.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= right.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else if (j > 0) {
                    int newValue = costs[j - 1];
                    if (left.charAt(i - 1) != right.charAt(j - 1)) {
                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    }
                    costs[j - 1] = lastValue;
                    lastValue = newValue;
                }
            }
            if (i > 0) costs[right.length()] = lastValue;
        }
        return costs[right.length()];
    }

    private static int safeLength(String text) {
        return text != null ? text.length() : 0;
    }



    private static String buildSearchResultGroupName(Context context, DanmakuItem item) {
        String from = item != null && !TextUtils.isEmpty(item.from) ? item.from.trim() : "默认";
        if (!shouldShowApiSourceLabel(context)) return from;
        return buildApiSourceLabel(context, item) + " · " + from;
    }

    private static String buildResultTitleWithSource(Context context, DanmakuItem item) {
        return item != null ? item.getTitleWithEp() : "";
    }

    private static boolean shouldShowApiSourceLabel(Context context) {
        try {
            DanmakuConfig config = DanmakuConfigManager.getConfig(context);
            return config != null && config.getEnabledApiSources().size() > 1;
        } catch (Exception e) {
            return false;
        }
    }

    private static String buildApiSourceLabel(Context context, DanmakuItem item) {
        if (item != null && !TextUtils.isEmpty(item.apiSourceName)) {
            return item.apiSourceName.trim();
        }
        return buildApiSourceLabel(context, item != null ? item.apiBase : null);
    }

    private static String buildApiSourceLabel(Context context, String apiBase) {
        String normalized = DanmakuApiSource.normalizeUrl(apiBase);
        if (TextUtils.isEmpty(normalized)) return "未知源";

        try {
            DanmakuConfig config = DanmakuConfigManager.getConfig(context);
            if (config != null) {
                List<DanmakuApiSource> sources = config.getApiSources();
                for (int i = 0; i < sources.size(); i++) {
                    DanmakuApiSource source = sources.get(i);
                    if (source != null && normalized.equals(DanmakuApiSource.normalizeUrl(source.url))) {
                        return source.getDisplayName("源" + (i + 1));
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return buildShortApiSourceLabel(normalized);
    }

    private static String buildShortApiSourceLabel(String url) {
        if (TextUtils.isEmpty(url)) return "未知源";
        String label = url;
        int schemeIndex = label.indexOf("://");
        if (schemeIndex >= 0) label = label.substring(schemeIndex + 3);
        int slashIndex = label.indexOf('/');
        if (slashIndex > 0) label = label.substring(0, slashIndex);
        return TextUtils.isEmpty(label) ? "未知源" : label;
    }

    private static void applyResultButtonStyle(Button button, boolean selected, boolean isTemplate3, boolean focused) {
        if (isTemplate3) {
            int fillColor = selected || focused ? DARK_TERTIARY_COLOR : DARK_BG_TERTIARY;
            int strokeColor = focused ? DARK_TERTIARY_DARK : (selected ? DARK_TERTIARY_COLOR : DARK_BORDER);
            int strokeWidth = focused || selected ? 4 : 2;
            button.setTextColor(DARK_TEXT_PRIMARY);
            button.setBackground(createRoundedStrokeBackgroundDrawable(fillColor, strokeColor, strokeWidth));
        } else {
            int fillColor = selected ? 0xFFEAF8EF : 0xFFF0F0F0;
            int strokeColor = focused ? TERTIARY_DARK : (selected ? TERTIARY_COLOR : 0xFFD6D6D6);
            int strokeWidth = focused || selected ? 4 : 2;
            button.setTextColor(selected ? 0xFF1F7A3A : TEXT_PRIMARY);
            button.setBackground(createRoundedStrokeBackgroundDrawable(fillColor, strokeColor, strokeWidth));
        }
    }

    private static void refreshResultButtonStyles(List<Button> buttons, String highlightedUrl, boolean isTemplate3) {
        if (buttons == null) return;
        // ====== 缓存三个状态的 Drawable，避免全量重建 ======
        android.graphics.drawable.Drawable selectedBg = createRoundedStrokeBackgroundDrawable(
                isTemplate3 ? DARK_TERTIARY_COLOR : 0xFFEAF8EF, TERTIARY_COLOR, 2);
        android.graphics.drawable.Drawable normalBg = createRoundedStrokeBackgroundDrawable(
                isTemplate3 ? DARK_BG_TERTIARY : 0xFFF0F0F0,
                isTemplate3 ? DARK_BORDER : 0xFFD6D6D6, 2);

        for (Button button : buttons) {
            if (button == null || !(button.getTag() instanceof DanmakuItem)) continue;
            DanmakuItem item = (DanmakuItem) button.getTag();
            String itemUrl = item.getDanmakuUrl();
            boolean selected = !TextUtils.isEmpty(highlightedUrl) && highlightedUrl.equals(itemUrl);
            if (selected) {
                button.setBackground(selectedBg);
                button.setTextColor(isTemplate3 ? DARK_TEXT_PRIMARY : 0xFF1F7A3A);
            } else {
                button.setBackground(normalBg);
                button.setTextColor(isTemplate3 ? DARK_TEXT_PRIMARY : TEXT_PRIMARY);
            }
        }
    }

    // 创建结果按钮的辅助方法 - 改进版本
    private static Button createResultButton(Activity activity,
                                             DanmakuItem item,
                                             AlertDialog dialog,
                                             boolean isTemplate3,
                                             String[] highlightedDanmakuUrl,
                                             List<Button> resultButtons) {
        Button resultItem = new Button(activity);
        resultItem.setFocusable(true);
        resultItem.setFocusableInTouchMode(true);
        resultItem.setClickable(true);
        resultItem.setText(buildResultTitleWithSource(activity, item));
        resultItem.setTextSize(13);
        resultItem.setPadding(dpToPx(activity, 14), dpToPx(activity, 10), dpToPx(activity, 14), dpToPx(activity, 10));
        resultItem.setGravity(Gravity.CENTER);

        String currentDanmakuUrl = item.getDanmakuUrl();
        String highlightedUrl = highlightedDanmakuUrl != null ? highlightedDanmakuUrl[0] : DanmakuManager.lastDanmakuUrl;
        boolean isSelected = currentDanmakuUrl != null && currentDanmakuUrl.equals(highlightedUrl);

        applyResultButtonStyle(resultItem, isSelected, isTemplate3, false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(activity, 6), 0, dpToPx(activity, 6));
        resultItem.setLayoutParams(params);

        resultItem.setTag(item);
        if (resultButtons != null) resultButtons.add(resultItem);

        resultItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                DanmakuItem item_tag = (DanmakuItem) v.getTag();
                String danmakuUrl = item_tag.getDanmakuUrl();
                String highlightedUrl = highlightedDanmakuUrl != null ? highlightedDanmakuUrl[0] : DanmakuManager.lastDanmakuUrl;
                boolean isCurrentlySelected = danmakuUrl != null && danmakuUrl.equals(highlightedUrl);

                if (hasFocus) {
                   sUserInteracted = true;   // ← 新增
                    applyResultButtonStyle((Button) v, isCurrentlySelected, isTemplate3, true);
                } else {
                    applyResultButtonStyle((Button) v, isCurrentlySelected, isTemplate3, false);
                }
            }
        });

        resultItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                DanmakuItem selected = (DanmakuItem) v1.getTag();
                if (highlightedDanmakuUrl != null) {
                    highlightedDanmakuUrl[0] = selected.getDanmakuUrl();
                    refreshResultButtonStyles(resultButtons, highlightedDanmakuUrl[0], isTemplate3);
                }
                // 记录弹幕URL
                DanmakuSpider.recordDanmakuUrl(selected, false);
                LeoDanmakuService.pushDanmakuDirect(selected, activity, false);
                dialog.dismiss();
            }
        });

        return resultItem;
    }

    // 创建网格布局结果按钮的辅助方法
    private static Button createGridResultButton(Activity activity, DanmakuItem item, AlertDialog dialog) {
        Button resultItem = new Button(activity);
        resultItem.setFocusable(true);
        resultItem.setFocusableInTouchMode(true);
        resultItem.setClickable(true);

        // 缩短文本显示，适合网格布局
        String displayText = DanmakuScanner.extractEpisodeNum(item.epTitle);
        if (TextUtils.isEmpty(displayText)) {
            // 安全地获取分割后的第二部分，避免数组越界
            String[] parts = item.epTitle != null ? item.epTitle.split(" ") : new String[0];
            if (parts.length > 1) {
                displayText = parts[1];
            } else if (parts.length > 0) {
                displayText = parts[0]; // 如果只有一个部分，使用第一部分
            } else {
                displayText = item.epTitle != null ? item.epTitle : "未知"; // 如果没有分割部分，使用原字符串或默认值
            }
        }

        resultItem.setText(displayText);
        resultItem.setTextSize(13); // 增大字号

        // 设置内边距
        int padding = dpToPx(activity, 10);
        resultItem.setPadding(padding, padding, padding, padding);

        // 设置单行显示，超出部分...省略
        resultItem.setSingleLine(true);
        resultItem.setEllipsize(TextUtils.TruncateAt.END);

        // 设置文本居中
        resultItem.setGravity(Gravity.CENTER);

        // 安全设置工具提示（完整标题）- 仅在 API 26+ 可用
        // 当按钮获得焦点或长按时，会显示完整的标题
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            resultItem.setTooltipText(buildResultTitleWithSource(activity, item));
        }

        // 设置圆角背景 - 第三层级：绿色
        String currentDanmakuUrl = item.getDanmakuUrl();
        if (currentDanmakuUrl != null && currentDanmakuUrl.equals(DanmakuManager.lastDanmakuUrl)) {
            // 高亮显示 - 使用绿色背景
            resultItem.setBackground(createRoundedBackgroundDrawable(TERTIARY_COLOR));
            resultItem.setTextColor(Color.WHITE);
        } else {
            // 普通显示
            resultItem.setBackground(createRoundedBackgroundDrawable(0xFFF0F0F0));
            resultItem.setTextColor(TEXT_PRIMARY);
        }

        resultItem.setTag(item);

        resultItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    sUserInteracted = true;
                    v.setBackground(createRoundedBackgroundDrawable(TERTIARY_DARK));
                    ((Button) v).setTextColor(Color.WHITE);
                } else {
                    DanmakuItem item_tag = (DanmakuItem) v.getTag();
                    String danmakuUrl = item_tag.getDanmakuUrl();
                    if (danmakuUrl != null && danmakuUrl.equals(DanmakuManager.lastDanmakuUrl)) {
                        v.setBackground(createRoundedBackgroundDrawable(TERTIARY_COLOR));
                        ((Button) v).setTextColor(Color.WHITE);
                    } else {
                        v.setBackground(createRoundedBackgroundDrawable(0xFFF0F0F0));
                        ((Button) v).setTextColor(TEXT_PRIMARY);
                    }
                }
            }
        });

        resultItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                DanmakuItem selected = (DanmakuItem) v1.getTag();
                // 记录弹幕URL
                DanmakuSpider.recordDanmakuUrl(selected, false);
                LeoDanmakuService.pushDanmakuDirect(selected, activity, false);
                dialog.dismiss();
            }
        });

        // 长按显示完整标题 - 这个功能在所有版本上都可用，作为低版本API的兼容
        resultItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DanmakuItem item = (DanmakuItem) v.getTag();
                Utils.safeShowToast(activity, buildResultTitleWithSource(activity, item),  true);
                return true;
            }
        });

        return resultItem;
    }

    public static void showQRCodeDialog(Activity activity, String url) {
        activity.runOnUiThread(() -> {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                LinearLayout mainLayout = new LinearLayout(activity);
                mainLayout.setOrientation(LinearLayout.VERTICAL);
                mainLayout.setGravity(Gravity.CENTER);
                mainLayout.setPadding(dpToPx(activity, 20), dpToPx(activity, 20), dpToPx(activity, 20), dpToPx(activity, 20));

                ImageView qrCodeView = new ImageView(activity);
                mainLayout.addView(qrCodeView);

                TextView urlView = new TextView(activity);
                urlView.setText(url);
                urlView.setGravity(Gravity.CENTER);
                urlView.setPadding(0, dpToPx(activity, 10), 0, 0);
                mainLayout.addView(urlView);

                // ====== 液态玻璃包装 ======
                boolean isLiquidQR = FORCE_LIQUID_GLASS;
                if (isLiquidQR) {
                    LiquidGlassView liquidRoot = new LiquidGlassView(activity);
                    liquidRoot.setBlurRadius(18f);
                    DanmakuConfig glassCfg = DanmakuConfigManager.getConfig(activity);
                    liquidRoot.setDarkMode(glassCfg.isDarkMode());
                    liquidRoot.setGlassAlpha(glassCfg.getLpLiquidAlpha());
                    liquidRoot.setCornerRadius(24f);
                    mainLayout.setBackgroundColor(Color.TRANSPARENT);
                    liquidRoot.addView(mainLayout, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    builder.setView(liquidRoot);
                } else {
                    builder.setView(mainLayout);
                }
                AlertDialog dialog = builder.create();
                if (isLiquidQR) {
                    applyRealBlur(dialog);
                    if (dialog.getWindow() != null)
                        dialog.getWindow().setBackgroundDrawable(
                                new android.graphics.drawable.ColorDrawable(0x00000000));
                }

                new Thread(() -> {
                    try {
                        String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=400x400&data=" + URLEncoder.encode(url, "UTF-8");
                        try (Response response = OkHttp.newCall(qrCodeUrl, "qrcode")) {
                            if (response.body() != null) {
                                InputStream in = response.body().byteStream();
                                Bitmap bitmap = BitmapFactory.decodeStream(in);
                                activity.runOnUiThread(() -> qrCodeView.setImageBitmap(bitmap));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

                safeShowDialog(activity, dialog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // 创建透明圆角背景
    private static android.graphics.drawable.Drawable createRoundedTransparentDrawable(int color) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(12);
        return drawable;
    }

    // 创建深色主题网格布局结果按钮
    private static Button createDarkGridResultButton(Activity activity, DanmakuItem item, AlertDialog dialog) {
        Button resultItem = new Button(activity);
        resultItem.setFocusable(true);
        resultItem.setFocusableInTouchMode(true);
        resultItem.setClickable(true);

        // 缩短文本显示，适合网格布局 (统一为模板二的逻辑)
        String displayText = DanmakuScanner.extractEpisodeNum(item.epTitle);
        if (TextUtils.isEmpty(displayText)) {
            // 安全地获取分割后的第二部分，避免数组越界
            String[] parts = item.epTitle != null ? item.epTitle.split(" ") : new String[0];
            if (parts.length > 1) {
                displayText = parts[1];
            } else if (parts.length > 0) {
                displayText = parts[0]; // 如果只有一个部分，使用第一部分
            } else {
                displayText = item.epTitle != null ? item.epTitle : "未知"; // 如果没有分割部分，使用原字符串或默认值
            }
        }

        resultItem.setText(displayText);
        resultItem.setTextSize(13); // 增大字号
        resultItem.setTextColor(DARK_TEXT_PRIMARY); // 白色文字

        // 设置内边距
        int padding = dpToPx(activity, 10);
        resultItem.setPadding(padding, padding, padding, padding);

        // 设置单行显示，超出部分...省略 (统一为模板二的逻辑)
        resultItem.setSingleLine(true);
        resultItem.setEllipsize(TextUtils.TruncateAt.END);

        // 设置文本居中
        resultItem.setGravity(Gravity.CENTER);

        // 安全设置工具提示（完整标题）- 仅在 API 26+ 可用
        // 添加版本检查避免在低版本上崩溃
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            resultItem.setTooltipText(buildResultTitleWithSource(activity, item));
        }

        // 设置圆角背景 - 第三层级：绿色
        String currentDanmakuUrl = item.getDanmakuUrl();
        if (currentDanmakuUrl != null && currentDanmakuUrl.equals(DanmakuManager.lastDanmakuUrl)) {
            // 高亮显示 - 使用绿色半透明背景
            resultItem.setBackground(createRoundedTransparentDrawable(DARK_TERTIARY_COLOR));
            resultItem.setTextColor(DARK_TEXT_PRIMARY);
        } else {
            // 普通显示 - 深灰半透明背景
            resultItem.setBackground(createRoundedTransparentDrawable(DARK_BG_TERTIARY));
            resultItem.setTextColor(DARK_TEXT_PRIMARY);
        }

        resultItem.setTag(item);

        resultItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    sUserInteracted = true;
                    v.setBackground(createRoundedTransparentDrawable(DARK_TERTIARY_DARK));
                    ((Button) v).setTextColor(DARK_TEXT_PRIMARY);
                } else {
                    DanmakuItem item_tag = (DanmakuItem) v.getTag();
                    String danmakuUrl = item_tag.getDanmakuUrl();
                    if (danmakuUrl != null && danmakuUrl.equals(DanmakuManager.lastDanmakuUrl)) {
                        v.setBackground(createRoundedTransparentDrawable(DARK_TERTIARY_COLOR));
                        ((Button) v).setTextColor(DARK_TEXT_PRIMARY);
                    } else {
                        v.setBackground(createRoundedTransparentDrawable(DARK_BG_TERTIARY));
                        ((Button) v).setTextColor(DARK_TEXT_PRIMARY);
                    }
                }
            }
        });

        resultItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                DanmakuItem selected = (DanmakuItem) v1.getTag();
                // 记录弹幕URL
                DanmakuSpider.recordDanmakuUrl(selected, false);
                LeoDanmakuService.pushDanmakuDirect(selected, activity, false);
                dialog.dismiss();
            }
        });

        // 长按显示完整标题 - 这个功能在所有版本上都可用
        resultItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DanmakuItem item = (DanmakuItem) v.getTag();
                Utils.safeShowToast(activity, buildResultTitleWithSource(activity, item),  true);
                return true;
            }
        });

        return resultItem;
    }

    // 创建深色主题实心按钮（焦点橙色）
    // 性能优化：Drawable 预创建，焦点切换零分配
    private static Button createDarkSolidButton(Activity activity, String text, int backgroundColor) {
        Button button = new Button(activity);
        button.setText(text);
        button.setTextColor(DARK_TEXT_PRIMARY);
        button.setTextSize(14);
        button.setTypeface(null, android.graphics.Typeface.BOLD);
        button.setFocusable(true);
        button.setFocusableInTouchMode(false);

        // ====== 预创建两个状态的 Drawable ======
        android.graphics.drawable.Drawable normalBg = createRoundedTransparentDrawable(backgroundColor);

        android.graphics.drawable.GradientDrawable focusBgDrawable = new android.graphics.drawable.GradientDrawable();
        focusBgDrawable.setCornerRadius(12);
        focusBgDrawable.setColor(darkenColor(backgroundColor, 0.3f));
        focusBgDrawable.setStroke(4, 0xFFFF6B00);

        button.setBackground(normalBg);

        button.setOnFocusChangeListener((v, hasFocus) -> {
            v.setBackground(hasFocus ? focusBgDrawable : normalBg);
            ((Button) v).setTextColor(DARK_TEXT_PRIMARY);
            // 去掉 setScaleX/setScaleY
        });

        return button;
    }
    
    /**
     * 多格模式专用按钮：一行3个，文字可换行，显示完整标题
     */
    private static Button createMultiGridResultButton(Activity activity, DanmakuItem item, AlertDialog dialog) {
        // ====== 性能/焦点优化：创建时读一次配置，焦点监听里不再读 ======
        final boolean isDarkCached = DanmakuConfigManager.getConfig(activity).isDarkUI();
        
        Button resultItem = new Button(activity);
        resultItem.setFocusable(true);
        resultItem.setFocusableInTouchMode(true);
        resultItem.setClickable(true);

        // ====== 提取来源+集数 ======
        String fullTitle = buildResultTitleWithSource(activity, item);
        String displayText = fullTitle;
        int sepIdx = fullTitle.lastIndexOf("－");
        if (sepIdx < 0) sepIdx = fullTitle.lastIndexOf("-");
        if (sepIdx >= 0 && sepIdx + 1 < fullTitle.length()) {
            displayText = fullTitle.substring(sepIdx + 1).trim();
        }
        // 去掉可能残留的开头横线和空格
        while (displayText.startsWith("-") || displayText.startsWith("－") || displayText.startsWith(" ")) {
            displayText = displayText.substring(1).trim();
        }
        resultItem.setText(displayText);
        resultItem.setTextSize(12);
        resultItem.setSingleLine(false);            // 允许多行换行
        resultItem.setMaxLines(3);                  // 最多3行
        resultItem.setMinHeight(dpToPx(activity, 44));   // ← 所有格子等高，焦点几何关系恒定
        resultItem.setEllipsize(TextUtils.TruncateAt.END);
        resultItem.setGravity(Gravity.CENTER);      // 居中显示

        // ====== 关键改动：缩小内边距 ======
        int paddingH = dpToPx(activity, 6);
        int paddingV = dpToPx(activity, 6);
        resultItem.setPadding(paddingH, paddingV, paddingH, paddingV);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resultItem.setTooltipText(buildResultTitleWithSource(activity, item));
        }

        // 样式：高亮/普通
        String currentDanmakuUrl = item.getDanmakuUrl();
        boolean isHighlighted = currentDanmakuUrl != null
                && currentDanmakuUrl.equals(DanmakuManager.lastDanmakuUrl);
        applyMultiGridResultStyle(resultItem, isDarkCached, isHighlighted, false);   // ← 加参数

        resultItem.setTag(item);

        // 焦点效果
        resultItem.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) sUserInteracted = true;   // ← 加这行
            DanmakuItem tagged = (DanmakuItem) v.getTag();
            boolean highlight = tagged.getDanmakuUrl() != null
                    && tagged.getDanmakuUrl().equals(DanmakuManager.lastDanmakuUrl);
            applyMultiGridResultStyle((Button) v, isDarkCached, highlight, hasFocus);
            // 去掉 setScaleX/setScaleY，避免缩放引起行高/位置变化
        });

        // 点击
        resultItem.setOnClickListener(v -> {
            DanmakuItem selected = (DanmakuItem) v.getTag();
            DanmakuSpider.recordDanmakuUrl(selected, false);
            LeoDanmakuService.pushDanmakuDirect(selected, activity, false);
            dialog.dismiss();
        });

        // 长按显示完整标题
        resultItem.setOnLongClickListener(v -> {
            DanmakuItem tagged = (DanmakuItem) v.getTag();
            Utils.safeShowToast(activity, buildResultTitleWithSource(activity, tagged), true);
            return true;
        });

        return resultItem;
    }
    
    /**
     * 多格模式按钮样式 - 支持深色模式
     * 焦点橙色，推送中绿色，普通灰色
     */
    private static void applyMultiGridResultStyle(Button button, boolean isDark, boolean highlighted, boolean focused) {
        if (isDark) {
            if (focused) {
                button.setBackground(createRoundedStrokeBackgroundDrawable(0x44FFE0B2, 0xFFFF6B00, 3));
                button.setTextColor(0xFFFFB74D);
            } else if (highlighted) {
                button.setBackground(createRoundedStrokeBackgroundDrawable(0x4434C759, 0xFF34C759, 3));
                button.setTextColor(0xFF81C784);
            } else {
                button.setBackground(createRoundedStrokeBackgroundDrawable(0x33FFFFFF, 0x44FFFFFF, 2));
                button.setTextColor(0xFFCCCCCC);
            }
        } else {
            if (focused) {
                button.setBackground(createRoundedStrokeBackgroundDrawable(0xFFE8F5E9, TERTIARY_DARK, 3));
                button.setTextColor(TERTIARY_DARK);
            } else if (highlighted) {
                button.setBackground(createRoundedStrokeBackgroundDrawable(0xFFE8F5E9, TERTIARY_COLOR, 2));
                button.setTextColor(0xFF1F7A3A);
            } else {
                button.setBackground(createRoundedStrokeBackgroundDrawable(0xFFF0F0F0, 0xFFD6D6D6, 1));
                button.setTextColor(TEXT_PRIMARY);
            }
        }
    }


    // ========== 液态模式专用（真液态玻璃）==========

    /**
     * 开启窗口级真实背景模糊（Android 12+）
     * 这是唯一能真正模糊视频画面的方式
     */
    private static void applyRealBlur(AlertDialog dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+：真实背景模糊
            try {
                android.view.Window window = dialog.getWindow();
                if (window != null) {
                    window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    window.setDimAmount(0.15f);
                    // 延迟到 DecorView 就绪后再设置模糊
                    window.getDecorView().post(() -> {
                        try {
                            android.view.Window w = dialog.getWindow();
                            if (w != null && w.getDecorView() != null) {
                                w.setBackgroundBlurRadius(28); //模糊半径
                                w.getAttributes().setBlurBehindRadius(28);
                                w.addFlags(android.view.WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                            }
                        } catch (Throwable t) {
                            DanmakuSpider.log("延迟背景模糊失败: " + t.getMessage());
                        }
                    });
                }
            } catch (Throwable t) {
                DanmakuSpider.log("背景模糊不支持: " + t.getMessage());
            }
        } else {
            // Android 12 以下：至少让窗口半透明
            try {
                android.view.Window window = dialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(0x00000000));
                    window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    window.setDimAmount(0.3f);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建液态玻璃集数按钮（真液态版）
     */
    private static Button createLiquidGridResultButton(Activity activity, DanmakuItem item, AlertDialog dialog) {
        // ====== 性能/焦点优化：创建时读一次 ======
        final boolean isDarkCached = DanmakuConfigManager.getConfig(activity).isDarkMode();
        
        Button resultItem = new Button(activity);
        resultItem.setFocusable(true);
        resultItem.setFocusableInTouchMode(true);
        resultItem.setClickable(true);

        // 提取来源+集数
        String fullTitle = buildResultTitleWithSource(activity, item);
        String displayText = fullTitle;
        int sepIdx = fullTitle.lastIndexOf("－");
        if (sepIdx < 0) sepIdx = fullTitle.lastIndexOf("-");
        if (sepIdx >= 0 && sepIdx + 1 < fullTitle.length()) {
            displayText = fullTitle.substring(sepIdx + 1).trim();
        }
        while (displayText.startsWith("-") || displayText.startsWith("－") || displayText.startsWith(" ")) {
            displayText = displayText.substring(1).trim();
        }

        resultItem.setText(displayText);
        resultItem.setTextSize(12);
        resultItem.setSingleLine(false);
        resultItem.setMaxLines(3);
        resultItem.setMinHeight(dpToPx(activity, 44));   // ← 所有格子等高，焦点几何关系恒定
        resultItem.setEllipsize(TextUtils.TruncateAt.END);
        resultItem.setGravity(Gravity.CENTER);

        int padding = dpToPx(activity, 6);
        resultItem.setPadding(padding, padding, padding, padding);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resultItem.setTooltipText(fullTitle);
        }

        String currentDanmakuUrl = item.getDanmakuUrl();
        boolean isHighlighted = currentDanmakuUrl != null
                && currentDanmakuUrl.equals(DanmakuManager.lastDanmakuUrl);
        applyLiquidResultStyle(activity, isDarkCached, resultItem, isHighlighted, false);

        resultItem.setTag(item);

        resultItem.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) sUserInteracted = true;   // ← 加这行
            DanmakuItem tagged = (DanmakuItem) v.getTag();
            boolean highlight = tagged.getDanmakuUrl() != null
                    && tagged.getDanmakuUrl().equals(DanmakuManager.lastDanmakuUrl);
            applyLiquidResultStyle(activity, isDarkCached, (Button) v, highlight, hasFocus);
            // 去掉 setScaleX/setScaleY
        });

        resultItem.setOnClickListener(v -> {
            DanmakuItem selected = (DanmakuItem) v.getTag();
            DanmakuSpider.recordDanmakuUrl(selected, false);
            LeoDanmakuService.pushDanmakuDirect(selected, activity, false);
            dialog.dismiss();
        });

        resultItem.setOnLongClickListener(v -> {
            DanmakuItem tagged = (DanmakuItem) v.getTag();
            Utils.safeShowToast(activity, buildResultTitleWithSource(activity, tagged), true);
            return true;
        });

        return resultItem;
    }

    /**
     * 真液态按钮样式：玻璃质感 + 边缘高光 + 折射渐变
     */
    /**
     * 液态模式按钮样式：支持深浅模式
     */
    private static void applyLiquidResultStyle(Activity activity, boolean isDark, Button button, boolean highlighted, boolean focused) {
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setCornerRadius(dpToPx(activity, 12));

        if (isDark) {
            if (focused) {
                bg.setColors(new int[]{0xCC4E342E, 0x99FF9800, 0xCC4E342E});
                bg.setStroke(dpToPx(activity, 3), 0xFFFF6B00);
                button.setTextColor(0xFFFFB74D);
            } else if (highlighted) {
                bg.setColors(new int[]{0xB31B3D2E, 0x8834C759, 0xB31B3D2E});
                bg.setStroke(dpToPx(activity, 3), 0xFF34C759);
                button.setTextColor(0xFF81C784);
            } else {
                bg.setColors(new int[]{0x55303040, 0x33303040, 0x55303040});
                bg.setStroke(dpToPx(activity, 2), 0x66FFFFFF);
                button.setTextColor(0xDDCCCCCC);
            }
        } else {
            if (focused) {
                bg.setColors(new int[]{0xCCFFE0B2, 0x99FF9800, 0xCCFFE0B2});
                bg.setStroke(dpToPx(activity, 3), 0xFFFF6B00);
                button.setTextColor(0xFFE65100);
            } else if (highlighted) {
                bg.setColors(new int[]{0xB3C8E6C9, 0x8866BB6A, 0xB3C8E6C9});
                bg.setStroke(dpToPx(activity, 3), 0xFF34C759);
                button.setTextColor(0xFF1B5E20);
            } else {
                bg.setColors(new int[]{0x77FFFFFF, 0x55FFFFFF, 0x77FFFFFF});
                bg.setStroke(dpToPx(activity, 2), 0x99AAAAAA);
                button.setTextColor(0xDD222222);
            }
        }

        button.setBackground(bg);
    }
    
    /**
     * 液态玻璃输入框背景
     * 性能优化：接受 isDark 参数，不再内部读配置
     */
    private static android.graphics.drawable.Drawable createLiquidInputDrawable(boolean isDark, boolean focused) {
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setCornerRadius(10 * 2.75f);   // ≈ dpToPx(10)，静态方法无法用 ctx
        if (isDark) {
            if (focused) {
                bg.setColors(new int[]{0xCC1A237E, 0x883F51B5, 0xCC1A237E});
                bg.setStroke(5, 0xFF00E5FF);
            } else {
                bg.setColors(new int[]{0x66303040, 0x44303040, 0x66303040});
                bg.setStroke(5, 0x66FFFFFF);
            }
        } else {
            if (focused) {
                bg.setColors(new int[]{0xCCBBDEFB, 0x8890CAF9, 0xCCBBDEFB});
                bg.setStroke(5, 0xFF2196F3);
            } else {
                bg.setColors(new int[]{0x66FFFFFF, 0x44FFFFFF, 0x66FFFFFF});
                bg.setStroke(5, 0x99BBBBBB);
            }
        }
        return bg;
    }

    /**
     * 为输入框添加电视端焦点效果（性能优化版：配置只读一次，Drawable 预创建）
     */
    private static void applyTVInputFocusEffect(EditText editText, boolean isDarkTheme) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);

        android.graphics.drawable.Drawable normalBg = createTVInputDrawable(false, isDarkTheme);
        android.graphics.drawable.Drawable focusBg = createTVInputDrawable(true, isDarkTheme);
        editText.setBackground(normalBg);

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            ((EditText) v).setBackground(hasFocus ? focusBg : normalBg);
            if (hasFocus) {
                ((EditText) v).setTextColor(isDarkTheme ? DARK_TEXT_PRIMARY : TEXT_PRIMARY);
            }
        });
    }

    /**
     * 液态玻璃按钮（用于搜索、反序等）
     * 性能优化：配置只读一次，Drawable 预创建，焦点切换零分配
     */
    private static Button createLiquidGlassButton(Activity activity, String text, int tintColor) {
        boolean isDark = DanmakuConfigManager.getConfig(activity).isDarkMode();   // ← 只读一次
        Button button = new Button(activity);
        button.setText(text);
        button.setTextSize(14);
        button.setTypeface(null, android.graphics.Typeface.BOLD);
        button.setFocusable(true);
        button.setFocusableInTouchMode(false);

        // ====== 预创建两个状态的 Drawable ======
        android.graphics.drawable.GradientDrawable normalBg = new android.graphics.drawable.GradientDrawable();
        normalBg.setCornerRadius(dpToPx(activity, 12));
        normalBg.setColors(new int[]{
            (tintColor & 0x00FFFFFF) | 0xCC000000,
            (tintColor & 0x00FFFFFF) | 0x88000000,
            (tintColor & 0x00FFFFFF) | 0xCC000000
        });
        normalBg.setStroke(dpToPx(activity, 2), isDark ? 0x44FFFFFF : 0x99FFFFFF);

        android.graphics.drawable.GradientDrawable focusBg = new android.graphics.drawable.GradientDrawable();
        focusBg.setCornerRadius(dpToPx(activity, 12));
        focusBg.setColors(new int[]{0x33FFFFFF, 0x22FFFFFF, 0x33FFFFFF});
        focusBg.setStroke(dpToPx(activity, 3), 0xFFFF6B00);

        button.setBackground(normalBg);
        button.setTextColor(Color.WHITE);

        button.setOnFocusChangeListener((v, hasFocus) -> {
            v.setBackground(hasFocus ? focusBg : normalBg);
            ((Button) v).setTextColor(hasFocus ? 0xFFE65100 : Color.WHITE);
            // 去掉 setScaleX/setScaleY，避免缩放引起重绘
        });

        return button;
    }

    /**
     * 液态玻璃通用 Drawable（有边框）
     */
    private static android.graphics.drawable.Drawable createLiquidGlassDrawable(Context ctx, int strokeColor, int strokeWidth) {
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setCornerRadius(dpToPx(ctx, 10));
        bg.setColors(new int[]{0x66FFFFFF, 0x44FFFFFF, 0x66FFFFFF});
        bg.setStroke(dpToPx(ctx, strokeWidth), strokeColor | 0xFF000000);
        return bg;
    }
    
    /**
     * 显示带液态玻璃效果的通用菜单对话框
     * 非液态模式时使用系统默认菜单
     */
    public static void showStyledMenuDialog(Activity activity, String title,
                                            String[] options,
                                            DialogInterface.OnClickListener listener) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;

        DanmakuConfig config = DanmakuConfigManager.getConfig(activity);
        boolean isLiquid = FORCE_LIQUID_GLASS;

        // ====== 非液态模式：使用系统默认菜单 ======
        if (!isLiquid) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setItems(options, listener);
            builder.setNegativeButton("取消", null);
            try {
                builder.show();
            } catch (Exception e) {
                DanmakuSpider.log("显示菜单失败: " + e.getMessage());
            }
            return;
        }

        // ====== 液态模式：自定义玻璃菜单 ======
        try {
            LinearLayout mainLayout = new LinearLayout(activity);
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            mainLayout.setBackgroundColor(Color.TRANSPARENT);
            mainLayout.setPadding(dpToPx(activity, 12), dpToPx(activity, 12),
                    dpToPx(activity, 12), dpToPx(activity, 12));

            // 标题
            TextView titleView = new TextView(activity);
            titleView.setText(title);
            titleView.setTextSize(16);
            titleView.setTextColor(PRIMARY_COLOR);
            titleView.setGravity(Gravity.CENTER);
            titleView.setTypeface(null, android.graphics.Typeface.BOLD);
            titleView.setPadding(0, 0, 0, dpToPx(activity, 10));
            mainLayout.addView(titleView);

            // ====== 改动1：GridLayout 一行两个 ======
            ScrollView scrollView = new ScrollView(activity);
            scrollView.setBackgroundColor(Color.TRANSPARENT);
            GridLayout gridLayout = new GridLayout(activity);
            gridLayout.setColumnCount(2);
            gridLayout.setUseDefaultMargins(false);
            gridLayout.setPadding(0, 0, 0, 0);

            for (int i = 0; i < options.length; i++) {
                Button optionBtn = createLiquidMenuButton(activity, options[i]);

                // ====== 改动2：GridLayout 一行两个的布局参数 ======
                GridLayout.LayoutParams btnParams = new GridLayout.LayoutParams();
                btnParams.height = dpToPx(activity, 44);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    btnParams.width = 0;
                    btnParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                    btnParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                } else {
                    btnParams.width = dpToPx(activity, 160);
                }
                int margin = dpToPx(activity, 4);
                btnParams.setMargins(margin, margin, margin, margin);
                optionBtn.setLayoutParams(btnParams);
                gridLayout.addView(optionBtn);
            }

            scrollView.addView(gridLayout);
            mainLayout.addView(scrollView, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

            // 取消按钮 - 焦点橙色空心边框，文字居中（性能优化：Drawable 预创建）
            Button cancelBtn = new Button(activity);
            cancelBtn.setText("取消");
            cancelBtn.setTextSize(12);
            cancelBtn.setTypeface(null, android.graphics.Typeface.BOLD);
            cancelBtn.setFocusable(true);
            cancelBtn.setFocusableInTouchMode(true);
            cancelBtn.setGravity(Gravity.CENTER);
            cancelBtn.setPadding(0, 0, 0, 0);

            android.graphics.drawable.GradientDrawable cancelNormalBg = new android.graphics.drawable.GradientDrawable();
            cancelNormalBg.setCornerRadius(dpToPx(activity, 10));
            cancelNormalBg.setColors(new int[]{0x66FFFFFF, 0x44FFFFFF, 0x66FFFFFF});
            cancelNormalBg.setStroke(dpToPx(activity, 2), 0x99AAAAAA);

            android.graphics.drawable.GradientDrawable cancelFocusBg = new android.graphics.drawable.GradientDrawable();
            cancelFocusBg.setCornerRadius(dpToPx(activity, 10));
            cancelFocusBg.setColors(new int[]{0x33FFFFFF, 0x22FFFFFF, 0x33FFFFFF});
            cancelFocusBg.setStroke(dpToPx(activity, 3), 0xFFFF6B00);

            cancelBtn.setBackground(cancelNormalBg);
            cancelBtn.setTextColor(0xDD222222);

            cancelBtn.setOnFocusChangeListener((v, hasFocus) -> {
                v.setBackground(hasFocus ? cancelFocusBg : cancelNormalBg);
                ((Button) v).setTextColor(hasFocus ? 0xFFE65100 : 0xDD222222);
            });

            LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(activity, 44));
            cancelParams.setMargins(dpToPx(activity, 4), dpToPx(activity, 8),
                    dpToPx(activity, 4), 0);
            cancelBtn.setLayoutParams(cancelParams);
            mainLayout.addView(cancelBtn);

            // 液态玻璃容器
            LiquidGlassView liquidRoot = new LiquidGlassView(activity);
            liquidRoot.setBlurRadius(18f);
            DanmakuConfig glassCfg = DanmakuConfigManager.getConfig(activity);
            liquidRoot.setDarkMode(config.isDarkMode());  // ← 加上
            liquidRoot.setGlassAlpha(config.getLpLiquidAlpha());
            liquidRoot.setCornerRadius(24f);
            liquidRoot.addView(mainLayout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(liquidRoot);
            final AlertDialog dialog = builder.create();

            // 设置点击事件
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                final int index = i;
                gridLayout.getChildAt(i).setOnClickListener(v -> {
                    dialog.dismiss();
                    if (listener != null) listener.onClick(dialog, index);
                });
            }
            cancelBtn.setOnClickListener(v -> dialog.dismiss());

            // 显示对话框
            safeShowDialog(activity, dialog);
            applyRealBlur(dialog);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(
                        new android.graphics.drawable.ColorDrawable(0x00000000));
            }

            // 设置窗口大小
            android.view.WindowManager.LayoutParams lp = new android.view.WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.75f);
            lp.height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.80f);
            dialog.getWindow().setAttributes(lp);

        } catch (Exception e) {
            DanmakuSpider.log("显示液态菜单失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 液态菜单专用按钮：橙色空心边框（不填充），保持圆角
     * 性能优化：Drawable 预创建，焦点切换零分配
     */
    private static Button createLiquidMenuButton(Activity activity, String text) {
        Button button = new Button(activity);
        button.setText(text);
        button.setTextSize(11);
        button.setTypeface(null, android.graphics.Typeface.BOLD);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        button.setPadding(dpToPx(activity, 10), 0, dpToPx(activity, 10), 0);

        // ====== 预创建两个状态的 Drawable ======
        android.graphics.drawable.GradientDrawable normalBg = new android.graphics.drawable.GradientDrawable();
        normalBg.setCornerRadius(dpToPx(activity, 10));
        normalBg.setColors(new int[]{0x66FFFFFF, 0x44FFFFFF, 0x66FFFFFF});
        normalBg.setStroke(dpToPx(activity, 2), 0x99AAAAAA);

        android.graphics.drawable.GradientDrawable focusBg = new android.graphics.drawable.GradientDrawable();
        focusBg.setCornerRadius(dpToPx(activity, 10));
        focusBg.setColors(new int[]{0x33FFFFFF, 0x22FFFFFF, 0x33FFFFFF});
        focusBg.setStroke(dpToPx(activity, 3), 0xFFFF6B00);

        button.setBackground(normalBg);
        button.setTextColor(0xDD222222);

        button.setOnFocusChangeListener((v, hasFocus) -> {
            v.setBackground(hasFocus ? focusBg : normalBg);
            ((Button) v).setTextColor(hasFocus ? 0xFFE65100 : 0xDD222222);
            // 去掉 setScaleX/setScaleY
        });

        return button;
    }
    
    // ====================== ListView 版：一行多列 + 视图回收 ======================

    private static class ResultRow {
        static final int TYPE_HEADER = 0;
        static final int TYPE_GRID_ROW = 1;
        static final int TYPE_MESSAGE = 2;

        final int type;
        final String groupTitle;
        final List<DanmakuItem> groupItems;
        boolean expanded;
        final List<DanmakuItem> rowItems;
        final String message;

        private ResultRow(int type, String groupTitle, List<DanmakuItem> groupItems,
                          boolean expanded, List<DanmakuItem> rowItems, String message) {
            this.type = type;
            this.groupTitle = groupTitle;
            this.groupItems = groupItems;
            this.expanded = expanded;
            this.rowItems = rowItems;
            this.message = message;
        }

        static ResultRow header(String title, List<DanmakuItem> items, boolean expanded) {
            return new ResultRow(TYPE_HEADER, title, items, expanded, null, null);
        }

        static ResultRow gridRow(List<DanmakuItem> items) {
            return new ResultRow(TYPE_GRID_ROW, null, null, false, items, null);
        }

        static ResultRow message(String msg) {
            return new ResultRow(TYPE_MESSAGE, null, null, false, null, msg);
        }
    }

    private static class ResultListAdapter extends android.widget.BaseAdapter {
        private final Activity activity;
        private final List<ResultRow> rows = new ArrayList<>();
        private AlertDialog dialog;
        private int gridColumns = 1;
        private boolean isTemplate3, isMultiGrid, isLiquid, isGrid;

        ResultListAdapter(Activity activity) {
            this.activity = activity;
        }

        void bindDialog(AlertDialog d) {
            this.dialog = d;
        }

        void setGridColumns(int cols) {
            this.gridColumns = Math.max(1, cols);
        }

        void showMessage(String msg) {
            rows.clear();
            rows.add(ResultRow.message(msg));
            notifyDataSetChanged();
        }

        void submit(java.util.Map<String, List<DanmakuItem>> orderedGroups,
                    java.util.Set<String> autoExpandKeys) {
            DanmakuConfig cfg = DanmakuConfigManager.getConfig(activity);
            isTemplate3 = cfg.isDarkUI();
            isMultiGrid = cfg.isMultiGridDanmakuStyle();
            isLiquid = cfg.isLiquidDanmakuStyle();
            isGrid = cfg.isGridDanmakuStyle() || isMultiGrid || isLiquid;

            rows.clear();
            for (java.util.Map.Entry<String, List<DanmakuItem>> e : orderedGroups.entrySet()) {
                String title = e.getKey();
                List<DanmakuItem> groupItems = e.getValue();
                boolean expanded = autoExpandKeys != null && autoExpandKeys.contains(title);
                if (!isGrid && groupItems.size() == 1) {
                    rows.add(ResultRow.gridRow(new ArrayList<>(groupItems)));
                    continue;
                }
                rows.add(ResultRow.header(title, groupItems, expanded));
                if (expanded) {
                    addGridRows(groupItems);
                }
            }
            notifyDataSetChanged();
        }

        private void addGridRows(List<DanmakuItem> items) {
            for (int i = 0; i < items.size(); i += gridColumns) {
                rows.add(ResultRow.gridRow(new ArrayList<>(items.subList(
                        i, Math.min(i + gridColumns, items.size())))));
            }
        }

        int findPositionByUrl(String url) {
            if (TextUtils.isEmpty(url)) return -1;
            for (int i = 0; i < rows.size(); i++) {
                ResultRow r = rows.get(i);
                if (r.type == ResultRow.TYPE_GRID_ROW && r.rowItems != null) {
                    for (DanmakuItem item : r.rowItems) {
                        if (url.equals(item.getDanmakuUrl())) return i;
                    }
                }
            }
            for (int i = 0; i < rows.size(); i++) {
                ResultRow r = rows.get(i);
                if (r.type == ResultRow.TYPE_HEADER) {
                    for (DanmakuItem item : r.groupItems) {
                        if (url.equals(item.getDanmakuUrl())) return i;
                    }
                }
            }
            return -1;
        }

        @Override public int getCount() { return rows.size(); }
        @Override public Object getItem(int position) { return rows.get(position); }
        @Override public long getItemId(int position) { return position; }
        @Override public int getItemViewType(int position) { return rows.get(position).type; }
        @Override public int getViewTypeCount() { return 3; }
        @Override public boolean areAllItemsEnabled() { return false; }
        @Override public boolean isEnabled(int position) {
            return rows.get(position).type == ResultRow.TYPE_HEADER;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ResultRow row = rows.get(position);
            if (row.type == ResultRow.TYPE_MESSAGE) return getMessageView(row, convertView);
            if (row.type == ResultRow.TYPE_HEADER) return getHeaderView(row, convertView);
            return getGridRowView(row, convertView);
        }

        private View getMessageView(ResultRow row, View convertView) {
            TextView tv = (convertView instanceof TextView) ? (TextView) convertView : new TextView(activity);
            tv.setText(row.message);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0, dpToPx(activity, 12), 0, dpToPx(activity, 12));
            tv.setTextColor(isTemplate3 ? DARK_TEXT_PRIMARY : TEXT_PRIMARY);
            return tv;
        }

        private View getHeaderView(ResultRow row, View convertView) {
            LinearLayout wrapper;
            Button header;
            if (convertView instanceof LinearLayout
                    && ((LinearLayout) convertView).getChildCount() == 1
                    && ((LinearLayout) convertView).getChildAt(0) instanceof Button) {
                wrapper = (LinearLayout) convertView;
                header = (Button) wrapper.getChildAt(0);
            } else {
                wrapper = new LinearLayout(activity);
                wrapper.setOrientation(LinearLayout.VERTICAL);
                wrapper.setFocusable(false);
                header = new Button(activity);
                header.setGravity(Gravity.CENTER);
                header.setTextSize(14);
                header.setTypeface(null, android.graphics.Typeface.BOLD);
                header.setAllCaps(false);
                header.setFocusable(true);
                header.setClickable(true);
                wrapper.addView(header, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            header.setText(buildGroupHeaderTitle(row.groupTitle, row.groupItems.size(), row.expanded));
            applyGroupHeaderStyle(header, row.expanded, isTemplate3, isLiquid, false);
            header.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) sUserInteracted = true;
                applyGroupHeaderStyle((Button) v, row.expanded, isTemplate3, isLiquid, hasFocus);
            });
            header.setOnClickListener(v -> toggleGroup(row));
            return wrapper;
        }

        private View getGridRowView(ResultRow row, View convertView) {
            // 不复用 convertView：每次新建，避免 removeAllViews 干扰焦点搜索
            LinearLayout rowLayout = new LinearLayout(activity);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setFocusable(false);
            rowLayout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

            for (DanmakuItem item : row.rowItems) {
                Button btn = createEpisodeButton(item);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                int m = dpToPx(activity, 4);
                lp.setMargins(m, m, m, m);
                btn.setLayoutParams(lp);
                rowLayout.addView(btn);
            }
            return rowLayout;
        }

        private Button createEpisodeButton(DanmakuItem item) {
            if (isLiquid) return createLiquidGridResultButton(activity, item, dialog);
            if (isMultiGrid) return createMultiGridResultButton(activity, item, dialog);
            if (isTemplate3 && isGrid) return createDarkGridResultButton(activity, item, dialog);
            if (isGrid) return createGridResultButton(activity, item, dialog);
            return createResultButton(activity, item, dialog, isTemplate3, null, null);
        }
        
        private void toggleGroup(ResultRow row) {
            int idx = rows.indexOf(row);
            if (idx < 0) return;
            row.expanded = !row.expanded;
            if (row.expanded) {
                List<ResultRow> add = new ArrayList<>();
                for (int i = 0; i < row.groupItems.size(); i += gridColumns) {
                    add.add(ResultRow.gridRow(new ArrayList<>(row.groupItems.subList(
                            i, Math.min(i + gridColumns, row.groupItems.size())))));
                }
                rows.addAll(idx + 1, add);
            } else {
                int removeCount = 0;
                for (int i = idx + 1; i < rows.size(); i++) {
                    if (rows.get(i).type == ResultRow.TYPE_GRID_ROW) removeCount++;
                    else break;
                }
                if (removeCount > 0) rows.subList(idx + 1, idx + 1 + removeCount).clear();
            }
            notifyDataSetChanged();
        }
    }
    
    // 颜色加深辅助方法
    private static int darkenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * (1 - factor));
        int g = (int) (((color >> 8) & 0xFF) * (1 - factor));
        int b = (int) ((color & 0xFF) * (1 - factor));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
