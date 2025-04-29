package com.example.ead2_ca2androidclient.models;

public class MealPlan {
    private int mealPlanId;
    private int recipeId;
    private String day;
    private String mealType;
    private String notes;
    
    // Default constructor required for JSON deserialization
    public MealPlan() {
    }

    public MealPlan(int mealPlanId, int recipeId, String day, String mealType, String notes) {
        this.mealPlanId = mealPlanId;
        this.recipeId = recipeId;
        this.day = day;
        this.mealType = mealType;
        this.notes = notes;
    }

    public int getMealPlanId() {
        return mealPlanId;
    }

    public void setMealPlanId(int mealPlanId) {
        this.mealPlanId = mealPlanId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}