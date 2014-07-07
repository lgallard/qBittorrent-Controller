package com.lgallardo.qbittorrentclient;

import android.app.Fragment;
import android.view.*;
import android.os.*;

public class HelpFragment extends Fragment {



	public HelpFragment() {		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		// Tell the host activity that your fragment has menu options that it
		// wants to add/replace/delete using the onCreateOptionsMenu method.
		setHasOptionsMenu(true);


		View rootView = inflater.inflate(R.layout.tablet_help, container,
										 false);

		return rootView;
	}
	

}
