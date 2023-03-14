/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.t_symbols;

/**
 *
 * @author Usuario
 */
public class Td_elemento {

    private int ambito;
    private Descripcion d;
    private int first;
//    private String id;

    public Td_elemento(Descripcion d, int a) {
        this.d = d;
        this.ambito = a;
    }

    public int getAmbito() {
        return ambito;
    }

    public void setAmbito(int ambito) {
        this.ambito = ambito;
    }

    public Descripcion getD() {
        return d;
    }

    public void setD(Descripcion d) {
        this.d = d;
    }

//    public String getId() {
//        return id;
//    }
//    public void setId(String id) {
//        this.id = id;
//    }
    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }
}
