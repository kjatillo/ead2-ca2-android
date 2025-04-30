package com.example.ead2_ca2androidclient.api;

import com.example.ead2_ca2androidclient.models.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RecipeApiService {
    @GET("api/Recipes")
    Call<List<Recipe>> getAllRecipes();

    @GET("api/Recipes/{recipeId}")
    Call<Recipe> getRecipeById(@Path("recipeId") int recipeId);

    @GET("api/Recipes/search")
    Call<List<Recipe>> searchRecipes(@Query("query") String query);

    @GET("api/Recipes/filter")
    Call<List<Recipe>> filterRecipes(
            @Query("category") String category,
            @Query("cuisine") String cuisine,
            @Query("minCalories") Integer minCalories,
            @Query("maxCalories") Integer maxCalories,
            @Query("ingredient") String ingredient,
            @Query("withImage") Boolean withImage,
            @Query("withVideo") Boolean withVideo
    );
}