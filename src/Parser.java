package icechest;

import java.util.Date;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;



public class Parser{ //takes a string from input buffer and parses what we want out of it, then packages into a HttpRequest object
    
    
    private String line = null;
    public Parser(){
        
    }

    private String[] splitter(String line, String delimeters){
        String[] splitWords = line.split(delimeters);
        return splitWords;
    }
    public HttpRequest parse(BufferedReader reader, ServerSocket server){
        HttpRequest httprequest = new HttpRequest();
        do{
            try{
               line = reader.readLine();//iterates http requesttry
                String[] words = splitter(line, "[ ]+");
                if(words[0].equals("Accept-Encoding:")){ //skip gzip encoding, etc., otherwise you're viable to get binary back
                    line = reader.readLine();
                    words = splitter(line, "[ ]+");
                }
                if (words[0].equals("Host:")){ //parse out host so we know where to open a socket
                    words = splitter(words[1], "[:]");
                    httprequest.setHost(words[0]);
                    if(words.length > 1){
                        int port = Integer.parseInt(words[1]);
                        httprequest.setPort(port);
                    }
                }
                System.out.println(line);
                httprequest.addLine(line);
            }
            catch(IOException i){
                System.out.println(i);
            }
        } while (!(line.isEmpty()));


        //this is where I will trigger the icebox 
        try{
            httprequest.sendRequest();
        }
        catch(IOException i){
            System.out.println(i);
        }
        return httprequest;
    }
}