/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.io.Serializable;

/**
 *
 * @author Sanjay
 */
public class Login implements Serializable {

    public String username, password;
    public int type; // 1 doc 2 paitent

    Login() {
        username = "";
        password = "";
        type = 1;
    }
}
