package com.example.inclass05;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //filename: MainActivity
    //Group Number: groups1 3
    //Members : Akshay Popli and Neel Solanki


    Button btn_go;
    TextView tv_searchKeyword;
    ImageView iv_main;
    ImageView iv_next;
    ImageView iv_prev;
    String[] imagesStringArr;
    int counter=0;
    ProgressBar progressBar;
    TextView tv_load;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main Activity");

        btn_go = findViewById(R.id.btn_go);
        tv_searchKeyword = findViewById(R.id.tv_searchKeyword);
        iv_main = findViewById(R.id.iv_main);
        iv_next = findViewById(R.id.iv_next);
        iv_prev= findViewById(R.id.iv_prev);
        iv_next.setAlpha((float) 0.2);
        iv_prev.setAlpha((float) 0.2);
        tv_load = findViewById(R.id.tv_load);
        progressBar= findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        tv_load.setVisibility(View.INVISIBLE);

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()){
                    new GetDataAsync().execute("http://dev.theappsdr.com/apis/photos/keywords.php");
                }else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(imagesStringArr != null){
                        counter += 1;
                        if(counter < imagesStringArr.length){
                            new GetImageAsync(iv_main).execute(imagesStringArr[counter]);
                            tv_load.setText("Loading Next...");
                            tv_load.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            iv_main.setVisibility(View.INVISIBLE);
                        } else {
                            new GetImageAsync(iv_main).execute(imagesStringArr[0]);
                            tv_load.setText("Loading Next...");
                            tv_load.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            iv_main.setVisibility(View.INVISIBLE);
                            counter = 0;

                        }
                    }
            }
        });

        iv_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imagesStringArr != null) {
                    counter -= 1;

                    if (counter >= 0) {
                        new GetImageAsync(iv_main).execute(imagesStringArr[counter]);
                        tv_load.setText("Loading Prev...");
                        tv_load.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        iv_main.setVisibility(View.INVISIBLE);
                    } else {
                        new GetImageAsync(iv_main).execute(imagesStringArr[imagesStringArr.length - 1]);
                        counter = imagesStringArr.length - 1;
                        tv_load.setText("Loading Prev...");
                        tv_load.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        iv_main.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }


    public class GetDataAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line="";

                while((line=reader.readLine())!=null){
                    stringBuilder.append(line);
                }
                Log.d("data", stringBuilder.toString());



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
            final String[] listItems = s.split(";");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Choose a Keyword");

            builder.setItems(listItems, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tv_searchKeyword.setText(listItems[which]);

                    String urlList = "http://dev.theappsdr.com/apis/photos/index.php?keyword=" + listItems[which];
                    new GetUrlList().execute(urlList);
                    tv_load.setText("Loading...");
                    progressBar.setVisibility(View.VISIBLE);
                    tv_load.setVisibility(View.VISIBLE);
                    iv_main.setVisibility(View.INVISIBLE);

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();


        }
    }

    public class GetUrlList extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            BufferedReader reader = null;
            StringBuilder sb2 = new StringBuilder();
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String urls="";

                while((urls=reader.readLine())!=null){
                    sb2.append(urls+ " ");
                }
                Log.d("URL", sb2.toString());



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return sb2.toString().trim();

        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("URLS", s);
            if(s=="" || s==null){
                Toast.makeText(MainActivity.this, "No Image Found", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                tv_load.setVisibility(View.INVISIBLE);
                iv_next.setAlpha((float) 0.2);
                iv_prev.setAlpha((float) 0.2);
                imagesStringArr = null;
                iv_main.setVisibility(View.VISIBLE);
                iv_main.setImageResource(android.R.drawable.ic_menu_camera);
            }else{
                imagesStringArr = s.split(" ");
                new GetImageAsync(iv_main).execute(imagesStringArr[0]);
            }
        }
    }


    private class GetImageAsync extends AsyncTask<String, Void, Void> {
        Bitmap bitmap = null;

        public GetImageAsync(ImageView iv) {
            iv_main = iv;
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpURLConnection imageConnection = null;
            bitmap = null;

            try {
                URL url = new URL(strings[0]);
                imageConnection = (HttpURLConnection) url.openConnection();
                imageConnection.connect();
                if (imageConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bitmap = BitmapFactory.decodeStream(imageConnection.getInputStream());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(bitmap != null && iv_main != null){
                iv_main.setImageBitmap(bitmap);
                progressBar.setVisibility(View.INVISIBLE);
                tv_load.setVisibility(View.INVISIBLE);
                iv_main.setVisibility(View.VISIBLE);
                iv_next.setAlpha((float) 1.0);
                iv_prev.setAlpha((float) 1.0);

            }
        }

    }

}
