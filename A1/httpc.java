import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Exception;

class httpc{

  /*
  To make it work locally:
  Go in SampleNodeJSProject directory from terminal, and write 'node index.js' to
  launch server.
  Then, you can write the below commands in your terminal with prefix 'java'.

  WARNING: Do not forget to change the port if you want to test httpc with real websites!

  Examples of commands for the program:
  1 httpc get -v localhost/
  2 httpc post -h "Content-Type: application/x-www-form-urlencoded"
  -h "Content-Length: 30" -d "username=zurfyx&pass=password" -v localhost/sendBody
  3 httpc get -v localhost/queryParam?a=artifact
  4 httpc post -h "Content-Type: application/x-www-form-urlencoded"
  -h "Content-Type: application/json" -h "Content-Length:30"
  -f /home/perforatebow757/Documents/COMP445/fileData.txt -v localhost/sendBody
  5 (needs more work, should create a different page for it)
  httpc post -h "Content-Type: application/x-www-form-urlencoded"
  -h "Content-Type: application/json" -h "Content-Length:50"
  -d '"username":{"orange":"red","strawberry":"white"}' -v localhost/sendBody

  For presentation:
  1 httpc post -h "Content-Type:application/json" -h "Content-Length:19" -v -d '{"Assignment": 1}' localhost/layout_assignment
  2 httpc get -h "Content-Type:application/json" -v localhost/layout_assignment?query=a
  NOTE: Do not use http:// or https:// in your urls, java wont recognize them.
  NOTE: Content-Length header matters!!! a too big/too small value will not work.
  */

  private static final String verboseOption = "-v";
  private static final String headerOption = "-h";
  private static final String inlineDataOption = "-d";
  private static final String fileOption = "-f";

  private static final int httpMethodIndex = 0;

  private static final int destinationPort = 1337; //normally 80
  private static final int testDestinationPort = 1337; //testing locally...
  private static final String httpVersion = "1.0";

  private static boolean isGetRequest = true;

  public static void main(String[] args){
    showTests();

    int URLIndex = args.length-1;

    if(args.length == 0 || args[0].equals("help")){
      showGeneralUsage();
    } else if(args.length == 2 && args[0].equals("post") && args[1].equals("help")){
      showPostUsage();
    } else if(args.length == 2 && args[0].equals("get") && args[1].equals("help")){
      showGetUsage();
    } else{
      String httpMethod = args[httpMethodIndex];

      int isMethodAccepted = verifyHTTPMethod(httpMethod);
      if(isMethodAccepted == -1){
        return;
      }

      //build request
      Request httpRequest = new Request();
      httpRequest.setDestinationPort(destinationPort);
      httpRequest.setHttpVersion(httpVersion);
      httpRequest.setRequestMethod(httpMethod.toUpperCase());

      //reads the whole command
      int isInputAccepted = readInputParameters(args, httpRequest, URLIndex);
      if(isInputAccepted == -1){
        return;
      }

      //System.out.println("Building URL...");
      String URL = args[URLIndex];
      //split URL into page and hostname
      int indexStartingWebPath = URL.indexOf('/');

      String hostname;
      String webPath;
      if(indexStartingWebPath != -1 && indexStartingWebPath != URL.length()-1){
        hostname = URL.substring(0,indexStartingWebPath);
        webPath = URL.substring(indexStartingWebPath+1);
      } else{
        hostname = URL;
        webPath = "";
      }

      httpRequest.buildURL(hostname);
      httpRequest.setDestinationPage(webPath);
      httpRequest.sendRequest();

    }

  }

  public static int verifyHTTPMethod(String httpMethod){
    if(httpMethod.equals("get") || httpMethod.equals("post")){
      if(httpMethod.equals("post")){
        isGetRequest = false;
      }
    } else{
      showGeneralUsage();
      return -1;
    }

    return 0;
  }

