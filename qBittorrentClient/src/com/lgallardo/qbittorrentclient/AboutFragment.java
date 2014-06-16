package com.lgallardo.qbittorrentclient;

import android.app.Fragment;
import android.view.*;
import android.os.*;

public class AboutFragment extends Fragment {



	public AboutFragment() {		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		// Tell the host activity that your fragment has menu options that it
		// wants to add/replace/delete using the onCreateOptionsMenu method.
		setHasOptionsMenu(true);


		View rootView = inflater.inflate(R.layout.about, container,
										 false);

		return rootView;
	}
	
	// @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (menu != null) {
			menu.findItem(R.id.action_refresh).setVisible(true);
			menu.findItem(R.id.action_add).setVisible(true);
			menu.findItem(R.id.action_resume_all).setVisible(true);
			menu.findItem(R.id.action_pause_all).setVisible(true);
			
			menu.findItem(R.id.action_resume).setVisible(false);
			menu.findItem(R.id.action_pause).setVisible(false);
			menu.findItem(R.id.action_delete).setVisible(false);
			menu.findItem(R.id.action_delete_drive).setVisible(false);
	
		}
	}

}
