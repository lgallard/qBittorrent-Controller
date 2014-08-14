/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Luis M. Gallardo D. - initial implementation
 ******************************************************************************/
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
		// inflater.inflate(R.menu.main, menu);
		// super.onCreateOptionsMenu(menu, inflater);

		if (menu != null) {
			menu.findItem(R.id.action_refresh).setVisible(true);
			menu.findItem(R.id.action_add).setVisible(true);

			if (menu.findItem(R.id.action_resume_all) != null) {
				menu.findItem(R.id.action_resume_all).setVisible(false);
			}
			
			if (menu.findItem(R.id.action_pause_all) != null) {
				menu.findItem(R.id.action_pause_all).setVisible(false);
			}

			
			if (menu.findItem(R.id.action_resume) != null) {
				menu.findItem(R.id.action_resume).setVisible(false);
			}
			if (menu.findItem(R.id.action_pause) != null) {
				menu.findItem(R.id.action_pause).setVisible(false);
			}
			if (menu.findItem(R.id.action_increase_prio) != null) {
				menu.findItem(R.id.action_increase_prio).setVisible(false);
			}
			if (menu.findItem(R.id.action_decrease_prio) != null) {
				menu.findItem(R.id.action_decrease_prio).setVisible(false);

			}
			if (menu.findItem(R.id.action_delete) != null) {
				menu.findItem(R.id.action_delete).setVisible(false);
			}
			if (menu.findItem(R.id.action_delete_drive) != null) {
				menu.findItem(R.id.action_delete_drive).setVisible(false);
			}
			
			if (menu.findItem(R.id.action_upload_rate_limit) != null) {
				menu.findItem(R.id.action_upload_rate_limit).setVisible(false);
			}
			
			if (menu.findItem(R.id.action_download_rate_limit) != null) {
				menu.findItem(R.id.action_download_rate_limit).setVisible(false);
			}
			

		}
	}
	

}
