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
                System.out.println(line);
                String[] words = splitter(line, "[ ]+");
                if (words[0].equals("Host:")){
                    words = splitter(words[1], "[:]");
                    httprequest.setHost(words[0]);
                    if(!words[1].isEmpty()){
                        int port = Integer.parseInt(words[1]);
                        httprequest.setPort(port);
                    }
                }
                httprequest.addLine(line);
            }
            catch(IOException i){
                System.out.println(i);
            }
        } while (!(line.isEmpty()));
        return httprequest;
    }
}