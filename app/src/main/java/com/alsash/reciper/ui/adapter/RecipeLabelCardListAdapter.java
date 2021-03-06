package com.alsash.reciper.ui.adapter;

import android.view.ViewGroup;

import com.alsash.reciper.mvp.model.entity.Label;
import com.alsash.reciper.ui.adapter.holder.RecipeLabelCardHolder;
import com.alsash.reciper.ui.adapter.interaction.RecipeGroupInteraction;

import java.util.List;

/**
 * An Adapter that makes Recipe Label view holders
 */
public class RecipeLabelCardListAdapter
        extends BaseRecipeGroupAdapter<Label, RecipeLabelCardHolder> {

    public RecipeLabelCardListAdapter(List<Label> groupList,
                                      RecipeGroupInteraction<Label> groupInteraction) {
        super(groupList, groupInteraction);
    }

    @Override
    public RecipeLabelCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecipeLabelCardHolder(parent);
    }
}
