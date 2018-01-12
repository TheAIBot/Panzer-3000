package logger;

import java.net.DatagramPacket;

public class Log {
		public static void message(DatagramPacket receivePacket) {
			System.out.println(receivePacket);
		}
		

		public static void message(String message) {
			System.out.println(message);
		}
		
		public static void message(int message) {
			System.out.println("" + message);
		}
		
		public static void exception(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
		}
}
