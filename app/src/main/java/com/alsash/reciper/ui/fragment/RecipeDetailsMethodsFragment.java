package com.alsash.reciper.ui.fragment;

import android.content.Intent;

import com.alsash.reciper.mvp.presenter.BasePresenter;
import com.alsash.reciper.mvp.view.BaseView;

/**
 * Created by alsash on 6/18/17.
 */

public class RecipeDetailsMethodsFragment extends BaseFragment<BaseView> {

    public static RecipeDetailsMethodsFragment newInstance(Intent intent) {
        return getThisFragment(new RecipeDetailsMethodsFragment(), intent);
    }

    @Override
    protected BasePresenter<BaseView> inject() {
        return new BasePresenter<BaseView>() {
            @Override
            public void attach(BaseView view) {

            }

            @Override
            public void visible(BaseView view) {

            }

            @Override
            public void invisible(BaseView view) {

            }

            @Override
            public void refresh(BaseView view) {

            }

            @Override
            public void detach() {

            }
        };
    }
}