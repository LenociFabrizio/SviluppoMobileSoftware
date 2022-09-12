package it.uniba.di.e_cultureexperience.QuizGame;

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

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }

    @Override
    public String toString() {
        return "SingolaRigaClassifica{" +
                "utente='" + nickname + '\'' +
                ", punteggio=" + punteggio +
                '}';
    }


}
