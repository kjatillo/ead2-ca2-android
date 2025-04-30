package com.example.ead2_ca2androidclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ead2_ca2androidclient.R;
import com.example.ead2_ca2androidclient.models.MealPlan;
import com.example.ead2_ca2androidclient.repository.RecipeRepository;
import com.example.ead2_ca2androidclient.models.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for displaying meal plans in a RecyclerView
 */
public class MealPlanAdapter extends RecyclerView.Adapter<MealPlanAdapter.MealPlanViewHolder> {
    
    private List<MealPlan> mealPlans;
    private final MealPlanListener listener;
    private final Map<Integer, String> recipeNameCache = new HashMap<>();
    private final RecipeRepository recipeRepository = new RecipeRepository();
    
    /**
     * Constructor for the adapter
     * @param listener Interface to handle meal plan item interactions
     */
    public MealPlanAdapter(MealPlanListener listener) {
        this.mealPlans = new ArrayList<>();
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public MealPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_plan, parent, false);
        return new MealPlanViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MealPlanViewHolder holder, int position) {
        MealPlan mealPlan = mealPlans.get(position);
        
        // Set day text with appropriate styling
        holder.dayText.setText(mealPlan.getDay());
        holder.dayText.setTextColor(android.graphics.Color.WHITE);
        holder.dayText.setVisibility(View.VISIBLE);
        
        holder.mealTypeText.setText(mealPlan.getMealType());
        
        // Load and set recipe name
        loadRecipeName(holder, mealPlan.getRecipeId());
        
        // Show notes if available
        if (mealPlan.getNotes() != null && !mealPlan.getNotes().isEmpty()) {
            holder.notesText.setText(mealPlan.getNotes());
            holder.notesText.setVisibility(View.VISIBLE);
        } else {
            holder.notesText.setVisibility(View.GONE);
        }
        
        // Set up button click listeners
        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(mealPlan);
            }
        });
        
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(mealPlan);
            }
        });
    }
    
    /**
     * Loads recipe name either from cache or from repository
     */
    private void loadRecipeName(@NonNull MealPlanViewHolder holder, int recipeId) {
        // Check cache first for better performance
        if (recipeNameCache.containsKey(recipeId)) {
            holder.recipeNameText.setText(recipeNameCache.get(recipeId));
            return;
        }
        
        // Show loading text while fetching
        holder.recipeNameText.setText(R.string.loading);
        
        // Load recipe data
        recipeRepository.getRecipeById(recipeId, new RecipeRepository.ApiCallback<Recipe>() {
            @Override
            public void onSuccess(Recipe result) {
                String recipeName = result.getName();
                // Store in cache for future use
                recipeNameCache.put(recipeId, recipeName);
                
                // Update UI on the main thread
                holder.recipeNameText.setText(recipeName);
            }
            
            @Override
            public void onError(String message) {
                holder.recipeNameText.setText(R.string.unknown_recipe);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return mealPlans.size();
    }
    
    /**
     * Updates the adapter data with new meal plans
     */
    public void updateMealPlanList(List<MealPlan> mealPlans) {
        this.mealPlans = mealPlans;
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder for meal plan items
     */
    public static class MealPlanViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        TextView mealTypeText;
        TextView recipeNameText;
        TextView notesText;
        ImageButton editButton;
        ImageButton deleteButton;
        
        public MealPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find and store all views
            dayText = itemView.findViewById(R.id.text_day);
            mealTypeText = itemView.findViewById(R.id.text_meal_type);
            recipeNameText = itemView.findViewById(R.id.text_recipe_name);
            notesText = itemView.findViewById(R.id.text_notes);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
    
    /**
     * Interface for meal plan item interactions
     */
    public interface MealPlanListener {
        void onEditClick(MealPlan mealPlan);
        void onDeleteClick(MealPlan mealPlan);
    }
}