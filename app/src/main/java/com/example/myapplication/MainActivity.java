package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    TextView position, duration;
    ImageView rewind, play, pause, forward;
    SeekBar seekBar;
    MediaPlayer player;
    MyThread myThread;
    ArrayList<String> arrayList;
    final static int MY_PERMISSION_REQUEST = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);

        }
        play = findViewById(R.id.play);
        play.setOnClickListener(this);
        pause = findViewById(R.id.pause);
        pause.setOnClickListener(this);
        player =  MediaPlayer.create(this, R.raw.track1);
        seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(player.getDuration());
        myThread = new MyThread();
        myThread.start();
        duration = findViewById(R.id.duration);
        duration.setText(timeFormatter(player.getDuration()));
        position = findViewById(R.id.position);
        arrayList = new ArrayList<>();
        getMusic();

    }

    public void getMusic(){
        ContentResolver contentResolver= getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null,null,null,null,null);
        if (songCursor!= null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                arrayList.add(currentTitle+" "+ currentArtist);
            }while (songCursor.moveToNext());
        }
        Log.d("MYTag",String.valueOf(arrayList.size()));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode){
           case MY_PERMISSION_REQUEST:
               if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();

               } else{
                   Toast.makeText(this, " No permission granted", Toast.LENGTH_SHORT).show();
                   finish();
               }
               return;
       }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        position.setText(timeFormatter(player.getCurrentPosition()));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        player.seekTo(seekBar.getProgress());



    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.play:
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                player.start();

                break;
            case R.id.pause:
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
                player.pause();
                break;
        }

    }
    public String timeFormatter(int time){
        time = time/1000;
        int seconds = time%60;
        int minutes = time/60;
        String result = String.format("%02d:%02d",minutes,seconds);
        return result;
    }
    class MyThread extends Thread{
        @Override
        public void run(){
            while(true){
                seekBar.setProgress(player.getCurrentPosition());
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}