package com.lgallardo.qbittorrentclient;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


public class TorrentDetailsFragment extends Fragment {
	
	// Torrent variables
	String name, info, hash, ratio, size, progress, state, leechs, seeds, priority = "";

	String hostname;
	String protocol;
	int port;
	String username;
	String password;
	
	int position;

	 
    public TorrentDetailsFragment() {
    }
    
    
    public void setPosition(int position){
    	this.position = position;
    }
    
    public int getPosition(){
    	return this.position;
    }
    
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.torrent_details, container, false);

		
//		Intent intent = getIntent();
        
        if( MainActivity.lines != null){
    		name = MainActivity.lines[position].getFile();
    		size = MainActivity.lines[position].getSize();
        }

//		info = intent.getStringExtra(MainActivity.TAG_INFO);
//		hash = intent.getStringExtra(MainActivity.TAG_HASH);
//		ratio = intent.getStringExtra(MainActivity.TAG_RATIO);
//		size = intent.getStringExtra(MainActivity.TAG_SIZE);
//		progress = intent.getStringExtra(MainActivity.TAG_PROGRESS);
//		state = intent.getStringExtra(MainActivity.TAG_STATE);
//		leechs = intent.getStringExtra(MainActivity.TAG_NUMLEECHS);
//		seeds = intent.getStringExtra(MainActivity.TAG_NUMSEEDS);
//		priority = intent.getStringExtra(MainActivity.TAG_PRIORITY);
//		
//
//		hostname = intent.getStringExtra("hostname");
//		protocol = intent.getStringExtra("protocol");
//		port = intent.getIntExtra("port", 0);
//		username = intent.getStringExtra("username");
//		password = intent.getStringExtra("password");
//
//		Log.i("TorrentAA", "port: " + port);

		TextView nameTextView = (TextView) rootView.findViewById(R.id.torrentName);
		TextView infoTextView = (TextView) rootView.findViewById(R.id.torrentSize);
//		TextView ratioTextView = (TextView) this.findViewById(R.id.torrentRatio);
//		TextView progressTextView = (TextView) this.findViewById(R.id.torrentProgress);
//		TextView stateTextView = (TextView) this.findViewById(R.id.torrentState);
//		TextView leechsTextView = (TextView) this.findViewById(R.id.torrentLeechs);
//		TextView seedsTextView = (TextView) this.findViewById(R.id.torrentSeeds);
//		TextView hashTextView = (TextView) this.findViewById(R.id.torrentHash);
//		TextView priorityTextView = (TextView) this.findViewById(R.id.torrentPriority);

		nameTextView.setText(name);

//		progressTextView.setText("Progress: " + progress);
		infoTextView.setText("Size: " + size);
//		ratioTextView.setText("Ratio: " + ratio);
//		stateTextView.setText("State: " + state);
//		leechsTextView.setText("Leechs: " + leechs);
//		seedsTextView.setText("Seeds: " + seeds);
//		hashTextView.setText("Hash: " + hash);
//		priorityTextView.setText("Priority: "+priority);
		
		

        
        //setListAdapter(new myAdapter());
 
        return rootView;
    }
    
    
	// @Override
	 public void onListItemClick(ListView parent, View v, int position, long
	 id) {
		 
			Log.i("FragmentLIst", "Item touched");
			
			
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