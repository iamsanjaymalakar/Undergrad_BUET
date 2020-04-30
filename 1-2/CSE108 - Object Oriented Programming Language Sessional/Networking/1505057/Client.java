package Offline;

import java.io.IOException;
import java.net.SocketException;

/**
 *
 * @author Sanjay
 */

public class Client {
     public static void main(String args[]) throws SocketException, IOException {
         String userName,IP,serverName;
         int port;
         if(args.length==0){
             userName="User";
             IP="localhost";
             serverName="Server";
             port=12345;
         }
         else{
             userName=args[0];
             port=Integer.parseInt(args[1]);
             IP=args[2];
             serverName=args[3];
         }
         
         // Starting Client Send And Recieve 
         ClientSend x= new ClientSend(userName,port,IP,serverName);
         ClientRecieve y= new ClientRecieve(userName,port,IP,serverName);
     }
    
}
