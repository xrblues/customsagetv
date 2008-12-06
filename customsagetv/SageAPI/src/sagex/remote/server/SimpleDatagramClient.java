package sagex.remote.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Properties;

public class SimpleDatagramClient {
	public SimpleDatagramClient() {
	}

	public void send(final String msg, final String server, final int port, final DatagramPacketHandler listener, final long timeout) throws Exception {
		Runnable client = new Runnable() {
			public void run() {
				MulticastSocket socket=null;
				try {
					socket = new MulticastSocket();
					InetAddress group = InetAddress.getByName(server);
					socket.joinGroup(group);

					byte buf[] = msg.getBytes();
					DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);

					socket.send(packet);

					// get response
					buf = new byte[1024];
					packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet);

					// display response
					listener.onDatagramPacketReceived(packet);
				} catch (Throwable t) {
					listener.onFailure(t);
				} finally {
					if (socket!=null) socket.close();
				}
			}
		};
		
		if (timeout<=0) {
			// no timeout, just do it
			client.run();
		} else {
			// start the connection in a thread and then wait for a reply
			Thread t = new Thread(client);
			t.start();
			
			long waittil = System.currentTimeMillis() + timeout;
			while (t.isAlive()) {
				if (System.currentTimeMillis() > waittil) {
					listener.onFailure(new RuntimeException("Timed out waiting for a Sage Server Reply."));
					t.stop();
					break;
				} else {
					Thread.sleep(100);
				}
			}
		}
	}

	public static Properties findRemoteServer(final long timeout) throws Exception {
		final Properties props = new Properties();
		SimpleDatagramClient client = new SimpleDatagramClient();
		client.send("Discover SageTV Remote API Server", DatagramServer.MULTICAST_GROUP, DatagramServer.MULTICAST_PORT, new DatagramPacketHandler() {
			public void onDatagramPacketReceived(DatagramPacket packet) {
				ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
				try {
					props.load(bais);
				} catch (IOException e) {
					onFailure(e);
				}
			}

			public void onFailure(Throwable t) {
				throw new RuntimeException(t);
			}
		}, timeout);

		return props;
	}
}
