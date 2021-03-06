package org.ethereum.net.message;

import org.spongycastle.util.encoders.Hex;

import static org.ethereum.net.Command.HELLO;

import org.ethereum.util.RLP;
import org.ethereum.util.RLPItem;
import org.ethereum.util.RLPList;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * www.ethereumJ.com
 * @author: Roman Mandeleil
 * Created on: 06/04/14 14:56
 */
public class HelloMessage extends Message {

    private byte protocolVersion;
    private byte networkId;
    private String clientId;
    private byte capabilities;
    private short peerPort;
    private byte[] peerId;

    public HelloMessage(RLPList rawData) {
        super(rawData);
    }

    public HelloMessage(byte protocolVersion, byte networkId, String clientId, byte capabilities, short peerPort, byte[] peerId) {
        this.protocolVersion = protocolVersion;
        this.networkId = networkId;
        this.clientId = clientId;
        this.capabilities = capabilities;
        this.peerPort = peerPort;
        this.peerId = peerId;
        this.parsed = true;
    }

    @Override
    public void parseRLP() {

        RLPList paramsList = (RLPList) rawData.get(0);

        // the message does no distinguish between the 0 and null so here I check command code for null
        // TODO: find out if it can be 00
        if (((RLPItem)(paramsList).get(0)).getRLPData() != null) {
            throw new Error("HelloMessage: parsing for mal data");
        }

        this.protocolVersion  =            ((RLPItem) paramsList.get(1)).getRLPData()[0];

        byte[] networkIdBytes = 			((RLPItem) paramsList.get(2)).getRLPData();
        this.networkId        = 			networkIdBytes == null ? 0 : networkIdBytes[0] ;

                                            byte[] idData =  ((RLPItem) paramsList.get(3)).getRLPData();
        this.clientId         =            new String(idData   != null ? idData : new byte[0]);
        this.capabilities     =            ((RLPItem) paramsList.get(4)).getRLPData()[0];

        ByteBuffer bb = ByteBuffer.wrap(((RLPItem) paramsList.get(5)).getRLPData());
        this.peerPort         =            new BigInteger(bb.array()).shortValue();
        this.peerId           =            ((RLPItem) paramsList.get(6)).getRLPData();
        this.parsed = true;
        // TODO: what to do when mal data ?
    }

    public byte[] getPayload() {

        byte[] command         = RLP.encodeByte(HELLO.asByte());
        byte[] protocolVersion = RLP.encodeByte(this.protocolVersion);
        byte[] networkId       = RLP.encodeByte(this.networkId);
        byte[] clientId        = RLP.encodeString(this.clientId);
        byte[] capabilities    = RLP.encodeByte(this.capabilities);
        byte[] peerPort        = RLP.encodeShort(this.peerPort);
        byte[] peerId          = RLP.encodeElement(this.peerId);

        byte[] data = RLP.encodeList(command, protocolVersion, networkId,
                clientId, capabilities, peerPort, peerId);

        return data;
    }

    public byte getCommandCode() {
        if (!parsed) parseRLP();
        return HELLO.asByte();
    }

    public byte getProtocolVersion() {
        if (!parsed) parseRLP();
        return protocolVersion;
    }

    public byte getNetworkId() {
        if (!parsed) parseRLP();
        return networkId;
    }

    public String getClientId() {
        if (!parsed) parseRLP();
        return clientId;
    }

    public byte getCapabilities() {
        if (!parsed) parseRLP();
        return capabilities;
    }

    public short getPeerPort() {
        if (!parsed) parseRLP();
        return peerPort;
    }

    public byte[] getPeerId() {
        if (!parsed) parseRLP();
        return peerId;
    }

    public String toString() {
        if (!parsed) parseRLP();
        return "Hello Message [ command=" + HELLO.asByte() + " " +
                " protocolVersion=" + this.protocolVersion + " " +
                " networkId=" + this.networkId + " " +
                " clientId=" + this.clientId + " " +
                " capabilities=" + this.capabilities + " " +
                " peerPort=" + this.peerPort + " " +
                " peerId=" + Hex.toHexString(this.peerId) + " " +
                "]";
    }
}

