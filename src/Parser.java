package icechest;

import java.util.Date;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;



public class Parser{ //takes a string from input buffer and parses what we want out of it, then packages into a HttpRequest object
    
    
    private String line = null;


    public Parser(){
        HttpRequest httprequest = new HttpRequest();
    }

    private String[] splitter(String line, String delimeters){
        String[] splitWords = line.split(delimeters);
        return splitWords;
    }
    public HttpRequest parse(BufferedReader reader, ServerSocket server){
        do{
            try{
               line = reader.readLine();//iterates http requesttry
                System.out.println(line);
                String[] words = splitter(line, "[ ]+");
                switch(words[0]){
                    case "OPTIONS":
                        break;
                    case "GET":
                        break;
                    case "HEAD":
                        break;
                    case "POST":
                        break;
                    case "PUT":
                        break;
                    case "DELETE":
                        break;
                    case "TRACE":
                        break;
                    case "CONNECT":
                        break;
                    default:
                        System.out.println("Error, http/1.1 wants "+words[0]+" ... invalid method.");
                }
            }
            catch(IOException i){
                System.out.println(i);
            }
        } while (!(line.isEmpty()));
        return null;
    }
}