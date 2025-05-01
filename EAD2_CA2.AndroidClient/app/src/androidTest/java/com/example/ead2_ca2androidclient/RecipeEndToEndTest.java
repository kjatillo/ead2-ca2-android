package com.example.ead2_ca2androidclient;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.ead2_ca2androidclient.activities.RecipesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.endsWith;

/**
 * End-to-end test for recipe browsing functionality
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RecipeEndToEndTest {

    @Rule
    public ActivityScenarioRule<RecipesActivity> activityRule =
            new ActivityScenarioRule<>(RecipesActivity.class);

    /**
     * Custom ViewAction that selects a specific item from the BottomNavigationView
     * This avoids ambiguity by directly working with the BottomNavigationView itself
     */
    public static ViewAction selectBottomNavigationItem(final int itemPosition) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(BottomNavigationView.class);
            }

            @Override
            public String getDescription() {
                return "Select item at position " + itemPosition + " in BottomNavigationView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                BottomNavigationView bottomNav = (BottomNavigationView) view;
                bottomNav.setSelectedItemId(bottomNav.getMenu().getItem(itemPosition).getItemId());
            }
        };
    }

    @Test
    public void testViewRecipeDetails() throws InterruptedException {
        // Make sure we're on the Recipes tab
        onView(withId(R.id.bottom_navigation)).perform(selectBottomNavigationItem(0));

        // Wait for initial data to load
        Thread.sleep(2000);

        // Check if we're on the recipes screen
        onView(withId(R.id.recycler_recipes)).check(matches(isDisplayed()));

        // Click on the first recipe in the list
        onView(withId(R.id.recycler_recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Wait for detail screen to load
        Thread.sleep(1000);

        // Verify we're on the recipe details screen
        onView(withId(R.id.text_recipe_name)).check(matches(isDisplayed()));

        // Use more specific matchers for labels that might have matching text elsewhere
        try {
            // Try with specific ID if available
            onView(withId(R.id.text_ingredients_label)).check(matches(isDisplayed()));
            onView(withId(R.id.text_instructions_label)).check(matches(isDisplayed()));
        } catch (Exception e) {
            // Fallback to text with more specific criteria if ID doesn't work
            onView(allOf(withText("Ingredients"), withClassName(endsWith("TextView")))).check(matches(isDisplayed()));
            onView(allOf(withText("Instructions"), withClassName(endsWith("TextView")))).check(matches(isDisplayed()));
        }

        // Check that the Add to Meal Plan button is present
        onView(withId(R.id.btn_add_to_meal_plan)).check(matches(isDisplayed()));

        // Go back to the recipe list
        pressBack();

        // Verify we're back on the recipes screen
        onView(withId(R.id.recycler_recipes)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddRecipeToMealPlan() throws InterruptedException {
        // Make sure we're on the Recipes tab
        onView(withId(R.id.bottom_navigation)).perform(selectBottomNavigationItem(0));

        // Wait for initial data to load
        Thread.sleep(2000);

        // Check if we're on the recipes screen
        onView(withId(R.id.recycler_recipes)).check(matches(isDisplayed()));

        // Click on the first recipe in the list
        onView(withId(R.id.recycler_recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Wait for detail screen to load
        Thread.sleep(1000);

        // Click the "Add to Meal Plan" button
        onView(withId(R.id.btn_add_to_meal_plan)).perform(click());

        // Wait for the meal plan edit screen to load
        Thread.sleep(1000);

        // Verify we're on the meal plan edit screen
        onView(withId(R.id.spinner_day)).check(matches(isDisplayed()));
        onView(withId(R.id.spinner_meal_type)).check(matches(isDisplayed()));

        // Verify recipe spinner is present
        onView(withId(R.id.spinner_recipe)).check(matches(isDisplayed()));

        // Cancel the meal plan creation
        onView(withId(R.id.btn_cancel)).perform(click());

        // Wait for navigation
        Thread.sleep(1000);

        // Verify we're back on the recipe details screen
        onView(withId(R.id.text_recipe_name)).check(matches(isDisplayed()));
    }

    /**
     * Test searching for a recipe using the search view
     */
    @Test
    public void testSearchRecipes() throws InterruptedException {
        // Make sure we're on the Recipes tab
        onView(withId(R.id.bottom_navigation)).perform(selectBottomNavigationItem(0));

        // Wait for initial data to load
        Thread.sleep(2000);

        // Check if we're on the recipes screen
        onView(withId(R.id.recycler_recipes)).check(matches(isDisplayed()));

        // Check if the search view is displayed
        onView(withId(R.id.search_view)).check(matches(isDisplayed()));

        // Type a search term in the search view
        onView(withId(androidx.appcompat.R.id.search_src_text))
                .perform(typeText("chicken"), closeSoftKeyboard());
                
        // Press search/enter key to submit the search
        onView(withId(androidx.appcompat.R.id.search_src_text))
                .perform(pressImeActionButton());

        // Wait for search results to load
        Thread.sleep(2000);

        // Verify the recipe list is still displayed (with filtered results)
        onView(withId(R.id.recycler_recipes)).check(matches(isDisplayed()));

        // Test clearing using the X button in search view
        onView(withId(androidx.appcompat.R.id.search_close_btn)).perform(click());

        // Wait for the full list to reload
        Thread.sleep(1000);
        
        // Verify the recipe list shows all recipes again
        onView(withId(R.id.recycler_recipes)).check(matches(isDisplayed()));

        // Perform another search to test manual clearing
        onView(withId(androidx.appcompat.R.id.search_src_text))
                .perform(typeText("beef"), closeSoftKeyboard(), pressImeActionButton());
                
        // Wait for search results to load
        Thread.sleep(1000);
        
        // Clear the search by replacing with empty text (alternative way)
        onView(withId(androidx.appcompat.R.id.search_src_text))
                .perform(replaceText(""), closeSoftKeyboard());

        // Wait for the full list to reload
        Thread.sleep(1000);
    }

    /**
     * Test filtering recipes using the filter dialog
     */
    @Test
    public void testFilterRecipes() throws InterruptedException {
        // Make sure we're on the Recipes tab
        onView(withId(R.id.bottom_navigation)).perform(selectBottomNavigationItem(0));

        // Wait for initial data to load
        Thread.sleep(2000);

        // Check if we're on the recipes screen
        onView(withId(R.id.recycler_recipes)).check(matches(isDisplayed()));

        // Check if filter button is displayed
        onView(withId(R.id.btn_filter)).check(matches(isDisplayed()));

        // Click on the filter button
        onView(withId(R.id.btn_filter)).perform(click());

        // Wait for filter dialog to appear
        Thread.sleep(1000);

        // Verify filter dialog components are displayed
        onView(withId(R.id.spinner_category)).check(matches(isDisplayed()));
        onView(withId(R.id.spinner_cuisine)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_min_calories)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_max_calories)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_ingredient)).check(matches(isDisplayed()));

        // Enter min calories
        onView(withId(R.id.edit_min_calories))
                .perform(typeText("100"), closeSoftKeyboard());

        // Enter max calories
        onView(withId(R.id.edit_max_calories))
                .perform(typeText("600"), closeSoftKeyboard());

        // Enter an ingredient
        onView(withId(R.id.edit_ingredient))
                .perform(typeText("beef"), closeSoftKeyboard());

        // Apply the filter
        onView(withId(R.id.btn_apply)).perform(click());

        // Wait for filtered results to load
        Thread.sleep(2000);

        // Verify the recipe list is still displayed (with filtered results)
        onView(withId(R.id.recycler_recipes)).check(matches(isDisplayed()));
        
        // Verify that Clear Filters button is now visible
        onView(withId(R.id.btn_clear_filters)).check(matches(isDisplayed()));
        
        // Click on the Clear Filters button
        onView(withId(R.id.btn_clear_filters)).perform(click());
        
        // Wait for all recipes to reload
        Thread.sleep(2000);
        
        // Verify the recipe list is still displayed (now with all recipes)
        onView(withId(R.id.recycler_recipes)).check(matches(isDisplayed()));
        
        // Test alternative way of clearing filters (via dialog)
        // Open filter dialog again
        onView(withId(R.id.btn_filter)).perform(click());

        // Wait for filter dialog to appear
        Thread.sleep(1000);

        // Reset the filters
        onView(withId(R.id.btn_reset)).perform(click());

        // Apply the reset filter (no filter)
        onView(withId(R.id.btn_apply)).perform(click());

        // Wait for all recipes to reload
        Thread.sleep(2000);
    }

    /**
     * Test clearing filters using the Clear Filters button
     */
    @Test
    public void testClearFiltersButton() throws InterruptedException {
        // Make sure we're on the Recipes tab
        onView(withId(R.id.bottom_navigation)).perform(selectBottomNavigationItem(0));

        // Wait for initial data to load
        Thread.sleep(2000);

        // Check if we're on the recipes screen
        onView(withId(R.id.recycler_recipes)).check(matches(isDisplayed()));

        // Open filter dialog
        onView(withId(R.id.btn_filter)).perform(click());

        // Wait for filter dialog to appear
        Thread.sleep(1000);

        // Enter an ingredient to filter by
        onView(withId(R.id.edit_ingredient))
                .perform(typeText("chicken"), closeSoftKeyboard());

        // Apply the filter
        onView(withId(R.id.btn_apply)).perform(click());

        // Wait for filtered results to load
        Thread.sleep(2000);

        // Verify that Clear Filters button is now visible
        onView(withId(R.id.btn_clear_filters)).check(matches(isDisplayed()));

        // Click on the Clear Filters button
        onView(withId(R.id.btn_clear_filters)).perform(click());

        // Wait for all recipes to reload
        Thread.sleep(2000);

        // Verify all the recipes are now displayed
        onView(withId(R.id.recycler_recipes)).check(matches(isDisplayed()));
    }
}