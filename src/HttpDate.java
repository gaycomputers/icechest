package icechest;

import java.io.Serializable;
public class HttpDate implements Serializable{
    private static final long serialVersionUID = 1L;
    long date;
    public HttpDate(){
        
    }
    public long getDate(){
        return date;
    }
    public void setDate(long dat){
        date = dat;
    }
}