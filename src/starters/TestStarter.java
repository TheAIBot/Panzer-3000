package starters;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.jspace.ActualField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

public class TestStarter {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		System.out.println(Arrays.toString(new byte[] {101, 110}));	
	}
}