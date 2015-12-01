import java.net.*;

public class UDPServer {
	static byte[] receiveData= new byte[15]; //array of bytes received
	static byte[] sendData = new byte[15]; //array of bytes to send

	public static void main(String args[]) throws Exception{
		@SuppressWarnings("resource")
		DatagramSocket serverSocket= new DatagramSocket(9876); //create socket at some port

		System.out.println("UDP Server is Running");
		InetAddress IPAddress= null; //client IPaddress
		int port=0; //client port

		int i=1; //counter to identify if client has locked on yet
		while (true){ //infinite loop

			if (i==1){
				DatagramPacket receivePacket = new DatagramPacket (receiveData, receiveData.length);
				serverSocket.receive(receivePacket);

				IPAddress= receivePacket.getAddress(); //get client IP Address
				port= receivePacket.getPort(); //get client port

				String sendTime=Long.toString(System.nanoTime());
				if (sendTime.length()!=15){
					sendTime=changeLength(sendTime); 
				}
				sendData= sendTime.getBytes();

				DatagramPacket sendPacket= new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
				i++;
				System.out.println("Connection Established");

			}
			else{ //connection established
				String sendTime=Long.toString(System.nanoTime());
				if (sendTime.length()!=15){
					sendTime=changeLength(sendTime); 
				}
				sendData= sendTime.getBytes();
				DatagramPacket sendPacket= new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
		
			}
		}
			
	}

	private static String changeLength(String data){
		int l= data.length();
		if (l==12){
			data="000"+data; 
		}
		else if(l==13){
			data="00"+data; 
		}
		else if (l==14){
			data="0"+data; 
		}
		else if (l==11){
			data="0000"+data;
		}
		else if (l==10){
			data="00000"+data;
		}
		return data;
	}
}




