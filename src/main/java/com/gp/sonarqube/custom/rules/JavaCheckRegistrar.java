package com.metricstream.sonarqube.custom.rules;

import org.sonar.plugins.java.api.CheckRegistrar;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonarsource.api.sonarlint.SonarLintSide;

import java.util.List;

import static com.metricstream.sonarqube.custom.rules.JavaRulesDefinition.REPOSITORY_KEY;

/**
 * @author GP
 * <p>
 * The plugin check registrar class. responsible to register all the checks
 * done by this plugin.
 */
@SonarLintSide
public class JavaCheckRegistrar implements CheckRegistrar {

    /**
     * register the check classes from the repository
     *
     * @param registrarContext the registrar context
     */
    @Override
    public void register(RegistrarContext registrarContext) {
        registrarContext.registerClassesForRepository(REPOSITORY_KEY, checkClasses(), testCheckClasses());
    }

    /**
     * call the rules list java checks
     *
     * @return the complete list of java checks class
     */
    public static List<Class<? extends JavaCheck>> checkClasses() {
        return RulesList.getJavaChecks();
    }

    /**
     * class the rules list java checks for test
     *
     * @return the complete list of java test checks class
     */
    public static List<Class<? extends JavaCheck>> testCheckClasses() {
        return RulesList.getJavaTestChecks();
    }
}
