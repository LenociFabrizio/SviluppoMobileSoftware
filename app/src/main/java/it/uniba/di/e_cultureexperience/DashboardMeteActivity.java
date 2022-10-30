package it.uniba.di.e_cultureexperience;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.LuogoDiInteresse.LuoghiDiInteresseAdapter;
import it.uniba.di.e_cultureexperience.LuogoDiInteresse.LuogoDiInteresse;
import it.uniba.di.e_cultureexperience.QrCodeScanner;

public class DashboardMeteActivity extends AppCompatActivity {

    private List<LuogoDiInteresse> meteList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView list_view_mete;
    public LuoghiDiInteresseAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_view_mete = findViewById(R.id.lista_luoghi);
        list_view_mete.setLayoutManager(new LinearLayoutManager(this));


        db.collection("mete").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    LuogoDiInteresse temp = document.toObject(LuogoDiInteresse.class);
                    temp.setId(document.getId());
                    meteList.add(temp);
                }
                customAdapter = new LuoghiDiInteresseAdapter(getApplicationContext(), meteList);
                list_view_mete.setLayoutManager(new LinearLayoutManager(DashboardMeteActivity.this,LinearLayoutManager.VERTICAL,false));
                list_view_mete.setAdapter(customAdapter);
            } else {
                Log.w("Error", "Errore nella lettura del database.", task.getException());
            }
        });

        onCreateBottomNavigation();
    }


    public void onCreateBottomNavigation(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //Set Home Selected
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.nav_home:
                    return true;

                case R.id.nav_scan:
                    startActivity(new Intent(getApplicationContext(), QrCodeScanner.class));
                    return true;

                case R.id.nav_profile:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class)/*.putExtra("segnalino",loginGoogle)*/);
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }


    public void onResume() {
        super.onResume();
        list_view_mete.setAdapter(customAdapter);
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_search) return true;

        return super.onOptionsItemSelected(item);
    }
}
