package com.lgallardo.qbittorrentclient;

class Torrent {

	private String file;
	private String size;
	private String info;
	private String state;
	private String hash;
	private String downloadSpeed;
	private String ratio;
	private String progress;
	private String leechs;
	private String seeds;
	private String priority;

	public Torrent(String file, String size, String state, String hash,
					String info, String ratio, String progress, String leechs,
					String seeds, String priority) {
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
	}

	public String getFile() {
		return this.file;
	}

	public String getSize() {
		return this.size;
	}

	public String getState() {
		return this.state;
	}

	public String getHash() {
		return this.hash;
	}

	public String getInfo() {
		return this.info;
	}

	public String getRatio() {
		return this.ratio;
	}

	public String getProgress() {
		return this.progress;
	}

	public String getLeechs() {
		return this.leechs;
	}

	public String getSeeds() {
		return this.seeds;
	}
	
	public String getPriority(){
		return this.priority;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
