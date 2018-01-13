package network;

public enum NetworkProtocol {
	TCP
	{
		@Override
		public String toString() {
			return "tcp";
		}
	},
	UDP
	{
		@Override
		public String toString() {
			return "udp";
		}
	}
}
