package com.example.ead2_ca2androidclient.models;

public class Recipe {
    private int recipeId;
    private String name;
    private String category;
    private String cuisine;
    private String ingredients;
    private String instructions;
    private String imageUrl;
    private String videoUrl;
    private double calories;

    // Default constructor required for JSON deserialization
    public Recipe() {
    }

    public Recipe(int recipeId, String name, String category, String cuisine, String ingredients,
                  String instructions, String imageUrl, String videoUrl, double calories) {
        this.recipeId = recipeId;
        this.name = name;
        this.category = category;
        this.cuisine = cuisine;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.calories = calories;
    }

    // Getters and setters
    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }
}