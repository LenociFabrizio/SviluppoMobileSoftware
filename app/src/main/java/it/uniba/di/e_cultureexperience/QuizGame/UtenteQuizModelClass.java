package it.uniba.di.e_cultureexperience.QuizGame;

public class UtenteQuizModelClass {
    String email; int punteggio;

    public UtenteQuizModelClass(String email, int punteggio) {
        this.email = email;
        this.punteggio = punteggio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }

    @Override
    public String toString() {
        return "UtenteQuizModelClass{" +
                "email='" + email + '\'' +
                ", punteggio=" + punteggio +
                '}';
    }
}
