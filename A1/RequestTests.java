/**
This class will use the nodejs project in this directory to conduct the tests.
It attempts to show the use cases of the program.
**/

class RequestTests{

  public static void sendGETRequest(){
    Request a = new Request();
    a.setVerbose();
    a.setDestinationPort(1337);
    a.setHttpVersion("1.0");
    a.setRequestMethod("GET");
    a.buildURL("localhost");
    a.setDestinationPage("");
    a.sendRequest();
  }

  public static void sendPOSTRequest(){
    Request a = new Request();
    a.setVerbose();
    a.setDestinationPort(1337);
    a.setHttpVersion("1.0");
    a.setRequestMethod("POST");
    a.buildURL("localhost");
    a.setDestinationPage("post");
    a.sendRequest();
  }

  public static void sendQueryParameterRequest(){
    Request a = new Request();
    a.setVerbose();
    a.setDestinationPort(1337);
    a.setHttpVersion("1.0");
    a.setRequestMethod("GET");
    a.buildURL("localhost");
    a.setDestinationPage("queryParam");
    a.setRequestBody("a=artifact");
    a.sendRequest();
  }

  public static void sendHeadersRequest(){
    Request a = new Request();
    a.setVerbose();
    a.setDestinationPort(1337);
    a.setHttpVersion("1.0");
    a.setRequestMethod("GET");
    a.buildURL("localhost");
    a.setDestinationPage("requestHeaders");
    a.setHeaders("Content-Type: application/json\r\nAccept-Language: en-us");
    a.sendRequest();
  }

  //this test will not work, as x-www-form-urlencoded has not been enabled on the server.
  public static void sendBodyRequest(){
    Request a = new Request();
    a.setVerbose();
    a.setDestinationPort(1337);
    a.setHttpVersion("1.0");
    a.setRequestMethod("POST");
    a.buildURL("localhost");
    a.setDestinationPage("sendBody");
    a.setHeaders("Content-Type: application/x-www-form-urlencoded\r\nContent-Length: 30");
    a.setRequestBody("username=zurfyx&pass=password");
    a.sendRequest();
  }

  public static void sendRandomRequest(){
    Request a = new Request();
    a.setVerbose();
    a.setDestinationPort(80);
    a.setHttpVersion("1.0");
    a.setRequestMethod("GET");
    a.buildURL("duckduckgo.com");
    a.setDestinationPage("");
    a.sendRequest();
  }

}
