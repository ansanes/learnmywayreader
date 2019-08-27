/*
 * WebViewActivity.java
 * SDKLauncher-Android
 *
 * Created by Yonathan Teitelbaum (Mantano) on 2013-07-10.
 */
//  Copyright (c) 2014 Readium Foundation and/or its licensees. All rights reserved.
//  Redistribution and use in source and binary forms, with or without modification, 
//  are permitted provided that the following conditions are met:
//  1. Redistributions of source code must retain the above copyright notice, this 
//  list of conditions and the following disclaimer.
//  2. Redistributions in binary form must reproduce the above copyright notice, 
//  this list of conditions and the following disclaimer in the documentation and/or 
//  other materials provided with the distribution.
//  3. Neither the name of the organization nor the names of its contributors may be 
//  used to endorse or promote products derived from this software without specific 
//  prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
//  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
//  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
//  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
//  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
//  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
//  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
//  OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
//  OF THE POSSIBILITY OF SUCH DAMAGE

package org.readium.sdk.android.launcher;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.readium.sdk.android.Container;
import org.readium.sdk.android.ManifestItem;
import org.readium.sdk.android.Package;
import org.readium.sdk.android.SpineItem;
import org.readium.sdk.android.launcher.model.BookmarkDatabase;
import org.readium.sdk.android.launcher.model.OpenPageRequest;
import org.readium.sdk.android.launcher.model.Page;
import org.readium.sdk.android.launcher.model.PaginationInfo;
import org.readium.sdk.android.launcher.model.ReadiumJSApi;
import org.readium.sdk.android.launcher.model.SignLanguageSmilRepo;
import org.readium.sdk.android.launcher.model.SignLanguageVideoElement;
import org.readium.sdk.android.launcher.model.ViewerSettings;
import org.readium.sdk.android.launcher.util.EpubServer;
import org.readium.sdk.android.launcher.util.EpubServer.DataPreProcessor;
import org.readium.sdk.android.launcher.util.HTMLUtil;
import org.readium.sdk.android.launcher.util.VideoViewScaleListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;


public class WebViewActivity extends FragmentActivity  {
    private static final int CONFIGURATION_OPTIONS = 1;
    private final boolean quiet = false;
    private static final String TAG = "WebViewActivity";
    private static final String ASSET_PREFIX_FILE = "file:///android_asset/readium-shared-js/";
    private static final String READER_SKELETON_FILE = ASSET_PREFIX_FILE + "reader.html";
    private static final String ASSET_PREFIX = "http://" + EpubServer.HTTP_HOST + ":" + EpubServer.HTTP_PORT + "/readium-shared-js/";
    private static final String READER_SKELETON = ASSET_PREFIX + "reader.html";

    // Installs "hook" function so that top-level window (application) can later
    // inject the window.navigator.epubReadingSystem into this HTML document's
    // iframe
    private static final String INJECT_EPUB_RSO_SCRIPT_1 = ""
            + "window.readium_set_epubReadingSystem = function (obj) {"
            + "\nwindow.navigator.epubReadingSystem = obj;"
            + "\nwindow.readium_set_epubReadingSystem = undefined;"
            + "\nvar el1 = document.getElementById(\"readium_epubReadingSystem_inject1\");"
            + "\nif (el1 && el1.parentNode) { el1.parentNode.removeChild(el1); }"
            + "\nvar el2 = document.getElementById(\"readium_epubReadingSystem_inject2\");"
            + "\nif (el2 && el2.parentNode) { el2.parentNode.removeChild(el2); }"
            + "\n};";

    // Iterate top-level iframes, inject global
    // window.navigator.epubReadingSystem if the expected hook function exists (
    // readium_set_epubReadingSystem() ).
    private static final String INJECT_EPUB_RSO_SCRIPT_2 = ""
            + "var epubRSInject =\nfunction(win) {" // 1
            + "\nvar ret = '';"
            + "\nret += win.location.href;"
            + "\nret += ' ---- ';"
            // "\nret += JSON.stringify(win.navigator.epubReadingSystem);" +
            // "\nret += ' ---- ';" +
            + "\nif (win.frames)"
            + "\n{" // 2
            + "\nfor (var i = 0; i < win.frames.length; i++)"
            + "\n{" // 3
            + "\nvar iframe = win.frames[i];"
            + "\nret += ' IFRAME ';"
            + "\ntry {\n" // 4
            + "\nif (iframe.readium_set_epubReadingSystem)"
            + "\n{" // 5
            + "\nret += ' EPBRS ';"
            + "\niframe.readium_set_epubReadingSystem(window.navigator.epubReadingSystem);"
            + "\n}" // 5
            + "\nret += epubRSInject(iframe);"
            + "\n} catch(err) { console.log(err); }" // 4
            + "\n}" // 3
            + "\n}" // 2
            + "\nreturn ret;"
            + "\n};" // 1
            + "\nepubRSInject(window);";

