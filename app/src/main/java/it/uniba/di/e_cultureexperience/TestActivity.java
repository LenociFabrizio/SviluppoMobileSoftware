package it.uniba.di.e_cultureexperience;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    public static final String DOMANDA_KEY = "domanda";
    public static final String PRIMA_OPZIONE_KEY = "primaOpzione";
    public static final String SECONDA_OPZIONE_KEY = "secondaOpzione";
    public static final String TERZA_OPZIONE_KEY = "terzaOpzione";
    public static final String OPZIONE_CORRETTA_KEY = "rispostaCorretta";

    int numeroDomanda = 0;

    public static ArrayList<ModelClass> list;

    TextView mDataTextView, mCounterText;
    Button mFetchBtn;

    private String document = "domanda";


    //private DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("domandeQuizBari").document(document);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mDataTextView = (TextView) findViewById(R.id.textViewOutput);
        mFetchBtn = (Button) findViewById(R.id.button_fetch);
        mCounterText = (TextView) findViewById(R.id.counterTxt);

        mFetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numeroDomanda++;
                fecthData(document);

                document = "domanda";
                document = document + numeroDomanda;
                Log.d("COUNTER DOC", String.valueOf(numeroDomanda));
                Log.d("NAME DOC", document);
            }
        });

    }



    public void fecthData(String question){
        Log.d("TEST OBJ", "dentro");
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("domandeQuizBari").document(question);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists()){
                    Log.d("TEST", "dentro OnSuc - exists");
                    String domanda = documentSnapshot.getString(DOMANDA_KEY);
                    String primaOpzione = documentSnapshot.getString(PRIMA_OPZIONE_KEY);
                    String secondaOpzione = documentSnapshot.getString(SECONDA_OPZIONE_KEY);
                    String terzaOpzione = documentSnapshot.getString(TERZA_OPZIONE_KEY);
                    String rispostaCorretta = documentSnapshot.getString(OPZIONE_CORRETTA_KEY);

                    mDataTextView.setText("\"" + domanda + "\" -- " + "\n"  + primaOpzione + "\n" + secondaOpzione + "\n"  + terzaOpzione + "\n"  + rispostaCorretta);

                }else{
                    Log.d("TEST", "dentro OnSuc - not exists");
                }
            }
        });
    }

    /*public void fecthDataToObj(View view){

        Log.d("TEST OBJ", "dentro");
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    ModelClass myQuestions = documentSnapshot.toObject(ModelClass.class);
                    list.add(myQuestions);
                    Log.d("TEST OBJ", "Dentro doc exists");
                    mDataTextView.setText(myQuestions.getDomanda());
                }
            }
        });
    }*/
}