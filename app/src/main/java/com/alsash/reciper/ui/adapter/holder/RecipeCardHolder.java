package com.alsash.reciper.ui.adapter.holder;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alsash.reciper.R;
import com.alsash.reciper.mvp.model.entity.Recipe;
import com.alsash.reciper.ui.loader.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * An view that holds the Recipe card,
 * response to flip animations
 * and send interaction events ro receiver
 */
public class RecipeCardHolder extends RecyclerView.ViewHolder {

    private static final TimeInterpolator FAVORITE_INTERPOLATOR = new OvershootInterpolator(5);
    private static final int FAVORITE_DURATION_MS = 400;

    private final CardView frontCard;
    private final ImageView frontImage;
    private final ProgressBar frontBar;
    private final TextView frontName;
    private final ImageButton frontFavButton;
    private final ImageButton frontFlipButton;

    private final CardView backCard;
    private final ImageView backAuthorImage;
    private final TextView backAuthorName;
    private final TextView backSource;
    private final TextView backDate;
    private final TextView backDescription;
    private final ImageButton backFavButton;
    private final ImageButton backFlipButton;

    private final SimpleDateFormat dateFormat;

    public RecipeCardHolder(ViewGroup parent, @LayoutRes int recipeCardLayoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(recipeCardLayoutId, parent, false));

        frontCard = (CardView) itemView.findViewById(R.id.item_recipe_card_front);
        frontImage = (ImageView) itemView.findViewById(R.id.item_recipe_front_image);
        frontBar = (ProgressBar) itemView.findViewById(R.id.item_recipe_front_image_bar);
        frontName = (TextView) itemView.findViewById(R.id.item_recipe_front_name);
        frontFavButton = (ImageButton) itemView.findViewById(R.id.item_recipe_front_bt_fav);
        frontFlipButton = (ImageButton) itemView.findViewById(R.id.item_recipe_front_bt_flip);

        backCard = (CardView) itemView.findViewById(R.id.item_recipe_card_back);
        backAuthorImage = (ImageView) itemView.findViewById(R.id.item_recipe_back_author_image);
        backAuthorName = (TextView) itemView.findViewById(R.id.item_recipe_back_author_name);
        backSource = (TextView) itemView.findViewById(R.id.item_recipe_back_source);
        backDate = (TextView) itemView.findViewById(R.id.item_recipe_back_date);
        backDescription = (TextView) itemView.findViewById(R.id.item_recipe_back_description);
        backFavButton = (ImageButton) itemView.findViewById(R.id.item_recipe_back_bt_fav);
        backFlipButton = (ImageButton) itemView.findViewById(R.id.item_recipe_back_bt_flip);

        dateFormat = new SimpleDateFormat(
                parent.getResources().getString(R.string.recipe_card_date_format),
                Locale.getDefault()
        );

        setBackVisible(false);
    }

    public void bindRecipe(Recipe recipe) {
        frontName.setText(recipe.getName());
        ImageLoader.get().source(recipe.getMainPhoto()).bar(frontBar).load(frontImage);
        ImageLoader.get().source(recipe.getAuthor()).load(backAuthorImage);
        backAuthorName.setText(recipe.getAuthor().getName());
        backSource.setText(recipe.getSource());
        backDate.setText(dateFormat.format(recipe.getCreatedAt()));
        backDescription.setText(recipe.getDescription());
        setFavorite(recipe.isFavorite(), false);
    }

    public void setFavorite(boolean favorite, boolean animate) {
        frontFavButton.setImageResource(favorite ?
                R.drawable.item_recipe_bt_fav_on :
                R.drawable.item_recipe_bt_fav_off);
        backFavButton.setImageResource(favorite ?
                R.drawable.item_recipe_bt_fav_on :
                R.drawable.item_recipe_bt_fav_off);
        if (animate) {
            animateFavorite(frontCard.getVisibility() == View.VISIBLE ?
                    frontFavButton : backFavButton);
        }
    }

    private void animateFavorite(ImageButton favButton) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(favButton, "scaleX", 0.4f, 1f);
        bounceAnimX.setDuration(FAVORITE_DURATION_MS);
        bounceAnimX.setInterpolator(FAVORITE_INTERPOLATOR);
        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(favButton, "scaleY", 0.4f, 1f);
        bounceAnimY.setDuration(FAVORITE_DURATION_MS);
        bounceAnimY.setInterpolator(FAVORITE_INTERPOLATOR);
        animatorSet.play(bounceAnimX).with(bounceAnimY);
        animatorSet.start();
    }

    /**
     * Set the listeners in the following sequence:
     *
     * @param listeners 0. flipListener
     *                  1. FavoriteListener
     *                  2. openListener
     */
    public void setListeners(View.OnClickListener... listeners) {
        for (int i = 0; i < listeners.length; i++) {
            switch (i) {
                case 0:
                    backFlipButton.setOnClickListener(listeners[i]);
                    frontFlipButton.setOnClickListener(listeners[i]);
                case 1:
                    backFavButton.setOnClickListener(listeners[i]);
                    frontFavButton.setOnClickListener(listeners[i]);
                    break;
                case 2:
                    backCard.setOnClickListener(listeners[i]);
                    frontCard.setOnClickListener(listeners[i]);
                    break;
                default:
                    break;
            }
        }
    }

    public void setBackVisible(boolean backVisible) {
        if (backVisible) {
            frontCard.setVisibility(View.GONE);
            backCard.setVisibility(View.VISIBLE);
        } else {
            frontCard.setVisibility(View.VISIBLE);
            backCard.setVisibility(View.GONE);
        }
        // Animations not always ends correctly. Here is the "medicine"
        frontCard.setAlpha(1.0f);
        frontCard.setRotationY(0.0f);
        backCard.setAlpha(1.0f);
        backCard.setRotationY(0.0f);
        frontFavButton.clearAnimation();
        backFavButton.clearAnimation();
    }
}
