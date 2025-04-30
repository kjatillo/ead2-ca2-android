package com.example.ead2_ca2androidclient.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ead2_ca2androidclient.R;
import com.example.ead2_ca2androidclient.adapter.MealPlanAdapter;
import com.example.ead2_ca2androidclient.models.MealPlan;
import com.example.ead2_ca2androidclient.models.Recipe;
import com.example.ead2_ca2androidclient.repository.MealPlanRepository;
import com.example.ead2_ca2androidclient.repository.RecipeRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MealPlansActivity extends BaseActivity implements MealPlanAdapter.MealPlanListener, 
        NavigationBarView.OnItemSelectedListener {
    
    // UI Components
    private RecyclerView recyclerView;
    private MealPlanAdapter adapter;
    private TextView noMealPlansText;
    private FloatingActionButton addButton;
    private BottomNavigationView bottomNavigationView;
    
    // Data and repositories
    private MealPlanRepository mealPlanRepository;
    private RecipeRepository recipeRepository;
    private List<Recipe> availableRecipes = new ArrayList<>();
    
    // Activity Result Launchers
    private final ActivityResultLauncher<Intent> addMealPlanLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadAllMealPlans();
                }
            });
    
    private final ActivityResultLauncher<Intent> editMealPlanLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadAllMealPlans();
                }
            });
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plans);
        
        // Initialize UI components
        initViews();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup add button
        setupAddButton();
        
        // Setup bottom navigation
        setupBottomNavigation();
        
        // Initialize repositories
        mealPlanRepository = new MealPlanRepository();
        recipeRepository = new RecipeRepository();
        
        // Load data
        loadAllMealPlans();
        loadAllRecipes();
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.recycler_meal_plans);
        noMealPlansText = findViewById(R.id.text_no_meal_plans);
        addButton = findViewById(R.id.fab_add_meal_plan);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }
    
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_meal_plans);
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MealPlanAdapter(this);
        recyclerView.setAdapter(adapter);
    }
    
    private void setupAddButton() {
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MealPlanEditActivity.class);
            addMealPlanLauncher.launch(intent);
        });
    }
    
    private void loadAllMealPlans() {
        MealPlansApiHandler handler = new MealPlansApiHandler();
        handler.beforeRequest();
        
        mealPlanRepository.getAllMealPlans(new MealPlanRepository.ApiCallback<List<MealPlan>>() {
            @Override
            public void onSuccess(List<MealPlan> result) {
                handler.handleMealPlansLoaded(result);
            }

            @Override
            public void onError(String message) {
                handler.handleError(getString(R.string.error_loading_meal_plans, message));
            }
        });
    }
    
    private void loadAllRecipes() {
        recipeRepository.getAllRecipes(new RecipeRepository.ApiCallback<List<Recipe>>() {
            @Override
            public void onSuccess(List<Recipe> result) {
                availableRecipes = result;
            }

            @Override
            public void onError(String message) {
                showToast(getString(R.string.error_loading_recipes, message));
            }
        });
    }
    
    @Override
    public void onEditClick(MealPlan mealPlan) {
        Intent intent = new Intent(this, MealPlanEditActivity.class);
        intent.putExtra(MealPlanEditActivity.EXTRA_MEAL_PLAN_ID, mealPlan.getMealPlanId());
        intent.putExtra(MealPlanEditActivity.EXTRA_DAY, mealPlan.getDay());
        intent.putExtra(MealPlanEditActivity.EXTRA_MEAL_TYPE, mealPlan.getMealType());
        intent.putExtra(MealPlanEditActivity.EXTRA_RECIPE_ID, mealPlan.getRecipeId());
        intent.putExtra(MealPlanEditActivity.EXTRA_NOTES, mealPlan.getNotes());
        editMealPlanLauncher.launch(intent);
    }

    @Override
    public void onDeleteClick(MealPlan mealPlan) {
        showDeleteConfirmationDialog(mealPlan);
    }
    
    private void showDeleteConfirmationDialog(MealPlan mealPlan) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_meal_plan_delete, null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        
        dialogView.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            deleteMealPlan(mealPlan.getMealPlanId());
            dialog.dismiss();
        });
        
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private void deleteMealPlan(int mealPlanId) {
        DeleteMealPlanHandler handler = new DeleteMealPlanHandler();
        handler.beforeRequest();
        
        mealPlanRepository.deleteMealPlan(mealPlanId, new MealPlanRepository.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                handler.handleSuccess(result);
            }

            @Override
            public void onError(String message) {
                handler.handleError(getString(R.string.error_deleting_meal_plan, message));
            }
        });
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_recipes) {
            Intent intent = new Intent(this, RecipesActivity.class);
            startActivity(intent);
            finish(); // Finish this activity when navigating away
            return true;
        } else if (itemId == R.id.navigation_meal_plans) {
            // We're already on meal plans page, just return true
            return true;
        }
        return false;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Make sure the correct navigation item is selected
        bottomNavigationView.setSelectedItemId(R.id.navigation_meal_plans);
    }
    
    /**
     * Custom callback handler for meal plans loading
     */
    private class MealPlansApiHandler extends ApiCallbackHandler<List<MealPlan>> {
        
        public void handleMealPlansLoaded(List<MealPlan> mealPlans) {
            showProgress(false);
            adapter.updateMealPlanList(mealPlans);
            updateEmptyState(noMealPlansText, mealPlans.isEmpty());
        }
        
        @Override
        public void handleSuccess(List<MealPlan> result) {
            super.handleSuccess(result);
            // Not used directly, using handleMealPlansLoaded instead
        }
    }
    
    /**
     * Custom callback handler for meal plan deletion
     */
    private class DeleteMealPlanHandler extends ApiCallbackHandler<Boolean> {
        
        @Override
        public void handleSuccess(Boolean result) {
            super.handleSuccess(result);
            showToast(getString(R.string.meal_plan_deleted));
            loadAllMealPlans(); // Reload meal plans after deletion
        }
    }
}