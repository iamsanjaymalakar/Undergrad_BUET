package Client;

import java.util.Hashtable;

/**
 *
 * @author Sanjay
 */
public class ClientClass {
    Hashtable<String, Sub> table;
    String name;
    Sub[] s;
    int sc;
    String user,msg;
    String id,text;
    String temp;
     
    public ClientClass() {
        table = new Hashtable<String, Sub>();
        sc=0;
        s= new Sub[100];
    }
    
    void setMessage(String user,String msg){
        this.user= user;
        this.msg= msg;
    }
    
    void showMessage(){
        if(table.containsKey(user)){
            Sub temp;
            temp=table.get(user);
            temp.showMessage(msg);
        }
    }
    
//    void getMessage(String id,String text){
//        this.id=id;
//        this.text=text;
//    }
//    
//    
//    
//    boolean sendMessage() throws Exception{
//        temp=id+"$"+text;
//        
//    }
    
    void entry(String str){
        name=str;
        // hashtable check 
        if(!table.containsKey(name)){
            s[sc]= new Sub(name);
            table.put(name,s[sc]);
        }
        sc++;
    }        
    
}
