package com.lgallardo.qbittorrentclient;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class TorrentDetailsFragment extends Fragment {

	// Torrent variables
	String name, info, hash, ratio, size, progress, state, leechs, seeds,
			priority = "";

	String hostname;
	String protocol;
	int port;
	String username;
	String password;

	int position;

	public TorrentDetailsFragment() {
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return this.position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Tell the host activity that your fragment has menu options that it
		// wants to add/replace/delete using the onCreateOptionsMenu method.
		setHasOptionsMenu(true);

		View rootView = inflater.inflate(R.layout.torrent_details, container,
				false);

		if (MainActivity.lines != null) {
			name = MainActivity.lines[position].getFile();
			size = MainActivity.lines[position].getSize();
			hash = MainActivity.lines[position].getHash();
			ratio = MainActivity.lines[position].getRatio();
			progress = MainActivity.lines[position].getProgress();
			state = MainActivity.lines[position].getState();
			leechs = MainActivity.lines[position].getLeechs();
			seeds = MainActivity.lines[position].getSeeds();
			hash = MainActivity.lines[position].getHash();
			priority = MainActivity.lines[position].getPriority();

		}

		TextView nameTextView = (TextView) rootView
				.findViewById(R.id.torrentName);
		TextView sizeTextView = (TextView) rootView
				.findViewById(R.id.torrentSize);
		TextView ratioTextView = (TextView) rootView
				.findViewById(R.id.torrentRatio);
		TextView progressTextView = (TextView) rootView
				.findViewById(R.id.torrentProgress);
		TextView stateTextView = (TextView) rootView
				.findViewById(R.id.torrentState);
		TextView leechsTextView = (TextView) rootView
				.findViewById(R.id.torrentLeechs);
		TextView seedsTextView = (TextView) rootView
				.findViewById(R.id.torrentSeeds);
		TextView hashTextView = (TextView) rootView
				.findViewById(R.id.torrentHash);
		TextView priorityTextView = (TextView) rootView
				.findViewById(R.id.torrentPriority);

		nameTextView.setText(name);
		sizeTextView.setText("Size: " + size);
		ratioTextView.setText("Ratio: " + ratio);
		progressTextView.setText("Progress: " + progress);
		stateTextView.setText("State: " + state);
		leechsTextView.setText("Leechs: " + leechs);
		seedsTextView.setText("Seeds: " + seeds);
		hashTextView.setText("Hash: " + hash);
		priorityTextView.setText("Priority: " + priority);

		return rootView;
	}

	// @Override
	public void onListItemClick(ListView parent, View v, int position, long id) {

		Log.i("FragmentLIst", "Item touched");
	}

	// @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.torrent_details, menu);
		super.onCreateOptionsMenu(menu, inflater);

		if (menu != null) {
			menu.findItem(R.id.action_refresh).setVisible(false);
			Log.i("menu", "Menu deleted");
		}

	}

}