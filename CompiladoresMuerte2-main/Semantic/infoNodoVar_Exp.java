/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.Semantic;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 */
public class infoNodoVar_Exp {

    private int r; 
/* 
    
    .r :
    
    Indica el número de variable a la que es deixa el resultat
    de l’operació associada a la reducció de E.
                   
                   ó
    
    Indica el número de variable de la base de referència associada a la reducció de R.
*/
    
    private int d;
    private String tp;
    
    public infoNodoVar_Exp(){
        
    }
    
     public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }
    

}
