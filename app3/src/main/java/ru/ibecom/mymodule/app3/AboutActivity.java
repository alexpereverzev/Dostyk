package ru.ibecom.mymodule.app3;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class AboutActivity extends ActionBarActivity {

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        webView=(WebView) findViewById(R.id.web_about);


        webView.loadUrl("file:///android_asset/1.html");

    }



}
