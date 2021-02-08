package org.nibbler.zoe.liteplayer;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/** Custom adapter for displaying an array of Planet objects. */  
public class MusicRowArrayAdapter extends ArrayAdapter<MusicItem> {  
    
  private LayoutInflater inflater;  
    
  public MusicRowArrayAdapter( Context context, List<MusicItem> musicList ) {  
    super( context, R.layout.row, R.id.musicFileRow, musicList ); 
    // Cache the LayoutInflate to avoid asking for a new one each time.  
    inflater = LayoutInflater.from(context) ;  
  }  

  @Override  
  public View getView(int position, View convertView, ViewGroup parent) {  
    // Planet to display  
    MusicItem music = (MusicItem) this.getItem( position );   

    // The child views in each row.  
    CheckBox checkBox ;   
    TextView textView ;   
      
    // Create a new row view  
    if ( convertView == null ) {  
      convertView = inflater.inflate(R.layout.row, null);  
        
      // Find the child views.  
      textView = (TextView) convertView.findViewById( R.id.musicFileRow );  
      checkBox = (CheckBox) convertView.findViewById( R.id.playlistCheckboxes );  
        
      // Optimization: Tag the row with it's child views, so we don't have to   
      // call findViewById() later when we reuse the row.  
     convertView.setTag( new MusicItemViewHolder(textView,checkBox) );  

      // If CheckBox is toggled, update the planet it is tagged with.  
      checkBox.setOnClickListener( new View.OnClickListener() {  
        public void onClick(View v) {  
          CheckBox cb = (CheckBox) v ;  
          MusicItem music = (MusicItem) cb.getTag();  
          music.setChecked( cb.isChecked() );  
        }  
      });          
    }  
    // Reuse existing row view  
    else {  
      // Because we use a ViewHolder, we avoid having to call findViewById().  
    	MusicItemViewHolder viewHolder = (MusicItemViewHolder) convertView.getTag();  
      checkBox = viewHolder.getCheckBox() ;  
      textView = viewHolder.getTextView() ;  
    }  

    // Tag the CheckBox with the Planet it is displaying, so that we can  
    // access the planet in onClick() when the CheckBox is toggled.  
    checkBox.setTag( music );   
      
    // Display planet data  
    checkBox.setChecked( music.isChecked() );  
    textView.setText( music.name );        
      
    return convertView;  
  }  
    
}  