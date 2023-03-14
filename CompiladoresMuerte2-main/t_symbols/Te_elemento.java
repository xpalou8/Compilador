/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.t_symbols;

/**
 *
 * @author Usuario
 */
public class Te_elemento {

    private int ambito;
    private Descripcion d;
    private String id;
    private int next;
    private String idCamp;

    public Te_elemento(String id, int n, Descripcion d) {
        this.d = d;
        ambito = n;
        this.id = id;

    }
    
    public Te_elemento() {
        

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public String getIdCamp() {
        return idCamp;
    }

    public void setIdCamp(String idCamp) {
        this.idCamp = idCamp;
    }
}
