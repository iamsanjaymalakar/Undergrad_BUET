package hmsserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 *
 * @author Sanjay
 */

public class DataStore {
    
    public void save(HashMap hm,String name){
        try {
            File fileOne = new File(name);
            FileOutputStream fos = new FileOutputStream(fileOne);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(hm);
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public HashMap getFile(String name) {
        HashMap temp= new HashMap<>();
        try {
            File toRead = new File(name);
            FileInputStream fis = new FileInputStream(toRead);
            ObjectInputStream ois = new ObjectInputStream(fis);
            temp = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return temp;
        }
   
}
