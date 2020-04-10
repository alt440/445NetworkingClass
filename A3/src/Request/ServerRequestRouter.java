import java.io.IOException;
import java.net.*;

public class ServerRequestRouter{
  public static final String hostAddress = "127.0.0.1";
  private static final int PACKET_DELAY_MS = 1000;

  public static void main(String[] args) {
      int portClient = args.length == 0 ? 41830 : Integer.parseInt(args[0]);
      int portRouter = args.length == 0? 3000 : Integer.parseInt(args[1]);
      int portServer = args.length == 0? 8007 : Integer.parseInt(args[2]);
      new ServerRequestRouter().run(portClient, portRouter, portServer);
  }

  private byte[] waitForPacketAndResend(DatagramSocket serverSocket,
  DatagramPacket packetWantingResponseTo, int expectedResponsePacketType, int portClient,
  String packetTypeSent)
  throws InterruptedException, IOException, UnknownHostException{
    byte[] response_bytes = new byte[1024];
    DatagramPacket responsePacket = new DatagramPacket(response_bytes,
        PacketDecoder.MAX_SIZE_PACKET);

    ObserverPacketReceived observerReceived = new ObserverPacketReceived();

    Runnable bodyOfReceiveThread = new PacketReceivedThread(serverSocket,
    responsePacket, observerReceived, expectedResponsePacketType, portClient);
    Thread waitingForResponseReception = new Thread(bodyOfReceiveThread);
    waitingForResponseReception.start();

    while(!observerReceived.hasBeenExecuted()){
      Thread.sleep(PACKET_DELAY_MS);
      System.out.println("SENDING "+packetTypeSent+"...");
      serverSocket.send(packetWantingResponseTo);
    }
    System.out.println("RECEIVED "+PacketDecoder.getPacketTypeString(expectedResponsePacketType)+"!");
    return observerReceived.getPacket();
  }

  private void respond3WayConnection(DatagramSocket serverSocket, int portClient, int portRouter)
  throws UnknownHostException, IOException, InterruptedException{

    PacketCreator syn_ack_packet = PacketBuilder.createSYN_ACKPacket(portClient, "SYN-ACK");
    byte[] packet_content = syn_ack_packet.getPacket();
    DatagramPacket syn_ack_to_router_packet = new DatagramPacket(packet_content,
    packet_content.length, InetAddress.getLocalHost(), portRouter);

    serverSocket.send(syn_ack_to_router_packet);

    //response should be ack
    byte[] response_ack = waitForPacketAndResend(serverSocket, syn_ack_to_router_packet,
    PacketDecoder.ACK, portClient, "SYN-ACK");

    PacketDecoder decode_packet = new PacketDecoder(response_ack);
  }

  private void respondWithData(DatagramSocket serverSocket, int portClient, int portRouter,
  PacketDecoder packet_decoder) throws UnknownHostException, IOException, InterruptedException{
    //analyze packet received
    String request = packet_decoder.getPayload();
    RequestDecoder request_decoder = new RequestDecoder(request);

    //handle response
    String responsePayload = Routes.getResponseFromDestination(request_decoder.getDestinationPage(),
    request_decoder.getHTTPMethod(), request_decoder.getRequestBody());
    Response response = new Response(responsePayload, true);

    PacketCreator data_packet_content = PacketBuilder.createDataPacket(portClient, response.getResponse());
    DatagramPacket data_packet = new DatagramPacket(data_packet_content.getPacket(),
    PacketDecoder.MAX_SIZE_PACKET, InetAddress.getLocalHost(), portRouter);

    serverSocket.send(data_packet);
  }

  public void run(int portClient, int portRouter, int portServer) {
    try {
      DatagramSocket serverSocket = new DatagramSocket(portServer);

      while(true){

        byte[] packet_content = new byte[1024];
        DatagramPacket packet = new DatagramPacket(packet_content,
            PacketDecoder.MAX_SIZE_PACKET);
        serverSocket.receive(packet);

        PacketDecoder decode_packet = new PacketDecoder(packet_content);
        if(decode_packet.getPacketType() == PacketDecoder.SYN){
          System.out.println("RECEIVED "+PacketDecoder.getPacketTypeString(decode_packet.getPacketType())+"!");
          respond3WayConnection(serverSocket, portClient, portRouter);
        } else if(decode_packet.getPacketType() == PacketDecoder.DATA){
          System.out.println("RECEIVED "+PacketDecoder.getPacketTypeString(decode_packet.getPacketType())+"!");
          respondWithData(serverSocket, portClient, portRouter, decode_packet);
        }
      }

    } catch (IOException e) {
      System.out.println(e);
    } catch (InterruptedException e){
      System.out.println(e);
    }
    // should close serverSocket in finally block
  }
}
