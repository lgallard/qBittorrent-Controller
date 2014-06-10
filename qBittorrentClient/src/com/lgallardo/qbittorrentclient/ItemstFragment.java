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
	
	int secondContainer;

	public ItemstFragment() {
	}
	
	public void setSecondFragmentContainer(int container){
		
		this.secondContainer = container;
		
	}
	
	public int getSecondFragmentContainer(){
		
		return this.secondContainer;
		
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_main_original,
				container, false);
		
		return rootView;
	}

	// @Override
	public void onListItemClick(ListView parent, View v, int position, long id) {

		Log.i("FragmentLIst", "Item touched");

		TorrentDetailsFragment fragment = new TorrentDetailsFragment();

		fragment.setPosition(position);
						

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(this.getSecondFragmentContainer(), fragment, "second")
					.addToBackStack(null)
					.commit();
		}

	}


	

}