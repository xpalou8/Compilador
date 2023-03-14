/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CompiladoresMuerte2.Semantic;

import CompiladoresMuerte2.Sintactic.*;
import CompiladoresMuerte2.t_symbols.Tipo;
import CompiladoresMuerte2.t_symbols.TipoSubyacente;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 */
enum Modo{
    CONST,VAR,PROCF,PROCP,MODEVAR,MODECONST,MODERESULT
}
public class semanticResponse {
    
    boolean valid;
    
    private TipoSubyacente tsb;
    private String tipo;
    private Modo mvp;
    private boolean teparams;
    private Modo mode;

    public semanticResponse() {
        tipo="";
    }

    public boolean isValid() {
        return valid;
    }

    public Modo getMode() {
        return mode;
    }

    public void setMode(Modo mode) {
        this.mode = mode;
    }
    

    public void setValid(boolean valid) {
        this.valid = valid;
    }


    public TipoSubyacente getTsb() {
        return tsb;
    }

    public void setTsb(TipoSubyacente tsb) {
        this.tsb = tsb;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public CompiladoresMuerte2.Semantic.Modo getMvp() {
        return mvp;
    }

    public void setMvp(CompiladoresMuerte2.Semantic.Modo mvp) {
        this.mvp = mvp;
    }

    public boolean isTeparams() {
        return teparams;
    }

    public void setTeparams(boolean teparams) {
        this.teparams = teparams;
    }
    
}
