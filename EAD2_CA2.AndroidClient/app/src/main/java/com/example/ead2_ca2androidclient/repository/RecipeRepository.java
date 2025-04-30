package com.example.ead2_ca2androidclient.repository;

import android.util.Log;

import com.example.ead2_ca2androidclient.api.RecipeApiService;
import com.example.ead2_ca2androidclient.api.RetrofitClient;
import com.example.ead2_ca2androidclient.models.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeRepository {
    private static final String TAG = "RecipeRepository";
    private final RecipeApiService recipeApiService;

    public RecipeRepository() {
        this.recipeApiService = RetrofitClient.getInstance().getRecipeApiService();
    }

    // Interface for handling API response callbacks
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public void getAllRecipes(final ApiCallback<List<Recipe>> callback) {
        Call<List<Recipe>> call = recipeApiService.getAllRecipes();

        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get recipes");
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, "Error getting recipes", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getRecipeById(int recipeId, ApiCallback<Recipe> callback) {
        Call<Recipe> call = recipeApiService.getRecipeById(recipeId);

        call.enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get recipe details");
                }
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                Log.e(TAG, "Error getting recipe details", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void searchRecipes(String query, ApiCallback<List<Recipe>> callback) {
        Call<List<Recipe>> call = recipeApiService.searchRecipes(query);

        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to search recipes");
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, "Error searching recipes", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void filterRecipes(String category, String cuisine, Integer minCalories,
                              Integer maxCalories, String ingredient, Boolean withImage,
                              Boolean withVideo, ApiCallback<List<Recipe>> callback) {
        Call<List<Recipe>> call = recipeApiService.filterRecipes(
                category, cuisine, minCalories, maxCalories,
                ingredient, withImage, withVideo);

        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to filter recipes");
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, "Error filtering recipes", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}