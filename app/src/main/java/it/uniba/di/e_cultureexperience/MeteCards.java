package it.uniba.di.e_cultureexperience;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class MeteCards implements Parcelable {
    private String id, nome, descrizione, url_immagine;

    public MeteCards() {
    }

    public MeteCards(String id, String nome, String descrizione, String url_immagine) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.url_immagine = url_immagine;
    }

    public MeteCards(Parcel in) {
        nome = in.readString();
        url_immagine = in.readString();
        descrizione = in.readString();
        id = in.readString();
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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getUrl_immagine() {
        return url_immagine;
    }

    public void setUrl_immagine(String url_immagine) {
        this.url_immagine = url_immagine;
    }

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

    public static Creator<MeteCards> getCREATOR() {
        return CREATOR;
    }
    
    public static final Creator<MeteCards> CREATOR = new Creator<MeteCards>() {
        @Override
        public MeteCards createFromParcel(Parcel in) {
            return new MeteCards(in);
        }

        @Override
        public MeteCards[] newArray(int size) {
            return new MeteCards[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        
        if (this == o) return true;
        
        if (o == null || getClass() != o.getClass()) return false;
        
        MeteCards that = (MeteCards) o;
        
        return Objects.equals(nome, that.nome) && Objects.equals(url_immagine, that.url_immagine) 
                && Objects.equals(descrizione, that.descrizione) && Objects.equals(id, that.id);
        
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, url_immagine, descrizione, id);
    }
}
