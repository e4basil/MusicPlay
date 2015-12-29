package com.sprdh.musicplay.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sprdh.musicplay.R;
import com.sprdh.musicplay.adapter.CustomAdapter;
import com.sprdh.musicplay.controller.Controls;
import com.sprdh.musicplay.services.SongService;
import com.sprdh.musicplay.util.Functions;
import com.sprdh.musicplay.util.MediaItem;
import com.sprdh.musicplay.util.PlayerConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Basil on 15-12-2015.
 */
public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    CustomAdapter customAdapter = null;
    static TextView playingSong;
    static Button btnPause, btnPlay, btnNext, btnPrevious;
    static LinearLayout linearLayoutPlayingSong;
    static ImageView imageViewAlbumArt;
    static Context context;
    @Bind(R.id.btnStop)
    Button btnStop;
    @Bind(R.id.linearLayoutMusicList)
    LinearLayout mediaLayout;
    @Bind(R.id.btnMusicPlayer)
    Button btnPlayer;
    @Bind(R.id.listViewMusic)
    ListView mediaListView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.textBufferDuration)
    TextView textBufferDuration;
    @Bind(R.id.textDuration)
    TextView textDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = MainActivity.this;
        findViewById();
        init();
    }

    private void findViewById() {

        playingSong = (TextView) findViewById(R.id.textNowPlaying);

        btnPause = (Button) findViewById(R.id.btnPause);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        linearLayoutPlayingSong = (LinearLayout) findViewById(R.id.linearLayoutPlayingSong);

        imageViewAlbumArt = (ImageView) findViewById(R.id.imageViewAlbumArt);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
    }

    private void init() {
        setListeners();
        playingSong.setSelected(true);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        if (PlayerConstants.SONGS_LIST.size() <= 0) {
            PlayerConstants.SONGS_LIST = Functions.listOfSongs(getApplicationContext());
        }
        setListItems();
    }

    private void setListeners() {
        mediaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                Log.d(TAG, "songList clicked @ " + position);
                PlayerConstants.SONG_PAUSED = false;
                PlayerConstants.SONG_NUMBER = position;
                boolean isServiceRunning = Functions.isServiceRunning(SongService.class.getName(), getApplicationContext());
                if (!isServiceRunning) {
                    Intent i = new Intent(getApplicationContext(), SongService.class);
                    startService(i);
                } else {
                    PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                }
                updateUI();
                changeButton();
                Log.d(TAG, " end mediaListView");
            }
        });

        btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AudioPlayerActivity.class);
                startActivity(i);
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.playControl(getApplicationContext());
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.pauseControl(getApplicationContext());
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Controls.nextControl(getApplicationContext());
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Controls.previousControl(getApplicationContext());
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SongService.class);
                stopService(i);
                Log.d(TAG, "btnStop clicked n stopService() called");
                linearLayoutPlayingSong.setVisibility(View.GONE);
            }
        });
        imageViewAlbumArt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AudioPlayerActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            boolean isServiceRunning = Functions.isServiceRunning(SongService.class.getName(), getApplicationContext());
            if (isServiceRunning) {
                updateUI();
            } else {
                linearLayoutPlayingSong.setVisibility(View.GONE);
            }
            changeButton();
            PlayerConstants.PROGRESSBAR_HANDLER = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Integer i[] = (Integer[]) msg.obj;
                    textBufferDuration.setText(Functions.getDuration(i[0]));
                    textDuration.setText(Functions.getDuration(i[1]));
                    progressBar.setProgress(i[2]);
                }
            };
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("deprecation")
    public static void updateUI() {
        try {
            MediaItem data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
            long albumId = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbumId();
            playingSong.setText(data.getTitle() + " " + data.getArtist() + "-" + data.getAlbum());
            Bitmap albumArt = Functions.getAlbumart(context, data.getAlbumId());
            Log.i(TAG, "albumArt -" + albumArt);
            if (albumArt != null) {
                imageViewAlbumArt.setBackgroundDrawable(new BitmapDrawable(albumArt));
            } else {
                imageViewAlbumArt.setBackgroundDrawable(new BitmapDrawable(Functions.getDefaultAlbumArt(context)));
            }
            linearLayoutPlayingSong.setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    public static void changeButton() {
        if (PlayerConstants.SONG_PAUSED) {
            btnPause.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
        } else {
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        }
    }

    public static void changeUI() {
        updateUI();
        changeButton();
    }

    private void setListItems() {
        customAdapter = new CustomAdapter(this, R.layout.custom_list, PlayerConstants.SONGS_LIST);
        mediaListView.setAdapter(customAdapter);
        mediaListView.setFastScrollEnabled(true);
    }
}
