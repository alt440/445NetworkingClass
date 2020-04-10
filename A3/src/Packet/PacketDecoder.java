public class PacketDecoder{

  public static final byte DATA = 0;
  public static final byte ACK = 1;
  public static final byte SYN = 2;
  public static final byte SYN_ACK = 3;
  public static final byte NAK = 4;

  public static final int MAX_SIZE_PACKET = 1024;
  private static final int SEQUENCE_NUMBER_LENGTH = 4;
  private static final int IPv4_ADDRESS_LENGTH = 4;
  private static final int DESTINATION_PORT_LENGTH = 2;
  private static final int PAYLOAD_LENGTH = MAX_SIZE_PACKET - SEQUENCE_NUMBER_LENGTH
  -IPv4_ADDRESS_LENGTH - DESTINATION_PORT_LENGTH - 1;

  private byte packetType;
  private byte[] sequenceNb;
  private byte[] IPv4addr;
  private byte[] destinationPort;
  private byte[] payload;

  public PacketDecoder(){
    this.sequenceNb = new byte[SEQUENCE_NUMBER_LENGTH];
    this.IPv4addr = new byte[IPv4_ADDRESS_LENGTH];
    this.destinationPort = new byte[DESTINATION_PORT_LENGTH];
    this.payload = new byte[PAYLOAD_LENGTH];
  }

  public PacketDecoder(byte[] whole_packet){
    this.sequenceNb = new byte[SEQUENCE_NUMBER_LENGTH];
    this.IPv4addr = new byte[IPv4_ADDRESS_LENGTH];
    this.destinationPort = new byte[DESTINATION_PORT_LENGTH];
    this.payload = new byte[PAYLOAD_LENGTH];
    decodePacket(whole_packet);
  }

  public void decodePacket(byte[] whole_packet){
    int index_packet_arr = 0;
    this.packetType = whole_packet[index_packet_arr];
    index_packet_arr+=1;

    for(int i=0;i<SEQUENCE_NUMBER_LENGTH;i++){
      this.sequenceNb[i] = whole_packet[index_packet_arr];
      index_packet_arr+=1;
    }
    for(int i=0;i<IPv4_ADDRESS_LENGTH;i++){
      this.IPv4addr[i] = whole_packet[index_packet_arr];
      index_packet_arr+=1;
    }
    for(int i=0;i<DESTINATION_PORT_LENGTH;i++){
      this.destinationPort[i] = whole_packet[index_packet_arr];
      index_packet_arr+=1;
    }
    for(int i=0;i<PAYLOAD_LENGTH;i++){
      this.payload[i] = whole_packet[index_packet_arr];
      index_packet_arr+=1;
    }
  }

  public byte[] getSequenceNumber(){
    return this.sequenceNb;
  }

  public byte getPacketType(){
    return this.packetType;
  }

  public static String getPacketTypeString(int packetType){
    if(packetType == PacketDecoder.DATA){
      return "DATA";
    } else if(packetType == PacketDecoder.SYN){
      return "SYN";
    } else if(packetType == PacketDecoder.ACK){
      return "ACK";
    } else if(packetType == PacketDecoder.NAK){
      return "NAK";
    } else if(packetType == PacketDecoder.SYN_ACK){
      return "SYN-ACK";
    }
    return "";
  }

  public byte[] getIPv4Addr(){
    return this.IPv4addr;
  }

  public byte[] getDestinationPort(){
    return this.destinationPort;
  }

  public String getPayload(){
    return new String(this.payload);
  }
}
