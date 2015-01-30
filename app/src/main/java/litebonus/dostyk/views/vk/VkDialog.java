package litebonus.dostyk.views.vk;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.internal.Utility;

/**
 * Created by prog on 07.11.14.
 */
public class VkDialog extends Dialog {

    private static final int NO_PADDING_SCREEN_WIDTH = 480;
    private static final int MAX_PADDING_SCREEN_WIDTH = 800;
    private static final int NO_PADDING_SCREEN_HEIGHT = 800;
    private static final int MAX_PADDING_SCREEN_HEIGHT = 1280;
    private static final double MIN_SCALE_FACTOR = 0.5;
    private static final int BACKGROUND_GRAY = 0xCC000000;

    public static final int DEFAULT_THEME = android.R.style.Theme_Translucent_NoTitleBar;

    private ImageView crossImageView;
    private FrameLayout contentFrameLayout;
    private WebView webView;
    private boolean isDetached = false;
    private ProgressDialog spinner;
    private String url;
    private OnSuccesListener listener;
    public VkDialog(Context context) {
        super(context);
    }

    public VkDialog(Context context, String url) {
        super(context);
        this.url = url;
    }

    public VkDialog(Context context, int theme) {
        super(context, theme);
    }

    protected VkDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void dismiss() {
        if (webView != null) {
            //webView.stopLoading();
        }
        if (!isDetached) {
            if (spinner.isShowing()) {
                spinner.dismiss();
            }
            super.dismiss();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        isDetached = true;
        super.onDetachedFromWindow();
    }

    @Override
    public void onAttachedToWindow() {
        isDetached = false;
        super.onAttachedToWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinner = new ProgressDialog(getContext());
        spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        spinner.setMessage(getContext().getString(com.facebook.android.R.string.com_facebook_loading));
        spinner.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                //sendCancelToListener();
                VkDialog.this.dismiss();
            }
        });

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        contentFrameLayout = new FrameLayout(getContext());
        contentFrameLayout.setBackgroundColor(Color.TRANSPARENT);
        calculateSize();
        getWindow().setGravity(Gravity.CENTER);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        createCrossImage();

        int crossWidth = crossImageView.getDrawable().getIntrinsicWidth();

        setUpWebView(crossWidth / 2 + 1);

        contentFrameLayout.addView(crossImageView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(contentFrameLayout);
    }

    private void calculateSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        int width = metrics.widthPixels < metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
        int height = metrics.widthPixels < metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;

        int dialogWidth = Math.min(
                getScaledSize(width, metrics.density, NO_PADDING_SCREEN_WIDTH, MAX_PADDING_SCREEN_WIDTH),
                metrics.widthPixels);
        int dialogHeight = Math.min(
                getScaledSize(height, metrics.density, NO_PADDING_SCREEN_HEIGHT, MAX_PADDING_SCREEN_HEIGHT),
                metrics.heightPixels);

        getWindow().setLayout(dialogWidth, dialogHeight);

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebView(int margin) {
        LinearLayout webViewContainer = new LinearLayout(getContext());
        webView = new WebView(getContext());
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new DialogWebViewClient());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        webView.getSettings().setSavePassword(false);
        webView.getSettings().setSaveFormData(false);
        webView.setVisibility(View.INVISIBLE);

        webViewContainer.setPadding(margin, margin, margin, margin);
        webViewContainer.addView(webView);
        webViewContainer.setBackgroundColor(Color.TRANSPARENT);
        contentFrameLayout.addView(webViewContainer);
    }

    private class DialogWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!isDetached) {
                spinner.dismiss();
            }
            contentFrameLayout.setBackgroundColor(Color.TRANSPARENT);
            webView.setVisibility(View.VISIBLE);
            crossImageView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Utility.logd("Andrey", "Webview loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            if (!isDetached) {
                spinner.show();
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            //Log.d("Andrey", "Load");
            if(url.contains("act=success")){
                if(listener != null) {
                    //view.stopLoading();
                    listener.actSucces();
                }
            }
            return super.shouldInterceptRequest(view, url);
        }
    }

    public void setListener(OnSuccesListener listener) {
        this.listener = listener;
    }

    private void createCrossImage() {
        crossImageView = new ImageView(getContext());
        // Dismiss the dialog when user click on the 'x'
        crossImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendCancelToListener();
                VkDialog.this.dismiss();
            }
        });
        Drawable crossDrawable = getContext().getResources().getDrawable(com.facebook.android.R.drawable.com_facebook_close);
        crossImageView.setImageDrawable(crossDrawable);
        /* 'x' should not be visible while webview is loading
         * make it visible only after webview has fully loaded
         */
        crossImageView.setVisibility(View.INVISIBLE);
    }

    private int getScaledSize(int screenSize, float density, int noPaddingSize, int maxPaddingSize) {
        int scaledSize = (int) ((float) screenSize / density);
        double scaleFactor;
        if (scaledSize <= noPaddingSize) {
            scaleFactor = 1.0;
        } else if (scaledSize >= maxPaddingSize) {
            scaleFactor = MIN_SCALE_FACTOR;
        } else {
            // between the noPadding and maxPadding widths, we take a linear reduction to go from 100%
            // of screen size down to MIN_SCALE_FACTOR
            scaleFactor = MIN_SCALE_FACTOR +
                    ((double) (maxPaddingSize - scaledSize))
                            / ((double) (maxPaddingSize - noPaddingSize))
                            * (1.0 - MIN_SCALE_FACTOR);
        }
        return (int) (screenSize * scaleFactor);
    }

    public interface OnSuccesListener{
        void actSucces();
    }
}
