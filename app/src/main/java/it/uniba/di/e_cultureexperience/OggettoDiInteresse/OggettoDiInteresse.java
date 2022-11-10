package it.uniba.di.e_cultureexperience.OggettoDiInteresse;

import android.os.Parcel;
import android.os.Parcelable;

public final class OggettoDiInteresse implements Parcelable {
    private String id, nome, descrizione, url_immagine, bluetooth_id;
    private double latitudine, longitudine;

    public OggettoDiInteresse(){}

    public OggettoDiInteresse(String id, String nome, String descrizione, String url_immagine, String bluetooth_id, double latitudine, double longitudine) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.url_immagine = url_immagine;
        this.bluetooth_id = bluetooth_id;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }


    protected OggettoDiInteresse(Parcel in) {
        id = in.readString();
        nome = in.readString();
        descrizione = in.readString();
        url_immagine = in.readString();
        bluetooth_id = in.readString();
        latitudine = in.readDouble();
        longitudine = in.readDouble();
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
        parcel.writeString(id);
        parcel.writeString(nome);
        parcel.writeString(descrizione);
        parcel.writeString(url_immagine);
        parcel.writeString(bluetooth_id);
        parcel.writeDouble(latitudine);
        parcel.writeDouble(longitudine);
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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

    public double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }

    @Override
    public String toString() {
        return "OggettoDiInteresse{" +
                "nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", url_immagine='" + url_immagine + '\'' +
                ", bluetooth_id='" + bluetooth_id + '\'' +
                ", latitudine='" + latitudine + '\'' +
                ", longitudine='" + longitudine + '\'' +
                '}';
    }


}

