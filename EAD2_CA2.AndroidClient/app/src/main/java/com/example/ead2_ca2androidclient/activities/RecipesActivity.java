package com.example.ead2_ca2androidclient.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ead2_ca2androidclient.R;
import com.example.ead2_ca2androidclient.adapter.RecipeAdapter;
import com.example.ead2_ca2androidclient.models.Recipe;
import com.example.ead2_ca2androidclient.repository.RecipeRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipesActivity extends BaseActivity implements RecipeAdapter.OnItemClickListener,
        NavigationBarView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private ProgressBar progressBar;
    private TextView noRecipesText;
    private BottomNavigationView bottomNavigationView;
    private SearchView searchView;
    private ImageButton filterButton;

    private RecipeRepository recipeRepository;
    private List<Recipe> allRecipes = new ArrayList<>();

    private String currentCategory = "";
    private String currentCuisine = "";
    private Integer currentMinCalories = null;
    private Integer currentMaxCalories = null;
    private String currentIngredient = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        recyclerView = findViewById(R.id.recycler_recipes);
        progressBar = findViewById(R.id.progress_bar);
        noRecipesText = findViewById(R.id.text_no_recipes);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchView = findViewById(R.id.search_view);
        filterButton = findViewById(R.id.btn_filter);

        setupRecyclerView();

        setupSearchView();
        setupFilterButton();

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_recipes);

        recipeRepository = new RecipeRepository();

        loadAllRecipes();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() && !searchView.isIconified()) {
                    loadAllRecipes();
                }
                return true;
            }
        });
    }

    private void setupFilterButton() {
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
    }

    private void loadAllRecipes() {
        showProgress(progressBar, true);

        recipeRepository.getAllRecipes(new RecipeRepository.ApiCallback<List<Recipe>>() {
            @Override
            public void onSuccess(final List<Recipe> result) {
                // Run on UI thread to update the UI safely
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(progressBar, false);
                        allRecipes = result;
                        adapter.setRecipes(result);
                        updateEmptyState(noRecipesText, result.isEmpty());
                    }
                });
            }

            @Override
            public void onError(final String message) {
                // Run on UI thread to update the UI safely
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(progressBar, false);
                        showToast("Error loading recipes: " + message);
                        updateEmptyState(noRecipesText, true);
                    }
                });
            }
        });
    }

    private void searchRecipes(String query) {
        if (query.isEmpty()) {
            loadAllRecipes();
            return;
        }

        showProgress(progressBar, true);

        recipeRepository.searchRecipes(query, new RecipeRepository.ApiCallback<List<Recipe>>() {
            @Override
            public void onSuccess(final List<Recipe> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(progressBar, false);
                        adapter.setRecipes(result);
                        updateEmptyState(noRecipesText, result.isEmpty());
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(progressBar, false);
                        showToast("Error searching recipes: " + message);
                        updateEmptyState(noRecipesText, true);
                    }
                });
            }
        });
    }

    private void filterRecipes() {
        showProgress(progressBar, true);

        // Convert empty strings to null for the API call
        String category = currentCategory.isEmpty() ? null : currentCategory;
        String cuisine = currentCuisine.isEmpty() ? null : currentCuisine;
        String ingredient = currentIngredient.isEmpty() ? null : currentIngredient;

        recipeRepository.filterRecipes(
                category, cuisine, currentMinCalories, currentMaxCalories,
                ingredient, null, null,
                new RecipeRepository.ApiCallback<List<Recipe>>() {
                    @Override
                    public void onSuccess(final List<Recipe> result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(progressBar, false);
                                adapter.setRecipes(result);
                                updateEmptyState(noRecipesText, result.isEmpty());
                            }
                        });
                    }

                    @Override
                    public void onError(final String message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(progressBar, false);
                                showToast("Error filtering recipes: " + message);
                                updateEmptyState(noRecipesText, true);
                            }
                        });
                    }
                }
        );
    }

    private void showFilterDialog() {
        // Create a dialog to show filter options
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_recipe_filter, null);
        builder.setView(dialogView);

        final Spinner categorySpinner = dialogView.findViewById(R.id.spinner_category);
        final Spinner cuisineSpinner = dialogView.findViewById(R.id.spinner_cuisine);
        final EditText minCaloriesEdit = dialogView.findViewById(R.id.edit_min_calories);
        final EditText maxCaloriesEdit = dialogView.findViewById(R.id.edit_max_calories);
        final EditText ingredientEdit = dialogView.findViewById(R.id.edit_ingredient);
        Button applyButton = dialogView.findViewById(R.id.btn_apply);
        Button resetButton = dialogView.findViewById(R.id.btn_reset);

        setupFilterSpinners(categorySpinner, cuisineSpinner);

        setCurrentFilterValues(categorySpinner, cuisineSpinner, minCaloriesEdit,
                maxCaloriesEdit, ingredientEdit);

        final AlertDialog dialog = builder.create();

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from UI
                currentCategory = categorySpinner.getSelectedItem().toString();
                currentCuisine = cuisineSpinner.getSelectedItem().toString();

                String minCalStr = minCaloriesEdit.getText().toString();
                String maxCalStr = maxCaloriesEdit.getText().toString();

                // Only set values if the text fields are not empty
                if (!minCalStr.isEmpty()) {
                    try {
                        currentMinCalories = Integer.parseInt(minCalStr);
                    } catch (NumberFormatException e) {
                        currentMinCalories = null;
                    }
                } else {
                    currentMinCalories = null;
                }

                if (!maxCalStr.isEmpty()) {
                    try {
                        currentMaxCalories = Integer.parseInt(maxCalStr);
                    } catch (NumberFormatException e) {
                        currentMaxCalories = null;
                    }
                } else {
                    currentMaxCalories = null;
                }

                currentIngredient = ingredientEdit.getText().toString();

                filterRecipes();
                dialog.dismiss();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFilters(categorySpinner, cuisineSpinner, minCaloriesEdit,
                        maxCaloriesEdit, ingredientEdit);
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void setupFilterSpinners(Spinner categorySpinner, Spinner cuisineSpinner) {
        Set<String> categories = new HashSet<>();
        Set<String> cuisines = new HashSet<>();

        categories.add("");
        cuisines.add("");

        // Collect unique categories and cuisines from recipes
        for (Recipe recipe : allRecipes) {
            if (recipe.getCategory() != null && !recipe.getCategory().isEmpty()) {
                categories.add(recipe.getCategory());
            }
            if (recipe.getCuisine() != null && !recipe.getCuisine().isEmpty()) {
                cuisines.add(recipe.getCuisine());
            }
        }

        // Convert sets to sorted arrays
        String[] categoryArray = categories.toArray(new String[0]);
        if (categoryArray.length > 1) {
            Arrays.sort(categoryArray, 1, categoryArray.length);
        }

        String[] cuisineArray = cuisines.toArray(new String[0]);
        if (cuisineArray.length > 1) {
            Arrays.sort(cuisineArray, 1, cuisineArray.length);
        }

        // Create and set adapters for spinners
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, categoryArray);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<String> cuisineAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, cuisineArray);
        cuisineSpinner.setAdapter(cuisineAdapter);
    }

    private void setCurrentFilterValues(Spinner categorySpinner, Spinner cuisineSpinner,
                                        EditText minCaloriesEdit, EditText maxCaloriesEdit,
                                        EditText ingredientEdit) {
        if (!currentCategory.isEmpty()) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) categorySpinner.getAdapter();
            int position = adapter.getPosition(currentCategory);
            if (position >= 0) {
                categorySpinner.setSelection(position);
            }
        }

        if (!currentCuisine.isEmpty()) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) cuisineSpinner.getAdapter();
            int position = adapter.getPosition(currentCuisine);
            if (position >= 0) {
                cuisineSpinner.setSelection(position);
            }
        }

        if (currentMinCalories != null) {
            minCaloriesEdit.setText(String.valueOf(currentMinCalories));
        }

        if (currentMaxCalories != null) {
            maxCaloriesEdit.setText(String.valueOf(currentMaxCalories));
        }

        if (!currentIngredient.isEmpty()) {
            ingredientEdit.setText(currentIngredient);
        }
    }

    private void resetFilters(Spinner categorySpinner, Spinner cuisineSpinner,
                              EditText minCaloriesEdit, EditText maxCaloriesEdit,
                              EditText ingredientEdit) {
        // Clear all filter values
        currentCategory = "";
        currentCuisine = "";
        currentMinCalories = null;
        currentMaxCalories = null;
        currentIngredient = "";

        // Reset UI elements
        categorySpinner.setSelection(0);
        cuisineSpinner.setSelection(0);
        minCaloriesEdit.setText("");
        maxCaloriesEdit.setText("");
        ingredientEdit.setText("");
    }

    @Override
    public void onItemClick(Recipe recipe) {
        // Navigate to recipe detail view
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getRecipeId());
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_meal_plans) {
            Intent intent = new Intent(this, MealPlansActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (itemId == R.id.navigation_recipes) {
            // We're already on recipes page, just return true
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.navigation_recipes);
    }
}