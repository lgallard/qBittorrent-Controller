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

class Torrent {

	private String file;
	private String size;
	private String info;
	private String state;
	private String hash;
	private String downloadSpeed;
	private String uploadSpeed;
	private String ratio;
	private String progress;
	private String downloaded;
	private String leechs;
	private String seeds;
	private String priority;
	private String eta;

	private String savePath;
	private String creationDate;
	private String comment;
	private String totalWasted;
	private String totalUploaded;
	private String totalDownloaded;
	private String timeElapsed;
	private String nbConnections;
	private String shareRatio;
	private String uploadLimit;
	private String downloadLimit;

	public Torrent(String file, String size, String state, String hash, String info, String ratio, String progress, String leechs, String seeds,
			String priority, String eta, String downloadSpeed, String uploadSpeed) {
		this.file = file;
		this.size = size;
		this.state = state;
		this.hash = hash;
		this.info = info;
		this.ratio = ratio;
		this.progress = progress;
		this.leechs = leechs;
		this.seeds = seeds;
		this.priority = priority;
		this.eta = eta;
		this.downloadSpeed = downloadSpeed;
		this.uploadSpeed = uploadSpeed;

	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}

	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @return the downloadSpeed
	 */
	public String getDownloadSpeed() {
		return downloadSpeed;
	}

	/**
	 * @return the uploadSpeed
	 */
	public String getUploadSpeed() {
		return uploadSpeed;
	}

	/**
	 * @return the ratio
	 */
	public String getRatio() {
		return ratio;
	}

	/**
	 * @return the progress
	 */
	public String getProgress() {
		return progress;
	}

	/**
	 * @return the downloaded size
	 */
	public String getDownloaded() {
		return downloaded;
	}

	/**
	 * @return the leechs
	 */
	public String getLeechs() {
		return leechs;
	}

	/**
	 * @return the seeds
	 */
	public String getSeeds() {
		return seeds;
	}

	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}

	/**
	 * @return the eta
	 */
	public String getEta() {
		return eta;
	}

	/**
	 * @return the path
	 */
	public String getSavePath() {
		return savePath;
	}

	/**
	 * @return the creationDate
	 */
	public String getCreationDate() {
		return creationDate;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return the totalWasted
	 */
	public String getTotalWasted() {
		return totalWasted;
	}

	/**
	 * @return the totalUploaded
	 */
	public String getTotalUploaded() {
		return totalUploaded;
	}

	/**
	 * @return the totalDownloaded
	 */
	public String getTotalDownloaded() {
		return totalDownloaded;
	}

	/**
	 * @return the timeElapsed
	 */
	public String getTimeElapsed() {
		return timeElapsed;
	}

	/**
	 * @return the nbConnections
	 */
	public String getNbConnections() {
		return nbConnections;
	}

	/**
	 * @return the shareRatio
	 */
	public String getShareRatio() {
		return shareRatio;
	}

	public String getUploadLimit() {
		return uploadLimit;
	}

	public String getDownloadLimit() {
		return downloadLimit;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * @param info
	 *            the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @param hash
	 *            the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @param downloadSpeed
	 *            the downloadSpeed to set
	 */
	public void setDownloadSpeed(String downloadSpeed) {
		this.downloadSpeed = downloadSpeed;
	}

	/**
	 * @param uploadSpeed
	 *            the uploadSpeed to set
	 */
	public void setUploadSpeed(String uploadSpeed) {
		this.uploadSpeed = uploadSpeed;
	}

	/**
	 * @param ratio
	 *            the ratio to set
	 */
	public void setRatio(String ratio) {
		this.ratio = ratio;
	}

	/**
	 * @param progress
	 *            the progress to set
	 */
	public void setProgress(String progress) {
		this.progress = progress;
	}

	/*
	 * @param downloaded the downloaded to set
	 */
	public void setDownloaded(String downloaded) {
		this.downloaded = downloaded;
	}

	/**
	 * @param leechs
	 *            the leechs to set
	 */
	public void setLeechs(String leechs) {
		this.leechs = leechs;
	}

	/**
	 * @param seeds
	 *            the seeds to set
	 */
	public void setSeeds(String seeds) {
		this.seeds = seeds;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	 * @param priority
	 *            the eta to set
	 */
	public void setEta(String eta) {
		this.eta = eta;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @param totalWasted
	 *            the totalWasted to set
	 */
	public void setTotalWasted(String totalWasted) {
		this.totalWasted = totalWasted;
	}

	/**
	 * @param totalUploaded
	 *            the totalUploaded to set
	 */
	public void setTotalUploaded(String totalUploaded) {
		this.totalUploaded = totalUploaded;
	}

	/**
	 * @param totalDownloaded
	 *            the totalDownloaded to set
	 */
	public void setTotalDownloaded(String totalDownloaded) {
		this.totalDownloaded = totalDownloaded;
	}

	/**
	 * @param timeElapsed
	 *            the timeElapsed to set
	 */
	public void setTimeElapsed(String timeElapsed) {
		this.timeElapsed = timeElapsed;
	}

	/**
	 * @param nbConnections
	 *            the nbConnections to set
	 */
	public void setNbConnections(String nbConnections) {
		this.nbConnections = nbConnections;
	}

	/**
	 * @param shareRatio
	 *            the shareRatio to set
	 */
	public void setShareRatio(String shareRatio) {
		this.shareRatio = shareRatio;
	}

	public void setUploadLimit(String uploadLimit) {
		this.uploadLimit = uploadLimit;
	}

	public void setDownloadLimit(String downloadLimit) {
		this.downloadLimit = downloadLimit;
	}

}
