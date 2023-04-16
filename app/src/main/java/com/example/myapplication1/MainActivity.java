package com.example.myapplication1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.example.myapplication1.models.Pokemon;
import com.example.myapplication1.models.Pokemonrepository;
import com.example.myapplication1.pokeapi.pokeapiServer;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;

import Common.Common;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
   private Retrofit retrofit;
   Toolbar toolbar;
   private RecyclerView recyclerView;
   private ListPokemonAdapter listPokemonAdapter;
   private static final String TAG="POKEDEX";
   private int offset;
   private boolean aptoparacarger;

   BroadcastReceiver showDetail = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           if(intent.getAction().equals(Common.KEY_ENABLE_HOME))
           {
               getSupportActionBar().setDisplayHomeAsUpEnabled(true);//Enable back Button on Toolbar
               getSupportActionBar().setDisplayShowHomeEnabled(true);//too

               //replace Fragment

               Fragment detailFragment = PokemonDetail.getInstance();
               int position = intent.getIntExtra("position",-1);
               Bundle bundle = new Bundle();
               bundle.putInt("position",position);
               detailFragment.setArguments(bundle);

               FragmentTransaction fragmentTransaction =getSupportFragmentManager().beginTransaction();
               //fragmentTransaction.replace(R.id.list_pokemon_fragment,detailFragment);
               fragmentTransaction.addToBackStack("detail");
               fragmentTransaction.commit();

               //Set Pokemon Name for Toolbar
               Pokemon pokemon = Common.commonPokemonList.get(position);
               toolbar.setTitle(pokemon.getName());
           }
       }
   };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //register Broadcast
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(showDetail,new IntentFilter(Common.KEY_ENABLE_HOME));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView =(RecyclerView) findViewById(R.id.recyclerView);
        listPokemonAdapter =new ListPokemonAdapter(this);
        recyclerView.setAdapter(listPokemonAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        public void onScrolled(RecyclerView recycleview,int dx,int dy){
            super.onScrolled(recyclerView,dx,dy);
            if(dy >0){
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                if (aptoparacarger){
                    if ((visibleItemCount + pastVisibleItems >= totalItemCount)) {
                    Log.i(TAG,"Llrgamos al final.");
                    aptoparacarger = false;
                    offset+=20;
                    obtenerDatos(offset);
                    }
                    }
                }
            }

        });

        retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        aptoparacarger = true;
        offset =0;
        obtenerDatos(offset);



    }

    private void obtenerDatos(int offset) {
        pokeapiServer service =retrofit.create(pokeapiServer.class);
        Call<Pokemonrepository> pokemonrepositoryCall = service.obtenerlistpokemon(20 ,offset);

        pokemonrepositoryCall.enqueue(new Callback<Pokemonrepository>() {
            @Override
            public void onResponse(Call<Pokemonrepository> call, Response<Pokemonrepository> response) {
                aptoparacarger=true;
                if(response.isSuccessful()){
                    Pokemonrepository pokemonrepository = response.body();
                    ArrayList<Pokemon> listPokemon = pokemonrepository.getResults();

                   listPokemonAdapter.adiciontlistpokemon(listPokemon);
                }else{
                    Log.e(TAG,"onResponse :"+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Pokemonrepository> call, Throwable t) {
                aptoparacarger=true;
               Log.e(TAG,"onFailure"+t.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                toolbar.setTitle("POKEMON LIST");
                //Clear all fragment detail and pop to list fragment
                getSupportFragmentManager().popBackStack("detail", FragmentManager.POP_BACK_STACK_INCLUSIVE );
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                break;
            default:
                break;
        }

        return true;
    }

}