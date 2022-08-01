package it.uniba.di.e_cultureexperience.QuizGame;

import android.os.Parcel;
import android.os.Parcelable;

//todo: rinominare classe in quesitoquizmodelclass
public class QuesitoQuiz implements Parcelable {

    String domanda, primaOpzione, secondaOpzione, terzaOpzione, rispostaCorretta;

    public QuesitoQuiz(){}

    public QuesitoQuiz(String domanda, String primaOpzione, String rispostaCorretta, String secondaOpzione, String terzaOpzione) {
        this.domanda = domanda;
        this.primaOpzione = primaOpzione;
        this.secondaOpzione = secondaOpzione;
        this.terzaOpzione = terzaOpzione;
        this.rispostaCorretta = rispostaCorretta;
    }

    protected QuesitoQuiz(Parcel in) {
        domanda = in.readString();
        primaOpzione = in.readString();
        secondaOpzione = in.readString();
        terzaOpzione = in.readString();
        rispostaCorretta = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(domanda);
        dest.writeString(primaOpzione);
        dest.writeString(secondaOpzione);
        dest.writeString(terzaOpzione);
        dest.writeString(rispostaCorretta);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuesitoQuiz> CREATOR = new Creator<QuesitoQuiz>() {
        @Override
        public QuesitoQuiz createFromParcel(Parcel in) {
            return new QuesitoQuiz(in);
        }

        @Override
        public QuesitoQuiz[] newArray(int size) {
            return new QuesitoQuiz[size];
        }
    };

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
