//it would be unethical for me to not site this article, https://www.baeldung.com/java-http-request which helped me understand a better method of sending HTTP than my original socket method
package icechest;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {
    private int port = 80;
    private String host = null;
    private ArrayList<String[]> lines = new ArrayList<String[]>();
    private ArrayList<String> responseHeader = new ArrayList<String>();
    private ArrayList<String> responseBody = new ArrayList<String>();
    private boolean drop = false;
    private StringBuffer response = null;

    public HttpRequest() {

    }

    public void drop() {
        drop = true;
    }

    public void setHost(String hos) {
        host = hos;
    }

    public void setPort(int por) {
        port = por;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void output() {
        System.out.println(lines);
    }

    public void getResponse(HttpURLConnection httpcon) throws IOException{
        int status = httpcon.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream())); //thanks, google
        response = new StringBuffer();
        String line;
        while((line = in.readLine()) != null){
            response.append(line);
        }
        in.close();
        System.out.println(response);
    }

    public void addLine(String[] line) {
        lines.add(line);
    }

    public void sendResponse(Socket socket) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
        System.out.println("Sending to client: \r\n");
        for (int i = 0; i < responseHeader.size(); i++) {
            System.out.println(responseHeader.get(i));
            out.write(responseHeader.get(i) + "\r\n");
        }
        out.write("\r\n");
        for (int i = 0; i < responseBody.size(); i++) {
            System.out.println(responseBody.get(i));
            out.write(responseBody.get(i) + "\r\n");
        }
        out.flush();
    }

    private String buildHttpString() throws UnsupportedEncodingException{
        StringBuilder message = new StringBuilder();
        for(int i = 0; i < lines.size(); i++){
            message.append(URLEncoder.encode(lines.get(i)[0], "UTF-8"));
            message.append("=");
            for(int j = 1; j < (lines.get(i)).length; j++){
                message.append(lines.get(i)[j]);
                if(lines.get(i).length >= j++){
                    message.append("");
                }
            }
            message.append("&");
        }
        String messageString = message.toString();
        System.out.println("TESTINGBUELDER");
        if(messageString.length() > 0){
            System.out.println(messageString.substring(0, messageString.length() - 1));
            return messageString.substring(0, messageString.length() - 1);
        }
        else{
            System.out.println(messageString);
            return messageString;
        }
    }

    private void sendHTTP() {
        try {
            URL url = new URL("http://"+host);
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            httpcon.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(httpcon.getOutputStream());
            out.writeBytes(buildHttpString());
            out.flush();
            out.close();
            getResponse(httpcon);
            httpcon.disconnect();
        } catch (MalformedURLException m) {
            System.out.println(m);
        } catch (IOException i){
            System.out.println(i);
        }
    }

    private void doHTTPS() {

        // blindly use sockets

        /*
         * old helpful code Socket socket = new Socket(host, port);
         * //socket.getInputStream.read(); BufferedWriter out = new BufferedWriter(new
         * OutputStreamWriter(socket.getOutputStream(), "UTF8")); BufferedReader in =
         * new BufferedReader(new InputStreamReader(socket.getInputStream())); for(int i
         * = 0; i < lines.size(); i++){ System.out.println(lines.get(i));
         * out.write(lines.get(i) + "\r\n"); } out.write(""); out.flush();
         * getReturn(in); socket.close();
         */
    }

    public void sendRequest() throws IOException {
        System.out.println(lines);
        if (!drop) {
            System.out.println(host);
            if (port == 80) {
                sendHTTP();
            } else if (port == 443) {
                doHTTPS();
            }
        }
    }
}