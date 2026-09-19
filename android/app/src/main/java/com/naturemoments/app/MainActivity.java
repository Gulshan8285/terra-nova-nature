package com.naturemoments.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.media.MediaScannerConnection;

public class MainActivity extends Activity {
    private static final String TAG = "NatureMomentsApp";
    private WebView webView;

    public class WebAppInterface {
        Activity mActivity;

        WebAppInterface(Activity activity) {
            mActivity = activity;
        }

        @JavascriptInterface
        public void exitApp() {
            mActivity.runOnUiThread(() -> mActivity.finishAffinity());
        }

        @JavascriptInterface
        public void shareWhatsApp(String shareText) {
            mActivity.runOnUiThread(() -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.setPackage("com.whatsapp");
                    intent.putExtra(Intent.EXTRA_TEXT, shareText);
                    mActivity.startActivity(intent);
                } catch (Exception e) {
                    try {
                        Intent fallbackIntent = new Intent(Intent.ACTION_SEND);
                        fallbackIntent.setType("text/plain");
                        fallbackIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                        mActivity.startActivity(Intent.createChooser(fallbackIntent, "Share Nature Reel via"));
                    } catch (Exception ex) {
                        Toast.makeText(mActivity, "Unable to open share sheet", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @JavascriptInterface
        public void shareApp(String shareText) {
            mActivity.runOnUiThread(() -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, shareText);
                    mActivity.startActivity(Intent.createChooser(intent, "Share Nature Moments App"));
                } catch (Exception e) {
                    Toast.makeText(mActivity, "Unable to share", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @JavascriptInterface
        public void downloadVideo(String videoUrl, String fileName) {
            new Thread(() -> {
                try {
                    final String cleanName = (fileName == null || fileName.trim().isEmpty())
                        ? "Nature_Reel_" + System.currentTimeMillis() + ".mp4"
                        : fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

                    if (videoUrl != null && (videoUrl.startsWith("http://") || videoUrl.startsWith("https://"))) {
                        // Remote HTTP download via DownloadManager
                        mActivity.runOnUiThread(() -> {
                            try {
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoUrl));
                                request.setTitle("Nature Moments Reel");
                                request.setDescription("Downloading " + cleanName);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, cleanName);
                                request.setAllowedOverMetered(true);
                                request.setAllowedOverRoaming(true);

                                DownloadManager manager = (DownloadManager) mActivity.getSystemService(Context.DOWNLOAD_SERVICE);
                                if (manager != null) {
                                    manager.enqueue(request);
                                    Toast.makeText(mActivity, "Downloading: " + cleanName, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception ex) {
                                Log.e(TAG, "DownloadManager error", ex);
                            }
                        });
                    } else {
                        // Local asset file (e.g. assets/videos/nature_stream.mp4 or videos/nature_stream.mp4)
                        String assetPath = videoUrl;
                        if (assetPath == null || assetPath.isEmpty()) {
                            assetPath = "assets/videos/nature_stream.mp4";
                        }
                        if (assetPath.startsWith("/")) {
                            assetPath = assetPath.substring(1);
                        }

                        InputStream in = null;
                        try {
                            in = mActivity.getAssets().open(assetPath);
                        } catch (Exception e1) {
                            try {
                                if (assetPath.startsWith("assets/")) {
                                    in = mActivity.getAssets().open(assetPath.substring("assets/".length()));
                                }
                            } catch (Exception e2) {
                                Log.e(TAG, "Cannot open asset: " + assetPath, e2);
                            }
                        }

                        if (in != null) {
                            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            if (!downloadsDir.exists()) {
                                downloadsDir.mkdirs();
                            }
                            File destFile = new File(downloadsDir, cleanName);
                            FileOutputStream out = new FileOutputStream(destFile);
                            byte[] buffer = new byte[8192];
                            int read;
                            while ((read = in.read(buffer)) != -1) {
                                out.write(buffer, 0, read);
                            }
                            out.flush();
                            out.close();
                            in.close();

                            // Trigger MediaScanner so video appears in Gallery/Photos app
                            MediaScannerConnection.scanFile(
                                mActivity,
                                new String[]{destFile.getAbsolutePath()},
                                new String[]{"video/mp4"},
                                null
                            );

                            mActivity.runOnUiThread(() -> {
                                Toast.makeText(mActivity, "Saved to Downloads & Gallery: " + cleanName, Toast.LENGTH_LONG).show();
                            });
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error saving video to device", e);
                    mActivity.runOnUiThread(() -> {
                        Toast.makeText(mActivity, "Reel saved in-app", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Immersive window setup
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        webView = new WebView(this);
        setContentView(webView);

        // WebSettings configuration
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Initialize Android Jetpack WebViewAssetLoader
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
            .build();

        webView.setWebViewClient(new WebViewClientCompat() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            @SuppressWarnings("deprecation")
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return assetLoader.shouldInterceptRequest(Uri.parse(url));
            }
        });

        // WebChromeClient for capturing console logs
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("NatureMomentsJS", cm.message() + " -- Line " + cm.lineNumber() + " of " + cm.sourceId());
                return true;
            }
        });

        webView.addJavascriptInterface(new WebAppInterface(this), "AndroidBridge");
        webView.setBackgroundColor(0xFF000000); // Black background matching Reels feed

        // Load via secure WebViewAssetLoader domain
        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html");

        hideSystemUI();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    @Override
    public void onBackPressed() {
        if (webView != null) {
            webView.evaluateJavascript("if (window.showExitConfirm) { window.showExitConfirm(); }", null);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) webView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
