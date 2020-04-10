public class RequestDecoder{

  private String headers;
  private int port;
  private String method;
  private String httpVersion;
  private String host;
  private String destinationPage;
  private String requestBody;
  private boolean isVerbose;
  private String verboseContent;

  private final static int HTTP_METHOD_INDEX = 0;
  private final static int DESTINATION_PAGE_INDEX = 1;
  private final static int REMOVE_FORWARD_SLASH = 1;
  private final static int HTTP_VERSION_INDEX = 2;
  private final static int REMOVE_HTTP = 5;
  private final static int FIRST_LINE = 0;
  private final static int SECOND_LINE = 1;
  private final static int THIRD_LINE = 2;
  private final static int REMOVE_HOST_HEADER = 6;

  public RequestDecoder(){
    requestBody="";
    destinationPage="";
    isVerbose = false;
    verboseContent = "";
    headers = "";
  }

  public RequestDecoder(String request){
    requestBody="";
    destinationPage="";
    isVerbose = false;
    verboseContent = "";
    headers = "";
    this.decodeRequest(request);
  }

  public void decodeRequest(String content){
    String[] contentBasedOnLines = content.split("\r\n");
    //first line contains method + destinationPage + requestBody for GET + HTTP version
    String[] firstLineInfo = contentBasedOnLines[FIRST_LINE].split(" ");
    this.method = firstLineInfo[HTTP_METHOD_INDEX];
    if(this.method.equals("GET")){
      //split in two parts: requestBody and destinationPage
      String[] destination_body = firstLineInfo[DESTINATION_PAGE_INDEX].split("\\?");
      this.destinationPage = destination_body[0].substring(REMOVE_FORWARD_SLASH);
      if(destination_body.length > 1)
        this.requestBody = destination_body[1];
    } else{
      this.destinationPage = firstLineInfo[DESTINATION_PAGE_INDEX].substring(REMOVE_FORWARD_SLASH);
    }

    this.httpVersion = firstLineInfo[HTTP_VERSION_INDEX].substring(REMOVE_HTTP);

    this.host = contentBasedOnLines[SECOND_LINE].substring(REMOVE_HOST_HEADER);

    int ending_headers_index = 0;
    int requestBodyPOSTIndex = -1;
    for(int i=THIRD_LINE;i<contentBasedOnLines.length;i++){
      if(ending_headers_index == 0 && (contentBasedOnLines[i].length()==0 ||
      i == contentBasedOnLines.length-1)){
        ending_headers_index = i;
      } else if(ending_headers_index != 0 && requestBodyPOSTIndex == -1
      && contentBasedOnLines[i].length()!=0){
        requestBodyPOSTIndex = i;
      }
    }
    //append headers until last line
    for(int i=THIRD_LINE;i<ending_headers_index;i++){
      this.headers+=contentBasedOnLines[i]+"\r\n";
    }

    if(this.method.equals("POST") && requestBodyPOSTIndex !=-1){
      this.requestBody = contentBasedOnLines[requestBodyPOSTIndex];
    }
  }

  public String getHTTPMethod(){
    return this.method;
  }

  public String getDestinationPage(){
    return this.destinationPage;
  }

  public String getRequestBody(){
    return this.requestBody;
  }

  public String getHTTPVersion(){
    return this.httpVersion;
  }

  public String getHostName(){
    return this.host;
  }

  public String getHeaders(){
    return this.headers;
  }

  /*public void writeRequest(PrintWriter pw){
    //content sent two different ways with GET/POST.
    if(this.requestBody.length() != 0 && this.method.equals("GET")){
      sendPartOfRequest(pw, this.method+" /"+this.destinationPage+"?"+this.requestBody+" HTTP/"+this.httpVersion+"\r\n");
    }else{
      sendPartOfRequest(pw, this.method+" /"+this.destinationPage+" HTTP/"+this.httpVersion+"\r\n");
    }

    sendPartOfRequest(pw, "Host: "+this.host_object.getHostName()+"\r\n");

    //check if headers at all
    if(this.headers.length()>0){
      // \r\n already added at the end of headers.
      sendPartOfRequest(pw, this.headers);
    }

    if(this.method.equals("POST") && this.requestBody.length() != 0){
      sendPartOfRequest(pw, "\r\n");
      //need space otherwise showing \r in the requestbody.
      sendPartOfRequest(pw, this.requestBody+" \r\n");
    }

    sendPartOfRequest(pw, "\n");
    pw.flush();
  }*/
}
