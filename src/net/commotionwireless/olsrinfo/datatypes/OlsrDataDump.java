package net.commotionwireless.olsrinfo.datatypes;

import java.util.Collection;

public class OlsrDataDump {
	public Config config;
	public Collection<Gateway> gateways;
	public Collection<HNA> hna;
	public Collection<Interface> interfaces;
	public Collection<Link> links;
	public Collection<MID> mid;
	public Collection<Neighbor> neighbors;
	public Collection<Node> topology;
	public Collection<Plugin> plugins;
	public Collection<Route> routes;
	public int systemTime;
	public String uuid;
}
