package icechest;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest{
    private int port = 80;
    private String host = null;
    private ArrayList<String> lines = new ArrayList<String>();
    private ArrayList<String> responseHeader = new ArrayList<String>();
    private ArrayList<String> responseBody = new ArrayList<String>();
    public HttpRequest(){

    }
    public void setHost(String hos){
        host = hos;
    }
    public void setPort(int por){
        port = por;
    }
    public String getHost(){
        return host;
    }
    public int getPort(){
        return port;
    }
    public void output(){
        System.out.println(lines);
    }
    public void getReturn(BufferedReader in){
        
        System.out.println("Writing return to object: \r\n");
        try{
            String line = "";
            line = in.readLine();
            if(line != null){
            while (!(line.isEmpty())) {
                    System.out.println(line);
                    responseHeader.add(line);
                    line = in.readLine();
            }
            line = in.readLine();
            while (!line.isEmpty()){
                System.out.println(line);
                responseBody.add(line);
                line = in.readLine();
            }
        }
        }
        catch(IOException i){
            //donothing
            System.out.println(i);
        }
        System.out.println("DONE");
        System.out.println(responseBody);
    }  
    public void addLine(String line){
        lines.add(line);
    }
    public void sendResponse(Socket socket) throws IOException{
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
        System.out.println("Sending to client: \r\n");
        for(int i = 0; i < responseHeader.size(); i++){
            System.out.println(responseHeader.get(i));
            out.write(responseHeader.get(i) + "\r\n");
        }
        out.write("\r\n");
        for(int i = 0; i < responseBody.size(); i++){
            System.out.println(responseBody.get(i));
            out.write(responseBody.get(i) + "\r\n");
        }
        out.flush();
    }
    public void sendRequest() throws IOException{
        Socket socket = new Socket(host, port);
        //socket.getInputStream.read();
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        for(int i = 0; i < lines.size(); i++){
            out.write(lines.get(i) + "\r\n");
        }
        out.flush();
        getReturn(in);
        socket.close();
    }
}