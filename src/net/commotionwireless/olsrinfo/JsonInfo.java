package net.commotionwireless.olsrinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.commotionwireless.olsrinfo.datatypes.Gateway;
import net.commotionwireless.olsrinfo.datatypes.HNA;
import net.commotionwireless.olsrinfo.datatypes.Interface;
import net.commotionwireless.olsrinfo.datatypes.Link;
import net.commotionwireless.olsrinfo.datatypes.MID;
import net.commotionwireless.olsrinfo.datatypes.Neighbor;
import net.commotionwireless.olsrinfo.datatypes.Node;
import net.commotionwireless.olsrinfo.datatypes.OlsrDataDump;
import net.commotionwireless.olsrinfo.datatypes.Plugin;
import net.commotionwireless.olsrinfo.datatypes.Route;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author Hans-Christoph Steiner
 *
 */
public class JsonInfo {

	String host = "127.0.0.1";
	int port = 9090;

	public JsonInfo() {
	}

	public JsonInfo(String sethost) {
		host = sethost;
	}

	public JsonInfo(String sethost, int setport) {
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
			if(! line.equals(""))
				retlist.add(line);
		}
		// the jsoninfo plugin drops the connection once it outputs
		out.close();
		in.close();
		sock.close();

		return retlist.toArray(new String[retlist.size()]);
	}

	public String command(String cmd) {
		String[] data = null;
		String ret = "";

		final Set<String> supportedCommands = new HashSet<String>(Arrays.asList(
				new String[] {
						// combined reports
						"/all", // all of the JSON info
						"/runtime", // all of the runtime status reports
						"/startup", // all of the startup config reports
						// individual runtime reports
						"/gateways", // gateways
						"/hna", // Host and Network Association
						"/interfaces", // network interfaces
						"/links", // links
						"/mid", // MID
						"/neighbors", // neighbors
						"/routes", // routes
						"/topology", // mesh network topology
						"/runtime", // all of the runtime info in a single
									// report
						// the following don't change during runtime, so they
						// are separate
						"/config", // the current running config info
						"/plugins", // loaded plugins and their config
						// the only non-JSON output, and can't be combined with
						// the others
						"/olsrd.conf", // current config info in olsrd.conf file
										// format
				}));
		if (!supportedCommands.contains(cmd))
			System.out.println("Unsupported command: " + cmd);

		try {
			data = request(cmd);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for socket to " + host + ":" + Integer.toString(port));
		}
		for (String s : data) {
			ret += s + "\n";
		}
		return ret;
	}

	/**
	 * all of the runtime and startup status information in a single report
	 * @return array of per-IP arrays of IP address, SYM, MPR, MPRS, Willingness, and 2 Hop Neighbors
	 */
	public String all() {
		return command("/all");
	}

	/**
	 * all of the runtime status information in a single report
	 * @return array of per-IP arrays of IP address, SYM, MPR, MPRS, Willingness, and 2 Hop Neighbors
	 */
	public String runtime() {
		return command("/runtime");
	}

	/**
	 * all of the startup config information in a single report
	 * @return array of per-IP arrays of IP address, SYM, MPR, MPRS, Willingness, and 2 Hop Neighbors
	 */
	public String startup() {
		return command("/startup");
	}

	/**
	 * immediate neighbors on the mesh
	 * @return array of per-IP arrays of IP address, SYM, MPR, MPRS, Willingness, and 2 Hop Neighbors
	 */
	public String neighbors() {
		return command("/neighbors");
	}

	/**
	 * direct connections on the mesh, i.e. nodes with direct IP connectivity via Ad-hoc
	 * @return array of per-IP arrays of Local IP, Remote IP, Hysteresis, LQ, NLQ, and Cost
	 */
	public String links() {
		return command("/links");
	}

	/**
	 * IP routes to nodes on the mesh
	 * @return array of per-IP arrays of Destination, Gateway IP, Metric, ETX, and Interface
	 */
	public String routes() {
		return command("/routes");
	}

	/**
	 * Host and Network Association (for supporting dynamic internet gateways)
	 * @return array of per-IP arrays of Destination and Gateway
	 */
	public String hna() {
		return command("/hna");
	}

	/**
	 * Multiple Interface Declaration
	 * @return array of per-IP arrays of IP address and Aliases
	 */
	public String mid() {
		return command("/mid");
	}

	/**
	 * topology of the whole mesh
	 * @return array of per-IP arrays of Destination IP, Last hop IP, LQ, NLQ, and Cost
	 */
	public String topology() {
		return command("/topology");
	}

	/**
	 * the network interfaces that olsrd is aware of
	 * @return array of per-IP arrays of Destination IP, Last hop IP, LQ, NLQ, and Cost
	 */
	public Collection<Interface> interfaces() {
		String data = command("/interfaces");
		//System.out.println(data);
		ObjectMapper mapper = new ObjectMapper();
		try {
			OlsrDataDump dump = mapper.readValue(data, OlsrDataDump.class);
			return dump.interfaces;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * the gateways to other networks that this node knows about
	 * @return array of per-IP arrays of Status, Gateway IP, ETX, Hopcount, Uplink, Downlink, IPv4, IPv6, Prefix
	 */
	public String gateways() {
		return command("/gateways");
	}

	/**
	 * The parsed configuration of olsrd in its current state
	 */
	public String config() {
		return command("/config");
	}

	/**
	 * The parsed configuration of plugins in their current state
	 */
	public String plugins() {
		return command("/plugins");
	}

	/**
	 * The current olsrd configuration in the olsrd.conf format, NOT json
	 */
	public String olsrdconf() {
		return command("/olsrd.conf");
	}

	/**
	 * for testing from the command line
	 */
	public static void main(String[] args) throws IOException {
		JsonInfo jsoninfo = new JsonInfo();
		ObjectMapper mapper = new ObjectMapper();
		OlsrDataDump dump = mapper.readValue(jsoninfo.all(), OlsrDataDump.class);
		System.out.println("gateways:");
		for (Gateway g : dump.gateways)
			System.out.println("\t" + g.ipAddress);
		System.out.println("hna:");
		for (HNA h : dump.hna)
			System.out.println("\t" + h.destination);
		System.out.println("Interfaces:");
		for (Interface i : dump.interfaces)
			System.out.println("\t" + i.name);
		System.out.println("Links:");
		for (Link l : dump.links)
			System.out.println("\t" + l.localIP + " <--> " + l.remoteIP);
		System.out.println("MID:");
		for (MID m : dump.mid)
			System.out.println("\t" + m.ipAddress);
		System.out.println("Neighbors:");
		for (Neighbor n : dump.neighbors)
			System.out.println("\t" + n.ipv4Address);
		System.out.println("Plugins:");
		for (Plugin p : dump.plugins)
			System.out.println("\t" + p.plugin);
		System.out.println("Routes:");
		for (Route r : dump.routes)
			System.out.println("\t" + r.localIP);
		System.out.println("Topology:");
		for (Node node : dump.topology)
			System.out.println("\t" + node.destinationIP);
	}
}
