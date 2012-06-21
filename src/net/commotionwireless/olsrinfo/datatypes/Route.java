package net.commotionwireless.olsrinfo.datatypes;

/**
 * A class representing a mesh route for a given node.
 * 
 * Written as part of the Commotion Wireless project
 * 
 * @author Hans-Christoph Steiner <hans@eds.org>
 * @see <a href="https://code.commotionwireless.net/projects/commotion/wiki/OLSR_Configuration_and_Management">OLSR Configuration and Management</a>
 */
public class Route {
	public String localIP;
	public String remoteIP;
	public int validityTime;
	public float linkQuality;
	public float neighborLinkQuality;
	public int linkCost;
}
