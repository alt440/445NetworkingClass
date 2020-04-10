public class ObserverPacketReceived{
  private boolean isDone;
  private byte[] packet;

  public ObserverPacketReceived(){
    isDone = false;
    packet = new byte[1024];
  }

  public boolean hasBeenExecuted(){
    return isDone;
  }

  public void setToExecuted(){
    isDone = true;
  }

  public byte[] getPacket(){
    return packet;
  }

  public void setPacket(byte[] packet){
    this.packet = packet;
  }
}
