package com.alsash.reciper.logic;

import android.content.Context;
import android.util.Log;

import com.alsash.reciper.R;
import com.alsash.reciper.logic.event.CategoryEvent;
import com.alsash.reciper.logic.event.LabelEvent;
import com.alsash.reciper.logic.event.RecipeEvent;
import com.alsash.reciper.logic.exception.UnitException;
import com.alsash.reciper.logic.unit.EnergyUnit;
import com.alsash.reciper.logic.unit.RecipeUnit;
import com.alsash.reciper.logic.unit.WeightUnit;
import com.alsash.reciper.mvp.model.derivative.Nutrient;
import com.alsash.reciper.mvp.model.entity.Author;
import com.alsash.reciper.mvp.model.entity.BaseEntity;
import com.alsash.reciper.mvp.model.entity.Category;
import com.alsash.reciper.mvp.model.entity.Food;
import com.alsash.reciper.mvp.model.entity.Ingredient;
import com.alsash.reciper.mvp.model.entity.Label;
import com.alsash.reciper.mvp.model.entity.Measure;
import com.alsash.reciper.mvp.model.entity.Photo;
import com.alsash.reciper.mvp.model.entity.Recipe;
import com.alsash.reciper.mvp.model.entity.RecipeFull;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * A Business Logic. Makes business processes clear for understanding and usage.
 */
public class BusinessLogic {

    private static final String TAG = "BusinessLogic";

    private final Subject<RecipeEvent> recipeEventSubject;
    private final Subject<CategoryEvent> categoryEventSubject;
    private final Subject<LabelEvent> labelEventSubject;
    private final Map<Class<?>, String> defaultNames;

    public BusinessLogic(Context context) {
        defaultNames = getDefaultNames(context);
        recipeEventSubject = BehaviorSubject.create();
        recipeEventSubject.subscribeOn(AndroidSchedulers.mainThread());
        categoryEventSubject = BehaviorSubject.create();
        categoryEventSubject.subscribeOn(AndroidSchedulers.mainThread());
        labelEventSubject = BehaviorSubject.create();
        labelEventSubject.subscribeOn(AndroidSchedulers.mainThread());
    }

    private Map<Class<?>, String> getDefaultNames(Context context) {
        Map<Class<?>, String> result = new HashMap<>();
        result.put(Author.class, context.getString(R.string.entity_author));
        result.put(Category.class, context.getString(R.string.entity_category));
        result.put(Food.class, context.getString(R.string.entity_food));
        result.put(Ingredient.class, context.getString(R.string.entity_ingredient));
        result.put(Label.class, context.getString(R.string.entity_label));
        result.put(Measure.class, context.getString(R.string.entity_measure));
        result.put(Photo.class, context.getString(R.string.entity_photo));
        result.put(Recipe.class, context.getString(R.string.entity_recipe));
        return result;
    }

    public String getEntityName(BaseEntity entity) {
        String result = null;
        if (entity instanceof Author) {
            result = ((Author) entity).getName();
            if (result == null || result.equals("")) result = defaultNames.get(Author.class);
        } else if (entity instanceof Category) {
            result = ((Category) entity).getName();
            if (result == null || result.equals("")) result = defaultNames.get(Category.class);
        } else if (entity instanceof Food) {
            result = ((Food) entity).getName();
            if (result == null || result.equals("")) result = defaultNames.get(Food.class);
        } else if (entity instanceof Ingredient) {
            result = ((Ingredient) entity).getName();
            if (result == null || result.equals("")) result = defaultNames.get(Ingredient.class);
        } else if (entity instanceof Label) {
            result = ((Label) entity).getName();
            if (result == null || result.equals("")) result = defaultNames.get(Label.class);
        } else if (entity instanceof Recipe) {
            result = ((Recipe) entity).getName();
            if (result == null || result.equals("")) result = defaultNames.get(Recipe.class);
        }
        return (result == null) ? "" : result;
    }

    public long getCookTime(RecipeFull recipe) {
        double gramPerSecond = recipe.getMassFlowRateGps();
        if (gramPerSecond == 0) return 0;
        double recipeWeightInGrams = getRecipeWeight(recipe, WeightUnit.GRAM);
        int seconds = (int) Math.round(recipeWeightInGrams / gramPerSecond);
        return seconds * 1000;
    }

    public double getMassFlowRate(RecipeFull recipe, long millis) {
        if (millis <= 0) return 0;
        double recipeWeightInGrams = getRecipeWeight(recipe, WeightUnit.GRAM);
        return (recipeWeightInGrams * 1000) / millis;
    }

    public Map<String, Double> getIngredientsWeight(RecipeFull recipe, int newWeight) {
        Map<String, Double> result = new HashMap<>();
        if (recipe == null) return result;
        double currentWeight = getRecipeWeight(recipe, WeightUnit.GRAM);
        if (currentWeight > 0) {
            double factor = (newWeight > 0) ? newWeight / currentWeight : 0;
            for (Ingredient ingredient : recipe.getIngredients()) {
                result.put(ingredient.getUuid(), ingredient.getWeight() * factor);
            }
        } else if (newWeight > 0) {
            int ingredientSize = recipe.getIngredients().size();
            double ingredientWeight = ingredientSize > 0 ? newWeight / ingredientSize : 0;
            for (Ingredient ingredient : recipe.getIngredients()) {
                result.put(ingredient.getUuid(), ingredientWeight);
            }
        }
        return result;
    }



