package org.nibbler.zoe.liteplayer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TableRow;

public class MainActivity extends Activity implements OnClickListener {

	 public static MediaHandler mh = new MediaHandler();
	 public static int playingId, menuItemID;
	 private ArrayList<MusicItem> music;
	 private ArrayAdapter<String> playList;
	 private ArrayAdapter<MusicItem> fileList;
	 private File path;
	 private String id, musicName, musicPath, albumName, songLength, artistName, songToPlay, nameOfPlayed;
	 private MediaMetadataRetriever song;
	 private ListView lView;
	 private double length;
	 private MusicDBConnection dbConn;
	 private Intent popup;
	 private Bundle bundle;
	 private LinearLayout layout;
	 private TableRow row;
	 private MenuItem mnuItem;
	 private Menu menu;
	 private Button btn_save_pl;
	 private Button btn_cancel_pl;
	 public static int maxId;
	 private CheckBox playlistCB;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		AdMob admob = new AdMob(this);

        lView = (ListView)findViewById(R.id.list);
        
        lView.setSelector(android.R.color.holo_blue_dark);
        
        //Find the two buttons for saving/canceling playlist creation - Begin
        findViewById(R.id.savePL).setOnClickListener(this);
        
        findViewById(R.id.cancelPL).setOnClickListener(this);
        //Find the two buttons for saving/canceling playlist creation - End

        layout = (TableRow)findViewById(R.id.banner);
        
        layout.addView(admob.getAdMobView());

		dbConn = new MusicDBConnection(MainActivity.this);

		dbConn.open();
		
		dbConn.dbOverwrite();
			
		dbConn.close();
		
