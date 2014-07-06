package org.ethereum.manager;

import static org.ethereum.config.SystemProperties.CONFIG;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ethereum.db.IpGeoDB;
import org.ethereum.net.client.ClientPeer;
import org.ethereum.net.client.PeerData;
import org.ethereum.net.peerdiscovery.PeerDiscovery;

import com.maxmind.geoip.Location;

/**
 * www.ethereumJ.com
 * @author: Roman Mandeleil
 * Created on: 21/04/14 20:35
 */
public class MainData {

    private List<PeerData> peers = Collections.synchronizedList(new ArrayList<PeerData>());
    private ClientPeer activePeer;

    private PeerDiscovery peerDiscovery;

    public static MainData instance = new MainData();

    public MainData() {

    	// Initialize PeerData
        try {
        	InetAddress ip = InetAddress.getByName(CONFIG.peerDiscoveryIP());
            int port = CONFIG.peerDiscoveryPort();
            PeerData peer = new PeerData(ip.getAddress(), port, new byte[]{00});
            peers.add(peer);
            peerDiscovery = new PeerDiscovery(peers);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void setActivePeer(ClientPeer peer) {
        this.activePeer = peer;
    }

    public ClientPeer getActivePeer() {
        return activePeer;
    }

    public List<PeerData> getPeers() {
        return peers;
    }

    public void updatePeerIsDead(String ip, short port) {
        for (PeerData peer : peers) {
            if (peer.getInetAddress().getHostAddress().equals(ip) && (peer.getPort() == port)) {
                System.out.println("update peer is dead: " + ip + ":" + port);
                peer.setOnline(false);
                break;
            }
        }
    }

    public void addPeers(List<PeerData> newPeers) {
        for (PeerData peer : newPeers) {
            if (this.peers.indexOf(peer) == -1) {
                Location location = IpGeoDB.getLocationForIp(peer.getInetAddress());
                if (location != null) {
                    this.peers.add(peer);
                    if (peerDiscovery.isStarted())
                        peerDiscovery.addNewPeerData(peer);
                }
            }
        }
    }

    public void startPeerDiscovery() {
        peerDiscovery.start();
    };
}
