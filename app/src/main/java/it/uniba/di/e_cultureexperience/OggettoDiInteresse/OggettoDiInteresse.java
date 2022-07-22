package it.uniba.di.e_cultureexperience.OggettoDiInteresse;

import android.os.Parcel;
import android.os.Parcelable;

public final class OggettoDiInteresse implements Parcelable {
    private String nome, descrizione, url_immagine, bluetooth_id;

    public OggettoDiInteresse(){}

    public OggettoDiInteresse(String nome, String descrizione, String url_immagine, String bluetooth_id) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.url_immagine = url_immagine;
        this.bluetooth_id = bluetooth_id;
    }

    protected OggettoDiInteresse(Parcel in) {
        nome = in.readString();
        descrizione = in.readString();
        url_immagine = in.readString();
        bluetooth_id = in.readString();
    }

    public static final Creator<OggettoDiInteresse> CREATOR = new Creator<OggettoDiInteresse>() {
        @Override
        public OggettoDiInteresse createFromParcel(Parcel in) {
            return new OggettoDiInteresse(in);
        }

        @Override
        public OggettoDiInteresse[] newArray(int size) {
            return new OggettoDiInteresse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nome);
        parcel.writeString(descrizione);
        parcel.writeString(url_immagine);
        parcel.writeString(bluetooth_id);
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

    public String getBluetooth_id() {
        return bluetooth_id;
    }

    public void setBluetooth_id(String bluetooth_id) {
        this.bluetooth_id = bluetooth_id;
    }

    @Override
    public String toString() {
        return "OggettoDiInteresse{" +
                "nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", url_immagine='" + url_immagine + '\'' +
                ", bluetooth_id='" + bluetooth_id + '\'' +
                '}';
    }
}

