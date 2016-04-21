package test_2;
/*
 ***********************
 * CS551 Qinyao Xu
 * Project 1
 * P2P System
 * class Peer
 * basic peer information
 * 09/03/2015
 */
public class Peer {
	
	private String hostName;
	
	private int portNumber;
	
	public Peer(String hostName, int portNumber) {
		this.hostName = hostName;
		this.portNumber = portNumber;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

}