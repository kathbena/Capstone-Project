package gradle.kathleenbenavides.com.flickpick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by kathleenbenavides on 3/13/17.
 */

public class WebViewActivity extends Activity {

    private WebView myWebView;
    private String url;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Intent intent = getIntent();
        url = intent.getExtras().getString("URL");

        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        //To show in app and not in browser
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(url);
    }

}
