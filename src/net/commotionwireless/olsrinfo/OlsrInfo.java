package net.commotionwireless.olsrinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


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
		out.println("/all");
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

	public static void main (String[] args) throws IOException {
		OlsrInfo txtinfo = new OlsrInfo();
		try {
			for (String s : txtinfo.request("/all") )
				System.out.println(s);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for socket to " + txtinfo.host + ":" + Integer.toString(txtinfo.port));
		}
	}
}
