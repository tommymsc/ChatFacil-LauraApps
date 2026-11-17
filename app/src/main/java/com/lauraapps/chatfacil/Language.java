package com.lauraapps.chatfacil;

import android.content.Context;
import android.content.res.Configuration;
import java.util.Locale;

/** User-selected language, independent of the phone language. */
public final class Language {
    private Language() {}
    public static Context wrap(Context base) {
        String selection = base.getSharedPreferences("chat_facil_settings", Context.MODE_PRIVATE)
                .getString("language", "system");
        if ("system".equals(selection)) return base;
        Locale language = "en".equals(selection) ? Locale.ENGLISH : new Locale("es");
        Configuration c = new Configuration(base.getResources().getConfiguration());
        c.setLocale(language);
        c.setLayoutDirection(language);
        return base.createConfigurationContext(c);
    }
}
