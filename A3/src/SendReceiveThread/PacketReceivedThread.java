import java.lang.Runnable;
import java.net.*;
import java.io.IOException;

public class PacketReceivedThread implements Runnable{

  private DatagramSocket socket;
  private DatagramPacket packet;
  private boolean isDone;
  private ObserverPacketReceived observer;
  private int expectedResponsePacketType;
  private int destinationPort;

  public PacketReceivedThread(DatagramSocket socket, DatagramPacket packet){
    this.socket = socket;
    this.packet = packet;
    this.isDone = false;
    this.destinationPort = -1;
  }

  public PacketReceivedThread(DatagramSocket socket, DatagramPacket packet,
  ObserverPacketReceived observer, int expectedResponsePacketType){
    this.socket = socket;
    this.packet = packet;
    this.isDone = false;
    this.observer = observer;
    this.expectedResponsePacketType = expectedResponsePacketType;
    this.destinationPort = -1;
  }

  public PacketReceivedThread(DatagramSocket socket, DatagramPacket packet,
  ObserverPacketReceived observer, int expectedResponsePacketType,
  int destinationPort){
    this.socket = socket;
    this.packet = packet;
    this.isDone = false;
    this.observer = observer;
    this.expectedResponsePacketType = expectedResponsePacketType;
    this.destinationPort = destinationPort;
  }

  public void run(){
    try{
      this.socket.receive(this.packet);
      PacketDecoder packet_decoder = new PacketDecoder(this.packet.getData());
      //only looking at packet type
      if(packet_decoder.getPacketType()!=expectedResponsePacketType){
        while(packet_decoder.getPacketType()!=expectedResponsePacketType){

          /*if(previousReceivedPacket==PacketDecoder.ACK &&
          packet_decoder.getPacketType()==PacketDecoder.NAK){
            PacketCreator packet_content = PacketBuilder.createACKPacket(this.destinationPort,
            "ACK");
            DatagramPacket ack_packet = new DatagramPacket(packet_content.getPacket(),
            PacketDecoder.MAX_SIZE_PACKET, InetAddress.getLocalHost(), this.destinationPort);
            this.socket.send(ack_packet);
          }*/
          isNeedToSendPacket(packet_decoder);

          this.socket.receive(this.packet);
          packet_decoder = new PacketDecoder(this.packet.getData());
        }
      }
    } catch(IOException ex){
      ex.printStackTrace();
    }

    this.isDone = true;
    if(this.observer != null){
      this.observer.setToExecuted();
      this.observer.setPacket(this.packet.getData());
    }
  }

  private void isNeedToSendPacket(PacketDecoder packet_decoder) throws UnknownHostException,
  IOException{
    if(this.expectedResponsePacketType == PacketDecoder.DATA &&
    packet_decoder.getPacketType()==PacketDecoder.NAK){ //client
      PacketCreator packet_content = PacketBuilder.createACKPacket(this.destinationPort,
      "ACK");
      DatagramPacket ack_packet = new DatagramPacket(packet_content.getPacket(),
      PacketDecoder.MAX_SIZE_PACKET, InetAddress.getLocalHost(), this.destinationPort);
      this.socket.send(ack_packet);
    } else if(this.expectedResponsePacketType==PacketDecoder.ACK &&
    packet_decoder.getPacketType()==PacketDecoder.DATA){
      PacketCreator packet_content = PacketBuilder.createNAKPacket(this.destinationPort,
      "NAK");
      DatagramPacket nak_packet = new DatagramPacket(packet_content.getPacket(),
      PacketDecoder.MAX_SIZE_PACKET, InetAddress.getLocalHost(), this.destinationPort);
      this.socket.send(nak_packet);
    }
  }

  public boolean hasBeenExecuted(){
    return isDone;
  }

  public byte[] getContentPacket(){
    return packet.getData();
  }
}
