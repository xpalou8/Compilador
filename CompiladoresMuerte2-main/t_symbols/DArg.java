/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.t_symbols;

/**
 *
 * @author toniborras
 */
public class DArg extends Td{

    private String tipo;
    private String nombre;
    private Tipo modo;

    public DArg() {

    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Tipo getModo() {
        return modo;
    }

    public void setModo(Tipo modo) {
        this.modo = modo;
    }

}
