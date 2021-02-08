package org.nibbler.zoe.liteplayer;

import java.io.File;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

@SuppressWarnings("unused")
public class PlayerPopUp extends Activity implements OnClickListener,
		OnDragListener, OnSeekBarChangeListener,
		MediaHandler.OnCompletionListener {

	public static MediaHandler mp = MainActivity.mh;
	private int id, currentPosition, currentProgress = 0;
	private double length;
	private String songName, nameOfPlayed, nextSongToPlay, song, nextSongName;
	private SeekBar seek;
	private TextView name;
	private Bundle extras;
	private MusicDBConnection dbConn;
	private TableRow layout;
	private ImageView btnBack, playPause, btnForward;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		AdMob admob = new AdMob(this);

		setContentView(R.layout.player_popup);

		layout = (TableRow) findViewById(R.id.popupBanner);

		layout.addView(admob.getAdMobView());

		extras = getIntent().getExtras();

		id = extras.getInt("songID");

		songName = extras.getString("path");

		length = extras.getDouble("length");

		name = (TextView) findViewById(R.id.songName);

		seek = (SeekBar) findViewById(R.id.musicBar);

		mp.setOnCompletionListener(this);

		seek.setOnSeekBarChangeListener(this);

		name.setText(id + ": " + extras.getString("name"));

		seek.setMax((int) length);
		    
		getInit(songName);

	}

	public void getInit(String songToPlay) {

		findViewById(R.id.btn_back).setOnClickListener(this);

		findViewById(R.id.btn_stop).setOnClickListener(this);

		findViewById(R.id.btn_forward).setOnClickListener(this);

		findViewById(R.id.musicBar).setOnDragListener(this);

		btnForward = (ImageView) findViewById(R.id.btn_forward);
		
		btnBack = (ImageView) findViewById(R.id.btn_back);
		
		playPause= (ImageView)findViewById(R.id.btn_stop);

		if (id != MainActivity.playingId) {

			audioPlayer(songToPlay);

			seekUpdation();

		}

	}

	final Runnable run = new Runnable() {

		@Override
		public void run() {

			seek.setProgress(mp.getCurrentPosition());

			seekUpdation();

//			if (MainActivity.playingId == MainActivity.maxId - 1 && btnForward.isEnabled()) {
//				btnForward.setEnabled(false);
//			}else{ btnForward.setEnabled(true); }
			
//			if (MainActivity.playingId == 0 && btnBack.isEnabled()) {
//				btnBack.setEnabled(false);
//			}else{ btnBack.setEnabled(true); }
		}

	};

	public void seekUpdation() {

		try {

			if (mp.getCurrentPosition() < 0) {

				seek.setProgress(0);

			}

			seek.setMax((int) length);

			seek.setProgress(mp.getCurrentPosition());

			currentPosition = mp.getCurrentPosition();

			seek.postDelayed(run, 50);

		} catch (Exception e) {

		}

		try {

		} catch (Exception error) {

		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btn_back:

			// Convert milliseconds to seconds and check for greater than, or
			// equal to 2 seconds
			if (mp.getCurrentPosition() / 1000 >= 2 & id == 0) {

				mp.seekTo(0);

				seek.setProgress(0);

			} else {

				if (mp.getCurrentPosition() / 1000 >= 2 & mp.isPlaying() == true) {

					mp.reset();

				}

				nextSongName = GetNextPreviousSong(id, false);

				getInit(nextSongName);

			}

			break;

		case R.id.btn_stop:

			if (mp.isPlaying()){
				
	            mp.pause();    
	            
				playPause.setImageResource(android.R.drawable.ic_media_play);
				
	        } else {
	        	
	        	mp.start();
	        	
	        	playPause.setImageResource(android.R.drawable.ic_media_pause);
	        	
	        }

			break;

		case R.id.btn_forward:
			
			if (mp.isPlaying() == true) {

				mp.reset();

			}

			nextSongName = GetNextPreviousSong(id, true);

			getInit(nextSongName);

			break;
		}

	}

	private String formatNameToPlay(String path) {

		Uri fileUri = null;

		try {

			path = path.trim();

			fileUri = Uri.fromFile(new File(path));

		} catch (Exception e) {

			return null;

		}

		return fileUri.toString();

	}

	@Override
	public boolean onDrag(View v, DragEvent event) {

		return false;
	}

	public void audioPlayer(String path) {

		try {

			mp.reset();

			mp.setDataSource(path);

			mp.prepare();

			MainActivity.playingId = id;

			mp.start();

		} catch (Exception e) {

			System.out.println("ERROR PLAYING...");

			e.printStackTrace();

		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		currentProgress = progress;

		seek.setProgress(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

		MainActivity.mh.seekTo(currentProgress);

	}

	@Override
	public void onCompletion(MediaPlayer mp) {

		if (mp.isPlaying() == false
				&& mp.getCurrentPosition() >= (mp.getDuration() - 500)) {

			nextSongName = GetNextPreviousSong(id, true);

			seek.postDelayed(run, 100);

		} else {

			seek.postDelayed(run, 50);

		}

		getInit(nextSongName);

	}

	private String GetNextPreviousSong(int currentSongID, boolean seekNext) {

		// seekNext states: true = advance ahead to next song | false = get song
		// before current song

		if (seekNext & id < MainActivity.maxId - 1) {

			id++;

		} else if(!seekNext){

			if (id > 0) {

				id--;

			} else {

				id = 0;
			}

		}else{
			
			id = MainActivity.maxId - 1;
		}

		seek.setMax(0);

		seek.setProgress(0);

		length = 0;

		dbConn = new MusicDBConnection(this);

		dbConn.open();

		nextSongToPlay = formatNameToPlay(dbConn.getRowByID(id + ""));

		nameOfPlayed = dbConn.getMusicName(id + "");

		name.setText(id + ": " + nameOfPlayed);

		length = dbConn.getMusicLength(id + "");

		seek.setMax((int) length);

		mp.seekTo(0);

		dbConn.close();

		return nextSongToPlay;

	}

}
