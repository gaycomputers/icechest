package icechest;

import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
//import java.net.

public class IceBox {// responsible for taking a httprequest and following the flow of retrieving a
                     // fresh one or getting etc.
    private File main = null;
    private Path temppath;


    public IceBox() {
        mainDirectory();
    }

    private void mainDirectory() {
        main = new File("../icebox");
        main.mkdirs();
    }

    private boolean subDirectory(File sub) {
        return sub.mkdirs();
    }

    public void justForward(HttpRequest httprequest) {
        // httprequest.sendRequest();
    }
    private String urlToDirectory(String url){
        StringBuilder line = new StringBuilder();
        try{
            String[] splitWords = url.split("[/]+");
            int i = 0;
            for(i = 0; i < splitWords.length-1;){  //anyeurism inducing
                line.append(splitWords[i]);
                line.append("+");
                i++;
            }
            line.append(splitWords[i]);
        }
        catch(NullPointerException e){
            System.out.println(e);
        }
        return line.toString();
    }
    private boolean checkIce(String url){
        temppath = Paths.get(main.getName()+"/"+urlToDirectory(url));
        if (Files.exists(temppath)){
            return true;
        }
        else {
            new File(temppath.toString()).mkdirs();
            return false;
        }
    }
    private boolean checkFresh()throws IOException,ClassNotFoundException{
        Path datePath = Paths.get(temppath.toString()+"/date");
        if (Files.exists(datePath)){
            try{
                FileInputStream datefile = new FileInputStream(temppath+"/date");
                ObjectInputStream inDate = new ObjectInputStream(datefile);
                HttpDate date = (HttpDate) inDate.readObject();
                inDate.close();
                if((((System.currentTimeMillis()) - (date.getDate()))) < 300000){
                    return true;
                }
                else {
                    return false;
                }
            }
            catch (FileNotFoundException e){
                System.out.println(e);
                return false;
            }
        }
        else{
            return false;
        }
    }
    private boolean checkNet(String urlString)throws IOException{
        try{
            URL url = new URL(urlString);
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            httpcon.connect();
            return true;
        } catch(IOException i){
            System.out.println(i);
            return false;
        }
    }
    private void cacheFresh(HttpRequest httprequest)throws IOException, ClassNotFoundException{
        HttpDate date = new HttpDate();
        Path datepath = Paths.get(temppath.toString()+"/date");
        Path path = Paths.get(temppath.toString()+"/html");
        File filepath = datepath.toFile();
        date.setDate(System.currentTimeMillis());
        FileOutputStream file = new FileOutputStream(filepath);
		ObjectOutputStream out = new ObjectOutputStream(file);
		out.writeObject(date);
        out.close();
        filepath = path.toFile();
        file = new FileOutputStream(filepath);
        out = new ObjectOutputStream(file);
        out.writeObject(httprequest);
        out.close();
    }

    private void serve(HttpRequest httprequest, Socket socket)throws IOException, ClassNotFoundException{//serves cached

        Path path = Paths.get(temppath.toString()+"/html");
        File filepath = path.toFile();
        FileInputStream htmlfile = new FileInputStream(filepath);
        ObjectInputStream inhtml = new ObjectInputStream(htmlfile);
        httprequest = (HttpRequest) inhtml.readObject();
        inhtml.close();
        try {
            httprequest.sendResponse(socket);
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        } catch (NullPointerException n) {
            System.out.println(n);
        }
    }
    public void handle(HttpRequest httprequest, Socket socket) {
        try {
            if (checkIce(httprequest.getUrl())) {
                if (checkFresh()) {
                    serve(httprequest, socket);
                } else if (checkNet(httprequest.getUrl())) {
                    cacheFresh(httprequest);
                    serve(httprequest, socket);
                } else {
                    serve(httprequest, socket);
                }
            } else if (checkNet(httprequest.getUrl())) {
                cacheFresh(httprequest);
                serve(httprequest, socket);
            } else {
                // we fucked
            }
        } catch (IOException i) {
            System.out.println(i);
        } catch (ClassNotFoundException c) {
            System.out.println(c);
        }
    }
}