    // Script tag to inject the "hook" function installer script, added to the
    // head of every epub iframe document
    private static final String INJECT_HEAD_EPUB_RSO_1 = ""
            + "<script id=\"readium_epubReadingSystem_inject1\" type=\"text/javascript\">\n"
            + "//<![CDATA[\n" + INJECT_EPUB_RSO_SCRIPT_1 + "\n" + "//]]>\n"
            + "</script>";
    // Script tag that generates an HTTP request to a fake script => triggers
    // push of window.navigator.epubReadingSystem into this HTML document's
    // iframe
    private static final String INJECT_HEAD_EPUB_RSO_2 = ""
            + "<script id=\"readium_epubReadingSystem_inject2\" type=\"text/javascript\" "
            + "src=\"/%d/readium_epubReadingSystem_inject.js\"> </script>";
    // Script tag to load the mathjax script payload, added to the head of epub
    // iframe documents, only if <math> tags are detected
    private static final String INJECT_HEAD_MATHJAX = "<script type=\"text/javascript\" src=\"/readium_MathJax.js\"> </script>";

    // Location of payloads in the asset folder
    private static final String PAYLOAD_MATHJAX_ASSET = "reader-payloads/MathJax.js";
    private static final String PAYLOAD_ANNOTATIONS_CSS_ASSET = "reader-payloads/annotations.css";

    private final DataPreProcessor dataPreProcessor = new DataPreProcessor() {

        @Override
        public byte[] handle(byte[] data, String mime, String uriPath,
                             ManifestItem item) {
            if (mime == null
                    || (mime != "text/html" && mime != "application/xhtml+xml")) {
                return null;
            }

            if (!quiet)
                Log.d(TAG, "PRE-PROCESSED HTML: " + uriPath);

            String htmlText = new String(data, Charset.forName("UTF-8"));

            // String uuid = mPackage.getUrlSafeUniqueID();
            String newHtml = htmlText; // HTMLUtil.htmlByReplacingMediaURLsInHTML(htmlText,
            // cleanedUrl, uuid);
            // //"PackageUUID"

            // Set up the script tags to add to the head
            String tagsToInjectToHead = INJECT_HEAD_EPUB_RSO_1
                    // Slightly change fake script src url with an
                    // increasing count to prevent caching of the
                    // request
                    + String.format(INJECT_HEAD_EPUB_RSO_2,
                    ++mEpubRsoInjectCounter);
            // Checks for the existance of MathML => request
            // MathJax payload
            if (newHtml.contains("<math") || newHtml.contains("<m:math")) {
                tagsToInjectToHead += INJECT_HEAD_MATHJAX;
            }

            newHtml = HTMLUtil.htmlByInjectingIntoHead(newHtml,
                    tagsToInjectToHead);

            // Log.d(TAG, "HTML head inject: " + newHtml);

            return newHtml.getBytes();
        }
    };

    private WebView mWebview;
    private MutedVideoView signLanguageVideoView;
    private Container mContainer;
    private Package mPackage;
    private OpenPageRequest mOpenPageRequestData;
    private TextView mPageInfo;
    private ViewerSettings mViewerSettings;
    private ReadiumJSApi mReadiumJSApi;
    private EpubServer mServer;

    private boolean mIsMoAvailable;
    private boolean mIsMoPlaying;
    private int mEpubRsoInjectCounter = 0;
    private RelativeLayout webviewContainerRelativeLayout;
    private float mScaleFactor = 1;
    private SignLanguageSmilRepo signLanguageSmilRepo;
    private boolean mediaOverlayEnabled;
    private Integer currentParIndex = 1000;
    private Integer currentPage = 6;
    private Uri currentVideoUri = null;
    private Boolean signlanguageVideoEnabled = false;
    private Boolean voiceOverEnabled = false;
    private Boolean likeABook = true;
    private Button signLanguageVideoButton;
    private ImageButton toggleSignLanguageImageButton;
    private ImageButton toggleVoiceOverImageButton;
    private LearnMyWayApplication learnMyWayApplication;
    private LearnMyWayUserOptions userOptions;
    private Boolean jsMediaOverlayEnabled = false;
    //private boolean mediaOverlayPaused = false;
    private String webViewPauseUrl;
    private Integer pausePageNumber;
    private java.util.Map<String, String> epubMap = new java.util.HashMap();
    private VideoViewScaleListener videoViewScaleListener;
    private ImageButton prevMoButton;
    private ImageButton nextMoButton;
    private boolean voiceOverDisabledOnActivity = false;
    private String currentParId = "";
    private LinearLayout optionsBarLinearLayout;
    private ImageButton floatingSignLanguageButton;
    private boolean extraHintsEnabled = false;
    private boolean firstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        prevMoButton = findViewById(R.id.previosMediaOverlay);
        nextMoButton = findViewById(R.id.nextMediaOverlay);
        optionsBarLinearLayout = findViewById(R.id.navigation);
        floatingSignLanguageButton = findViewById(R.id.floatingSignLanguageImageButton);
        floatingSignLanguageButton.setVisibility(View.INVISIBLE);
        this.learnMyWayApplication = (LearnMyWayApplication) getApplication();
        this.userOptions = this.learnMyWayApplication.getLearnMyWayUserOptions();

