public class RoutesTests{

  public static void main(String[] args){
    System.out.println("Response from home page of server (lists all files)");
    System.out.println(Routes.getResponseFromDestination("","GET",""));
    System.out.println("Response for getting content of a file on server");
    System.out.println(Routes.getResponseFromDestination("ordinary.txt","GET",""));
    System.out.println("Response for modifying content of a file on server");
    System.out.println(Routes.getResponseFromDestination("ordinary.txt","POST","{'text':'hurray it worked!'}"));
  }
}
