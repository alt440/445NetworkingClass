import java.io.IOException;
import java.net.*;

public class PacketTests{

  public static String getBinaryRepresentation(byte value){
    return Integer.toBinaryString(value & 0xFF);
  }

  public static boolean isErrorTestSequenceNumber(int value){
    PacketCreator some_packet = new PacketCreator();
    some_packet.setSequenceNumber(value);
    byte[] seqNb = some_packet.getSequenceNumber();
    String binString = "0"; //to ensure its not negative value displayed
    for(int i=0;i<seqNb.length;i++){
      binString+=getBinaryRepresentation(seqNb[i]);
    }

    if(value != Integer.parseInt(binString, 2)){
      System.out.println("Test 'Packet Sequence Number' Failed: Expected: "+value+
      " Got: "+Integer.parseInt(binString, 2));
      return true;
    }

    return false;
  }

  public static void inspectPacketSequenceNb(){
    boolean isError = false;
    if(isErrorTestSequenceNumber(53))
        isError = true;

    if(!isError){
      System.out.println("Test Successful!");
    }
  }

  public static void setPacketIP(){
    boolean isError = false;
    final int[] BYTES = {192,168,2,1};

    PacketCreator some_packet = new PacketCreator();
    some_packet.setIPv4Addr(BYTES[0]+"."+BYTES[1]+"."+BYTES[2]+"."+BYTES[3]);
    byte[] ipv4 = some_packet.getIPv4Addr();
    for (int i=0;i<ipv4.length;i++){
      String binaryValue = '0'+getBinaryRepresentation(ipv4[i]);
      if(BYTES[i]!=Integer.parseInt(binaryValue,2)){
        isError = true;
        System.out.println("Test 'Set Packet IP' Failed: Byte "+i+" incorrect."+
        "Received: "+Integer.parseInt(binaryValue,2)+" instead of "+BYTES[i]);
      }
    }

    if(!isError){
      System.out.println("Test Successful!");
    }
  }

  public static void main(String[] args){
    System.out.println("Testing sequence number being set...");
    inspectPacketSequenceNb();
    System.out.println("Testing IPv4 address being set...");
    setPacketIP();
  }
}
