package com.games.theory.ui;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import javafx.application.Application;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ThemeManager {
    private final Theme LIGHT_THEME = new PrimerLight();
    private final Theme DARK_THEME = new PrimerDark();

    public void applyLightTheme() {
        apply(false);
    }

    public void apply(boolean darkMode) {
        Theme theme = darkMode ? DARK_THEME : LIGHT_THEME;
        Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
    }
}
