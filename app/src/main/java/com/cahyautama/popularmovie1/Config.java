package com.cahyautama.popularmovie1;

import android.content.Context;

import com.github.fernandodev.androidproperties.lib.AssetsProperties;
import com.github.fernandodev.androidproperties.lib.Property;

/**
 * Created by hanief on 10/07/2017.
 */

public class Config extends AssetsProperties {
    @Property("api_key") String apiKey;

    public Config(Context context) { super(context); }
}
