package net.commotionwireless.olsrinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class OlsrInfo {

	String host = "127.0.0.1";
	int port = 2006;

	public OlsrInfo() {
	}

	public OlsrInfo(String sethost) {
		host = sethost;
	}

	public OlsrInfo(String sethost, int setport) {
		host = sethost;
		port = setport;
	}

	public String[] request(String req) throws IOException {
		Socket sock = null;
		BufferedReader in = null;
		PrintWriter out = null;
		List<String> retlist = new ArrayList<String>();

		try {
			sock = new Socket(host, port);  
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);
		} catch (UnknownHostException e) {
			throw new IOException();
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for socket to " + host + ":" + Integer.toString(port));
		}
		out.println(req);
		String line;
		while((line = in.readLine()) != null) {
			retlist.add(line);
		}
		// the txtinfo plugin drops the connection once it outputs
		out.close();
		in.close();
		sock.close();

		return retlist.toArray(new String[retlist.size()]);
	}

	public String[][] command(String cmd) {
		String[] data = null;
		int startpos = 0;

		final Set<String> supportedCommands = new HashSet<String>(Arrays.asList(
				new String[] {
						"/neigh",
						"/link",
						"/route",
						"/hna",
						"/mid",
						"/topo",
				}
		));
		if(! supportedCommands.contains(cmd))
			System.out.println("Unsupported command: " + cmd);

		try {
			data = request(cmd);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for socket to " + host + ":" + Integer.toString(port));
		}
		for(int i = 0; i < data.length; i++) {
			if(data[i].startsWith("Table: ")) {
				startpos = i + 2;
				break;
			}
		}
		int fields = data[startpos + 1].split("\t").length;
		String[][] ret = new String[data.length - startpos][fields];
		for (int i = 0; i < ret.length; i++)
			ret[i] = data[i + startpos].split("\t");
		return ret;
	}

	// IP address, SYM, MPR, MPRS, Willingness, 2 Hop Neighbors
	public String[][] neighbors() {
		return command("/neigh");
	}

	// Local IP, Remote IP, Hysteresis, LQ, NLQ, Cost
	public String[][] links() {
		return command("/link");
	}

	// Destination, Gateway IP, Metric, ETX, Interface
	public String[][] routes() {
		return command("/route");
	}

	// Destination, Gateway
	public String[][] hna() {
		return command("/hna");
	}

	// IP address, Aliases
	public String[][] mid() {
		return command("/mid");
	}

	// Destination IP, Last hop IP, LQ, NLQ, Cost
	public String[][] topography() {
		return command("/topo");
	}

	public static void main(String[] args) throws IOException {
		OlsrInfo txtinfo = new OlsrInfo();
		System.out.println("NEIGHBORS----------");
		for(String[] s : txtinfo.neighbors()) {
			for(String t : s)
				System.out.print(t + ",");
			System.out.println();
		}
		System.out.println("LINKS----------");
		for(String[] s : txtinfo.links()) {
			for(String t : s)
				System.out.print(t + ",");
			System.out.println();
		}
		System.out.println("ROUTES----------");
		for(String[] s : txtinfo.routes()) {
			for(String t : s)
				System.out.print(t + ",");
			System.out.println();
		}
		System.out.println("HNA----------");
		for(String[] s : txtinfo.hna()) {
			for(String t : s)
				System.out.print(t + ",");
			System.out.println();
		}
		System.out.println("MID----------");
		for(String[] s : txtinfo.mid()) {
			for(String t : s)
				System.out.print(t + ",");
			System.out.println();
		}
		System.out.println("TOPOGRAPHY----------");
		for(String[] s : txtinfo.topography()) {
			for(String t : s)
				System.out.print(t + ",");
			System.out.println();
		}
	}
}
