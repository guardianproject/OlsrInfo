package net.commotionwireless.olsrinfo.datatypes;

import java.util.Collection;

public class Neighbor {
	public String ipv4Address;
	public boolean symmetric;
	public boolean multiPointRelay;
	public boolean multiPointRelaySelector;
	public int willingness;
	public int twoHopNeighborCount;
	public Collection<String> twoHopNeighbors;
}
