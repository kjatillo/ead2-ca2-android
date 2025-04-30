package com.example.ead2_ca2androidclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.example.ead2_ca2androidclient.R;
import com.example.ead2_ca2androidclient.models.Recipe;
import com.example.ead2_ca2androidclient.repository.RecipeRepository;

public class RecipeDetailActivity extends BaseActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    private ImageView recipeImageView;
    private TextView recipeNameText;
    private TextView categoryText;
    private TextView cuisineText;
    private TextView caloriesText;
    private TextView ingredientsText;
    private TextView instructionsText;
    private Button addToMealPlanButton;

    private RecipeRepository recipeRepository;
    private Recipe currentRecipe;
    private int recipeId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_EAD2_CA2AndroidClient_WithActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        initViews();

        setupActionBar();

        recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        if (recipeId == -1) {
            showToast(getString(R.string.recipe_not_found));
            finish();
            return;
        }

        recipeRepository = new RecipeRepository();

        loadRecipeDetails(recipeId);

        setupAddToMealPlanButton();
    }

    private void initViews() {
        recipeImageView = findViewById(R.id.image_recipe);
        recipeNameText = findViewById(R.id.text_recipe_name);
        categoryText = findViewById(R.id.text_category);
        cuisineText = findViewById(R.id.text_cuisine);
        caloriesText = findViewById(R.id.text_calories);
        ingredientsText = findViewById(R.id.text_ingredients);
        instructionsText = findViewById(R.id.text_instructions);
        addToMealPlanButton = findViewById(R.id.btn_add_to_meal_plan);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(R.string.back_to_recipes);
        } else {
            addManualBackButton();
        }
    }

    private void addManualBackButton() {
        try {
            // Find the root view (NestedScrollView) from the layout
            NestedScrollView scrollView = findViewById(R.id.recipe_detail_scroll_view);
            if (scrollView == null) {
                // If the scrollview doesn't have an ID, try finding the first child of the content view
                View contentView = findViewById(android.R.id.content);
                if (contentView instanceof ViewGroup && ((ViewGroup)contentView).getChildCount() > 0) {
                    View firstChild = ((ViewGroup)contentView).getChildAt(0);
                    if (firstChild instanceof NestedScrollView) {
                        scrollView = (NestedScrollView) firstChild;
                    }
                }
            }

            if (scrollView != null) {
                // Create a button with proper styling
                Button backButton = new Button(this);
                backButton.setText("← Back to Recipes");
                backButton.setOnClickListener(v -> onBackPressed());

                // Create layout parameters for the button
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                buttonParams.setMargins(16, 16, 16, 16);
                backButton.setLayoutParams(buttonParams);

                // Create a container to hold the button and existing content
                LinearLayout newContainer = new LinearLayout(this);
                newContainer.setOrientation(LinearLayout.VERTICAL);
                newContainer.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

                // Get the original content
                ViewGroup parent = (ViewGroup) scrollView.getParent();
                int scrollViewIndex = -1;
                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (parent.getChildAt(i) == scrollView) {
                        scrollViewIndex = i;
                        break;
                    }
                }

                if (scrollViewIndex != -1) {
                    // Remove scrollView from its parent
                    parent.removeView(scrollView);

                    // Add the button and original content to the new container
                    newContainer.addView(backButton);
                    newContainer.addView(scrollView);

                    // Add the new container to the original parent
                    parent.addView(newContainer, scrollViewIndex);
                }
            } else {
                // If we can't find the scrollView, add a button at the top
                View contentView = findViewById(android.R.id.content);
                if (contentView instanceof ViewGroup) {
                    Button backButton = new Button(this);
                    backButton.setText("← Back to Recipes");
                    backButton.setOnClickListener(v -> onBackPressed());

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(16, 16, 16, 16);
                    backButton.setLayoutParams(params);

                    // Add to existing layout as first element
                    ((ViewGroup) contentView).addView(backButton, 0);
                }
            }
        } catch (Exception e) {
            // If any error occurs, fallback to the simplest approach
            showToast("Use the Back button or gesture to return to Recipes");
        }
    }

    private void loadRecipeDetails(int recipeId) {
        showProgress(true);

        recipeRepository.getRecipeById(recipeId, new RecipeRepository.ApiCallback<Recipe>() {
            @Override
            public void onSuccess(Recipe recipe) {
                runOnUiThread(() -> {
                    showProgress(false);
                    if (recipe != null) {
                        displayRecipeDetails(recipe);
                        currentRecipe = recipe;
                    } else {
                        showToast(getString(R.string.recipe_not_found));
                        finish();
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showProgress(false);
                    showToast(getString(R.string.error_loading_recipe, message));
                    finish();
                });
            }
        });
    }

    private void displayRecipeDetails(Recipe recipe) {
        recipeNameText.setText(recipe.getName());

        if (recipe.getCategory() != null && !recipe.getCategory().isEmpty()) {
            categoryText.setText(recipe.getCategory());
        } else {
            categoryText.setText(R.string.not_specified);
        }

        if (recipe.getCuisine() != null && !recipe.getCuisine().isEmpty()) {
            cuisineText.setText(recipe.getCuisine());
        } else {
            cuisineText.setText(R.string.not_specified);
        }

        if (recipe.getCalories() > 0) {
            caloriesText.setText(String.valueOf(recipe.getCalories()));
        } else {
            caloriesText.setText(R.string.not_specified);
        }

        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            ingredientsText.setText(recipe.getIngredients());
        } else {
            ingredientsText.setText(R.string.no_ingredients);
        }

        if (recipe.getInstructions() != null && !recipe.getInstructions().isEmpty()) {
            instructionsText.setText(recipe.getInstructions());
        } else {
            instructionsText.setText(R.string.no_instructions);
        }

        // Load image with Glide if available
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(recipe.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(recipeImageView);
        } else {
            recipeImageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    private void setupAddToMealPlanButton() {
        addToMealPlanButton.setOnClickListener(v -> {
            if (currentRecipe != null) {
                Intent intent = new Intent(this, MealPlanEditActivity.class);
                intent.putExtra(MealPlanEditActivity.EXTRA_RECIPE_ID, currentRecipe.getRecipeId());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}