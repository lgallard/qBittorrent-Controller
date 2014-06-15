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

}
