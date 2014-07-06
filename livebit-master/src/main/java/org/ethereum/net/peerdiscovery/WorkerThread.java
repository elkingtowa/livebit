package org.ethereum.net.peerdiscovery;

import org.ethereum.net.client.PeerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * www.ethereumJ.com
 * @author: Roman Mandeleil
 * Created on: 22/05/2014 09:26
 */
public class WorkerThread implements Runnable {

    Logger logger = LoggerFactory.getLogger("peerdiscovery");

    ThreadPoolExecutor poolExecutor;
    private PeerData peerData;

    public WorkerThread(PeerData peerData, ThreadPoolExecutor poolExecutor) {
        this.poolExecutor = poolExecutor;
        this.peerData = peerData;
    }

    @Override
    public void run() {
        logger.info(Thread.currentThread().getName()+" Start. Command = "+ peerData.toString());
        processCommand();
        logger.info(Thread.currentThread().getName()+" End.");

        try {Thread.sleep(10000); } catch (InterruptedException e) {logger.error("sleep interrupted");}
        poolExecutor.execute(this);
    }

    private void processCommand() {

        try {
            PeerTaster peerTaster = new PeerTaster();
            peerTaster.connect(peerData.getInetAddress().getHostName(), peerData.getPort());
            peerData.setOnline(true);
            logger.info("Peer: " + peerData.toString() + " isOnline: true");
        }
        catch (Throwable e) {
            logger.info("Peer: " + peerData.toString() + " isOnline: false");
            peerData.setOnline(false);
        }
    }

    @Override
    public String toString() {
        return " Worker for: " + this.peerData.toString();
    }
}
