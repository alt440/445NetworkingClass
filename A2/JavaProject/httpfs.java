import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Exception;

class httpfs{

  /*
  TEST COMMANDS:
  java httpfs get -p 1337
  java httpfs get -p 1337 -D ordinary.txt
  java httpfs post -p 1337 -d 'Hello my friends' -D ordinary.txt
  java httpfs get -v -p 1337 -D ordinary.txt
  */

  private static final String hostname = "localhost";

  private static final String verboseOption = "-v";
  private static final String headerOption = "-h";
  private static final String inlineDataOption = "-d";
  private static final String fileOption = "-f";
  private static final String portOption = "-p";
  private static final String directoryOption = "-D";

  private static final int httpMethodIndex = 0;

  private static final int destinationPort = 8080; //normally 80
  private static final int testDestinationPort = 1337; //testing locally...
  private static final String httpVersion = "1.0";

  private static boolean isGetRequest = true;

  //verifies length of content sent (using post, inline-data)
  private static int lengthDataSent = 0;

  //for post request only. cannot use both d and f.
  private static boolean isD_FOptionUsed = false;

  //keeps status of inputs in command
  private static boolean isHeaderOption = false;
  private static boolean isInlineDataOption = false;
  private static boolean isFileOption = false;
  private static boolean isPortOption = false;
  private static boolean isDirOption = false;

  private static Request httpRequest;

  public static void main(String[] args){

    if(args.length==0 || args[0].equals("help")){
      showGeneralUsage();
    } else{
      String httpMethod = args[httpMethodIndex];

      int isMethodAccepted = verifyHTTPMethod(httpMethod);
      if(isMethodAccepted == -1){
        return;
      }

      //build request
      httpRequest = new Request();
      //by default
      httpRequest.setDestinationPort(destinationPort);
      httpRequest.setHttpVersion(httpVersion);
      httpRequest.setRequestMethod(httpMethod.toUpperCase());

      //reads the whole command
      int isInputAccepted = readInputParameters(args);
      if(isInputAccepted == -1){
        return;
      }

      if(!isGetRequest){
        setPostHeaders();
      }

      httpRequest.buildURL(hostname);
      httpRequest.sendRequest();
    }

  }

  public static void setPostHeaders(){
    httpRequest.appendHeaders("Content-Type: application/json");
    httpRequest.appendHeaders("Content-Length: "+String.valueOf(lengthDataSent));
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

  public static int readInputParameters(String[] args){
    for(int i=httpMethodIndex+1;i<args.length;i++){
      if(args[i].equals(verboseOption)){
        httpRequest.setVerbose();
      } else if(args[i].equals(headerOption)){
        isHeaderOption = true;
      } else if(args[i].equals(inlineDataOption)){
        if(isGetRequest)
          return showGeneralUsage();
        isInlineDataOption = true;
      } else if(args[i].equals(fileOption)){
        if(isGetRequest)
          return showGeneralUsage();
        isFileOption = true;
      } else if(args[i].equals(portOption)){
        isPortOption = true;
      } else if(args[i].equals(directoryOption)){
        isDirOption = true;
      } else{ //check for any status flags
        int returnVal = setAppropriateRequestFlag(args, i);
        switch(returnVal){
          case 0: break;
          case -1: return -1;
        }
      }
    }

    return 0;
  }

  public static int setAppropriateRequestFlag(String[] args, int iteration){
    if(isHeaderOption){
      httpRequest.appendHeaders(args[iteration]);
      isHeaderOption = false;
    } else if(isInlineDataOption && !isD_FOptionUsed){
      String dataSent = formatData(args[iteration]);
      httpRequest.setRequestBody(dataSent);
      isInlineDataOption = false;
      isD_FOptionUsed = true;
    } else if(isFileOption && !isD_FOptionUsed){
      //read from file and set as body of request...
      String data = "";

      try{
        data = getFileContents(args[iteration]);
      } catch(Exception ex){
        ex.printStackTrace();
        return -1;
      }
      String dataSent = formatData(data);
      httpRequest.setRequestBody(dataSent);
      isD_FOptionUsed = true;
    } else if(isD_FOptionUsed && (isInlineDataOption || isFileOption)){
      //because cannot use both -d and -f, or multiple -d or multiple -f.
      return showGeneralUsage();
    } else if(isPortOption){
      isPortOption = false;
      int port = -1;

      try{
        port = Integer.parseInt(args[iteration]);
      } catch(NumberFormatException ex){
        System.out.println("Port number "+args[iteration]+" is incorrect.");
        return -1;
      }

      httpRequest.setDestinationPort(port);
    } else if(isDirOption){
      isDirOption = false;
      httpRequest.setDestinationPage(args[iteration]);
    }

    return 0;
  }

  public static String formatData(String data){
    data = "{\"text\":\""+data+"\"}";
    lengthDataSent = data.length();
    return data;
  }

  public static String getFileContents(String filePath) throws IOException, FileNotFoundException{
    File file = new File(filePath);
    BufferedReader br = new BufferedReader(new FileReader(file));

    String data = br.readLine();
    br.close();
    return data;
  }

  public static int showGeneralUsage(){
    System.out.println("httpfs is a simple file server.");
    System.out.println("Usage:");
    System.out.println("    httpfs get|post [-v] [-p PORT] [-d inline-data] [-D PATH-TO-DIR]");
    System.out.println("The commands are:");
    System.out.println("    get   executes a HTTP GET request and prints the response.");
    System.out.println("    post  executes a HTTP POST request and prints the response.");
    System.out.println("    help  prints this screen.");
    System.out.println("The arguments are:");
    System.out.println("    -v    shows verbose output");
    System.out.println("    -p    allows to set the port number");
    System.out.println("    -d    allows to set data to be sent to file. Only for HTTP POST requests");
    System.out.println("    -D    sets the path to the file to be modified/seen");
    System.out.println();
    return -1; //to indicate its because command was done wrong
  }
}
