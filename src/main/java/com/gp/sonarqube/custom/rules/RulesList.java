package com.gp.sonarqube.custom.rules;

import com.google.common.collect.ImmutableList;
import com.gp.sonarqube.custom.rules.checks.ConnectionCloseCheckRule;
import com.gp.sonarqube.custom.rules.checks.ResultSetCloseCheckRule;
import com.gp.sonarqube.custom.rules.checks.StatementCloseCheckRule;
import org.sonar.plugins.java.api.JavaCheck;

import java.util.List;

/**
 * @author GP
 * <p>
 * Class that has the complete list of rules that are part of this plugin.
 */
public final class RulesList {
    /**
     * private constructor to stop instantiating the class
     */
    private RulesList() {
    }

    /**
     * static method to get the complete list of java checks and test java checks
     *
     * @return list of classes that does java rule checks
     */
    public static List<Class> getChecks() {
        return ImmutableList.<Class>builder().addAll(getJavaChecks()).addAll(getJavaTestChecks()).build();
    }

    /**
     * static method to return the complete list of java checks supported by
     * the plugin. if you add new java check class then add that class to the
     * list here so that the plugin recognizes the class.
     *
     * @return list of classes that does java checks
     */
    public static List<Class<? extends JavaCheck>> getJavaChecks() {
        return ImmutableList.<Class<? extends JavaCheck>>builder()
                .add(ResultSetCloseCheckRule.class)
                .add(StatementCloseCheckRule.class)
                .add(ConnectionCloseCheckRule.class)
                .build();
    }

    /**
     * static method to return the complete list of test java checks supported
     * by the plugin. if you add a new java test check class then add that class
     * to the list here so that the plugin recognizes the class
     *
     * @return list of classes that does java test checks
     */
    public static List<Class<? extends JavaCheck>> getJavaTestChecks() {
        return ImmutableList.<Class<? extends JavaCheck>>builder()
                .build();
    }
}
