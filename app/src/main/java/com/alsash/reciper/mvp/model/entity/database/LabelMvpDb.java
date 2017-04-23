package com.alsash.reciper.mvp.model.entity.database;


import com.alsash.reciper.mvp.model.entity.Label;
import com.alsash.reciper.mvp.model.entity.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Label class that represents Label MVP interface with
 * delegation to Label entity stored in database
 */
public class LabelMvpDb implements Label {

    private final com.alsash.reciper.database.entity.Label labelDb;
    private final List<Recipe> labelRecipes;

    public LabelMvpDb(com.alsash.reciper.database.entity.Label labelDb) {
        this.labelDb = labelDb;
        // Fetch items from database
        this.labelRecipes = getRecipes();
    }

    @Override
    public Long getId() {
        return labelDb.getId();
    }

    @Override
    public String getName() {
        return labelDb.getName();
    }

    @Override
    public List<Recipe> getRecipes() {
        if (labelRecipes != null) return labelRecipes;
        List<Recipe> recipes = new ArrayList<>();
        for (com.alsash.reciper.database.entity.Recipe recipe : labelDb.getRecipes()) {
            recipes.add(new RecipeMvpDb(recipe));
        }
        return recipes;
    }
}
