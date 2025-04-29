package com.example.ead2_ca2androidclient.repository;

import android.util.Log;

import com.example.ead2_ca2androidclient.api.MealPlanApiService;
import com.example.ead2_ca2androidclient.api.RetrofitClient;
import com.example.ead2_ca2androidclient.models.MealPlan;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealPlanRepository {
    private static final String TAG = "MealPlanRepository";
    private final MealPlanApiService mealPlanService;

    public MealPlanRepository() {
        this.mealPlanService = RetrofitClient.getInstance().getMealPlanApiService();
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public void getAllMealPlans(ApiCallback<List<MealPlan>> callback) {
        mealPlanService.getAllMealPlans().enqueue(new Callback<List<MealPlan>>() {
            @Override
            public void onResponse(Call<List<MealPlan>> call, Response<List<MealPlan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch meal plans");
                }
            }

            @Override
            public void onFailure(Call<List<MealPlan>> call, Throwable t) {
                Log.e(TAG, "Error fetching meal plans", t);
                callback.onError(t.getMessage());
            }
        });
    }

    public void getMealPlanById(int mealPlanId, ApiCallback<MealPlan> callback) {
        mealPlanService.getMealPlanById(mealPlanId).enqueue(new Callback<MealPlan>() {
            @Override
            public void onResponse(Call<MealPlan> call, Response<MealPlan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch meal plan");
                }
            }

            @Override
            public void onFailure(Call<MealPlan> call, Throwable t) {
                Log.e(TAG, "Error fetching meal plan", t);
                callback.onError(t.getMessage());
            }
        });
    }

    public void getMealPlansByDay(String day, ApiCallback<List<MealPlan>> callback) {
        mealPlanService.getMealPlansByDay(day).enqueue(new Callback<List<MealPlan>>() {
            @Override
            public void onResponse(Call<List<MealPlan>> call, Response<List<MealPlan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch meal plans for day: " + day);
                }
            }

            @Override
            public void onFailure(Call<List<MealPlan>> call, Throwable t) {
                Log.e(TAG, "Error fetching meal plans by day", t);
                callback.onError(t.getMessage());
            }
        });
    }

    public void createMealPlan(MealPlan mealPlan, ApiCallback<MealPlan> callback) {
        mealPlanService.createMealPlan(mealPlan).enqueue(new Callback<MealPlan>() {
            @Override
            public void onResponse(Call<MealPlan> call, Response<MealPlan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to create meal plan");
                }
            }

            @Override
            public void onFailure(Call<MealPlan> call, Throwable t) {
                Log.e(TAG, "Error creating meal plan", t);
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateMealPlan(MealPlan mealPlan, ApiCallback<MealPlan> callback) {
        mealPlanService.updateMealPlan(mealPlan.getMealPlanId(), mealPlan).enqueue(new Callback<MealPlan>() {
            @Override
            public void onResponse(Call<MealPlan> call, Response<MealPlan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update meal plan");
                }
            }

            @Override
            public void onFailure(Call<MealPlan> call, Throwable t) {
                Log.e(TAG, "Error updating meal plan", t);
                callback.onError(t.getMessage());
            }
        });
    }

    public void deleteMealPlan(int mealPlanId, ApiCallback<Boolean> callback) {
        mealPlanService.deleteMealPlan(mealPlanId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(true);
                } else {
                    callback.onError("Failed to delete meal plan");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error deleting meal plan", t);
                callback.onError(t.getMessage());
            }
        });
    }
}