    public Nutrient getNutrient(RecipeFull recipe, RecipeUnit recipeUnit) {
        double protein = 0.0D;
        double carbs = 0.0D;
        double fat = 0.0D;
        double energy = 0.0D;
        try {
            double recipeProteinInGrams = 0.0D;
            double recipeCarbsInGrams = 0.0D;
            double recipeFatInGrams = 0.0D;
            double recipeEnergyInKiloCalories = 0.0D;
            double recipeWeightInGrams = 0.0D;
            for (Ingredient ingredient : recipe.getIngredients()) {
                // Getting weight multiplier
                WeightUnit ingredientWeightUnit = WeightUnit.getValueOf(ingredient.getWeightUnit());
                if (ingredientWeightUnit == null)
                    throw new UnitException(ingredient, "weightUnit", ingredient.getWeightUnit());

                Food food = ingredient.getFood();
                WeightUnit foodWeightUnit = WeightUnit.getValueOf(food.getWeightUnit());
                if (foodWeightUnit == null)
                    throw new UnitException(food, "weightUnit", food.getWeightUnit());

                double ingredientFoodWeightMultiplier = getWeightMultiplier(WeightUnit.GRAM,
                        ingredientWeightUnit, foodWeightUnit);

                // Calculating nutrition values - food contains value per 1 unit
                recipeProteinInGrams += food.getProtein() * ingredient.getWeight() *
                        ingredientFoodWeightMultiplier;
                recipeCarbsInGrams += food.getCarbs() * ingredient.getWeight() *
                        ingredientFoodWeightMultiplier;
                recipeFatInGrams += food.getFat() * ingredient.getWeight() *
                        ingredientFoodWeightMultiplier;

                // Getting energy per weight multiplier
                EnergyUnit foodEnergyUnit = EnergyUnit.getValueOf(food.getEnergyUnit());
                if (foodEnergyUnit == null)
                    throw new UnitException(food, "energyUnit", food.getEnergyUnit());

                double energyWeightMultiplier = getEnergyMultiplier(EnergyUnit.CALORIE,
                        foodEnergyUnit) * getWeightMultiplier(WeightUnit.GRAM,
                        ingredientWeightUnit);

                // Calculating energy value
                recipeEnergyInKiloCalories += food.getEnergy() * ingredient.getWeight() *
                        energyWeightMultiplier;

                // Calculating recipe weight
                recipeWeightInGrams += ingredient.getWeight() *
                        getWeightMultiplier(WeightUnit.GRAM, ingredientWeightUnit);

            }

            // Calculating values per RecipeUnit
            double nutrientMultiplier = recipeUnit.getDefaultQuantity();
            switch (recipeUnit) {
                case GRAM:
                    // * 100 gram / all weight
                    nutrientMultiplier /= recipeWeightInGrams;
                    break;
                case SERVING:
                    //  [1 serving = ( all weight / all servings )] / all weight
                    // * 1 serving / all servings
                    nutrientMultiplier /= recipe.getServings();
            }
            protein = recipeProteinInGrams * nutrientMultiplier;
            carbs = recipeCarbsInGrams * nutrientMultiplier;
            fat = recipeFatInGrams * nutrientMultiplier;
            energy = recipeEnergyInKiloCalories * nutrientMultiplier;

        } catch (Throwable e) {
            Log.d(TAG, e.getMessage(), e);
        }

        return Nutrient.builder()
                .protein(protein)
                .carbs(carbs)
                .fat(fat)
                .unit(WeightUnit.GRAM)
                .energy(energy)
                .unit(EnergyUnit.CALORIE)
                .build();
    }

    public double getRecipeWeight(RecipeFull recipe, WeightUnit weightUnit) {
        double recipeWeight = 0.0D;
        try {
            for (Ingredient ingredient : recipe.getIngredients()) {
                // Getting ingredient weight unit
                WeightUnit ingredientWeightUnit = WeightUnit.getValueOf(ingredient.getWeightUnit());
                if (ingredientWeightUnit == null)
                    throw new UnitException(ingredient, "weightUnit", ingredient.getWeightUnit());
                // Calculating recipe weight
                recipeWeight += ingredient.getWeight() *
                        getWeightMultiplier(weightUnit, ingredientWeightUnit);

            }
        } catch (Throwable e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return recipeWeight;
    }

    private double getWeightMultiplier(WeightUnit requiredUnit, WeightUnit... units) {
        double multiplier = 1.0D;
        for (WeightUnit unit : units) {
            switch (requiredUnit) {
                case GRAM:
                    switch (unit) {
                        case GRAM:
                            break;
                        case KILOGRAM:
                            multiplier *= 1000.0D;
                            break;
                    }
                    break;
                case KILOGRAM:
                    switch (unit) {
                        case GRAM:
                            multiplier /= 1000.0D;
                            break;
                        case KILOGRAM:
                            break;
                    }
                    break;
            }
        }
        return multiplier;
    }

    private double getEnergyMultiplier(EnergyUnit requiredUnit, EnergyUnit... units) {
        double multiplier = 1.0D;
        for (EnergyUnit unit : units) {
            switch (requiredUnit) {
                case CALORIE:
                    switch (unit) {
                        case CALORIE:
                            break;
                        case KILOCALORIE:
                            multiplier *= 1000.0D;
                            break;
                    }
                    break;
                case KILOCALORIE:
                    switch (unit) {
                        case CALORIE:
                            multiplier /= 1000.0D;
                        case KILOCALORIE:
                            break;
                    }
                    break;
            }
        }
        return multiplier;
    }

    public Subject<RecipeEvent> getRecipeEventSubject() {
        return recipeEventSubject;
    }

    public Subject<CategoryEvent> getCategoryEventSubject() {
        return categoryEventSubject;
    }

    public Subject<LabelEvent> getLabelEventSubject() {
        return labelEventSubject;
    }
}
