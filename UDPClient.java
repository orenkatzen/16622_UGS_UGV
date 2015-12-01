import java.io.*; 
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class UDPClient {

	//arrays to store timestamps, probably more than we need, but won't slow down code
	static ArrayList<String> list1= new ArrayList<String>(); 

	static int i=1; //counter for first pass or not
	static int n=0; //counter for packets received
	static byte[] receiveData= new byte[15]; //preallocate memory for packet
	static DatagramSocket clientSocket;
	static InetAddress IPAddress;
	static DatagramPacket receivePacket;

	public static void main (String args[]) throws Exception{
		clientSocket= new DatagramSocket();
		IPAddress= InetAddress.getByName("192.168.1.2"); //address of UGS
		System.out.println("UDP Client is Running");

		while(true){ //infinite loop

			while (i==1){ //while still trying to establish with UGS

				//send message "go" to establish connection
				byte[] sendData= new byte[16]; 
				String sentence= "go";
				boolean fail=false;
				sendData= sentence.getBytes();
				DatagramPacket sendPacket= new DatagramPacket(sendData,  sendData.length, IPAddress, 9876);
				clientSocket.send(sendPacket);


				clientSocket.setSoTimeout(1); //set timeout if no packet received
				receivePacket= new DatagramPacket(receiveData, receiveData.length);
				//try to receive packet
				try {
					clientSocket.receive(receivePacket);
				} 
				//if no packet received, set boolean to true
				catch (SocketTimeoutException e) { //end of test code
					fail= true;
				}
				//if it did not fail, increment packets received and counter to enter data download section
				finally{
					if (fail==false){
						System.out.println("Connection established");
						n++; //increment files received
						i++; //go to mass download section
					}

				}
				//				System.out.println(i);
			}
			String packetReceived = new String(receivePacket.getData());
			list1.add(packetReceived);

			//massive download section
			clientSocket.setSoTimeout(5000); //ten second timeout if no packet received
			while(true) { //infinite loop
				receivePacket= new DatagramPacket(receiveData, receiveData.length);
				try {
					clientSocket.receive(receivePacket);
				} 

				catch (SocketTimeoutException e) { //end of test code
					System.out.println("Lost Connection");
					endOfTest(n);
					clientSocket.close();
					System.exit(0);
				}
				packetReceived = new String(receivePacket.getData());
				list1.add(packetReceived);
				n++;
			}
		}
	}





	/*
	 end of test method. 
	 writes text files
	 */
	public static void endOfTest(int n) throws IOException {
		System.out.println("Test Ended");
		//Write Test Overview
		//Write to USB
		String total= Integer.toString(n);

		String newLine = System.getProperty( "line.separator" );
		String date = "/media/JAKEOREN____/"+total+".txt";
		FileWriter bfw=new FileWriter(date);
		bfw.write("Test Ended at " + LocalDateTime.now().toString());
		bfw.write(newLine);
		bfw.write("Total Packets Received: " +total);
		bfw.flush();
		bfw.close();
		//Write to Desktop as Backup
		date = "/home/pi/Desktop/"+total+".txt";
		FileWriter bfw2=new FileWriter(date);
		bfw2.write("Test Ended at " + LocalDateTime.now().toString());
		bfw2.write(newLine);
		bfw2.write("Total Packets Received: " +total);
		bfw2.flush();
		bfw2.close();


		System.out.println("N = " +n);
		System.out.println("Number of Timestamp files = " + (list1.size()));
		//Write Files of TimeStamps
		//Write to USB

		String name ="/media/JAKEOREN____/"+ total+"timestamps"+".txt";
		FileWriter bw=new FileWriter(name);
		for(String str: list1) {
			bw.write(str);
			bw.write(newLine);
		}
		bw.flush();
		bw.close();

		//write to local drive as backup
		name = "/home/pi/Desktop/"+ total+"timestamps"+".txt";
		FileWriter bw2=new FileWriter(name);
		for(String str: list1) {
			bw2.write(str);
			bw2.write(newLine);
		}
		bw2.flush();
		bw2.close();

	}
}