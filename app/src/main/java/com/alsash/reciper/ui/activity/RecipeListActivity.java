package com.alsash.reciper.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alsash.reciper.R;
import com.alsash.reciper.ui.fragment.dialog.RecipeBottomDialog;
import com.alsash.reciper.ui.presenter.entity.Recipe;
import com.alsash.reciper.ui.presenter.interaction.RecipeListInteraction;
import com.alsash.reciper.ui.vector.VectorHelper;

public class RecipeListActivity extends BaseDrawerActivity implements RecipeListInteraction {

    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    public void onExpand(Recipe recipe, int position) {
        RecipeBottomDialog bottomDialog = RecipeBottomDialog.newInstance(recipe);
        bottomDialog.show(getSupportFragmentManager(), bottomDialog.getTag());
    }

    @Override
    public void onOpen(Recipe recipe, int position) {
        RecipeDetailActivity.start(RecipeListActivity.this, recipe.getId());
    }

    @Nullable
    @Override
    protected Integer getNavItemId() {
        return R.id.navigation_recipes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list); // Include RecipeSingleListFragment
        bindViews();
        setupToolbar();
        setupFab();
    }

    private void bindViews() {
        toolbar = (Toolbar) findViewById(R.id.activity_recipe_list_toolbar);
        fab = (FloatingActionButton) findViewById(R.id.activity_recipe_list_fab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setupDrawer(toolbar); // parent BaseDrawerActivity call
    }

    private void setupFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_recipe_list, menu);
        new VectorHelper(this).tintMenuItems(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Main menu options
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.search:
                // Call to Search Activity;
                return true;
            case R.id.search_clear:
                clearSearchPrevious();
                return true;
            case R.id.reset:
                resetRecipeListOptions();
                return true;
        }
        // SubMenu options
        int groupId = item.getGroupId();
        switch (groupId) {
            case R.id.group_menu:
                groupRecipeList(itemId);
                return true;
            case R.id.sort_menu:
                sortRecipeList(itemId);
                return true;
            case R.id.filter_menu:
                filterRecipeList(itemId);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetRecipeListOptions() {

    }

    private void clearSearchPrevious() {

    }

    private void groupRecipeList(int groupMenuId) {
        switch (groupMenuId) {
            case R.id.group_all:
            case R.id.group_category:
            case R.id.group_food:
            case R.id.group_label:
        }
    }

    private void sortRecipeList(int sortMenuId) {

        switch (sortMenuId) {
            case R.id.sort_account:
            case R.id.sort_clear:
            case R.id.sort_date_top:
            case R.id.sort_date_end:
            case R.id.sort_name:
            case R.id.sort_rating:
        }
    }

    private void filterRecipeList(int filterMenuId) {
        switch (filterMenuId) {
            case R.id.filter_account:
            case R.id.filter_category:
            case R.id.filter_clear:
            case R.id.filter_date_top:
            case R.id.filter_date_end:
            case R.id.filter_energy:
            case R.id.filter_food:
            case R.id.filter_label:
            case R.id.filter_name:
            case R.id.filter_nutrition:
            case R.id.filter_rating:
            case R.id.filter_time:
        }
    }
}
