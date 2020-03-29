import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.URLEncoder;

/*
NOTE: When getting the response from your request, some case is not
handled. In HTTP 1.1, you could also send/receive multiple requests
at once. So, when reading the response, you have to be able to distinguish
if multiple responses have been received. To do that, you would first
read all the headers of the first response, and determine if there was
the 'Content-Length' header to determine if some Body has been sent. If
so, you would know that there is something after the response headers.
Then, you would continue up the next blank line, and get the headers of
the next response. Just outputting the whole response does not distinguish
between multiple responses.
*/

public class Request {

    private String headers;
    private int port;
    private String method;
    private String httpVersion;
    private InetAddress host_object;
    private String destinationPage;
    private String requestBody;
    private boolean isVerbose;
    private String verboseContent;

    public Request(){
      requestBody="";
      destinationPage="";
      isVerbose = false;
      verboseContent = "";
      headers = "";
    }

    //return -1 if URL malformed
    public int buildURL(String host){
        try {
            this.host_object = InetAddress.getByName(host);
        } catch(UnknownHostException ex){
            ex.printStackTrace();
            return -1;
        }

        return 0;
    }

    public int setDestinationPort(int port){
        this.port = port;
        return 0;
    }

    //return -1 if version not available
    public int setHttpVersion(String version){
        if(version.compareTo("1.0") == 0 || version.compareTo("1.1") == 0){
            this.httpVersion = version;
            return 0;
        }

        return -1;
    }

    //return -1 if request method not available
    public int setRequestMethod(String requestMethod){
        requestMethod = requestMethod.toUpperCase();
        if(requestMethod.compareTo("GET") == 0 || requestMethod.compareTo("POST") == 0){
            this.method = requestMethod;
            return 0;
        }

        return -1;
    }

    public int setHeaders(String headers){
        this.headers = headers+"\r\n";
        return 0;
    }

    public int appendHeaders(String headers){
        this.headers+=headers+"\r\n";
        return 0;
    }

    public int setDestinationPage(String page){
        this.destinationPage = page;
        return 0;
    }

    public int setRequestBody(String body){
        this.requestBody = body;
        return 0;
    }

    public int setVerbose(){
        this.isVerbose = true;
        return 0;
    }

    public String getVerboseContent(){
        return verboseContent;
    }

    //registering content to printwriter
    public int sendPartOfRequest(PrintWriter pw, String content){
        pw.print(content);
        return 0;
    }

    public void writeRequest(PrintWriter pw){
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
    }

    public void readResponse(Socket s) throws IOException{
      BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
      String responseLine;
      boolean isResponseOutputStarting = false;
      //it is possible the program lags here because it cannot read response...
      while ((responseLine = br.readLine()) != null){
        if(responseLine.length() == 0 && !isVerbose){ //this line separates whole response with actual content of response...
          isResponseOutputStarting = true;
        }
        else if(isVerbose || isResponseOutputStarting){
          verboseContent += responseLine;
          System.out.println(responseLine);
        }
      }
      br.close();
    }

    //send request and receive response
    public int sendRequest(){
        try {
            Socket s = new Socket(this.host_object.getHostAddress(), this.port);
            PrintWriter pw = new PrintWriter(s.getOutputStream());

            writeRequest(pw);
            readResponse(s);

        } catch(IOException ex){
            ex.printStackTrace();
            return -1;
        }

        return 0;
    }

}
