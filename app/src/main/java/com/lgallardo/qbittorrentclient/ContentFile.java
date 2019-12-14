/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */

package com.lgallardo.qbittorrentclient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContentFile {
	// New format
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("size")
	@Expose
	private double size;
	@SerializedName("progress")
	@Expose
	private double progress;
	@SerializedName("priority")
	@Expose
	private int priority;
	@SerializedName("recyclerViewItemHeight")
	@Expose
	private int recyclerViewItemHeight;
	@SerializedName("isSeed")
	@Expose
	private boolean isSeed;
	@SerializedName("piece_range")
	@Expose
	private int[] piece_range;
	@SerializedName("douavailability")
	@Expose
	private double douavailability;

	public ContentFile(String name, double size, Double progress, int priority) {

		this.name = name;
		this.size = size;
		this.progress = progress;
		this.priority = priority;

	}

	public String getName() {
		return name;
	}

	public String getSize() {
		return Common.calculateSize(size);
	}

	public double getProgress() {
		return progress;
	}

	public int getPriority() {
		return priority;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public void setProgress(Double progress) {
		this.progress = progress;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public String getProgressAsString(){
		return String.format("%.2f", this.progress * 100) + "%";
	}

	public int getRecyclerViewItemHeight() {
		return recyclerViewItemHeight;
	}

	public void setRecyclerViewItemHeight(int recyclerViewItemHeight) {
		this.recyclerViewItemHeight = recyclerViewItemHeight;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

	public boolean isSeed() {
		return isSeed;
	}

	public void setSeed(boolean seed) {
		isSeed = seed;
	}

	public int[] getPiece_range() {
		return piece_range;
	}

	public void setPiece_range(int[] piece_range) {
		this.piece_range = piece_range;
	}

	public double getDouavailability() {
		return douavailability;
	}

	public void setDouavailability(double douavailability) {
		this.douavailability = douavailability;
	}
}
