package it.uniba.di.e_cultureexperience.QuizGame;
//TODO: Cambiare nome classe a "Utente"
public class SingolaRigaClassifica {
    String nickname; int punteggio;

    public SingolaRigaClassifica(String nickname, int punteggio) {
        this.nickname = nickname;
        this.punteggio = punteggio;
    }

    public SingolaRigaClassifica(){}

    public String getNickname() {
        return nickname;
    }

    public int getPunteggio() {
        return punteggio;
    }



    @Override
    public String toString() {
        return "SingolaRigaClassifica{" +
                "utente='" + nickname + '\'' +
                ", punteggio=" + punteggio +
                '}';
    }


}
