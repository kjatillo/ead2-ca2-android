package com.example.ead2_ca2androidclient;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.example.ead2_ca2androidclient.activities.RecipesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertTrue;

/**
 * End-to-end test for meal plan functionality using Java
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
// Use FixMethodOrder to ensure tests run in alphabetical order (1. Add, 2. Delete)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MealPlanEndToEndTest {

    // Launch the main activity (RecipesActivity) before each test
    @Rule
    public ActivityScenarioRule<RecipesActivity> activityRule =
            new ActivityScenarioRule<>(RecipesActivity.class);
            
    // Unique test identifier to find our test meal plan
    private static final String TEST_MEAL_PLAN_NOTE = "Test meal plan " + System.currentTimeMillis();

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
                uiController.loopMainThreadForAtLeast(500);
            }
        };
    }

    /**
     * Custom ViewAssertion that verifies a view is displayed or logs detailed error if not
     */
    public static ViewAssertion isDisplayedWithDetails() {
        return (view, noViewFoundException) -> {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            if (!view.isShown()) {
                throw new AssertionError("View is not displayed: " +
                        HumanReadables.describe(view));
            }
        };
    }

    /**
     * Test to add a new meal plan with a unique identifier in notes
     */
    @Test
    public void test1_AddMealPlanFlow() throws InterruptedException {
        // Navigate to Meal Plans tab using our custom action
        onView(withId(R.id.bottom_navigation)).perform(selectBottomNavigationItem(1));

        // Wait for the screen to load
        Thread.sleep(2000);

        // Check if the meal plan recycler view is displayed
        onView(withId(R.id.recycler_meal_plans)).check(isDisplayedWithDetails());

        // Click on the FAB to add a new meal plan
        onView(withId(R.id.fab_add_meal_plan)).perform(click());

        // Fill in the meal plan form
        // Select day (Monday)
        onView(withId(R.id.spinner_day)).perform(click());
        // Wait for dropdown to fully appear
        Thread.sleep(500);
        onView(withText("Monday")).inRoot(isPlatformPopup()).perform(click());

        // Select meal type (Dinner)
        onView(withId(R.id.spinner_meal_type)).perform(click());
        // Wait for dropdown to fully appear
        Thread.sleep(500);
        onView(withText("Dinner")).inRoot(isPlatformPopup()).perform(click());

        // Select recipe using spinner - click to open dropdown
        onView(withId(R.id.spinner_recipe)).perform(click());

        // Wait for dropdown to fully appear
        Thread.sleep(1000);

        // Select the first item in the spinner dropdown
        try {
            onData(anything())
                    .inRoot(isPlatformPopup())
                    .atPosition(0)
                    .perform(click());
        } catch (Exception e) {
            pressBack();
            Thread.sleep(500);
        }

        // Add test notes with unique identifier to find this meal plan later
        onView(withId(R.id.edit_notes))
                .perform(typeText(TEST_MEAL_PLAN_NOTE), closeSoftKeyboard());

        // Save the meal plan
        onView(withId(R.id.btn_save)).perform(click());

        Thread.sleep(3000);

        // Verify we returned to the Meal Plans screen
        onView(withId(R.id.recycler_meal_plans)).check(isDisplayedWithDetails());
        
        // Verify our test meal plan is in the list
        onView(withText(containsString(TEST_MEAL_PLAN_NOTE))).check(isDisplayedWithDetails());
    }

    @Test
    public void testNavigateBetweenTabs() throws InterruptedException {
        // Make sure we're on the Recipes tab and check for the recyclerview
        onView(withId(R.id.recycler_recipes)).check(isDisplayedWithDetails());

        // Navigate to Meal Plans tab
        onView(withId(R.id.bottom_navigation)).perform(selectBottomNavigationItem(1));

        Thread.sleep(1000);

        // Verify we're on Meal Plans screen
        onView(withId(R.id.recycler_meal_plans)).check(isDisplayedWithDetails());

        // Navigate back to Recipes tab
        onView(withId(R.id.bottom_navigation)).perform(selectBottomNavigationItem(0));

        Thread.sleep(1000);

        // Verify we're back on Recipes screen
        onView(withId(R.id.recycler_recipes)).check(isDisplayedWithDetails());
    }
    
    /**
     * Test to delete the meal plan created in test1_AddMealPlanFlow
     */
    @Test
    public void test2_DeleteMealPlan() throws InterruptedException {
        // Navigate to Meal Plans tab
        onView(withId(R.id.bottom_navigation)).perform(selectBottomNavigationItem(1));

        // Wait for the screen to load
        Thread.sleep(2000);

        // Check if the meal plan recycler view is displayed
        onView(withId(R.id.recycler_meal_plans)).check(isDisplayedWithDetails());
        
        // Find the meal plan with our unique test note and scroll to it
        onView(withId(R.id.recycler_meal_plans))
            .perform(RecyclerViewActions.scrollTo(
                hasDescendant(withText(containsString(TEST_MEAL_PLAN_NOTE)))
            ));
        
        // Find and click the delete button on our specific test meal plan
        onView(allOf(withId(R.id.btn_delete), 
                hasSibling(withText(containsString(TEST_MEAL_PLAN_NOTE)))))
            .perform(click());
        
        // Wait for confirmation dialog to appear
        Thread.sleep(1000);
        
        // Click the delete/confirm button in dialog
        onView(withId(R.id.btn_delete)).perform(click());
        
        // Wait for deletion and reload to complete
        Thread.sleep(2000);
        
        // Verify we're still on the Meal Plans screen
        onView(withId(R.id.recycler_meal_plans)).check(isDisplayedWithDetails());
        
        // Verify our test meal plan is no longer in the list
        boolean mealPlanDeleted = true;
        try {
            onView(withText(containsString(TEST_MEAL_PLAN_NOTE))).check(doesNotExist());
        } catch (Exception e) {
            mealPlanDeleted = false;
        }
        
        // Assert that the meal plan was deleted
        assertTrue("The test meal plan was not properly deleted", mealPlanDeleted);
    }
    
    /**
     * Custom matcher to find a view that has a sibling matching the given matcher
     */
    public static Matcher<View> hasSibling(final Matcher<View> siblingMatcher) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("has sibling: ");
                siblingMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                if (!(parent instanceof ViewGroup)) {
                    return false;
                }

                ViewGroup group = (ViewGroup) parent;
                for (int i = 0; i < group.getChildCount(); i++) {
                    View child = group.getChildAt(i);
                    if (child != view && siblingMatcher.matches(child)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}