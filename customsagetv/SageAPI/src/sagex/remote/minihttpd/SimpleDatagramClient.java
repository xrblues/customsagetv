package sagex.remote.minihttpd;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import sagex.remote.MarshalUtils;

public class SimpleDatagramClient<T2> {
	public SimpleDatagramClient() {
	}
	
	public T2 send(String msg, String server, int port, long timeout) throws Exception {
		T2 reply=null;
		
        MulticastSocket socket = new MulticastSocket();
        InetAddress group = InetAddress.getByName(server); 
        socket.joinGroup(group);
        
		try {
			byte buf[] = msg.getBytes(); 
	        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);

	        socket.send(packet);

	        // get response
	        buf = new byte[1024];
	        packet = new DatagramPacket(buf, buf.length);
	        socket.receive(packet);

	        // display response
	        reply = (T2)MarshalUtils.unmarshal(new String(packet.getData(), MarshalUtils.ENCODING));
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			socket.close();
		}
		
		
		return reply;
	}
}
