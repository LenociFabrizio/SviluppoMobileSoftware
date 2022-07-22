package it.uniba.di.e_cultureexperience;
//todo: rinominare classe in quesitoquizmodelclass
public class ModelClass {

    String domanda, primaOpzione, secondaOpzione, terzaOpzione, rispostaCorretta;

    public ModelClass(){}

    public ModelClass(String domanda, String primaOpzione, String rispostaCorretta, String secondaOpzione, String terzaOpzione) {
        this.domanda = domanda;
        this.primaOpzione = primaOpzione;
        this.secondaOpzione = secondaOpzione;
        this.terzaOpzione = terzaOpzione;
        this.rispostaCorretta = rispostaCorretta;
    }

    public String getDomanda() {
        return domanda;
    }

    public void setDomanda(String domanda) {
        this.domanda = domanda;
    }

    public String getPrimaOpzione() {
        return primaOpzione;
    }

    public void setPrimaOpzione(String primaOpzione) {
        this.primaOpzione = primaOpzione;
    }

    public String getSecondaOpzione() {
        return secondaOpzione;
    }

    public void setSecondaOpzione(String secondaOpzione) {
        this.secondaOpzione = secondaOpzione;
    }

    public void setTerzaOpzione(String terzaOpzione){ this.terzaOpzione = terzaOpzione;}

    public String getTerzaOpzione(){ return terzaOpzione;}

    public String getRispostaCorretta() {
        return rispostaCorretta;
    }

    public void setRispostaCorretta(String rispostaCorretta) {
        this.rispostaCorretta = rispostaCorretta;
    }

    @Override
    public String toString() {
        return "ModelClass{" +
                "domanda='" + domanda + '\'' +
                ", primaOpzione='" + primaOpzione + '\'' +
                ", secondaOpzione='" + secondaOpzione + '\'' +
                ", terzaOpzione='" + terzaOpzione + '\'' +
                ", rispostaCorretta='" + rispostaCorretta + '\'' +
                '}';
    }

}
