package test_2;
/*
 ***********************
 * CS551 Qinyao Xu
 * Project 1
 * P2P System
 * class PeerConnection
 * connect the index server to the peer 
 * 09/03/2015
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



public class PeerConnection implements Runnable{
	
	private Socket clientSocket;
	List<Peer> activePeers;
	List<PeerTrack> trackIndexes;
	int connectionNumber;
	
	DataInputStream in;
	DataOutputStream out;
 	
	public PeerConnection(Socket socket, int connectionNumber) throws IOException{
		
		this.connectionNumber = connectionNumber;
		this.clientSocket = socket;

		// get the input and output stream
		in = new DataInputStream(clientSocket.getInputStream());
		out = new DataOutputStream(clientSocket.getOutputStream());
		activePeers = IndexServer.activePeers;
		trackIndexes = IndexServer.trackIndexes;
		
		
	}
	
	Peer peer;
	
	@Override
	public void run() {
		try{//connect the index server to the peer client
			System.out.println("Connected to "+clientSocket.getRemoteSocketAddress());
			String host = in.readUTF();
			int port = Integer.parseInt(in.readUTF());
			peer = new Peer(host, port);	
			List<PeerTrack> TrackList = new LinkedList<PeerTrack>();
			while(true){
				
				String input = in.readUTF();
				System.out.println(input);
				
				if(input.equals("end")){
					break;
				}
				
				// add peerTrack to the indexes list
				String[] temp = input.split(",");
				PeerTrack track= new PeerTrack(Integer.parseInt(temp[0]),temp[1],host);
				TrackList.add(track);
			}
			
			IndexServer.joinSystem(host, port, TrackList);
			
			System.out.println(host +" on "+port+" has "+TrackList.size()+" peers added");
			System.out.println("connection number : "+connectionNumber);
			
			presentOptionsToPeer(host);
			
			out.writeUTF("Success connect to "+clientSocket.getLocalSocketAddress() +"\nBye");
			clientSocket.close();
		}
		catch(SocketTimeoutException s)
		{
			System.out.println("Socket timed out");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void presentOptionsToPeer(String host) throws IOException {
		boolean exit = false;
		
		while(true){
			//deal with user's request and 
			out.writeUTF("following function are available with the server:\n");
			out.writeUTF("1. ADD\n"
					+ "2. LOOKUP\n"
					+ "3. LIST\n"
					+ "4. Download\n"
					+ "5. End\n");
			
			// reading the option header
			String input = in.readUTF();
			System.out.println(input);
			String[] temp = input.split(" ");
			
			int trackNum, resSize;
			String version, title;
			PeerTrack track;
			
			ArrayList<String> response;
			
			switch (temp[0].toLowerCase()) {
			// add peer index to the indexes list
			case "add":
				
				trackNum = Integer.parseInt(temp[2]);
				version = temp[3];
				
				// reading the host header
				input = in.readUTF();
				System.out.println(input);
				
				
				// reading the port header
				input = in.readUTF();
				System.out.println(input);
				
				
				// reading the title header
				input = in.readUTF();
				temp = input.split(" ");
				title = temp[1];
				System.out.println(input);
				
				track = new PeerTrack(trackNum, title, host);
				
				// adding the peer index to the central index list
				trackIndexes.add(0, track);
				
				System.out.println("added peer now");
				break;
			
			case "lookup":
				
				trackNum = Integer.parseInt(temp[2]);
				version = temp[3];
				
				// reading the host header
				input = in.readUTF();
				System.out.println(input);
				
				
				// reading the port header
				input = in.readUTF();
				System.out.println(input);
				
				
				// reading the title header
				input = in.readUTF();
				temp = input.split(" ");
				title = temp[1];
				System.out.println(input);
				
				track = new PeerTrack(trackNum, title, host);
				
				// find if a peer  exists
				response = IndexServer.getPeerList(track);
				resSize = response.size();
				
				if(resSize != 0){
					out.writeUTF("P2P-CI/1.0 200 OK");
					for(String s : response){
						out.writeUTF(s);
					}
				}
				else{
					out.writeUTF("P2P-CI/1.0 404 Not Found");
				}
				
				out.writeUTF("end");
				
				break;
				
			case "list":
				String type = temp[1];
				version = temp[2];
				
				// reading the host header
				input = in.readUTF();
				System.out.println(input);
				
				// reading the port header
				input = in.readUTF();
				System.out.println(input);
				
				response = IndexServer.getAllPeers();
				resSize = response.size();
				
				if(resSize != 0){
					out.writeUTF("P2P-CI/1.0 200 OK");
					for(String s : response){
						out.writeUTF(s);
					}
				}
				else{
					out.writeUTF("P2P-CI/1.0 404 Not Found");
				}
				
				out.writeUTF("end");
				
				break;
				
			case "download":
				break;
				
			case "end":
				out.writeUTF("server was asked to close the connection. \n");
				// remove the client rfc's from the central index
				IndexServer.leaveSystem(peer);
				exit = true;
				break;
				
			default:
				System.out.println("exiting now");
				break;
			}
			
			if(exit)
				break;
		}
	}
}