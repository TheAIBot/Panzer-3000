package network;

public enum CommunicationType {
	SERVER_CLIENT(0),
	P2P(1);
	
	private final int type;
	
	CommunicationType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public CommunicationType fromType(int type) throws Exception {
		switch (type) {
		case 0:
			return CommunicationType.SERVER_CLIENT;
		case 1:
			return CommunicationType.P2P;
		}
		throw new Exception("WAAAAA BLA BLA BLA BLA");
	}
}