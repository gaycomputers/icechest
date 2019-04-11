package icechest;

import java.net.Socket;
import java.io.*;

public class IceBox{//responsible for taking a httprequest and following the flow of retrieving a fresh one or getting etc.
    public IceBox(){

    }
    public void justForward(HttpRequest httprequest){
        //httprequest.sendRequest();
    }
    public void handle(HttpRequest httprequest, Socket socket){
        try{
            httprequest.sendResponse(socket);
        }
        catch(IOException i){
            System.out.println(i);
        }
    }
}