  public static int readInputParameters(String[] args, Request httpRequest, int URLIndex){

    //for post request only. cannot use both d and f.
    boolean isD_FOptionUsed = false;

    //keeps status
    boolean isHeaderOption = false;
    boolean isInlineDataOption = false;
    boolean isFileOption = false;

    for(int i=httpMethodIndex+1;i<URLIndex;i++){
      if(args[i].equals(verboseOption)){
        httpRequest.setVerbose();
      } else if(args[i].equals(headerOption)){
        isHeaderOption = true;
      } else if(args[i].equals(inlineDataOption)){
        if(isGetRequest){
          showGetUsage();
          return -1;
        }
        isInlineDataOption = true;
      } else if(args[i].equals(fileOption)){
        if(isGetRequest){
          showGetUsage();
          return -1;
        }
        isFileOption = true;
      } else{ //check for any status flags
        if(isHeaderOption){
          httpRequest.appendHeaders(args[i]);
          isHeaderOption = false;
        } else if(isInlineDataOption && !isD_FOptionUsed){
          httpRequest.setRequestBody(args[i]);
          isInlineDataOption = false;
          isD_FOptionUsed = true;
        } else if(isFileOption && !isD_FOptionUsed){
          //read from file and set as body of request...
          String data = "";
          try{
            data = getFileContents(args[i]);
          } catch(Exception ex){
            ex.printStackTrace();
            return -1;
          }
          httpRequest.setRequestBody(data);
          isD_FOptionUsed = true;
        } else if(isD_FOptionUsed && (isInlineDataOption || isFileOption)){
          //because cannot use both -d and -f, or multiple -d or multiple -f.
          showPostUsage();
          return -1;
        }
      }
    }

    return 0;
  }

  public static String getFileContents(String filePath) throws IOException, FileNotFoundException{
    File file = new File(filePath);
    BufferedReader br = new BufferedReader(new FileReader(file));

    String data = br.readLine();
    br.close();
    return data;
  }

  public static void showGeneralUsage(){
    System.out.println("httpc is a curl-like application but supports HTTP protocol only.");
    System.out.println("Usage:");
    System.out.println("    httpc command [arguments]");
    System.out.println("The commands are:");
    System.out.println("    get   executes a HTTP GET request and prints the response.");
    System.out.println("    post  executes a HTTP POST request and prints the response.");
    System.out.println("    help  prints this screen.");
    System.out.println();
    System.out.println("Use \"httpc help [command]\" for more information about a command.");
  }

  public static void showGetUsage(){
    System.out.println("usage: httpc get [-v] [-h key:value] URL");
    System.out.println();
    System.out.println("Get executes a HTTP GET request for a given URL.");
    System.out.println();
    System.out.println("    -v              Prints the details of the response such as protocol, status, and headers.");
    System.out.println("    -h key:value    Associates headers to HTTP Request with the format 'key:value'.");
  }

  public static void showPostUsage(){
    System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL");
    System.out.println();
    System.out.println("Post executes a HTTP POST request for a given URL with inline data or from file.");
    System.out.println();
    System.out.println("    -v              Prints the detail of the response such as protocol, status, and headers.");
    System.out.println("    -h key:value    Associates headers to HTTP Request with the format 'key:value'.");
    System.out.println("    -d string       Associates an inline data to the body HTTP POST request.");
    System.out.println("    -f file         Associates the content of a file to the body HTTP POST request.");
    System.out.println();
    System.out.println("Either [-d] or [-f] can be used but not both.");
  }

  public static void showTests(){
    System.out.println("Basic GET Request");
    System.out.println("-----------------------------------------");
    RequestTests.sendGETRequest();
    System.out.println("-----------------------------------------");
    System.out.println("Basic POST Request");
    System.out.println("-----------------------------------------");
    RequestTests.sendPOSTRequest();
    System.out.println("-----------------------------------------");
    System.out.println("Basic GET Request with Query Parameter");
    System.out.println("-----------------------------------------");
    RequestTests.sendQueryParameterRequest();
    System.out.println("-----------------------------------------");
    System.out.println("Basic GET Request with Specific Headers");
    System.out.println("-----------------------------------------");
    RequestTests.sendHeadersRequest();
    System.out.println("-----------------------------------------");
    System.out.println("Basic POST Request with Body Sent");
    System.out.println("-----------------------------------------");
    RequestTests.sendBodyRequest();
    System.out.println("-----------------------------------------");
    System.out.println("Basic Duckduckgo Request");
    System.out.println("-----------------------------------------");
    RequestTests.sendRandomRequest();
  }

}
