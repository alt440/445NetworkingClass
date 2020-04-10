import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.lang.Thread;

/*
All packets go to router before going to the server. Same goes for server.

Router must be engaged for this program to work.
https://www.cspsprotocol.com/tcp-three-way-handshake/
*/
public class httpfs {

    private static int PORT_CLIENT = 41830;
    private static int PORT_ROUTER = 3000;
    private static int PORT_SERVER = 8007;
    private static int PACKET_DELAY_MS = 1000;
    public static final String hostAddress = "127.0.0.1";

    public static void main(String[] args) {
        int portClient = PORT_CLIENT;
        int portRouter = PORT_ROUTER;
        int portServer = PORT_SERVER;
        Request httpRequest = null;

        if(args.length==0 || args[0].equals("help")){
          commandInterpreter.showGeneralUsage();
          return;
        } else{
          String httpMethod = args[commandInterpreter.getHttpMethodIndex()];

          int isMethodAccepted = commandInterpreter.verifyHTTPMethod(httpMethod);
          if(isMethodAccepted == -1){
            return;
          }

          //build request
          httpRequest = commandInterpreter.getRequest();
          //by default
          httpRequest.setDestinationPort(commandInterpreter.getDestinationPort());
          httpRequest.setHttpVersion(commandInterpreter.getHttpVersion());
          httpRequest.setRequestMethod(httpMethod.toUpperCase());

          //reads the whole command
          int isInputAccepted = commandInterpreter.readInputParameters(args, httpRequest);
          if(isInputAccepted == -1){
            return;
          }

          if(!commandInterpreter.isGetRequest()){
            commandInterpreter.setPostHeaders();
          }

          httpRequest.buildURL(hostAddress);
        }
        System.out.println(httpRequest.getRequestBody());
        new httpfs().run(portClient, portRouter, portServer, httpRequest);
    }

    private String createDataPacketAndSend(DatagramSocket clientSocket, int portServer,
    int portRouter, Request httpRequest) throws IOException, UnknownHostException, InterruptedException{

      PacketCreator packet_content = PacketBuilder.createDataPacket(portServer, httpRequest.writeRequest());
      DatagramPacket packet = new DatagramPacket(packet_content.getPacket(),
      PacketDecoder.MAX_SIZE_PACKET, InetAddress.getLocalHost(), portRouter);

      clientSocket.send(packet);

      byte[] response = waitForPacketAndResend(clientSocket, packet, PacketDecoder.DATA,
      portServer, "DATA");
      PacketDecoder packet_decoder = new PacketDecoder(response);
      //also returns verbose output. to change...
      return packet_decoder.getPayload();
    }

    private byte[] waitForPacketAndResend(DatagramSocket clientSocket,
    DatagramPacket packetWantingResponseTo, int packetExpected,
    int destinationPort, String sendingPacket)
    throws InterruptedException, IOException{
      byte[] response_bytes = new byte[1024];
      DatagramPacket responsePacket = new DatagramPacket(response_bytes,
          PacketDecoder.MAX_SIZE_PACKET);

      ObserverPacketReceived observerReceived = new ObserverPacketReceived();

      Runnable bodyOfReceiveThread = new PacketReceivedThread(clientSocket,
      responsePacket, observerReceived, packetExpected, destinationPort);
      Thread waitingForResponseReception = new Thread(bodyOfReceiveThread);
      waitingForResponseReception.start();

      while(!observerReceived.hasBeenExecuted()){
        Thread.sleep(PACKET_DELAY_MS);
        clientSocket.send(packetWantingResponseTo);
        System.out.println("SENDING "+sendingPacket+"...");
      }

      System.out.println("RECEIVED "+PacketDecoder.getPacketTypeString(packetExpected)+"!");
      return observerReceived.getPacket();
    }

    //initiating 3 way connection
    private void initiate3WayConnection(DatagramSocket clientSocket, int portServer, int portRouter)
    throws UnknownHostException, IOException, InterruptedException{
      PacketCreator syn_packet = PacketBuilder.createSYNPacket(portServer, "SYN");
      byte[] packet_content = syn_packet.getPacket();
      DatagramPacket syn_to_router_packet = new DatagramPacket(packet_content,
      packet_content.length, InetAddress.getLocalHost(), portRouter);

      clientSocket.send(syn_to_router_packet);

      //response should be syn-ack
      byte[] response = waitForPacketAndResend(clientSocket, syn_to_router_packet,
      PacketDecoder.SYN_ACK, portServer, "SYN");

      PacketDecoder decode_packet = new PacketDecoder(response);

      PacketCreator ack_packet = PacketBuilder.createACKPacket(portServer, "ACK");
      byte[] ack_packet_content = ack_packet.getPacket();
      DatagramPacket ack_to_router_packet = new DatagramPacket(ack_packet_content,
      ack_packet_content.length, InetAddress.getLocalHost(), portRouter);

      clientSocket.send(ack_to_router_packet);
    }

    public void run(int portClient, int portRouter, int portServer, Request httpRequest) {
      Scanner input = new Scanner(System.in);
      try {
        DatagramSocket clientSocket = new DatagramSocket(portClient);

        initiate3WayConnection(clientSocket, portServer, portRouter);

        String response = createDataPacketAndSend(clientSocket, portServer, portRouter, httpRequest);
        if(httpRequest.isVerbose())
          System.out.println(response);
        else{
          //get the blank line
          String[] response_lines = response.split("\r\n");
          boolean isToPrint = false;
          for(int i=0;i<response_lines.length;i++){
            if(response_lines[i].length()==0){
              isToPrint = true;
            }
            if(isToPrint){
              System.out.println(response_lines[i]);
            }
          }
        }
      } catch (IOException e) {
        System.out.println(e);
      } catch(InterruptedException e){
        System.out.println(e);
      }
    }


}
