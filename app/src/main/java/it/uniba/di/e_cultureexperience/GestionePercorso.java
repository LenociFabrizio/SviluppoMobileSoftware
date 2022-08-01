package it.uniba.di.e_cultureexperience;

import android.app.Application;
import java.util.ArrayList;

public class GestionePercorso extends Application {
    private String id_oggetto_da_trovare, bluetooth_id_oggetto_da_trovare;
    private ArrayList id_oggetti_da_trovare, id_bluetooth_oggetti_da_trovare;

    public ArrayList getId_oggetti_da_trovare() {
        return id_oggetti_da_trovare;
    }

    public void setId_oggetti_da_trovare(ArrayList<String> id_oggetti_da_trovare) {
        this.id_oggetti_da_trovare = (ArrayList)id_oggetti_da_trovare.clone();
    }

    public ArrayList getId_bluetooth_oggetti_da_trovare() {
        return id_bluetooth_oggetti_da_trovare;
    }

    public void setId_bluetooth_oggetti_da_trovare(ArrayList<String> id_bluetooth_oggetti_da_trovare) {
        this.id_bluetooth_oggetti_da_trovare = (ArrayList)id_bluetooth_oggetti_da_trovare.clone();
    }

    public String getId_oggetto_da_trovare() {
        return id_oggetto_da_trovare;
    }

    public void setId_oggetto_da_trovare(String id_oggetto_da_trovare) {
        this.id_oggetto_da_trovare = id_oggetto_da_trovare;
    }

    public String getBluetooth_id_oggetto_da_trovare() {
        return bluetooth_id_oggetto_da_trovare;
    }

    public void setBluetooth_id_oggetto_da_trovare(String bluetooth_id_oggetto_da_trovare) {
        this.bluetooth_id_oggetto_da_trovare = bluetooth_id_oggetto_da_trovare;
    }
}
