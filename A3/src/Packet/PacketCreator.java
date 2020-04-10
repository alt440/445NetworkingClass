public class PacketCreator{

  public static final byte DATA = 0;
  public static final byte ACK = 1;
  public static final byte SYN = 2;
  public static final byte SYN_ACK = 3;
  public static final byte NAK = 4;

  private byte packetType;
  private byte[] sequenceNb;
  private byte[] IPv4addr;
  private byte[] destinationPort;
  private byte[] payload;

  public PacketCreator(){
    this.sequenceNb = new byte[4];
    this.IPv4addr = new byte[4];
    this.destinationPort = new byte[2];
  }

  public byte[] getSequenceNumber(){
    return this.sequenceNb;
  }

  public byte getPacketType(){
    return this.packetType;
  }

  public byte[] getIPv4Addr(){
    return this.IPv4addr;
  }

  public byte[] getDestinationPort(){
    return this.destinationPort;
  }

  public byte[] getPayload(){
    return this.payload;
  }

  private String formatBinaryString(String binaryString, boolean isSequenceNumber){
    int formattedLength = 32;
    if(!isSequenceNumber)
      formattedLength = 16;
    int stringLength = binaryString.length();
    while(stringLength < formattedLength){
      binaryString = '0'+binaryString;
      stringLength+=1;
    }
    return binaryString;
  }

  private void convertIntToByteArray(int valueToBeSet, byte[] destinationArray, boolean isSequenceNumber){
    final int BYTE_LENGTH = 8;
    String binaryVs = Integer.toBinaryString(valueToBeSet);
    binaryVs = formatBinaryString(binaryVs, isSequenceNumber);
    for(int i=0;i<binaryVs.length();i+=BYTE_LENGTH){

      if(i!=0){
        int startIndex = i - BYTE_LENGTH;
        int endIndex = i;
        String byteString = binaryVs.substring(startIndex, endIndex);
        int indexInSequenceNbArray = i/BYTE_LENGTH-1;

        destinationArray[indexInSequenceNbArray] = (byte)Integer.parseInt(byteString, 2);
      }
    }

    int endIndex = binaryVs.length();
    int startIndex = endIndex - BYTE_LENGTH;
    String byteString = binaryVs.substring(startIndex,endIndex);

    destinationArray[destinationArray.length-1] = (byte)Integer.parseInt(byteString, 2);
  }

  public void setSequenceNumber(int sequenceNbInt){ //0-65535
    convertIntToByteArray(sequenceNbInt, this.sequenceNb, true);
  }

  public void setIPv4Addr(String ipv4){ //delimit with . in btw nbs
    String[] diffValuesInIPv4 = ipv4.split("\\.");
    for(int i=0;i<this.IPv4addr.length;i++){
      this.IPv4addr[i] = (byte)Integer.parseInt(diffValuesInIPv4[i]);
    }
  }

  public void setDestinationPort(int destinationPortInt){
    convertIntToByteArray(destinationPortInt, this.destinationPort, false);
  }

  public void setPacketType(byte value){ // 0-255
    this.packetType = value;
  }

  public void setPayload(String content){
    this.payload = content.getBytes();
  }

  private int getBytesToFill(int LENGTH_PAYLOAD, int LENGTH_PORT,
  int LENGTH_PACKET_TYPE, int LENGTH_SEQNB_ADDRESS){
    final int MAX_SIZE_PACKET = 1024;
    int bytesToFill = MAX_SIZE_PACKET - LENGTH_PAYLOAD - LENGTH_PORT
    - LENGTH_PACKET_TYPE - (2*LENGTH_SEQNB_ADDRESS);
    return bytesToFill;
  }

  public byte[] getPacket(){
    final int MAX_SIZE_PACKET = 1024; //need to set it to max size bc recipient needs to set byte array size.
    final int LENGTH_SEQNB_ADDRESS = 4;
    final int LENGTH_PORT = 2;
    final int LENGTH_PAYLOAD = this.payload.length;
    final int LENGTH_PACKET_TYPE = 1;
    byte[] whole_packet = new byte[MAX_SIZE_PACKET];
    int index_packet_arr = 0;
    whole_packet[index_packet_arr] = this.packetType;
    index_packet_arr+=1;
    for(int i=0;i<LENGTH_SEQNB_ADDRESS;i++){
      whole_packet[index_packet_arr] = this.sequenceNb[i];
      index_packet_arr+=1;
    }
    for(int i=0;i<LENGTH_SEQNB_ADDRESS;i++){
      whole_packet[index_packet_arr] = this.IPv4addr[i];
      index_packet_arr+=1;
    }
    for(int i=0;i<LENGTH_PORT;i++){
      whole_packet[index_packet_arr] = this.destinationPort[i];
      index_packet_arr+=1;
    }
    for(int i=0;i<LENGTH_PAYLOAD;i++){
      whole_packet[index_packet_arr] = this.payload[i];
      index_packet_arr+=1;
    }
    for(int i=0;i<getBytesToFill(LENGTH_PAYLOAD, LENGTH_PORT, LENGTH_PACKET_TYPE,
    LENGTH_SEQNB_ADDRESS); i++){
      whole_packet[index_packet_arr] = 0;
      index_packet_arr+=1;
    }
    return whole_packet;
  }
}
