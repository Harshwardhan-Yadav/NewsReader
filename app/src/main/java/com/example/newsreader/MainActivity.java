package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> title;
    ArrayList<String> urls;

    class DownloadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String result="";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data!=-1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        title=new ArrayList<>();
        urls=new ArrayList<>();
        SQLiteDatabase myDataBase = this.openOrCreateDatabase("URLS",MODE_PRIVATE,null);
        try {
            DownloadTask task = new DownloadTask();
            String s="";
            s=task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();
            s=s.substring(2,s.length()-2);
            String[] arr = s.split(", ");
            for(int i=0;i<10;i++){
                DownloadTask tasktemp = new DownloadTask();
                String temp="";
                temp=tasktemp.execute("https://hacker-news.firebaseio.com/v0/item/"+arr[i]+".json?print=pretty").get();
                try{
                    JSONObject jsonObject = new JSONObject(temp);
                    title.add(jsonObject.getString("title"));
                    urls.add(jsonObject.getString("url"));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,title);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    myDataBase.execSQL("DROP TABLE urls");
                } catch(Exception e){
                    e.printStackTrace();
                }
                myDataBase.execSQL("CREATE TABLE urls (url VARCHAR)");
                for(int i=0;i<10;i++){
                    String add=urls.get(i);
                    myDataBase.execSQL("INSERT INTO urls (url) VALUES (?)", new String[]{add});
                }
                Cursor c = myDataBase.rawQuery("SELECT * FROM urls",null);
                c.moveToFirst();
                while(!c.isAfterLast()){
                    System.out.println(c.getString(c.getColumnIndex("url")));
                    c.moveToNext();
                }
                Intent intent = new Intent(getApplicationContext(),SecondActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }
}