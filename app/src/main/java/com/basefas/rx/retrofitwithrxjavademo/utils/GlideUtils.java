package com.basefas.rx.retrofitwithrxjavademo.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Glide 工具类
 */

public class GlideUtils {

    public static void loadNetImage(final Context context, final String imageUrl, final ImageView imageView) {
        if (imageUrl == null || imageUrl.length() == 0) {
            return;
        }
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
    }
}
