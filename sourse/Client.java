package test_2;
/*
 ***********************
 * CS551 Qinyao Xu
 * Project 1
 * P2P System
 * class Client
 * class Client used to verify the client function 
 * 09/03/2015
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
	
	static DataOutputStream data_out;
	static DataInputStream in;
	
	static String directoryName = "C:\\p2pfiles\\";
	
	public static void main(String[] args) {
		String serverName = "127.0.0.1";
		int port = 3333, peer_count =0,peerNumber;
		String title ="Demo peer";
		int option;
		boolean exit = false;

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try
		{
			System.out.println("Connecting to "+serverName +" on port "+port);
			// this is where the client is connecting to the central server
			Socket client = new Socket(serverName,port);
			System.out.println("Just connected to "+client.getRemoteSocketAddress());
			
			
			// start the peer upload connection
			// spawn PeerUpload
			Thread t = new Thread(new PeerUpload(directoryName));
			t.start();
			
			OutputStream out = client.getOutputStream();
			data_out = new DataOutputStream(out);
			data_out.writeUTF(client.getLocalAddress().toString());
			data_out.writeUTF(Integer.toString(client.getLocalPort()));
			System.out.println("How many peers do you have?");
			peer_count = Integer.parseInt(reader.readLine());
			for(int i=0;i<peer_count;i++)
			{
				System.out.println("Enter peer number "+(i+1));
				peerNumber= Integer.parseInt(reader.readLine());
				System.out.println("Enter peer title");
				title = reader.readLine();
				data_out.writeUTF(peerNumber+","+title);
			}
			data_out.writeUTF("end");
			InputStream inFromServer = client.getInputStream();
			in = new DataInputStream(inFromServer);
			String peerNum,Title,versionNum;
			while(true)
			{
				System.out.println("");
				System.out.println(in.readUTF());
				System.out.println(in.readUTF());
				option = Integer.parseInt(reader.readLine());
				switch(option)
				{
				case 1: //Add
					System.out.println("Add option selected");
					System.out.println("Enter peer number :");
					 peerNum = reader.readLine();
					
					System.out.println("Enter file title");
					Title = reader.readLine();
					data_out.writeUTF("ADD peer "+ peerNum+" "+"P2P-CI/1.0");
					data_out.writeUTF("Host: "+client.getLocalAddress().toString());
					data_out.writeUTF("Port: "+client.getLocalPort());
					data_out.writeUTF("Title: "+Title);
					break;
					
				case 2: // Lookup
					System.out.println("Lookup option selected");
					System.out.println("Enter peer number :");
					 peerNum = reader.readLine();
					
					System.out.println("Enter peer title :");
					Title = reader.readLine();
					data_out.writeUTF("LOOKUP peer "+ peerNum+" "+"P2P-CI/1.0");
					data_out.writeUTF("Host: "+client.getLocalAddress().toString());
					data_out.writeUTF("Port: "+client.getLocalPort());
					data_out.writeUTF("Title: "+Title);
					System.out.println("");
					
					//Displaying input reader from server
					while(true)
					{
						String temp = in.readUTF();
						if(temp.equals("end"))
							break;
						System.out.println(temp);
					}
					break;
					
				case 3: // List
					System.out.println("List option selected");
					data_out.writeUTF("LIST ALL P2P-CI/1.0");
					data_out.writeUTF("Host: "+client.getLocalAddress());
					data_out.writeUTF("Port: "+client.getLocalPort());
					System.out.println("");
					while(true)
					{
						String temp = in.readUTF();
						if(temp.equals("end"))
							break;
						System.out.println(temp);
					}
					break;
				
				case 4: // download  file
					downloadFile(reader);
					System.out.println("i am  download now");
					break;
					
				case 5: // Close connection
					data_out.writeUTF("end");
					exit = true;
					System.out.println(in.readUTF());
					
					client.close();
					System.exit(0);    
					break;
				}
				
				//Checking for  exit condition
				if(exit)
					break;
			}
			System.out.println(in.readUTF());
			client.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}

	private static void downloadFile(BufferedReader reader) throws IOException {

		String filePath = "C:\\p2pfiles\\";

		System.out.println("Enter peer Number");
		String FileNumber = reader.readLine();
		String FileName = "file " + FileNumber +".txt";
		
		System.out.println("Enter file Title");
		String Title = reader.readLine();
		
		System.out.println("Enter file Version");
		String versionNumber = reader.readLine();
		
		System.out.println("Enter Host Name");
		String hostName = reader.readLine();

		
		int portNumber = 3334;
		
		System.out.println("downloading begins now...");
		
		// check if the entered RFC number is available for download

		// if available establish a connection and download the file from the appropriate peer.

		System.out.println("Connecting to host " + hostName + " on port.");
		System.out.println("clients port number is "+portNumber);
		Socket peerClient = new Socket(hostName, portNumber);

		DataInputStream in = new DataInputStream(peerClient.getInputStream());
		DataOutputStream out = new DataOutputStream(peerClient.getOutputStream());

		String msgToSend = "GET File ";
		msgToSend += FileNumber;
		msgToSend += " P2P-CI/"+versionNumber+" ";
		msgToSend += "Host: "+hostName+" ";
		msgToSend += "OS: " + System.getProperty("os.name");

		out.writeUTF(msgToSend);
		
		String response = in.readUTF();
		System.out.println(response);
		
		boolean addRFCDetails = false;
		
		if(response.indexOf("200 OK") != -1){
			byte[] b = new byte[1024];
			int len = 0;
			int bytcount = 1024;
			FileOutputStream inFile = new FileOutputStream(filePath + FileName);
			InputStream peerInputStream = peerClient.getInputStream();
			BufferedInputStream pis = new BufferedInputStream(peerInputStream, 1024);
			while ((len = pis.read(b, 0, 1024)) != -1) {
				bytcount = bytcount + 1024;
				inFile.write(b, 0, len);
				System.out.println(new String(b));
			}
			
			peerInputStream.close();
			pis.close();
			inFile.close();
			
			addRFCDetails = true;
		}
		else if(response.indexOf("505 P2P-CI Version Not Supported") != -1){
			System.out.println("Bad version error");
		}
		else if(response.indexOf("404 Not Found") != -1){
			System.out.println("The peer is a liar. It does not have the file.");
		}
		else if(response.indexOf("400 Bad Request") != -1){
			System.out.println("Dude get the rfc details right");
		}
		
		in.close();
		out.close();
		peerClient.close();
		
		System.out.println("i am here right now");
		if(addRFCDetails){
			// send details to the server
			data_out.writeUTF("ADD RFC "+FileNumber+" "+"P2P-CI/"+versionNumber);
			data_out.writeUTF("Host: "+peerClient.getLocalAddress().toString());
			data_out.writeUTF("Port: "+peerClient.getLocalPort());
			data_out.writeUTF("Title: "+Title);
		}
		else{
			data_out.writeUTF("Download");
		}
		System.out.println("i have just sent the details to the server");
	}
}