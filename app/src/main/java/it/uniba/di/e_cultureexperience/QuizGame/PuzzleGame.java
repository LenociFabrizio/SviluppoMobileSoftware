package it.uniba.di.e_cultureexperience.QuizGame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Locale;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.R;

public class PuzzleGame extends AppCompatActivity {

    private final int COUNTDOWN_INTERVAL = 1000;
    private final int NUMERO_SPLIT_X = 3;
    private final int NUMERO_SPLIT_Y = 3;

    private final Random random = new Random();

    private GridView gridView;
    private ImageAdapter imageAdapter;

    private int posizioneBlanco;
    private Drawable blanco;
    Button riordina;

    //Creating drawable's vector 1d
    Drawable[] imageSplittedDrawableOneDimension = new Drawable[NUMERO_SPLIT_X * NUMERO_SPLIT_Y];
    Drawable[] randomImages = new Drawable[imageSplittedDrawableOneDimension.length];

    private TextView countdown;

    private boolean verificaSpostamento = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_game);

        gridView = findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this);


        int DURATA_TIMER = 5;
        long durataTimer = TimeUnit.MINUTES.toMillis(DURATA_TIMER);
        countdown = findViewById(R.id.countdownTextView);

        new CountDownTimer(durataTimer, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long l) {

                String durataSec = String.format(Locale.ITALY,"%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));

                countdown.setText(durataSec);
            }

            //todo: Aggiungere popup che mostra il riprova
            @Override
            public void onFinish() {
                countdown.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Il Tempo è scaduto!",Toast.LENGTH_LONG).show();
                goBack();
            }
        }.start();

        Glide.with(PuzzleGame.this)
                .asBitmap()
                //todo: aggiustare link per la foto
                .load("https://firebasestorage.googleapis.com/v0/b/ecolture-experience.appspot.com/o/aldo.jpg?alt=media&token=4703a946-5fba-40d9-b5cb-4f5d7de914c3")
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        //Resource è la foto letta in formato Bitmap
                        //Insert all bitmap splitted into vectorBitmap
                        Bitmap[][] imageSplittedBitmap = splitBitmap(NUMERO_SPLIT_X, NUMERO_SPLIT_Y, resource);
                        //Creating Drawable vector 2d
                        Drawable[][] imageSplittedDrawable = new Drawable[NUMERO_SPLIT_X][NUMERO_SPLIT_Y];

                        //Put all bitMaSplitted into a vector 2d Drawable's
                        for(int i = 0; i < NUMERO_SPLIT_X; i++){
                            for (int y = 0; y < NUMERO_SPLIT_Y; y++){
                                imageSplittedDrawable[i][y] = new BitmapDrawable(getResources(), imageSplittedBitmap[i][y]);
                            }
                        }

                        //Conversion from 2d to 1d vector -- drawable
                        for(int i = 0; i < NUMERO_SPLIT_X; i ++) {
                            System.arraycopy(imageSplittedDrawable[i], 0, imageSplittedDrawableOneDimension, (i * imageSplittedDrawable.length), NUMERO_SPLIT_X);
                        }

                        Glide.with(PuzzleGame.this).asBitmap().load("https://firebasestorage.googleapis.com/v0/b/ecolture-experience.appspot.com/o/blanco.png?alt=media&token=7ec0aae6-c19a-42c1-af78-c8cf2886bfd6")
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                        blanco = new BitmapDrawable(getResources(), resource);

                                        //Elimino ultima foto splittata per inserire blanco -> permetto spostamento
                                        int lastPosition = imageSplittedDrawableOneDimension.length - 1;
                                        imageSplittedDrawableOneDimension[lastPosition] = blanco;

                                        //Randomizzo le immagini splittate
                                        randomImages = getRandomImages();


                                        //Output immagini splittate
                                        gridView.setAdapter(imageAdapter);

                                        gridView.setOnItemClickListener((parent, v, position, id) -> {
                                            System.out.println("posizione="+position);
                                            //riordina();
                                            if(checkUp(position) || checkDown(position) || checkLeft(position) || checkRight(position) ){//|| checkDown(position) || checkLeft(position) || checkRight(position)) {
                                                //System.out.println("posizione="+position);
                                                swap(position);
                                                verificaSpostamento=false;

                                            }
                                        });
                                    }
                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                });
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

        Button resa = findViewById(R.id.bottoneDiResa);
        resa.setOnClickListener(v -> goBack());
    }

    /**
     * creo una matrice bitmap per splittare la foto
     * @return Bitmap[][]
     */
    public Bitmap[][] splitBitmap(int xCount, int yCount, Bitmap resource) {

        //Array di bitmap -- immagine completa splittata
        Bitmap[][] bitmaps = new Bitmap[xCount][yCount];

        int width = resource.getWidth() / xCount, height = resource.getHeight() / yCount;

        // Loop the array and create bitmaps for each coordinate
        for(int x = 0; x < xCount; ++x) {
            for(int y = 0; y < yCount; ++y) {
                // Create the sliced bitmap
                bitmaps[x][y] = Bitmap.createBitmap(resource, x * width, y * height, width, height);
            }
        }

        return bitmaps;
    }

    /**
     * randomizzo le posizioni delle foto nel vettore
     */
    private Drawable[] getRandomImages() {

        int i = -1;

        Vector<Integer> segnalini = new Vector<>();

        do{
            i = i + 1;

            int z = random.nextInt(imageSplittedDrawableOneDimension.length);

            if(!(segnalini.contains(z))){

                segnalini.add(z);

                randomImages[z] = imageSplittedDrawableOneDimension[i];

            }else{
                i = i - 1;
            }
        }while(i < (imageSplittedDrawableOneDimension.length - 1));

        return randomImages;

    }

    public class ImageAdapter extends BaseAdapter {

        private final Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return imageSplittedDrawableOneDimension.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            if(view == null)
                view = layoutInflater.inflate(R.layout.rows_file, viewGroup, false);

            ImageView imageView = view.findViewById(R.id.imageViewRow);

            imageView.setImageDrawable(randomImages[i]);

            return view;
        }
    }

    /**
     * Funzione che permette, al click della foto, lo spostamento nell'unica posizione disponibile
     */
    public void swap(int pos) {

        Drawable[] trasporta = new Drawable[imageSplittedDrawableOneDimension.length];

        System.arraycopy(randomImages, 0, trasporta, 0, imageSplittedDrawableOneDimension.length);

        randomImages[pos] = blanco;
        randomImages[posizioneBlanco] = trasporta[pos];

        posizioneBlanco = pos;

        imageAdapter.notifyDataSetChanged();
        gridView.invalidateViews();

        //todo: non finisce se è corretto
        if(randomImages == imageSplittedDrawableOneDimension){
            System.out.println("okokokokookoo");
            Toast.makeText(getApplicationContext(), "Hai vinto finalmente!",Toast.LENGTH_LONG).show();
            goBack();
        }


    }

    /**
     * Controllo se posso spostare sopra la foto
     * @return verificaSpostamento
     */
    public boolean checkUp(int position){

        boolean verifica = false;

        if (position >= 0 && (position - 3) >=0) {
            if (randomImages[position - 3] == blanco) {
                posizioneBlanco = position - 3;
                System.out.println("posizione="+position + "posizione bianco="+ posizioneBlanco);
                verifica = true;
            }
        }
        return verifica;
    }

    /**
     * Controllo se posso spostare sotto la foto
     * @return verificaSpostamento
     */
    public boolean checkDown(int position) {

        if (position >= 0 && (position + 3) < randomImages.length) {
            if (randomImages[position + 3] == blanco) {
                posizioneBlanco = position + 3;
                verificaSpostamento = true;
            }
        }
        return verificaSpostamento;
    }

    /**
     * Controllo se posso spostare dx la foto
     * @return verificaSpostamento
     */
    public boolean checkRight(int position) {

        if (position >= 0 && (position + 1) < randomImages.length) {
            if (randomImages[position + 1] == blanco && position != 2 && position != 5 ) {
                posizioneBlanco = position + 1;
                verificaSpostamento = true;
            }
        }
        return verificaSpostamento;
    }

    /**
     * Controllo se posso spostare sx la foto
     * @return verificaSpostamento
     */
    public boolean checkLeft(int position) {

        if (position >= 0 && (position - 1) >= 0) {
            if (randomImages[position - 1] == blanco && position != 3 && position != 6 ) {
                posizioneBlanco = position - 1;
                verificaSpostamento = true;
            }
        }
        return verificaSpostamento;
    }

    /**
     * esce dall'activity, ritornando indietro
     */
    private void goBack() {
        Intent i = new Intent(this, DashboardMeteActivity.class);
        startActivity(i);
        finish();
    }

    public void riordina(){
        randomImages=imageSplittedDrawableOneDimension;
    }

}