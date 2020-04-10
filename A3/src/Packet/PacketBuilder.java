public class PacketBuilder{//untested

  private static int sequenceNumber = 1;
  private static final String hostAddress = "127.0.0.1";

  public static void resetSequenceNumber(){
    sequenceNumber = 1;
  }

  public static void isReset(){
    if(sequenceNumber == 2147483647){
      resetSequenceNumber();
    }
  }

  public static PacketCreator createSYNPacket(int destinationPort, String payload){
    PacketCreator packet = new PacketCreator();
    packet.setPacketType(PacketCreator.SYN);
    packet.setSequenceNumber(sequenceNumber);
    packet.setIPv4Addr(hostAddress);
    packet.setDestinationPort(destinationPort);
    packet.setPayload(payload);

    isReset();
    sequenceNumber += 1;
    return packet;
  }

  public static PacketCreator createACKPacket(int destinationPort, String payload){
    PacketCreator packet = new PacketCreator();
    packet.setPacketType(PacketCreator.ACK);
    packet.setSequenceNumber(sequenceNumber);
    packet.setIPv4Addr(hostAddress);
    packet.setDestinationPort(destinationPort);
    packet.setPayload(payload);

    isReset();
    sequenceNumber += 1;
    return packet;
  }

  public static PacketCreator createSYN_ACKPacket(int destinationPort, String payload){
    PacketCreator packet = new PacketCreator();
    packet.setPacketType(PacketCreator.SYN_ACK);
    packet.setSequenceNumber(sequenceNumber);
    packet.setIPv4Addr(hostAddress);
    packet.setDestinationPort(destinationPort);
    packet.setPayload(payload);

    isReset();
    sequenceNumber += 1;
    return packet;
  }

  public static PacketCreator createNAKPacket(int destinationPort, String payload){
    PacketCreator packet = new PacketCreator();
    packet.setPacketType(PacketCreator.NAK);
    packet.setSequenceNumber(sequenceNumber);
    packet.setIPv4Addr(hostAddress);
    packet.setDestinationPort(destinationPort);
    packet.setPayload(payload);

    isReset();
    sequenceNumber += 1;
    return packet;
  }

  public static PacketCreator createDataPacket(int destinationPort, String payload){
    PacketCreator packet = new PacketCreator();
    packet.setPacketType(PacketCreator.DATA);
    packet.setSequenceNumber(sequenceNumber);
    packet.setIPv4Addr(hostAddress);
    packet.setDestinationPort(destinationPort);
    packet.setPayload(payload);

    isReset();
    sequenceNumber += 1;
    return packet;
  }
}
