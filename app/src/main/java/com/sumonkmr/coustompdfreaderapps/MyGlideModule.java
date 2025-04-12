package com.sumonkmr.coustompdfreaderapps;

import android.content.Context;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.LruResourceCache;

@GlideModule
public class MyGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Customize Glide's default settings if needed
        builder.setMemoryCache(new LruResourceCache(20 * 1024 * 1024)); // 20MB cache
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}