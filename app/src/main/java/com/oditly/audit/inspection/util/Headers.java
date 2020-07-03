package com.oditly.audit.inspection.util;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

/**
 * Created by Amit on 18-01-2019.
 */

public class Headers {

    public static GlideUrl getUrlWithHeaders(String url, String token){
        return new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("access-token", token)
                .build());
    }
}
