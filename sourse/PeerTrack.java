package test_2;
/*
 ***********************
 * CS551 Qinyao Xu
 * Project 1
 * P2P System
 * class PeerTrack
 * track the thread in the system
 * 09/03/2015
 */
public class PeerTrack {
	
	private int number;
	
	private String title;
	
	private String peerHostName;
//constructor
	public PeerTrack(int num, String Name, String host) {
		this.number = num;
		this.title = Name;
		this.peerHostName = host;
	}
//basic function
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPeerHostName() {
		return peerHostName;
	}

	public void setPeerHostName(String belongsToHost) {
		this.peerHostName = belongsToHost;
	}
	
}