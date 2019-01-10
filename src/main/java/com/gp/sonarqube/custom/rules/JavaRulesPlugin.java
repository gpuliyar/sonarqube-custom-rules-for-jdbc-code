package com.gp.sonarqube.custom.rules;

import org.sonar.api.Plugin;

/**
 * Starting point of defining any custom plugin for SonarQube. The same class
 * name is defined in the pom.xml in the section in the build - plugin for the
 * group id : org.sonarsource.sonar-packaging-maven-plugin.
 * The sonarqube registers the rules that are defined in this project through
 * the plugin
 */
public class JavaRulesPlugin implements Plugin {

    /**
     * adds the custom rules definition class and the custom check registrar
     * class. to know more details, go to the specific classes.
     *
     * @param context Sonarqube API Plugin Context
     */
    @Override
    public void define(Context context) {
        context.addExtension(JavaRulesDefinition.class);
        context.addExtension(JavaCheckRegistrar.class);
    }
}
