package it.uniba.di.e_cultureexperience;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.Accesso.ProfileActivityGoogle;
import it.uniba.di.e_cultureexperience.LuogoDiInteresse.LuoghiDiInteresseAdapter;
import it.uniba.di.e_cultureexperience.LuogoDiInteresse.LuogoDiInteresse;
import it.uniba.di.e_cultureexperience.QRScanner.QRScanner;

public class DashboardMeteActivity extends AppCompatActivity {

    List<LuogoDiInteresse> mete;

    RecyclerView list_view_mete;
    public LuoghiDiInteresseAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db;

        mete = new ArrayList<>();

        list_view_mete = findViewById(R.id.lista_luoghi);
        db = FirebaseFirestore.getInstance();

        db.collection("mete").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        LuogoDiInteresse temp = document.toObject(LuogoDiInteresse.class);
                        temp.setId(document.getId());
                        mete.add(temp);
                    }
                    customAdapter = new LuoghiDiInteresseAdapter(getApplicationContext(), mete);
                    list_view_mete.setLayoutManager(new LinearLayoutManager(DashboardMeteActivity.this,LinearLayoutManager.VERTICAL,false));
                    list_view_mete.setAdapter(customAdapter);
                } else {
                    Log.w("ENDRIT", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                }
            }
        });

        onCreateBottomNavigation();
    }


    public void onCreateBottomNavigation(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //Set Home Selected
        bottomNav.setSelectedItemId(R.id.nav_home);
        //Perform ItemSelectedListener
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_scan:
                        startActivity(new Intent(getApplicationContext(), QRScanner.class));
                        return true;
                    //selectedFragment = new ScanFragment();
                    //break;
                    case R.id.nav_profile:

                        //PASSAGGIO VARIABILE "SEGNALINO" DAL'ACTIVITY gOOGLElOGINaCTIVITY
                        //boolean loginGoogle = getIntent().getExtras().getBoolean("segnalino");

                        /*if(loginGoogle==true){
                            startActivity(new Intent(getApplicationContext(), ProfileActivityGoogle.class).putExtra("segnalino",loginGoogle));
                            overridePendingTransition(0, 0);
                            return true;
                        }else {*/
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class)/*.putExtra("segnalino",loginGoogle)*/);
                            overridePendingTransition(0, 0);
                            return true;
                        /*}*/

                }

                /*if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }*/
                return false;
            }
        });
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setQueryHint("Inserisci e cerca");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("MainActivity", "onQueryTextSubmit: "+query);
                if (customAdapter!=null)
                customAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("MainActivity", "onQueryTextChange: "+newText);
                if (customAdapter!=null)
                customAdapter.getFilter().filter(newText);
                return false;
            }
        });

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (customAdapter!=null)
                customAdapter.getFilter().filter("");
                return true;
            }
        });

//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean queryTextFocused) {
//                if(!queryTextFocused) {
//                    customAdapter.getFilter().filter("");
//                    searchView.setQuery("", false);
//                }
//            }
//        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_search) return true;

        return super.onOptionsItemSelected(item);
    }
}
