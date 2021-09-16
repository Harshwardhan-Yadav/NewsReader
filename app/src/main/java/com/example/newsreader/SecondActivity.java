package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SecondActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        SQLiteDatabase myDataBase = this.openOrCreateDatabase("URLS",MODE_PRIVATE,null);
        Cursor c=myDataBase.rawQuery("SELECT * FROM urls",null);
        c.moveToFirst();
        int i=0;

        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        while(i!=position){
            c.moveToNext();
            i++;
        }
        webView.loadUrl(c.getString(c.getColumnIndex("url")));
    }
}