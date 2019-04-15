package icechest;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    private Socket clientSocket = null; 
    private InputStreamReader input =  null;
    private BufferedReader reader = null;
    private ServerSocket server = null;
    private Parser parser = null;
    private IceBox icebox = new IceBox();
    private int port = 0;
    
    public Server(int por) throws IOException {
        port = por;
        server = new ServerSocket(port);
        parser = new Parser();
    }
    
    
    private void mainLoop(){//this mainLoop is looped from Main to drive the program
        if(server.isClosed()){
            try{
            server = new ServerSocket(port);
            }
            catch(IOException i){
                System.out.println(i);
            }
        }
        if(findRequestLoop()){
            System.out.println("requestfound");
            HttpRequest httprequest = readFoundRequest();
            icebox.handle(httprequest, clientSocket);
            try{
                clientSocket.close();
                //server.close();
            } catch (IOException i){
                System.out.println(i);
            }
        }
    }
    
    
    private boolean findRequestLoop(){ //makes a check for incoming http request, then moves stuff into correct members
        try{
            clientSocket = server.accept();
            input = new InputStreamReader(clientSocket.getInputStream());
            reader = new BufferedReader(input);
            return true;
        }
        catch(IOException i){
            System.out.println(i);
            return false;
        }
    }
    
    
    private HttpRequest readFoundRequest(){
        HttpRequest readRequest = parser.parse(reader, server);
        return readRequest;
    }   
    
    
    public void initialize(){
        while(true){
            mainLoop();
        }
    }


}