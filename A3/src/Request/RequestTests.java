public class RequestTests{

  public static String createRequest(){
    String headers = "Accept: application/json\r\n";
    String httpVersion = "1.0";
    String requestMethod = "GET";
    String destinationPage = "hullahoop";
    String requestBody = "arr=6";
    String host = "localhost";

    System.out.println(headers);
    System.out.println(httpVersion);
    System.out.println(requestMethod);
    System.out.println(destinationPage);
    System.out.println(requestBody);
    System.out.println(host);

    Request request = new Request();
    request.setHttpVersion(httpVersion);
    request.appendHeaders(headers);
    request.setRequestMethod(requestMethod);
    request.setDestinationPage(destinationPage);
    request.setRequestBody(requestBody);
    request.buildURL(host);
    return request.writeRequest();
  }

  public static String createPOSTRequest(){
    String headers = "Accept: application/json\r\n";
    String httpVersion = "1.0";
    String requestMethod = "POST";
    String destinationPage = "hullahoop";
    String requestBody = "{'text':'arr=6'}";
    String host = "localhost";

    System.out.println(headers);
    System.out.println(httpVersion);
    System.out.println(requestMethod);
    System.out.println(destinationPage);
    System.out.println(requestBody);
    System.out.println(host);

    Request request = new Request();
    request.setHttpVersion(httpVersion);
    request.appendHeaders(headers);
    request.setRequestMethod(requestMethod);
    request.setDestinationPage(destinationPage);
    request.setRequestBody(requestBody);
    request.buildURL(host);
    return request.writeRequest();
  }

  public static void decodeRequest(String request){
    RequestDecoder request_decoder = new RequestDecoder(request);
    System.out.println("Headers: "+request_decoder.getHeaders());
    System.out.println("HTTP version: "+request_decoder.getHTTPVersion());
    System.out.println("HTTP method: "+request_decoder.getHTTPMethod());
    System.out.println("Destination page: "+request_decoder.getDestinationPage());
    System.out.println("Request body: "+request_decoder.getRequestBody());
    System.out.println("Host name: "+request_decoder.getHostName());
  }

  public static void main(String[] args){
    String request = createRequest();
    decodeRequest(request);

    String requestPOST = createPOSTRequest();
    decodeRequest(requestPOST);
  }
}
