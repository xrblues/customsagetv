import org.jdna.sagetv.networkencoder.DVBTunerServer;
import org.jdna.sagetv.networkencoder.INetworkEncoder;
import org.jdna.sagetv.networkencoder.StartCommand;
import org.jdna.sagetv.networkencoder.impl.dvb.linux.DVBNetworkEncoder;


public class TestTuner {

	public static void main(String args[]) throws Exception {
		DVBTunerServer server = new DVBTunerServer();
		server.init();
		
		StartCommand sc = new StartCommand("START Linux DVB Network Encoder TV Tuner|1896929541|400|2387712826160|/var/media/tv/ATPTennis-BNPParibasMastersEarlyRound-3266918-0.mpg|Great");
		INetworkEncoder enc = new DVBNetworkEncoder();
		enc.start(sc);
		
		System.out.println("Tuned to 400");
		while (true) {
			Thread.yield();
		}
	}
}
