package com.example.ead2_ca2androidclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ead2_ca2androidclient.R;
import com.example.ead2_ca2androidclient.models.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying recipe items in a RecyclerView
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipes;
    private final OnItemClickListener listener;

    /**
     * Constructor for the adapter
     * @param listener Callback for recipe item clicks
     */
    public RecipeAdapter(OnItemClickListener listener) {
        this.recipes = new ArrayList<>();
        this.listener = listener;
    }

    /**
     * Updates the adapter with a new list of recipes
     * @param recipes The new recipe list
     */
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe, listener);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /**
     * ViewHolder for recipe items
     */
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final TextView recipeName;
        private final TextView recipeCategory;
        private final TextView recipeCuisine;
        private final TextView recipeCalories;
        private final ImageView recipeImage;
        private final Context context;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            recipeName = itemView.findViewById(R.id.text_recipe_name);
            recipeCategory = itemView.findViewById(R.id.text_recipe_category);
            recipeCuisine = itemView.findViewById(R.id.text_recipe_cuisine);
            recipeCalories = itemView.findViewById(R.id.text_recipe_calories);
            recipeImage = itemView.findViewById(R.id.image_recipe);
        }

        /**
         * Binds recipe data to the view elements
         * @param recipe Recipe to display
         * @param listener Click listener for the recipe item
         */
        public void bind(Recipe recipe, OnItemClickListener listener) {
            // Set recipe name
            recipeName.setText(recipe.getName());

            // Set category with a readable format if available
            setTextWithLabel(recipeCategory, context.getString(R.string.category_label), recipe.getCategory());

            // Set cuisine with a readable format if available
            setTextWithLabel(recipeCuisine, context.getString(R.string.cuisine_label), recipe.getCuisine());

            // Set calories if available
            if (recipe.getCalories() > 0) {
                recipeCalories.setText(context.getString(R.string.calories_label) + " " + recipe.getCalories());
            } else {
                recipeCalories.setText("");
            }

            // Load image with Glide
            loadRecipeImage(recipe.getImageUrl());

            // Set click listener for the item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(recipe);
                }
            });
        }

        /**
         * Sets text with a label if value is available, otherwise clears the field
         */
        private void setTextWithLabel(TextView textView, String label, String value) {
            if (value != null && !value.isEmpty()) {
                textView.setText(label + " " + value);
            } else {
                textView.setText("");
            }
        }

        /**
         * Loads recipe image using Glide
         */
        private void loadRecipeImage(String imageUrl) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(recipeImage);
            } else {
                recipeImage.setImageResource(R.drawable.placeholder_image);
            }
        }
    }

    /**
     * Interface for handling recipe item clicks
     */
    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }
}