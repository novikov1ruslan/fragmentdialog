package com.ivygames.morskoiboi;

import org.apache.commons.lang3.Validate;

public class RulesFactory {

    private static Rules sRules;

    private RulesFactory() {
    }

    public static void setRules(Rules rules) {
        sRules = Validate.notNull(rules);
    }

    public static Rules getRules() {
        return sRules;
    }

}
