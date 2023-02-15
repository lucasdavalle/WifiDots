package com.example.safesporting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class Web : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        var webView = findViewById<WebView>(R.id.web)
        var webSettings : WebSettings = webView.settings
        webSettings.javaScriptEnabled=true
        webView.setWebViewClient(WebViewClient())
        webView.loadUrl("https://github.com/lucasdavalle/WifiDots")
    }
}