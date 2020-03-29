

class RequestTests{

  public static void getAllFiles(){
    Request httpRequest = new Request();
    httpRequest.setVerbose();
    httpRequest.setDestinationPort(1337);
    httpRequest.setHttpVersion("1.0");
    httpRequest.setRequestMethod("GET");
    httpRequest.buildURL("localhost");
    httpRequest.sendRequest();
  }

  public static void getContentsOfFile(){
    Request httpRequest = new Request();
    httpRequest.setVerbose();
    httpRequest.setDestinationPort(1337);
    httpRequest.setHttpVersion("1.0");
    httpRequest.setRequestMethod("GET");
    httpRequest.setDestinationPage("ordinary.txt");
    httpRequest.buildURL("localhost");
    httpRequest.sendRequest();
  }

  public static void setContentsOfFile(){
    Request httpRequest = new Request();
    httpRequest.setVerbose();
    httpRequest.setDestinationPort(1337);
    httpRequest.setHttpVersion("1.0");
    httpRequest.setRequestMethod("POST");
    httpRequest.setRequestBody("{\"text\":\"Test Post Request\"}");
    httpRequest.appendHeaders("Content-Type: application/json");
    httpRequest.appendHeaders("Content-Length: 30");
    httpRequest.setDestinationPage("ordinary.txt");
    httpRequest.buildURL("localhost");
    httpRequest.sendRequest();
  }

  public static void main(String[] args){
    System.out.println("---------------------Tests-------------------");
    System.out.println("-------Test 1: Get Request For Files---------");
    getAllFiles();
    System.out.println("--Test 2: Get Request For Content of File----");
    getContentsOfFile();
    System.out.println("--Test 3: Post Request For Content of File---");
    setContentsOfFile();
  }
}
