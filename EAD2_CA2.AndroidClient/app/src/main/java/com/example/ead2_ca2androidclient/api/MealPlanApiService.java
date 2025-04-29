package com.example.ead2_ca2androidclient.api;

import com.example.ead2_ca2androidclient.models.MealPlan;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MealPlanApiService {
    @GET("api/MealPlans")
    Call<List<MealPlan>> getAllMealPlans();
    
    @GET("api/MealPlans")
    Call<List<MealPlan>> getMealPlansByDay(@Query("day") String day);
    
    @GET("api/MealPlans/{mealPlanId}")
    Call<MealPlan> getMealPlanById(@Path("mealPlanId") int mealPlanId);
    
    @POST("api/MealPlans")
    Call<MealPlan> createMealPlan(@Body MealPlan mealPlan);
    
    @PUT("api/MealPlans/{mealPlanId}")
    Call<MealPlan> updateMealPlan(@Path("mealPlanId") int mealPlanId, @Body MealPlan mealPlan);
    
    @DELETE("api/MealPlans/{mealPlanId}")
    Call<Void> deleteMealPlan(@Path("mealPlanId") int mealPlanId);
}