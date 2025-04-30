package com.example.ead2_ca2androidclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.ead2_ca2androidclient.R;
import com.example.ead2_ca2androidclient.models.MealPlan;
import com.example.ead2_ca2androidclient.models.Recipe;
import com.example.ead2_ca2androidclient.repository.MealPlanRepository;
import com.example.ead2_ca2androidclient.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;

public class MealPlanEditActivity extends BaseActivity {
    
    public static final String EXTRA_MEAL_PLAN_ID = "extra_meal_plan_id";
    public static final String EXTRA_DAY = "extra_day";
    public static final String EXTRA_MEAL_TYPE = "extra_meal_type";
    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";
    public static final String EXTRA_NOTES = "extra_notes";
    
    private TextView titleText;
    private Spinner daySpinner;
    private Spinner mealTypeSpinner;
    private Spinner recipeSpinner;
    private EditText notesEdit;
    private Button saveButton;
    private Button cancelButton;
    
    private MealPlanRepository mealPlanRepository;
    private RecipeRepository recipeRepository;
    
    private List<Recipe> availableRecipes = new ArrayList<>();
    private int mealPlanId = -1;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan_edit);
        
        // Initialize UI components
        initViews();
        
        // Initialize repositories
        mealPlanRepository = new MealPlanRepository();
        recipeRepository = new RecipeRepository();
        
        // Setup spinners
        setupSpinners();
        
        // Setup buttons
        setupButtons();
        
        // Check if we're editing an existing meal plan
        checkForExistingMealPlan();
        
        // Load recipes for spinner
        loadRecipes();
    }
    
    private void initViews() {
        titleText = findViewById(R.id.text_title);
        daySpinner = findViewById(R.id.spinner_day);
        mealTypeSpinner = findViewById(R.id.spinner_meal_type);
        recipeSpinner = findViewById(R.id.spinner_recipe);
        notesEdit = findViewById(R.id.edit_notes);
        saveButton = findViewById(R.id.btn_save);
        cancelButton = findViewById(R.id.btn_cancel);
    }
    
    private void setupSpinners() {
        // Setup day spinner
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, DAYS);
        daySpinner.setAdapter(dayAdapter);
        
        // Setup meal type spinner
        ArrayAdapter<String> mealTypeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, MEAL_TYPES);
        mealTypeSpinner.setAdapter(mealTypeAdapter);
    }
    
    private void loadRecipes() {
        RecipeLoadingHandler handler = new RecipeLoadingHandler();
        handler.beforeRequest();
        
        recipeRepository.getAllRecipes(new RecipeRepository.ApiCallback<List<Recipe>>() {
            @Override
            public void onSuccess(List<Recipe> result) {
                handler.handleRecipesLoaded(result);
            }

            @Override
            public void onError(String message) {
                handler.handleError(getString(R.string.error_loading_recipes, message));
            }
        });
    }
    
    private void setupRecipeSpinner() {
        String[] recipeNames = new String[availableRecipes.size()];
        for (int i = 0; i < availableRecipes.size(); i++) {
            recipeNames[i] = availableRecipes.get(i).getName();
        }
        
        ArrayAdapter<String> recipeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, recipeNames);
        recipeSpinner.setAdapter(recipeAdapter);
    }
    
    private void setupButtons() {
        saveButton.setOnClickListener(v -> saveMealPlan());
        cancelButton.setOnClickListener(v -> finish());
    }
    
    private void checkForExistingMealPlan() {
        Intent intent = getIntent();
        
        if (intent.hasExtra(EXTRA_MEAL_PLAN_ID)) {
            // We're editing an existing meal plan
            mealPlanId = intent.getIntExtra(EXTRA_MEAL_PLAN_ID, -1);
            titleText.setText(R.string.edit_meal_plan);
            
            // Set existing data
            String day = intent.getStringExtra(EXTRA_DAY);
            String mealType = intent.getStringExtra(EXTRA_MEAL_TYPE);
            String notes = intent.getStringExtra(EXTRA_NOTES);
            
            int dayIndex = getDayIndex(day);
            if (dayIndex >= 0) daySpinner.setSelection(dayIndex);
            
            int mealTypeIndex = getMealTypeIndex(mealType);
            if (mealTypeIndex >= 0) mealTypeSpinner.setSelection(mealTypeIndex);
            
            if (notes != null) {
                notesEdit.setText(notes);
            }
        } else {
            titleText.setText(R.string.add_meal_plan);
        }
    }
    
    private void saveMealPlan() {
        MealPlan mealPlan = new MealPlan();
        
        if (mealPlanId != -1) {
            mealPlan.setMealPlanId(mealPlanId);
        }
        
        mealPlan.setDay(DAYS[daySpinner.getSelectedItemPosition()]);
        mealPlan.setMealType(MEAL_TYPES[mealTypeSpinner.getSelectedItemPosition()]);
        
        int recipePosition = recipeSpinner.getSelectedItemPosition();
        if (recipePosition >= 0 && recipePosition < availableRecipes.size()) {
            Recipe selectedRecipe = availableRecipes.get(recipePosition);
            mealPlan.setRecipeId(selectedRecipe.getRecipeId());
        } else {
            showToast(getString(R.string.please_select_recipe));
            return;
        }
        
        mealPlan.setNotes(notesEdit.getText().toString());
        
        if (mealPlanId == -1) {
            createMealPlan(mealPlan);
        } else {
            updateMealPlan(mealPlan);
        }
    }
    
    private void createMealPlan(MealPlan mealPlan) {
        MealPlanCreationHandler handler = new MealPlanCreationHandler();
        handler.beforeRequest();
        
        mealPlanRepository.createMealPlan(mealPlan, new MealPlanRepository.ApiCallback<MealPlan>() {
            @Override
            public void onSuccess(MealPlan result) {
                handler.handleSuccess(result);
            }

            @Override
            public void onError(String message) {
                handler.handleError(getString(R.string.error_adding_meal_plan, message));
            }
        });
    }
    
    private void updateMealPlan(MealPlan mealPlan) {
        MealPlanUpdateHandler handler = new MealPlanUpdateHandler();
        handler.beforeRequest();
        
        mealPlanRepository.updateMealPlan(mealPlan, new MealPlanRepository.ApiCallback<MealPlan>() {
            @Override
            public void onSuccess(MealPlan result) {
                handler.handleSuccess(result);
            }

            @Override
            public void onError(String message) {
                handler.handleError(getString(R.string.error_updating_meal_plan, message));
            }
        });
    }
    
    private int getRecipeIndex(int recipeId) {
        for (int i = 0; i < availableRecipes.size(); i++) {
            if (availableRecipes.get(i).getRecipeId() == recipeId) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Handler for recipe loading
     */
    private class RecipeLoadingHandler extends ApiCallbackHandler<List<Recipe>> {
        public void handleRecipesLoaded(List<Recipe> recipes) {
            showProgress(false);
            availableRecipes = recipes;
            setupRecipeSpinner();
            
            // Get recipe ID from intent (whether editing a meal plan or coming from recipe details)
            int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
            if (recipeId != -1) {
                int recipeIndex = getRecipeIndex(recipeId);
                if (recipeIndex != -1) {
                    recipeSpinner.setSelection(recipeIndex);
                }
            }
        }
        
        @Override
        public void handleSuccess(List<Recipe> result) {
            super.handleSuccess(result);
            // Not used directly, using handleRecipesLoaded instead
        }
    }
    
    /**
     * Handler for meal plan creation
     */
    private class MealPlanCreationHandler extends ApiCallbackHandler<MealPlan> {
        @Override
        public void handleSuccess(MealPlan result) {
            super.handleSuccess(result);
            showToast(getString(R.string.meal_plan_added));
            setResult(RESULT_OK);
            finish();
        }
    }
    
    /**
     * Handler for meal plan update
     */
    private class MealPlanUpdateHandler extends ApiCallbackHandler<MealPlan> {
        @Override
        public void handleSuccess(MealPlan result) {
            super.handleSuccess(result);
            showToast(getString(R.string.meal_plan_updated));
            setResult(RESULT_OK);
            finish();
        }
    }
}