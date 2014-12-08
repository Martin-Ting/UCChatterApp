/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cs166.webservices;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author mting005
 */
@WebService(serviceName = "Hermes")
public class Hermes {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "operation")
    public String operation() {
        //TODO write your implementation code here:
        return null;
    }
    
}
