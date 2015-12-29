package com.sprdh.musicplay.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sprdh.musicplay.R;
import com.sprdh.musicplay.controller.Controls;
import com.sprdh.musicplay.services.SongService;
import com.sprdh.musicplay.util.PlayerConstants;
import com.sprdh.musicplay.util.Functions;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AudioPlayerActivity extends AppCompatActivity {

    private static String TAG="AudioPlayerActivity";
	@Bind(R.id.btnBack)Button btnBack;
	static Button btnPause;
	@Bind(R.id.btnNext)Button btnNext;
	static Button btnPlay;
	static TextView textNowPlaying;
	static TextView textAlbumArtist;
	static TextView textComposer;
	static LinearLayout linearLayoutPlayer;
//	@Bind(R.id.progressBar)ProgressBar progressBar;
    @Bind(R.id.seekBar)SeekBar seekBar;
	static Context context;
	@Bind(R.id.textBufferDuration)TextView textBufferDuration;
    @Bind(R.id.textDuration)TextView textDuration;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getActionBar().hide();
		setContentView(R.layout.audio_player);
        ButterKnife.bind(this);
		context = this;
		init();
	}

	private void init() {
		getViews();
		setListeners();
//		progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), Mode.SRC_IN);
		PlayerConstants.PROGRESSBAR_HANDLER = new Handler(){
			 @Override
		        public void handleMessage(Message msg){
				 Integer i[] = (Integer[])msg.obj;
				 textBufferDuration.setText(Functions.getDuration(i[0]));
				 textDuration.setText(Functions.getDuration(i[1]));
//				 progressBar.setProgress(i[2]);
                 seekBar.setProgress(i[2]);
		    	}
		};
	}

	private void setListeners() {
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Controls.previousControl(getApplicationContext());
			}
		});
		
		btnPause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Controls.pauseControl(getApplicationContext());
			}
		});
		
		btnPlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Controls.playControl(getApplicationContext());
			}
		});
		
		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Controls.nextControl(getApplicationContext());
			}
		});


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
	}
	
	public static void changeUI(){
		updateUI();
		changeButton();
	}
	
	private void getViews() {
		//btnBack = (Button) findViewById(R.id.btnBack);
		btnPause = (Button) findViewById(R.id.btnPause);
		//btnNext = (Button) findViewById(R.id.btnNext);
		btnPlay = (Button) findViewById(R.id.btnPlay);
		textNowPlaying = (TextView) findViewById(R.id.textNowPlaying);
		linearLayoutPlayer = (LinearLayout) findViewById(R.id.linearLayoutPlayer);
		textAlbumArtist = (TextView) findViewById(R.id.textAlbumArtist);
		textComposer = (TextView) findViewById(R.id.textComposer);
		//progressBar = (ProgressBar) findViewById(R.id.progressBar);
		//textBufferDuration = (TextView) findViewById(R.id.textBufferDuration);
		//textDuration = (TextView) findViewById(R.id.textDuration);
		textNowPlaying.setSelected(true);
		textAlbumArtist.setSelected(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		boolean isServiceRunning = Functions.isServiceRunning(SongService.class.getName(), getApplicationContext());
		if (isServiceRunning) {
			updateUI();
		}
		changeButton();
	}
	
	public static void changeButton() {
		if(PlayerConstants.SONG_PAUSED){
			btnPause.setVisibility(View.GONE);
			btnPlay.setVisibility(View.VISIBLE);
		}else{
			btnPause.setVisibility(View.VISIBLE);
			btnPlay.setVisibility(View.GONE);
		}
	}
	
	private static void updateUI() {
		try{
			String songName = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getTitle();
			String artist = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getArtist();
			String album = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbum();
			String composer = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getComposer();
			textNowPlaying.setText(songName);
			textAlbumArtist.setText(artist + " - " + album);
			if(composer != null && composer.length() > 0){
				textComposer.setVisibility(View.VISIBLE);
				textComposer.setText(composer);
			}else{
				textComposer.setVisibility(View.GONE);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			long albumId = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbumId();
			Bitmap albumArt = Functions.getAlbumart(context, albumId);
            Log.d(TAG,"albumArt -"+albumArt);
			if(albumArt != null){
				linearLayoutPlayer.setBackground(new BitmapDrawable(albumArt));
			}else{
				linearLayoutPlayer.setBackground(new BitmapDrawable(Functions.getDefaultAlbumArt(context)));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
