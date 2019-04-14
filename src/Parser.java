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
        try{
            String[] splitWords = line.split(delimeters);
            return splitWords;
        }
        catch(NullPointerException e){
            System.out.println(e);
            String[] splitWords = {};
            return splitWords;
        }
    }
    public HttpRequest parse(BufferedReader reader, ServerSocket server){
        HttpRequest httprequest = new HttpRequest();
        boolean parsing = true;
        do{
            try{
               line = reader.readLine();//iterates http requesttry
            if (line != null && line.length() > 0){
                String[] words = splitter(line, "[ ]+");
                    boolean uglyword = true;
                    while(uglyword){
                        if (words[0].equals("Accept-Encoding:")) { // skip gzip encoding, etc., otherwise you're liable to get binary
                            line = reader.readLine();
                            words = splitter(line, "[ ]+");//drive loop
                        } else if (words[0].equals("Host:")) { // parse out host so we know where to open a socket
                            if (words[1] == "detectportal.firefox.com") {
                                httprequest.drop();
                            }
                            words = splitter(words[1], "[:]");
                            httprequest.setHost(words[0]);
                            if (words.length > 1) {
                                int port = Integer.parseInt(words[1]);
                                httprequest.setPort(port);
                            }
                            line = reader.readLine();
                            words = splitter(line, "[ ]+");//drive loop
                        } else {
                            uglyword = false;
                        }
                    }
                httprequest.addLine(words);
                }
                else{
                    parsing = false;
                }
            }
            catch(IOException i){
                System.out.println(i);
            }
        } while (parsing);
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