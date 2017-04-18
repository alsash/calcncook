package com.alsash.reciper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alsash.reciper.database.ApiDatabase;
import com.alsash.reciper.mvp.model.entity.Category;
import com.alsash.reciper.mvp.presenter.CategoriesPresenter;
import com.alsash.reciper.mvp.view.CategoriesView;
import com.alsash.reciper.ui.adapter.RecipeGroupCardListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alsash on 4/18/17.
 */

public class RecipeCategoriesFragment extends BaseRecipesFragment<CategoriesView, CategoriesPresenter>
        implements CategoriesView {

    private CategoriesPresenter presenter;
    private List<Category> categories;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CategoriesPresenter(ApiDatabase.getInstance().getSession(getContext()));
        categories = new ArrayList<>();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.loadCategories();
    }

    @Override
    public void addCategories(List<Category> categories) {

    }

    @Override
    public void showContent() {

    }

    @Override
    protected void setupList(RecyclerView list) {
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(new RecipeGroupCardListAdapter(this, categories));
        list.setNestedScrollingEnabled(false);
    }

    @Override
    public CategoriesPresenter createPresenter() {
        return presenter;
    }
}
