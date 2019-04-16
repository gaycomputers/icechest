//it would be unethical for me to not cite this article, https://www.baeldung.com/java-http-request which helped me understand a better method of sending HTTP than my original socket method
package icechest;

import java.io.*;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class HttpRequest implements Serializable{
    private static final long serialVersionUID = 1L;
    private int port = 80;
    private String host = null;
    private ArrayList<String[]> lines = new ArrayList<String[]>();
    private StringBuffer responseHeader = null;
    // private ArrayList<String> responseBody = new ArrayList<String>();
    private boolean drop = false;
    private StringBuffer responseBody = null;
    private String status = null;
    private String contentline = null;
    private String url;

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
    public void addURL(String ur){
        url = ur;
    }
    public String getUrl(){
        return url;
    }
    public String getFullResponse(HttpURLConnection httpcon) throws IOException {
        System.out.println("get full response");
        StringBuilder response = new StringBuilder();
        /*
        response.append(httpcon.getResponseCode()).append(" ").append(httpcon.getResponseMessage()).append("\n"); // why
        status = response.toString();
        */

        // actually 100% from baeldung.com
        httpcon.getHeaderFields().entrySet().stream().filter(entry -> entry.getKey() != null).forEach(entry -> {
            response.append(entry.getKey()).append(": ");
            List headerValues = entry.getValue();
            Iterator it = headerValues.iterator();
            if (it.hasNext()) {
                response.append(it.next());
                while (it.hasNext()) {
                    response.append(", ").append(it.next());
                }
            }
        });
        // ~

        System.out.println(response);
        return response.toString();
    }

    public void getResponse(HttpURLConnection httpcon) throws IOException, NullPointerException{
        System.out.println("get response");
        int status = httpcon.getResponseCode();
        Reader inputRead = null;
        if (status > 299) {
            inputRead = new InputStreamReader(httpcon.getErrorStream());
        } else {
            inputRead = new InputStreamReader(httpcon.getInputStream());
        }
        BufferedReader in = new BufferedReader(inputRead);
        responseBody = new StringBuffer();
        responseHeader = new StringBuffer();
        responseHeader.append(getFullResponse(httpcon));
        String line;
        if((line = in.readLine()) != null){
            responseBody.append(line);
        }
        while ((line = in.readLine()) != null) {
            responseBody.append("\n");
            responseBody.append(line);
        }

        System.out.println(responseBody);
        in.close();
    }

    public void addLine(String[] line) {
        lines.add(line);
    }

    public void sendResponse(Socket socket) throws IOException, NullPointerException, SocketException{
        if (!drop) {
            // BufferedWriter out = new BufferedWriter(new
            // OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            
            System.out.println(status);

            System.out.println("Sending to client: \n");

            //out.writeBytes(responseHeader.toString());
            out.writeBytes("HTTP/1.1 200 OK\r\n");
            out.writeBytes("Server: Java CacheProxy\r\n");
            out.writeBytes("Content-Type: text/html\r\n");
            out.writeBytes("Content-Length: "+responseBody.toString().length()+"\r\n");
            //out.writeBytes(contentline+"\r\n");
            out.writeBytes("Content-Type: text/html\r\n");
            out.writeBytes("\r\n");
            out.writeBytes(responseBody.toString());
            //out.flush();
            out.close();
            System.out.println("Sent");
        }
    }

    private String buildHttpString() throws UnsupportedEncodingException {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            message.append(URLEncoder.encode(lines.get(i)[0], "UTF-8"));
            message.append("=");
            for (int j = 1; j < (lines.get(i)).length; j++) {
                message.append(lines.get(i)[j]);
                if (lines.get(i).length >= j++) {
                    message.append("");
                }
            }
            message.append("&");
        }
        String messageString = message.toString();
        if (messageString.length() > 0) {
            System.out.println(messageString.substring(0, messageString.length() - 1));
            return messageString.substring(0, messageString.length() - 1);
        } else {
            return messageString;
        }
    }

    private void sendHTTP() {
        try {
            URL url = new URL("http://" + host);
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
        } catch (IOException i) {
            System.out.println(i);
        } catch (NullPointerException n){
            System.out.println(n);
        } //catch (SocketException s) {
            //System.out.println(s);
        //}
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
        try {
            if (!drop) {
                System.out.println(host + "\n");
                if (port == 80) {
                    sendHTTP();
                } else if (port == 443) {
                    // doHTTPS(); //not set to do https yet.
                    drop();
                }
            }
        } catch (NullPointerException n) {
            drop();
            System.out.println(n);
        }
    }
}