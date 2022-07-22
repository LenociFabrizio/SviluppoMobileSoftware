package it.uniba.di.e_cultureexperience;

import android.os.Parcel;
import android.os.Parcelable;

public final class Percorso implements Parcelable {
    private String nome, descrizione, meta, id;
    private int durata;

    public Percorso(){}

    public Percorso(String nome, String descrizione, int durata, String meta, String id) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.durata = durata;
        this.meta = meta;
        this.id = id;
    }

    protected Percorso(Parcel in) {
        nome = in.readString();
        descrizione = in.readString();
        durata = in.readInt();
        meta = in.readString();
        id = in.readString();
    }

    public static final Creator<Percorso> CREATOR = new Creator<Percorso>() {
        @Override
        public Percorso createFromParcel(Parcel in) {
            return new Percorso(in);
        }

        @Override
        public Percorso[] newArray(int size) {
            return new Percorso[size];
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
        parcel.writeInt(durata);
        parcel.writeString(meta);
        parcel.writeString(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
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

    public int getDurata() {
        return durata;
    }

    public void setDurata(int durata) {
        this.durata = durata;
    }

    @Override
    public String toString() {
        return "Percorso{" +
                "nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", meta='" + meta + '\'' +
                ", id='" + id + '\'' +
                ", durata=" + durata +
                '}';
    }
}

