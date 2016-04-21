package test_2;
/*
 ***********************
 * CS551 Qinyao Xu
 * Project 1
 * P2P System
 * class IndexServer
 * index server has the contents of all the peer
 * 09/03/2015
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class IndexServer{
	
	volatile static List<Peer> activePeers;
	
	volatile static List<PeerTrack> trackIndexes;
	
	private static ServerSocket serverSocket;
	
	public final static int PORT = 3333;
	
	public List<Peer> getActivePeers() {
		return activePeers;//return the in use peer
	}

	public List<PeerTrack> getTrackIndexes() {
		return trackIndexes;// return the peer index
	}
	
	public static void start() throws IOException{
		
		// starting the server
		InetAddress addr = InetAddress.getByName("127.0.0.1");
		serverSocket = new ServerSocket(PORT,1000,addr);
		activePeers = new LinkedList<Peer>();
		trackIndexes = new LinkedList<PeerTrack>();	
		waitForConnections();
	}

	private static void waitForConnections() throws IOException {
		// crate thread and connect the server to client
		List<Thread> threads = new ArrayList<Thread>();
		int connections = 1;
		while(true){
			System.out.println("Waiting for client connect to the port "+serverSocket.getLocalPort()+"...");
			Socket server = serverSocket.accept();
			Thread newThread = new Thread(new PeerConnection(server, connections++));
			newThread.start();
			threads.add(newThread);
			System.out.println("number of in running threads : "+threads.size()+"\n");
		}
	}

	
	
	public static void joinSystem(String hostName, int portNumber, List<PeerTrack> peerTrack){
		// create a new process which controls the actions of a peer
		Peer NewPeer = new Peer(hostName, portNumber);
		activePeers.add(0,  NewPeer);
		
		for(PeerTrack index : peerTrack){
			trackIndexes.add(0, index);
		}
	}
	
	public static void leaveSystem(Peer peer) {
		//remove a peer form the system
		Iterator iter;
		synchronized (activePeers) {
			iter = activePeers.listIterator();
			while (iter.hasNext()) {
				//iterator the whole list to find
				Peer p = (Peer) iter.next();
				if (p.getHostName().equals(peer.getHostName())) {
					iter.remove();
				}
			}
		}

		synchronized (trackIndexes) {
			iter = trackIndexes.listIterator();
			while (iter.hasNext()) {
				//iterator the whole list to find
				PeerTrack r = (PeerTrack) iter.next();
				if (r.getPeerHostName().equals(peer.getHostName())) {
					iter.remove();
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		start();
	}

	public static boolean findIndex(PeerTrack track) {
		//return the information about find a peer or not
		Iterator iter = trackIndexes.listIterator();
		while (iter.hasNext()) {
			PeerTrack r = (PeerTrack) iter.next();
			if (r.getNumber() == track.getNumber()) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<String> getPeerList(PeerTrack track) {
		//return the whole peer list
		ArrayList<String> response = new ArrayList<String>();
		for( PeerTrack r : trackIndexes) {
			if (r.getNumber() == track.getNumber()) {
				for (Peer p : activePeers) {
					if (p.getHostName().equals(r.getPeerHostName())) {
						String s = "Peer "+r.getNumber()+" "+r.getTitle()+" "+r.getPeerHostName()+" "+p.getPortNumber();
						response.add(s);
					}
				}
			}
		}
		
		return response;
	}
	
	public static ArrayList<String> getAllPeers() {
		//get the whole peer point
		ArrayList<String> response = new ArrayList<String>();
		for( PeerTrack r : trackIndexes) {
			for (Peer p : activePeers) {
				if (p.getHostName().equals(r.getPeerHostName())) {
					String s = "Peer "+r.getNumber()+" "+r.getTitle()+" "+r.getPeerHostName()+" "+p.getPortNumber();
					response.add(s);
				}
			}
		}
		
		return response;
	}
	
	
	public static PeerTrack getPeerIndex(String hostName, int Number){
		PeerTrack index = null;
		
		for(PeerTrack r : trackIndexes){
			if(r.getPeerHostName().equals(hostName) && index.getNumber() == Number){
				index= new PeerTrack(r.getNumber(), r.getTitle(), r.getPeerHostName());
			}
		}
		
		return index;
	}
}