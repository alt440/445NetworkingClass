public class Response{
  private static final int SUCCESS_STATUS = 200;
  private static final String HTTP_VERSION = "1.0";
  private static final String JSON = "application/json";
  private String response;

  public Response(){
    response = "";
  }

  public Response(String payload, boolean isVerbose){
    response = "";
    createResponse(payload, isVerbose);
  }

  public void createResponse(String payload, boolean isVerbose){
    if(isVerbose){
      response+="HTTP/"+HTTP_VERSION+" "+SUCCESS_STATUS+" OK\r\n";
      response+="Content-Length: "+payload.length()+"\r\n";
      response+="Content-Type: "+JSON+"\r\n";
      response+="Connection: Close\r\n";
      response+="\r\n";
    }

    response+=payload;
  }

  public String getResponse(){
    return response;
  }
}
