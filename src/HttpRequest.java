package icechest;

public class HttpRequest{
    private int port;
    private String host;
    private ArrayList<String> lines = new ArrayList<String>();
    public HttpRequest(){

    }
    public void setHost(String hos){
        host = hos;
    }
    public void setPort(int por){
        port = por;
    }
    public void addLine(String line){
        lines.add(line);
        //add to lines 
    }
}