        mWebview = (WebView) findViewById(R.id.webview);
        webviewContainerRelativeLayout = findViewById(R.id.webviewContainerRelativeLayout);
        //  signLanguageVideoButton = findViewById(R.id.signlanguagevideoonoffbutton);
        signLanguageVideoView = findViewById(R.id.signLanguageVideoView);
        toggleSignLanguageImageButton = findViewById(R.id.bottonsignlanguageImageButton);
        toggleVoiceOverImageButton = findViewById(R.id.bottomvoiceoverImageButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && 0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mPageInfo = findViewById(R.id.page_info);
        mPageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initWebView();
        Intent intent = getIntent();
        if (intent.getFlags() == Intent.FLAG_ACTIVITY_NEW_TASK) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mContainer = ContainerHolder.getInstance().get(
                        extras.getLong(Constants.CONTAINER_ID));
                if (mContainer == null) {
                    finish();
                    return;
                }
                mPackage = mContainer.getDefaultPackage();
                String rootUrl = "http://" + EpubServer.HTTP_HOST + ":"
                        + EpubServer.HTTP_PORT + "/";
                mPackage.setRootUrls(rootUrl, null);
                try {
                    mOpenPageRequestData = OpenPageRequest.fromJSON(extras
                            .getString(Constants.OPEN_PAGE_REQUEST_DATA));
                } catch (JSONException e) {
                    Log.e(TAG,
                            "Constants.OPEN_PAGE_REQUEST_DATA must be a valid JSON object: "
                                    + e.getMessage(), e);
                }
            }
        }

        mServer = new EpubServer(EpubServer.HTTP_HOST, EpubServer.HTTP_PORT,
                mPackage, quiet, dataPreProcessor);
        mServer.startServer();

        // Load the page skeleton
        mWebview.loadUrl(READER_SKELETON);
        mViewerSettings = new ViewerSettings(
                ViewerSettings.SyntheticSpreadMode.SINGLE,
                (likeABook ? ViewerSettings.ScrollMode.AUTO : ViewerSettings.ScrollMode.CONTINUOUS), 100, 20);

        mReadiumJSApi = new ReadiumJSApi(new ReadiumJSApi.JSLoader() {
            @Override
            public void loadJS(String javascript) {
                mWebview.loadUrl(javascript);
            }
        });
        signLanguageVideoView.bringToFront();
        signLanguageVideoView.setClickable(true);
        signLanguageVideoView.setFocusable(true);
        signLanguageVideoView.setVisibility(View.INVISIBLE);
        this.videoViewScaleListener = new VideoViewScaleListener(signLanguageVideoView);
        final ScaleGestureDetector mScaleDetector = new ScaleGestureDetector(this, videoViewScaleListener);
        signLanguageVideoView.setTag("videoviewtag");
        signLanguageVideoView.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                if (videoViewScaleListener.isScalingVideo()) {
                    return false;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:

                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;

            }
        });
        signLanguageSmilRepo = new SignLanguageSmilRepo(this);
        ImageButton petHelperImageButton = findViewById(R.id.optionsImageButton);
        petHelperImageButton.setImageResource(((LearnMyWayApplication) getApplication()).getImageIdForPetHelper());

        /*
        //
        /// USER OPTIONS
        */

        signlanguageVideoEnabled = userOptions.getSignLanguageVideo();
        voiceOverEnabled = userOptions.getVoiceOver();
        likeABook = userOptions.getLikeABook();
        extraHintsEnabled = userOptions.getExtraHints();

        updateSettings(mViewerSettings);

        if (signlanguageVideoEnabled) {
            signLanguageVideoView.setVisibility(View.VISIBLE);
            toggleSignLanguageImageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_sign_language_on));
            floatingSignLanguageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_sign_language_on));
        } else {
            signLanguageVideoView.setVisibility(View.INVISIBLE);
            toggleSignLanguageImageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_sign_language_off));
            floatingSignLanguageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_sign_language_off));
        }
        if (voiceOverEnabled) {
            toggleVoiceOverImageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_voice_over_on));
            turnOnJsMediaOverlay();
        } else {
            nextMoButton.setVisibility(View.INVISIBLE);
            prevMoButton.setVisibility(View.INVISIBLE);
            toggleVoiceOverImageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_voice_over_off));
        }
    }



    public void toggleExtraHints(boolean enabled){
        mWebview.evaluateJavascript("document.getElementsByTagName('iframe')[0].contentWindow.playerFunctions.toggleExtraHints("+(enabled?"true":"false")+");", null);
    }

    private void turnOnJsMediaOverlay() {
        Log.d("videosync", "turn on media overlay");
        mReadiumJSApi.turnOnMediaOverlay();
    }

    private void turnOffJsMediaOverlay() {
        Log.d("videosync", "turn off media overlay");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mReadiumJSApi.turnOffMediaOverlay();
            }
        });
    }



    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    private void initWebView() {
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setMediaPlaybackRequiresUserGesture(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mWebview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        mWebview.setWebViewClient(new EpubWebViewClient());
     //   mWebview.setWebChromeClient(new EpubWebChromeClient());

        mWebview.addJavascriptInterface(new EpubInterface(), "LauncherUI");
        mWebview.addJavascriptInterface(new WebAppInterface(this), "UnicefPlayer");
    }

    public void onClick(View v) {
        if (v.getId() == R.id.left) {
            mReadiumJSApi.openPageLeft();
        } else if (v.getId() == R.id.right) {
            mReadiumJSApi.openPageRight();
        } else if (v.getId() == R.id.previosMediaOverlay) {
            mReadiumJSApi.previousMediaOverlay();
        } else if (v.getId() == R.id.nextMediaOverlay) {
            mReadiumJSApi.nextMediaOverlay();
        } else if (v.getId() == R.id.bottomvoiceoverImageButton) {
            toggleVoiceOver();
        } else if (v.getId() == R.id.bottonsignlanguageImageButton || v.getId() == R.id.floatingSignLanguageImageButton)  {
            toggleSignLanguage();
        } else if (v.getId() == R.id.optionsImageButton) {
            Intent intent = new Intent(this, ConfigurationOptionsActivity.class);
            turnOffJsMediaOverlay();
            startActivityForResult(intent, CONFIGURATION_OPTIONS);
        }
    }

    private void toggleVoiceOver() {
        voiceOverEnabled = !voiceOverEnabled;
        userOptions.setVoiceOver(voiceOverEnabled);
        if (voiceOverEnabled) {
            nextMoButton.setVisibility(View.VISIBLE);
            prevMoButton.setVisibility(View.VISIBLE);
            turnOnJsMediaOverlay();
            mReadiumJSApi.resumeNextMediaOverlay();
            mWebview.evaluateJavascript("document.getElementsByTagName('iframe')[0].contentWindow.playerFunctions.voiceOverStatusChanged(true);", null);
            if (signlanguageVideoEnabled) {
                signLanguageVideoView.setVisibility(View.VISIBLE);
                signLanguageVideoView.resume();
            }
            toggleVoiceOverImageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_voice_over_on));
        } else {
            nextMoButton.setVisibility(View.INVISIBLE);
            prevMoButton.setVisibility(View.INVISIBLE);
            mWebview.evaluateJavascript("document.getElementsByTagName('iframe')[0].contentWindow.playerFunctions.voiceOverStatusChanged(false);", null);
            signLanguageVideoView.pause();
            signLanguageVideoView.setVisibility(View.INVISIBLE);
            toggleVoiceOverImageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_voice_over_off));
        }
    }

    private void disableTouchVoiceOver() {
        mViewerSettings.setMediaOverlaysEnableClick(false);
        updateSettings(mViewerSettings);
    }

    private void enableTouchVoiceOver() {
        mViewerSettings.setMediaOverlaysEnableClick(true);
        updateSettings(mViewerSettings);
    }

    private void toggleSignLanguage() {
        signlanguageVideoEnabled = !signlanguageVideoEnabled;
        userOptions.setSignLanguageVideo(signlanguageVideoEnabled);
        if (signlanguageVideoEnabled) {
            signLanguageVideoView.setVisibility(View.VISIBLE);
            toggleSignLanguageImageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_sign_language_on));
            floatingSignLanguageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_sign_language_on));
        } else {
            signLanguageVideoView.setVisibility(View.INVISIBLE);
            toggleSignLanguageImageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_sign_language_off));
            floatingSignLanguageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_sign_language_off));
        }
    }

    private void updateSettings(ViewerSettings viewerSettings) {
        mViewerSettings = viewerSettings;
        mReadiumJSApi.updateSettings(viewerSettings);
    }


    public final class EpubWebViewClient extends WebViewClient {

        private static final String HTTP = "http";
        private static final String UTF_8 = "utf-8";

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (!quiet)
                Log.d(TAG, "onPageStarted: " + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (!quiet)
                Log.d(TAG, "onPageFinished: " + url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            if (!quiet)
                Log.d(TAG, "onLoadResource: " + url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!quiet)
                Log.d(TAG, "shouldOverrideUrlLoading: " + url);
            return false;
        }

        private void evaluateJavascript(final String script) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (!quiet)
                        Log.d(TAG, "WebView evaluateJavascript: " + script + "");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                        if (!quiet)
                            Log.d(TAG, "WebView evaluateJavascript KitKat+ API");

                        mWebview.evaluateJavascript(script,
                                new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String str) {
                                        if (!quiet)
                                            Log.d(TAG,
                                                    "WebView evaluateJavascript RETURN: "
                                                            + str);
                                    }
                                });
                    } else {

                        if (!quiet)
                            Log.d(TAG, "WebView loadUrl() API");

                        mWebview.loadUrl("javascript:var exec = function(){\n"
                                + script + "\n}; exec();");
                    }
                }
            });
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view,
                                                          String url) {
            if (!quiet)
                Log.d(TAG, "-------- shouldInterceptRequest: " + url);

            if (url != null && url != "undefined") {

                String localHttpUrlPrefix = "http://" + EpubServer.HTTP_HOST
                        + ":" + EpubServer.HTTP_PORT;
                boolean isLocalHttp = url.startsWith(localHttpUrlPrefix);

                // Uri uri = Uri.parse(url);
                // uri.getScheme()

                if (url.startsWith("http") && !isLocalHttp) {
                    if (!quiet)
                        Log.d(TAG, "HTTP (NOT LOCAL): " + url);
                    return super.shouldInterceptRequest(view, url);
                }

                String cleanedUrl = cleanResourceUrl(url, false);
                if (!quiet)
                    Log.d(TAG, url + " => " + cleanedUrl);

                String mime = null;
                int dot = cleanedUrl.lastIndexOf('.');
                if (dot >= 0) {
                    mime = EpubServer.MIME_TYPES.get(cleanedUrl.substring(
                            dot + 1).toLowerCase());
                }
                if (mime == null) {
                    mime = "application/octet-stream";
                }

                if (url.startsWith(ASSET_PREFIX)) {

                    if (!quiet)
                        Log.d(TAG, "READER ASSET ...");

                    // reader.html
                    if (mime.equals("application/xhtml+xml")) {
                        mime = "text/html";
                    }

                    InputStream is = null;
                    try {
                        is = getAssets().open(cleanedUrl);
                    } catch (IOException e) {

                        Log.e(TAG, "asset fail: " + cleanedUrl);

                        return new WebResourceResponse(null, UTF_8,
                                new ByteArrayInputStream("".getBytes()));
                    }

                    return new WebResourceResponse(mime, UTF_8, is);
                }

                if (cleanedUrl
                        .matches("\\/?\\d*\\/readium_epubReadingSystem_inject.js")) {
                    if (!quiet)
                        Log.d(TAG, "navigator.epubReadingSystem inject ...");

                    // Fake script requested, this is immediately invoked after
                    // epubReadingSystem hook is in place,
                    // => execute js on the reader.html context to push the
                    // global window.navigator.epubReadingSystem into the
                    // iframe(s)

                    evaluateJavascript(INJECT_EPUB_RSO_SCRIPT_2);

                    return new WebResourceResponse("text/javascript", UTF_8,
                            new ByteArrayInputStream(
                                    "(function(){})()".getBytes()));
                }

                if (cleanedUrl.matches("\\/?readium_MathJax.js")) {
                    if (!quiet)
                        Log.d(TAG, "MathJax.js inject ...");

                    InputStream is = null;
                    try {
                        is = getAssets().open(PAYLOAD_MATHJAX_ASSET);
                    } catch (IOException e) {

                        Log.e(TAG, "MathJax.js asset fail!");

                        return new WebResourceResponse(null, UTF_8,
                                new ByteArrayInputStream("".getBytes()));
                    }

                    return new WebResourceResponse("text/javascript", UTF_8, is);
                }

                if (cleanedUrl.matches("\\/?readium_Annotations.css")) {
                    if (!quiet)
                        Log.d(TAG, "annotations.css inject ...");

                    InputStream is = null;
                    try {
                        is = getAssets().open(PAYLOAD_ANNOTATIONS_CSS_ASSET);
                    } catch (IOException e) {

                        Log.e(TAG, "annotations.css asset fail!");

                        return new WebResourceResponse(null, UTF_8,
                                new ByteArrayInputStream("".getBytes()));
                    }

                    return new WebResourceResponse("text/css", UTF_8, is);
                }


                ManifestItem item = mPackage.getManifestItem(cleanedUrl);
                String contentType = item != null ? item.getMediaType() : null;
                if (mime != "application/xhtml+xml"
                        && mime != "application/xml" // FORCE
                        && contentType != null && contentType.length() > 0) {
                    mime = contentType;
                }

                if (url.startsWith("file:")) {
                    if (item == null) {
                        Log.e(TAG, "NO MANIFEST ITEM ... " + url);
                        return super.shouldInterceptRequest(view, url);
                    }

                    String cleanedUrlWithQueryFragment = cleanResourceUrl(url,
                            true);
                    String httpUrl = "http://" + EpubServer.HTTP_HOST + ":"
                            + EpubServer.HTTP_PORT + "/"
                            + cleanedUrlWithQueryFragment;
                    Log.e(TAG, "FILE to HTTP REDIRECT: " + httpUrl);

                    try {
                        URLConnection c = new URL(httpUrl).openConnection();
                        ((HttpURLConnection) c).setUseCaches(false);
                        if (mime == "application/xhtml+xml"
                                || mime == "text/html") {
                            ((HttpURLConnection) c).setRequestProperty(
                                    "Accept-Ranges", "none");
                        }
                        InputStream is = c.getInputStream();
                        return new WebResourceResponse(mime, null, is);
                    } catch (Exception ex) {
                        Log.e(TAG,
                                "FAIL: " + httpUrl + " -- " + ex.getMessage(),
                                ex);
                    }
                }
                if (!quiet)
                    Log.d(TAG, "RESOURCE FETCH ... " + url);
                return super.shouldInterceptRequest(view, url);
            }

            Log.e(TAG, "NULL URL RESPONSE: " + url);
            return new WebResourceResponse(null, UTF_8,
                    new ByteArrayInputStream("".getBytes()));
        }
    }

    private void onNewPageLoading() {
        voiceOverDisabledOnActivity = false;
        Log.d("videosync", "current PAGE:" + currentPage + " jsmediaoverlay:" + jsMediaOverlayEnabled);
        //mediaOverlayPaused = false;
        //currentParIndex = 1000;
        currentParId = "";
        runOnUiThread(new Runnable() {

            public void run() {
                showOptionsBar();
                if (voiceOverEnabled) {
                    mReadiumJSApi.resumeNextMediaOverlay();
                    if (firstRun && voiceOverEnabled) {
                        turnOnJsMediaOverlay();
                        firstRun = false;
                    }
                } else {
                  //  turnOffJsMediaOverlay();
                }
                Log.d("videosync", "video stopPlayBack");
                signLanguageVideoView.stopPlayback();
            }
        });
    }

    private String cleanResourceUrl(String url, boolean preserveQueryFragment) {
        String cleanUrl = null;
        String httpUrl = "http://" + EpubServer.HTTP_HOST + ":"
                + EpubServer.HTTP_PORT;
        if (url.startsWith(httpUrl)) {
            cleanUrl = url.replaceFirst(httpUrl, "");
        } else {
            cleanUrl = (url.startsWith(ASSET_PREFIX)) ? url.replaceFirst(
                    ASSET_PREFIX, "") : url.replaceFirst("file://", "");
        }

        String basePath = mPackage.getBasePath();
        if (basePath.charAt(0) != '/') {
            basePath = '/' + basePath;
        }
        if (cleanUrl.charAt(0) != '/') {
            cleanUrl = '/' + cleanUrl;
        }
        cleanUrl = (cleanUrl.startsWith(basePath)) ? cleanUrl.replaceFirst(
                basePath, "") : cleanUrl;

        if (cleanUrl.charAt(0) == '/') {
            cleanUrl = cleanUrl.substring(1);
        }
        if (!preserveQueryFragment) {
            int indexOfQ = cleanUrl.indexOf('?');
            if (indexOfQ >= 0) {
                cleanUrl = cleanUrl.substring(0, indexOfQ);
            }

            int indexOfSharp = cleanUrl.indexOf('#');
            if (indexOfSharp >= 0) {
                cleanUrl = cleanUrl.substring(0, indexOfSharp);
            }
        }
        return cleanUrl;
    }

    private void playVideo(String video) {
        if (signlanguageVideoEnabled) {
            String uriPath = "android.resource://" + getPackageName() + "/raw/" + video.replaceAll(".mp4", "").replace("-", "_");
            final Uri uri = Uri.parse(uriPath);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    signLanguageVideoView.setVisibility(View.VISIBLE);
                    signLanguageVideoView.setVideoURI(uri);
                    signLanguageVideoView.start();
                    signLanguageVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            signLanguageVideoView.stopPlayback();
                        }
                    });
                }
            });
        }
    }

    private void checkVideoPosition(JSONObject jsonObject) {
        try {
            final SmilInfo smilInfo = new SmilInfo();
            smilInfo.setPlayPosition(jsonObject.getDouble("playPosition"));
            smilInfo.setParIndex(jsonObject.getInt("parIndex"));
            smilInfo.setSmilIndex(jsonObject.getInt("smilIndex"));
            smilInfo.parId = jsonObject.getString("parId");
            if (!smilInfo.parId.equals(currentParId)) {
                currentParId = smilInfo.parId;
                final SignLanguageVideoElement signLanguageVideoElement = signLanguageSmilRepo.findSmilInformation(currentPage, smilInfo);
                if (signLanguageVideoElement != null) {
                    Log.d("videosync", "video element:" + smilInfo.getParIndex() + " " + signLanguageVideoElement.getParId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playVideoAtPosition(signLanguageVideoElement);
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void playVideoAtPosition(final SignLanguageVideoElement signLanguageVideoElement) {
        final String video = signLanguageVideoElement.getVideoFileName();
        final Integer begin = signLanguageVideoElement.getClipBegin();
        final Integer end = signLanguageVideoElement.getClipEnd();
        final Integer duration = end - begin;
        String uriPath = "android.resource://" + getPackageName() + "/raw/" + video.substring(0, video.lastIndexOf("."));
        final Uri uri = Uri.parse(uriPath);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                signLanguageVideoView.setVideoURI(uri);
                signLanguageVideoView.setVisibility(View.VISIBLE);
                Log.d("videosync", "play video" + video + " begin:" + begin + " duration:" + duration);
                if (!signLanguageVideoElement.getParId().equals("par0")) {
                    mReadiumJSApi.holdNextMediaOverlay();
                }
                signLanguageVideoView.start();
                signLanguageVideoView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("videosync", "resume");
                        mReadiumJSApi.resumeNextMediaOverlay();
                    }
                }, duration + 500);
            }
        });
    }

    private void playSignLanguageVideo(String name) {
        Log.d("videoplayingname", name);

        if (signlanguageVideoEnabled) {

            String uriPath = "android.resource://" + getPackageName() + "/raw/" + name;
            final Uri uri = Uri.parse(uriPath);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (signLanguageVideoView.getmMediaPlayer() != null){
                        signLanguageVideoView.getmMediaPlayer().reset();
                    }
                    signLanguageVideoView.stopPlayback();
                    signLanguageVideoView.clearAnimation();
                    signLanguageVideoView.suspend(); // clears media player
                    signLanguageVideoView.setVideoURI(null);
                    System.out.println("playing video normally");
                    signLanguageVideoView.setVisibility(View.VISIBLE);

                    currentVideoUri = uri;
                    signLanguageVideoView.setVideoURI(uri);
                    signLanguageVideoView.start();
                    signLanguageVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            signLanguageVideoView.stopPlayback();
                        }
                    });


                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONFIGURATION_OPTIONS) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String navigateTo = data.getStringExtra("navigation");
                    if (navigateTo != null) {
                        if (navigateTo.equals("toHome")) {
                            Intent intent = new Intent(this, BookshelfActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        if (navigateTo.equals("toContent")) {
                            finish();
                        }
                    }
                    return;
                }

                LearnMyWayUserOptions userOptions = this.learnMyWayApplication.getLearnMyWayUserOptions();
                if (userOptions.getLikeABook() != likeABook) {
                    likeABook = userOptions.getLikeABook();
                    if (userOptions.getLikeABook()) {
                        mViewerSettings = new ViewerSettings(
                                ViewerSettings.SyntheticSpreadMode.SINGLE,
                                ViewerSettings.ScrollMode.AUTO, 150, 20);
                    } else {
                        mViewerSettings = new ViewerSettings(
                                ViewerSettings.SyntheticSpreadMode.SINGLE,
                                ViewerSettings.ScrollMode.CONTINUOUS, 100, 20);
                    }
                }
                if (userOptions.getSignLanguageVideo() != signlanguageVideoEnabled) {
                    toggleSignLanguage();
                }
                if (userOptions.getVoiceOver() != voiceOverEnabled) {
                    toggleVoiceOver();
                }
                if (userOptions.getExtraHints() != extraHintsEnabled){
                    this.extraHintsEnabled = userOptions.getExtraHints();
                }
                mViewerSettings.setMediaOverlaysRate(userOptions.getNarrationSpeedFloat());
                mViewerSettings.setAutoplay(userOptions.getAutoplay());
                /*LearnMyWayUserOptions.FONT_SIZE fontSize = userOptions.getFontSize();

                int fontSizeValue = 100;
                if (fontSize.equals(LearnMyWayUserOptions.FONT_SIZE.FONT_SIZE_BIG)) {
                    fontSizeValue = 150;
                }
                mViewerSettings.setFontSize(fontSizeValue);
                LearnMyWayUserOptions.FONT_TYPE fontType = userOptions.getFontType();
                LearnMyWayUserOptions.NARRATION_SPEED narrationSpeed = userOptions.getNarrationSpeed();*/


                updateSettings(mViewerSettings);
            }
        }
    }


    public java.util.Map<String, String> getEpubMap() {
        return epubMap;
    }


    private void showOptionsBar() {
        runOnUiThread(new Runnable() {
            public void run() {
                optionsBarLinearLayout.setVisibility(View.VISIBLE);
                floatingSignLanguageButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void hideOptionsBar() {
        runOnUiThread(new Runnable() {
            public void run() {
                optionsBarLinearLayout.setVisibility(View.GONE);
                floatingSignLanguageButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public void stopCurrentSignLanguageVideo() {
        runOnUiThread(new Runnable() {
            public void run() {
                signLanguageVideoView.stopPlayback();
                signLanguageVideoView.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServer.stop();
        mWebview.loadUrl(READER_SKELETON_FILE);
        ((ViewGroup) mWebview.getParent()).removeView(mWebview);
        mWebview.removeAllViews();
        mWebview.clearCache(true);
        mWebview.clearHistory();
        mWebview.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        signLanguageVideoView.stopPlayback();
        turnOffJsMediaOverlay();
        if (mIsMoPlaying) {
            mReadiumJSApi.turnOffMediaOverlay();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebview.onPause();
        }
        pausePageNumber = currentPage;
        webViewPauseUrl = mWebview.getUrl();
        mWebview.loadUrl("file:///android_asset/infAppPaused.html");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webViewPauseUrl != null && !webViewPauseUrl.equals("")) {
            mWebview.loadUrl(webViewPauseUrl);
            if (pausePageNumber != null) {
                mReadiumJSApi.openSpineItemPage(null, pausePageNumber);
            }
        }
        if (mediaOverlayEnabled) {
            turnOnJsMediaOverlay();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebview.onResume();
        }
    }

      public class EpubInterface {
        @JavascriptInterface
        public void onPaginationChanged(final String currentPagesInfo) {
            if (!quiet)
                Log.d(TAG, "onPaginationChanged: " + currentPagesInfo);
            try {
                final PaginationInfo paginationInfo = PaginationInfo
                        .fromJson(currentPagesInfo);
                List<Page> openPages = paginationInfo.getOpenPages();
                if (!openPages.isEmpty()) {
                    final Page page = openPages.get(0);
                    Log.d(TAG, "onPaginationChanged currentPage: " + (page.getSpineItemIndex() + 1));
                    if (page.getSpineItemIndex() + 1 != currentPage) {
                        currentPage = page.getSpineItemIndex() + 1;
                        Log.d(TAG, "onPaginationChanged set Current page:" + currentPage);
                        Log.d(TAG, "onPaginationChanged ----------------");
                        onNewPageLoading();
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String pageNumber = getString(R.string.page_x_of_y,
                                    currentPage,
                                    paginationInfo.getSpineItemCount());
                            String text = "<b>Chapter one: porta aberta</h2></b><br/>" + pageNumber;
                            mPageInfo.setText(Html.fromHtml(text));
                            SpineItem spineItem = mPackage.getSpineItem(page
                                    .getIdref());
                            boolean isFixedLayout = spineItem
                                    .isFixedLayout(mPackage);
                            mWebview.getSettings().setBuiltInZoomControls(
                                    isFixedLayout);
                            mWebview.getSettings()
                                    .setDisplayZoomControls(false);
                        }
                    });
                }
            } catch (JSONException e) {
                Log.e(TAG, "" + e.getMessage(), e);
            }
        }

        @JavascriptInterface
        public void onSettingsApplied() {
            if (!quiet)
                Log.d(TAG, "onSettingsApplied");
        }

        @JavascriptInterface
        public void onReaderInitialized() {
            if (!quiet)
                Log.d(TAG, "onReaderInitialized");

            if (!quiet)
                Log.d(TAG, "openPageRequestData: " + mOpenPageRequestData);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mReadiumJSApi.openBook(mPackage, mViewerSettings,
                            mOpenPageRequestData);
                }
            });

        }

        @JavascriptInterface
        public void onContentLoaded() {
            if (!quiet) {
                Log.d(TAG, "onContentLoaded");
            }
        }

        @JavascriptInterface
        public void onPageLoaded() {
            if (!quiet) {
                Log.d(TAG, "onPageLoaded");
            }
        }

        @JavascriptInterface
        public void onIsMediaOverlayAvailable(String available) {
            if (!quiet) {
                Log.d(TAG, "onIsMediaOverlayAvailable:" + available);
            }
            mIsMoAvailable = available.equals("true");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidateOptionsMenu();
                }
            });
        }

        @JavascriptInterface
        public void onMediaOverlayStatusChanged(String status) {
            // this should be real json parsing if there will be more data that
            // needs to be extracted

            if (status.indexOf("isPlaying") > -1) {
                mIsMoPlaying = status.indexOf("\"isPlaying\":true") > -1;

            }
            if (!quiet) {
                Log.d(TAG, "onMediaOverlayStatusChanged:" + status);
            }
            if (voiceOverDisabledOnActivity) {
                Log.d(TAG, "onMediaOverlayStatusChanged:" + "disabled on activity");
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(status);
                if (signlanguageVideoEnabled) {
                    boolean newSmilElementIsPlaying = jsonObject.has("isPlaying") && (Boolean) jsonObject.get("isPlaying");
                    if (newSmilElementIsPlaying) {
                        checkVideoPosition(jsonObject);
                    } else {
                        if (jsonObject.has("parId")) {
                            String parId = (String) jsonObject.get("parId");
                            if (parId != currentParId && !jsonObject.has("isPlaying")) {
                                checkVideoPosition(jsonObject);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidateOptionsMenu();
                }
            });
        }


        @JavascriptInterface
        public void onMediaOverlayTTSSpeak(String tts) {
            Log.d(TAG, "onMediaOverlayTTSSpeak" + tts);
        }

        @JavascriptInterface
        public void onMediaOverlayTTSStop() {
            Log.d(TAG, "onMediaOverlayTTSStop");
        }

        @JavascriptInterface
        public void getBookmarkData(final String bookmarkData) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    WebViewActivity.this).setTitle(R.string.add_bookmark);

            final EditText editText = new EditText(WebViewActivity.this);
            editText.setId(android.R.id.edit);
            editText.setHint(R.string.title);
            builder.setView(editText);
            builder.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                String title = editText.getText().toString();
                                try {
                                    JSONObject bookmarkJson = new JSONObject(
                                            bookmarkData);
                                    BookmarkDatabase.getInstance().addBookmark(
                                            mContainer.getName(),
                                            title,
                                            bookmarkJson.getString("idref"),
                                            bookmarkJson
                                                    .getString("contentCFI"));
                                } catch (JSONException e) {
                                    Log.e(TAG, "" + e.getMessage(), e);
                                }
                            }
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.show();
        }
    }

    public class WebAppInterface {
        private final WebViewActivity webViewActivity;

        WebAppInterface(WebViewActivity webViewActivity) {
            this.webViewActivity = webViewActivity;
        }

        @JavascriptInterface
        public void setValue(String key, String value) {
            this.webViewActivity.getEpubMap().put(key, value);
        }

        @JavascriptInterface
        public String getValue(String key) {
            return this.webViewActivity.getEpubMap().get(key);
        }


        @JavascriptInterface
        public void logMessage(String message) {
            System.out.println("log from js:" + message);
        }

        @JavascriptInterface
        public void playVideo(String video, String x, String y, String scale) {
            webViewActivity.playVideo(video);
        }

        @JavascriptInterface
        public boolean isVoiceOverEnabled() {
            return webViewActivity.voiceOverEnabled;
        }


        @JavascriptInterface
        public boolean isExtraHintsEnabled() {
            return webViewActivity.extraHintsEnabled;
        }


        @JavascriptInterface
        public void playSignLanguageVideo(String name) {
            webViewActivity.playSignLanguageVideo(name);
        }


        @JavascriptInterface
        public void enableVoiceOver() {

        }

        @JavascriptInterface
        public void disableVoiceOver() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webViewActivity.voiceOverDisabledOnActivity = true;
                    mReadiumJSApi.turnOffMediaOverlay();
                    webViewActivity.stopCurrentSignLanguageVideo();
                }
            });
        }

        @JavascriptInterface
        public void hideOptionsBar() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webViewActivity.hideOptionsBar();

                }
            });

        }

        @JavascriptInterface
        public void showOptionsBar() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webViewActivity.showOptionsBar();
                }
            });
        }


        @JavascriptInterface
        public void stopCurrentSignLanguageVideo() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webViewActivity.stopCurrentSignLanguageVideo();
                }

            });

        }

        @JavascriptInterface
        public float getMediaOverlayRate() {
            return userOptions.getNarrationSpeedFloat();
        }

    }
}
