package it.uniba.di.e_cultureexperience;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public final class LuogoDiInteresse implements Parcelable {
    private String nome;
    private String url_immagine;
    private String descrizione;
    private String id;

    public LuogoDiInteresse(){}

    public LuogoDiInteresse(String nome, String url_immagine, String descrizione, String id) {
        this.nome = nome;
        this.url_immagine = url_immagine;
        this.descrizione = descrizione;
        this.id = id;
    }

    public LuogoDiInteresse(Parcel in) {
        nome = in.readString();
        url_immagine = in.readString();
        descrizione = in.readString();
        id = in.readString();
    }

    public static final Creator<LuogoDiInteresse> CREATOR = new Creator<LuogoDiInteresse>() {
        @Override
        public LuogoDiInteresse createFromParcel(Parcel in) {
            return new LuogoDiInteresse(in);
        }

        @Override
        public LuogoDiInteresse[] newArray(int size) {
            return new LuogoDiInteresse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nome);
        parcel.writeString(url_immagine);
        parcel.writeString(descrizione);
        parcel.writeString(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrl_immagine() {
        return url_immagine;
    }

    public void setUrl_immagine(String url_immagine) {
        this.url_immagine = url_immagine;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public static Creator<LuogoDiInteresse> getCREATOR() {
        return CREATOR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LuogoDiInteresse that = (LuogoDiInteresse) o;
        return Objects.equals(nome, that.nome) && Objects.equals(url_immagine, that.url_immagine) && Objects.equals(descrizione, that.descrizione) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, url_immagine, descrizione, id);
    }

    @Override
    public String toString() {
        return "LuogoDiInteresse{" +
                "nome='" + nome + '\'' +
                ", url_immagine='" + url_immagine + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
