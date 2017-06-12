package com.alsash.reciper.di.module;

import android.content.Context;

import com.alsash.reciper.app.ReciperApp;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

/**
 * Glide integration with injection by Dagger 2
 */
public class GlideDiModule implements GlideModule {

    @Inject
    OkHttpClient okHttpClient;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        ((ReciperApp) context.getApplicationContext())
                .getAppComponent()
                .inject(this);

        glide.register(
                GlideUrl.class,
                InputStream.class,
                new OkHttpUrlLoader.Factory(okHttpClient)
        );
    }
}
