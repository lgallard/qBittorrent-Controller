package com.lgallardo.qbittorrentclient;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class ItemstFragment extends ListFragment {
	 
    public ItemstFragment() {
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.activity_main_original, container, false);
        
        //setListAdapter(new myAdapter());
 
        return rootView;
    }
    
    
	// @Override
	 public void onListItemClick(ListView parent, View v, int position, long
	 id) {
		 
			Log.i("FragmentLIst", "Item touched");
			
			TorrentDetailsFragment fragment = new TorrentDetailsFragment();
			
			fragment.setPosition(position);
			
			
			if (fragment != null) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, fragment).commit();
			}
			
	 // selection.setText(items[position]);
	
//	 Intent intent = new Intent(this, TorrentActionsActivity.class);
//	
//	 // Torrent info
//	 intent.putExtra(MainActivity.TAG_NAME, MainActivity.lines[position].getFile());
//	 intent.putExtra(MainActivity.TAG_SIZE, MainActivity.lines[position].getSize());
//	 intent.putExtra(MainActivity.TAG_INFO, MainActivity.lines[position].getInfo());
//	 intent.putExtra(MainActivity.TAG_RATIO, MainActivity.lines[position].getRatio());
//	 intent.putExtra(MainActivity.TAG_PROGRESS,
//	 MainActivity.lines[position].getProgress());
//	 intent.putExtra(MainActivity.TAG_STATE, MainActivity.lines[position].getState());
//	 intent.putExtra(MainActivity.TAG_NUMLEECHS, MainActivity.lines[position].getLeechs());
//	 intent.putExtra(MainActivity.TAG_NUMSEEDS, MainActivity.lines[position].getSeeds());
//	 intent.putExtra(MainActivity.TAG_PRIORITY,
//	 MainActivity.lines[position].getPriority());
//	
//	 intent.putExtra(TAG_HASH, MainActivity.lines[position].getHash());
//	
//	 // Http client params
//	 intent.putExtra("hostname", MainActivity.hostname);
//	 intent.putExtra("protocol", MainActivity.protocol);
//	 intent.putExtra("port", MainActivity.port);
//	 intent.putExtra("username", MainActivity.username);
//	 intent.putExtra("password", MainActivity.password);
//	
//	 startActivityForResult(intent, MainActivity.ACTION_CODE);
	 }   
 
}