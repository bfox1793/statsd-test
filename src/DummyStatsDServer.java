import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

class DummyStatsDServer {
		private final List<String> messagesReceived = new ArrayList<String>();
		private final DatagramSocket server;
		private boolean run;

		public DummyStatsDServer(int port) {
			run = true;
			try {
				server = new DatagramSocket(port);
			} catch (SocketException e) {
				throw new IllegalStateException(e);
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (run) {
						try {
							final DatagramPacket packet = new DatagramPacket(
									new byte[256], 256);
							server.receive(packet);
							messagesReceived.add(new String(packet.getData(),
									Charset.forName("UTF-8")).trim());
						} catch (Exception e) {
						}

					}
				}
			}).start();
		}

		public void stop() {
			run = false;
			server.close();
		}

		public void waitForMessage() {
			while (messagesReceived.isEmpty()) {
				try {
					Thread.sleep(50L);
				} catch (InterruptedException e) {
				}
			}
		}

		public List<String> messagesReceived() {
			return new ArrayList<String>(messagesReceived);
		}
	}