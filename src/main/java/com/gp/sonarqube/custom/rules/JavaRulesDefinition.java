package com.gp.sonarqube.custom.rules;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.check.Cardinality;
import org.sonar.squidbridge.annotations.RuleTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JavaRulesDefinition implements RulesDefinition {
    private static final String RESOURCE_BASE_PATH = "/org/sonar/l10n/java/rules/squid";
    public static final String REPOSITORY_KEY = "custom-java-jdbc-rules";
    private final Gson gson = new Gson();

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY_KEY, "java").setName("Custom Java Repository");
        List<Class> checks = RulesList.getChecks();
        new RulesDefinitionAnnotationLoader().load(repository, Iterables.toArray(checks, Class.class));
        for (Class ruleClass : checks) {
            newRule(ruleClass, repository);
        }
        repository.done();
    }

    @VisibleForTesting
    protected void newRule(Class<?> ruleClass, NewRepository repository) {
        org.sonar.check.Rule ruleAnnotation = AnnotationUtils.getAnnotation(ruleClass, org.sonar.check.Rule.class);
        if (ruleAnnotation == null) {
            throw new IllegalArgumentException("No Rule annotation was found on " + ruleClass);
        }
        String ruleKey = ruleAnnotation.key();
        if (StringUtils.isEmpty(ruleKey)) {
            throw new IllegalArgumentException("No key defined in the Rule annotation of " + ruleClass);
        }
        NewRule rule = repository.rule(ruleKey);
        if (rule == null) {
            throw new IllegalStateException("No rule created for " + ruleClass + " in " + repository.key());
        }
        ruleMetadata(ruleClass, rule);
        rule.setTemplate(AnnotationUtils.getAnnotation(ruleClass, RuleTemplate.class) != null);
        if (ruleAnnotation.cardinality() == Cardinality.MULTIPLE) {
            throw new IllegalArgumentException("Cardinality not supported, use the RuleTemplate annotation instead for " + ruleClass);
        }
    }

    private String ruleMetadata(Class<?> ruleClass, NewRule rule) {
        String metadataKey = rule.key();
        org.sonar.java.RspecKey rspecKeyAnnotation = AnnotationUtils.getAnnotation(ruleClass, org.sonar.java.RspecKey.class);
        if (rspecKeyAnnotation != null) {
            metadataKey = rspecKeyAnnotation.value();
            rule.setInternalKey(metadataKey);
        }
        addHtmlDescription(rule, metadataKey);
        addMetadata(rule, metadataKey);
        return metadataKey;
    }

    private void addMetadata(NewRule rule, String metadataKey) {
        URL resource = JavaRulesDefinition.class.getResource(RESOURCE_BASE_PATH + "/" + metadataKey + "_java.json");
        if (resource != null) {
            RuleMetadata metadata = gson.fromJson(readResource(resource), RuleMetadata.class);
            rule.setSeverity(metadata.defaultSeverity.toUpperCase(Locale.US));
            rule.setName(metadata.title);
            rule.addTags(metadata.tags);
            rule.setType(RuleType.valueOf(metadata.type));
            rule.setStatus(RuleStatus.valueOf(metadata.status.toUpperCase(Locale.US)));
            if (metadata.remediation != null) {
                rule.setDebtRemediationFunction(metadata.remediation.remediationFunction(rule.debtRemediationFunctions()));
                rule.setGapDescription(metadata.remediation.linearDesc);
            }
        }
    }

    private static void addHtmlDescription(NewRule rule, String metadataKey) {
        URL resource = JavaRulesDefinition.class.getResource(RESOURCE_BASE_PATH + "/" + metadataKey + "_java.html");
        if (resource != null) {
            rule.setHtmlDescription(readResource(resource));
        }
    }

    private static String readResource(URL resource) {
        try {
            return Resources.toString(resource, UTF_8);
        } catch (IOException ioException) {
            throw new IllegalStateException("Failed to read: " + resource, ioException);
        }
    }
}
