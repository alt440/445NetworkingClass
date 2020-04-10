import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;

public class Routes{

  private static String prefix = "Files/";

  private static boolean isFilePathLegitimate(String filePath){
    //for security purposes - dont go outside Files/ folder
    String[] filePathParts = filePath.split("/");

    int countBackFolder = 0;
    for(int i=0;i<filePathParts.length;i++){
      if(filePathParts[i].equals("..")){
        countBackFolder+=1;
      }
    }

    if(filePathParts.length-countBackFolder<=countBackFolder){
      return false;
    } else{
      return true;
    }
  }

  public static String getResponseFromDestination(String destinationPage,
  String requestMethod, String requestBody){
    if(requestMethod.equals("GET")){
      if(destinationPage.length()==0){
        return seeAllFiles();
      } else{
        return getFileContent(destinationPage);
      }
    } else{
      return modifyFileContent(destinationPage, requestBody);
    }
  }

  private static String getFileContent(String filePath){
    boolean isFilePathOK = isFilePathLegitimate(filePath);
    if(!isFilePathOK) return "HTTP ERROR 503";

    try{
      Scanner input_scanner = new Scanner(new File(prefix+filePath));
      String file_content = "";
      while(input_scanner.hasNextLine()){
        file_content += input_scanner.nextLine();
      }

      input_scanner.close();
      return "{'file_content':'"+file_content+"'}";
    } catch(FileNotFoundException ex){
      ex.printStackTrace();
      return "HTTP ERROR 404";
    }
  }

  private static String modifyFileContent(String filePath, String requestBody){
    boolean isFilePathOK = isFilePathLegitimate(filePath);
    if(!isFilePathOK) return "HTTP ERROR 503";
    //requestBody: {'text':'blablabla'}
    String textContent = requestBody.substring(9,requestBody.length()-3);
    try{
      PrintWriter writer = new PrintWriter(prefix+filePath);
      writer.println(textContent);
      writer.close();
      return "{'status':'Success'}";
    } catch(FileNotFoundException ex){
      ex.printStackTrace();
      return "HTTP ERROR 404";
    }
  }

  private static String seeAllFiles(){
    File folder = new File(prefix);
    File[] listOfFiles = folder.listFiles();

    String listOfFileTitles = "";
    for(int i=0;i<listOfFiles.length;i++){
      if(listOfFiles[i].isFile()){
        listOfFileTitles+="'"+listOfFiles[i].getName()+"',";
      }
    }
    listOfFileTitles = listOfFileTitles.substring(0,listOfFileTitles.length()-1);

    return "{'files':["+listOfFileTitles+"]}";
  }
}
