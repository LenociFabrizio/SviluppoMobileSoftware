package it.uniba.di.e_cultureexperience.Accesso;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Utente  {

    String nickname;
    String passw;
    String email;

    public Utente(String nickname, String pass, String email) {
        this.nickname = nickname;
        this.passw = pass;
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPass() {
        return passw;
    }

    public void setPass(String pass) {
        this.passw = pass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}