package com.codingblocks.musicplayer;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songs=new ArrayList<>();
    TextView playingSongTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        ListView listView = (ListView) findViewById(R.id.listView);
        playingSongTitle=(TextView) findViewById(R.id.songTitleBox);

        songs = getAudioList();
        ArrayList<String> songNames=new ArrayList<>();

        for(int i=0;i<songs.size();i++){
            String name=songs.get(i).getDisplayName();
            songNames.add(name);
        }

        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,songNames);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Log.i("Music player", songs.get(position).getPath());
                    playingSongTitle.setText(songs.get(position).getDisplayName());
                    playSong(songs.get(position).getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private ArrayList<Song> getAudioList(){
        //Retrieve a list of Music files currently listed in the Media store DB via URI.

        //Some audio may be explicitly marked as not being music
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        ArrayList<Song> songs=new ArrayList<>();

        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,selection, null, null);


        while(cursor.moveToNext()){
            Song s=new Song();

            s.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
            s.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
            s.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
            s.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
            s.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));

            songs.add(s);
            //Log.i("Music player", cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
        }
        return songs;
    }

    private void playSong(String path) throws IllegalArgumentException,
            IllegalStateException, IOException {

        Log.d("ringtone", "playSong :: " + path);

        mediaPlayer.reset();
        mediaPlayer.setDataSource(path);
        //mediaPlayer.setLooping(true);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

}
