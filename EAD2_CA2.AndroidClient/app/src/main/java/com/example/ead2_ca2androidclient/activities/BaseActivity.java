package com.example.ead2_ca2androidclient.activities;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ead2_ca2androidclient.R;

/**
 * Base activity class containing common functionality used across multiple activities.
 */
public class BaseActivity extends AppCompatActivity {

    // Using cached string arrays for better performance
    protected String[] DAYS;
    protected String[] MEAL_TYPES;

    private FrameLayout progressContainer;
    private ProgressBar standardProgressBar;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load string arrays from resources
        DAYS = getResources().getStringArray(R.array.days_of_week);
        MEAL_TYPES = getResources().getStringArray(R.array.meal_types);
    }

    /**
     * Shows or hides a progress indicator
     */
    protected void showProgress(ProgressBar progressBar, boolean show) {
        if (progressBar == null) {
            // Use the standard progress bar if none provided
            ensureStandardProgressBarExists();
            progressBar = standardProgressBar;
        }

        // Check if progressBar has a container attached as a tag
        if (progressBar.getTag() instanceof View) {
            View container = (View) progressBar.getTag();
            container.setVisibility(show ? View.VISIBLE : View.GONE);
        } else {
            // Simple visibility toggle
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Shows or hides a standard progress indicator overlay
     */
    protected void showProgress(boolean show) {
        ensureStandardProgressBarExists();
        showProgress(standardProgressBar, show);
    }

    /**
     * Ensures that a standard progress bar exists and is properly set up
     */
    private void ensureStandardProgressBarExists() {
        if (standardProgressBar == null) {
            standardProgressBar = new ProgressBar(this);
            standardProgressBar.setIndeterminateTintList(getColorStateList(R.color.secondary));

            progressContainer = new FrameLayout(this);
            FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            progressContainer.setLayoutParams(containerParams);

            FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            progressParams.gravity = android.view.Gravity.CENTER;
            standardProgressBar.setLayoutParams(progressParams);
            progressContainer.addView(standardProgressBar);

            progressContainer.setBackgroundColor(0x33000000);

            progressContainer.setVisibility(View.GONE);
            addContentView(progressContainer, containerParams);

            standardProgressBar.setTag(progressContainer);
        }
    }

    /**
     * Updates the visibility of an empty state view based on data availability
     */
    protected void updateEmptyState(TextView emptyView, boolean isEmpty) {
        if (emptyView != null) {
            emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to find index of a day in the DAYS array
     */
    protected int getDayIndex(String day) {
        if (day == null) return -1;

        for (int i = 0; i < DAYS.length; i++) {
            if (DAYS[i].equalsIgnoreCase(day)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Helper method to find index of a meal type in the MEAL_TYPES array
     */
    protected int getMealTypeIndex(String mealType) {
        if (mealType == null) return -1;

        for (int i = 0; i < MEAL_TYPES.length; i++) {
            if (MEAL_TYPES[i].equalsIgnoreCase(mealType)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * API callback handler to reduce boilerplate code in activities
     * @param <T> The type of data returned by the API
     */
    protected abstract class ApiCallbackHandler<T> {
        public void beforeRequest() {
            showProgress(true);
        }

        public void handleSuccess(T result) {
            showProgress(false);
        }

        public void handleError(String errorMessage) {
            showProgress(false);
            showToast(errorMessage);
        }
    }
}