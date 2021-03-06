package com.alsash.reciper.mvp.presenter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.alsash.reciper.R;
import com.alsash.reciper.app.lib.MutableBoolean;
import com.alsash.reciper.app.lib.MutableString;
import com.alsash.reciper.logic.BusinessLogic;
import com.alsash.reciper.logic.StorageLogic;
import com.alsash.reciper.logic.action.RecipeAction;
import com.alsash.reciper.logic.event.RecipeEvent;
import com.alsash.reciper.logic.restriction.EntityRestriction;
import com.alsash.reciper.mvp.model.entity.BaseEntity;
import com.alsash.reciper.mvp.model.entity.RecipeFull;
import com.alsash.reciper.mvp.model.tab.SwipeTab;
import com.alsash.reciper.mvp.view.RecipeDetailsView;
import com.alsash.reciper.ui.fragment.RecipeDetailsDescriptionFragment;
import com.alsash.reciper.ui.fragment.RecipeDetailsIngredientsFragment;
import com.alsash.reciper.ui.fragment.RecipeDetailsMethodsFragment;
import com.alsash.reciper.ui.fragment.RecipeDetailsNutritionFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * A Presenter that represents details of a single recipe
 */
public class RecipeDetailsPresenter extends BaseRestrictPresenter<RecipeDetailsView> {

    private final StorageLogic storageLogic;
    private final BusinessLogic businessLogic;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<Object> listeners = new ArrayList<>();

    private RecipeFull recipeFull;
    private SwipeTab[] swipeTabs;
    private int shownPosition;

    public RecipeDetailsPresenter(StorageLogic storageLogic, BusinessLogic businessLogic) {
        super(storageLogic);
        this.storageLogic = storageLogic;
        this.businessLogic = businessLogic;
    }

    @Override
    public RecipeDetailsPresenter setRestriction(EntityRestriction restriction) {
        return (RecipeDetailsPresenter) super.setRestriction(restriction);
    }

    public RecipeDetailsPresenter setFragments(Fragment[] fragments) {
        swipeTabs = new SwipeTab[fragments.length];
        for (int i = 0; i < fragments.length; i++) {

            if (fragments[i] instanceof RecipeDetailsDescriptionFragment) {
                swipeTabs[i] = SwipeTab.builder()
                        .fragment(fragments[i])
                        .title(R.string.fragment_recipe_details_description_title)
                        .swiped(true)
                        .build();
            } else if (fragments[i] instanceof RecipeDetailsNutritionFragment) {
                swipeTabs[i] = SwipeTab.builder()
                        .fragment(fragments[i])
                        .title(R.string.fragment_recipe_details_nutrition_title)
                        .swiped(true)
                        .build();
            } else if (fragments[i] instanceof RecipeDetailsIngredientsFragment) {
                swipeTabs[i] = SwipeTab.builder()
                        .fragment(fragments[i])
                        .title(R.string.fragment_recipe_details_ingredients_title)
                        .swiped(true)
                        .build();
            } else if (fragments[i] instanceof RecipeDetailsMethodsFragment) {
                swipeTabs[i] = SwipeTab.builder()
                        .fragment(fragments[i])
                        .title(R.string.fragment_recipe_details_methods_title)
                        .swiped(true)
                        .build();
            } else {
                swipeTabs[i] = SwipeTab.builder()
                        .fragment(fragments[i])
                        .swiped(true)
                        .build();
            }
        }
        return this;
    }

    @Override
    public void attach(final RecipeDetailsView view) {
        view.setDetails(swipeTabs);
        super.attach(view);
    }

    @Override
    public void visible(RecipeDetailsView view) {
        view.showDetail(shownPosition);
        if (recipeFull == null) return;
        view.showTitle(recipeFull.getName());
        view.showPhoto(recipeFull.getMainPhoto());
    }

    @Override
    public void invisible(RecipeDetailsView view) {
        shownPosition = view.shownDetail();
    }

    @Override
    public void detach() {
        super.detach();
        listeners.clear();
    }

    public void editRecipeName(RecipeDetailsView view) {
        if (recipeFull == null) return;
        final WeakReference<RecipeDetailsView> viewRef = new WeakReference<>(view);
        final MutableString listener = new MutableString() {
            @Override
            public synchronized MutableString set(final String value) {

                storageLogic.updateSync(recipeFull, RecipeAction.EDIT_NAME, value);

                if (viewRef.get() != null) viewRef.get().showTitle(value);

                businessLogic.getRecipeEventSubject().onNext(new RecipeEvent(RecipeAction.EDIT_NAME,
                        recipeFull.getUuid()));

                getComposite().add(Completable
                        .fromAction(new Action() {
                            @Override
                            public void run() throws Exception {
                                storageLogic.updateAsync(recipeFull, RecipeAction.EDIT_NAME);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe());
                return super.set(value);
            }
        };
        listeners.add(listener);
        view.showNameEditDialog(recipeFull, listener);
    }

    public void editRecipePhoto(RecipeDetailsView view) {
        if (recipeFull == null) return;
        final WeakReference<RecipeDetailsView> viewRef = new WeakReference<>(view);
        final MutableString listener = new MutableString() {
            @Override
            public synchronized MutableString set(final String value) {

                storageLogic.updateSync(recipeFull, RecipeAction.EDIT_PHOTO, value);

                businessLogic.getRecipeEventSubject().onNext(
                        new RecipeEvent(RecipeAction.EDIT_PHOTO, recipeFull.getUuid()));

                getComposite().add(Completable
                        .fromAction(new Action() {
                            @Override
                            public void run() throws Exception {
                                storageLogic.updateAsync(recipeFull, RecipeAction.EDIT_PHOTO);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe());

                if (viewRef.get() != null) viewRef.get().showPhoto(recipeFull.getMainPhoto());
                return super.set(value);
            }
        };
        listeners.add(listener);
        view.showPhotoEditDialog(recipeFull.getMainPhoto(), listener);
    }

    public void deleteRecipe(RecipeDetailsView view) {
        if (recipeFull == null) return;
        final WeakReference<RecipeDetailsView> viewRef = new WeakReference<>(view);
        final MutableBoolean listener = new MutableBoolean() {
            @Override
            public synchronized MutableBoolean set(final boolean delete) {
                if (!delete) return this;

                businessLogic.getRecipeEventSubject().onNext(new RecipeEvent(RecipeAction.DELETE,
                        recipeFull.getUuid()));

                getComposite().add(Completable
                        .fromAction(new Action() {
                            @Override
                            public void run() throws Exception {
                                storageLogic.deleteAsync(recipeFull);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe());

                if (viewRef.get() != null) viewRef.get().finishView();
                return this;
            }
        };
        listeners.add(listener);
        view.showConfirmDeleteDialog(businessLogic.getEntityName(recipeFull), listener);
    }

    public void finish(RecipeDetailsView view) {
        if (recipeFull != null)
            businessLogic.getRecipeEventSubject().onNext(
                    new RecipeEvent(RecipeAction.EDIT, recipeFull.getUuid()));
        view.finishView();
    }

    @Nullable
    @Override
    protected BaseEntity getEntity() {
        return recipeFull;
    }

    @Override
    protected void setEntity(@Nullable BaseEntity entity) {
        recipeFull = (RecipeFull) entity;
    }


}
