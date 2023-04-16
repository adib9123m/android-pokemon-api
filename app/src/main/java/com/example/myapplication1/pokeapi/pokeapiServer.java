package com.example.myapplication1.pokeapi;

import com.example.myapplication1.models.Pokemonrepository;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface pokeapiServer {
    @GET("pokemon")
    Call<Pokemonrepository> obtenerlistpokemon(@Query("limit") int limit,@Query("offset") int offset);
}
