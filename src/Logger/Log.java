package Logger;

public class Log {
		public static void message(String message) {
			System.out.println(message);
		}
		
		public static void exception(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
		}
}