        lView.setOnItemClickListener(new OnItemClickListener() {
	    	
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
            	
            	//If user hasn't clicked Menu > Create Playlist then do this, otherwise go to else
            	if (!Globals.isCreatingPlaylist) {
            		
                    //lView.setChoiceMode(lView.CHOICE_MODE_SINGLE);

	    			dbConn = new MusicDBConnection(MainActivity.this);
	    			
	    			dbConn.open();
	
	    			songToPlay = formatNameToPlay(dbConn.getRowByID(id + ""));
	    			
	    			nameOfPlayed = dbConn.getMusicName(id + "");
	    			
	    			length = dbConn.getMusicLength(id + "");
	
	    			dbConn.close();
	    			
	    			popup = new Intent(getApplicationContext(), PlayerPopUp.class);
	    			
	    			bundle = new Bundle();
	    			
	    			bundle.putInt("songID", position);
	    			
	    			bundle.putString("path", songToPlay);
	    			
	    			bundle.putString("name", nameOfPlayed);
	
	    			bundle.putDouble("length", length);
	
	    			popup.putExtras(bundle);
	
	    			startActivity(popup);
    			
            	} else {
            		
            		//If Menu > Create Playlist has been selected, we want the user to be able to
            		//select songs to add to a playlist, and not start playing the selected song.
            		
    				if (mh.isPlaying()) {
    					
    					mh.stop();
    					
    					mh.release();
    					
    				}
            		
                    //lView.setChoiceMode(lView.CHOICE_MODE_MULTIPLE);
                    
                    //view.setSelected(true);
            		
            	}

            }
               
        });
		
		//starting up the DataBase for writing.		
		dbConn.open();
		
		try {
	
		    music = new ArrayList<MusicItem>();   
		    
		    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);

		    File mp3[] = path.listFiles();
	
		    for (int x = 0; x < mp3.length; x++) {
		    	
		    	id = x + "";
		    	
		    	musicName = mp3[x].getName();
		    	
		    	musicPath = path + "/" + mp3[x].getName();
		    			    	
		    	song = new MediaMetadataRetriever();
		    	
		    	song.setDataSource(musicPath);
		    	
		    	albumName = song.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		    	
		    	songLength = song.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		    	
		    	artistName = song.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		    	
		    	if (albumName == null) {
		    		
		    		albumName = "";
		    	}
		    	
		    	if (artistName == null) {
		    		
		    		artistName = "";
		    	}
		    	
		    	if (songLength == null) {
		    		
		    		songLength = "";
		    	}

	    		albumName.trim();

	    		songLength.trim();
	    		
	    		artistName.trim();

		    	if (artistName == null && albumName == null || artistName == "" && albumName == "") {

			    	dbConn.createEntry(id, musicName, musicPath, artistName, albumName, songLength);
			    	
		    		music.add(new MusicItem(dbConn.getMusicName(id), false));
		    		
		    	} else {
		    	
			    	if (albumName == "") {
			    		
			    		albumName = "<unknown>";
			    	}
			    	
			    	if (artistName == "") {
			    		
			    		artistName = "<unknown>";
			    	}
			        
			    	//MATT 05/22/14 // Moved here to prevent duplication. (it was three lines below where it is now.
			    	dbConn.createEntry(id, musicName, musicPath, artistName, albumName, songLength);
			    	
		    		music.add(new MusicItem(dbConn.getMusicName(id), false));
		    		
		    	}
		        
		    }
	    
		} catch(Exception e) {
			
			
		} finally {
			
			dbConn.close();
			
			maxId = music.size();
			
		}

		fileList = new MusicRowArrayAdapter(this, this.music);
		//checkboxList = new ArrayAdapter<String>(this, R.layout.row, R.id.musicFileRow, this.music);

		lView.setAdapter(fileList);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
        
		mnuItem = item;
		
		menuItemID = item.getItemId();
		
		switch (menuItemID) {
		
			case R.id.action_load_playlist:
				
				break;
				
			case R.id.action_create_playlist:
				
				lView.setSelector(android.R.color.background_dark);
				
				if (!Globals.isCreatingPlaylist) {
					
					if (mh.isPlaying()) {
						
						mh.stop();
						
					}

    				item.setTitle("Cancel Create Playlist");
    				
    				Globals.isCreatingPlaylist = true;
	
    				//playList = new ArrayAdapter<String>(this, R.layout.row, R.id.musicFileRow , this.music);
    				
    				//lView.setAdapter(playList);

    				findViewById(R.id.savePL).setVisibility(View.VISIBLE);
    				
    				findViewById(R.id.cancelPL).setVisibility(View.VISIBLE);
    				
    				View v;
    				
    				LinearLayout vg = new LinearLayout(this);
    				
    				android.view.ViewGroup.LayoutParams lp;
    				
    				//lp = vg.getLayoutParams();
    				
    				//vg.setLayoutParams(lp);
    				
    				setContentView(vg);
    				
    			    for (int x = 0; x < lView.getCount(); x++) {
    			    	
    			    	v = lView.getAdapter().getView(x, null, lView);
    			    	
    			    	playlistCB = (CheckBox)v.findViewById(R.id.playlistCheckboxes);

    			    	playlistCB.setVisibility(View.VISIBLE);
    			    	
    			    	vg.addView(v, x);
    			    	
    			    }

				} else {
    				
    				item.setTitle("Create Playlist");
    				
    				Globals.isCreatingPlaylist = false;
    				
    				findViewById(R.id.savePL).setVisibility(View.GONE);
    				
    				findViewById(R.id.cancelPL).setVisibility(View.GONE);
    				
    				//playlistCB.setVisibility(View.GONE);
					
				}
				
				break;

			case R.id.action_exit:
				
				if (mh.isPlaying()) {
					
					mh.stop();
					
					mh.release();
					
				}
				
	            finish();
	            
	            System.exit(0);
				
				break;
		
		}
		
		return super.onOptionsItemSelected(item);
		
	}

	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.activity_main, container, false);
			
			return rootView;
			
		}
		
	}

	@Override
	public void onClick(View v) {
		
		//Find the button that was clicked
    	switch(v.getId()) {
    	
    	//Handle the button click
    	case R.id.savePL:
    		
    		break;
    		
    	case R.id.cancelPL:
    		
    		mnuItem.setTitle("Create Playlist");
			
			Globals.isCreatingPlaylist = false;
			
			findViewById(R.id.savePL).setVisibility(View.GONE);
			
			findViewById(R.id.cancelPL).setVisibility(View.GONE);
			
            //lView.setChoiceMode(lView.CHOICE_MODE_SINGLE);
    		
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
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return parent;
		
	}
	
   
}


