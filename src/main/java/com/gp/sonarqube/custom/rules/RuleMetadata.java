package com.gp.sonarqube.custom.rules;

import javax.annotation.Nullable;

/**
 * @author GP
 * <p>
 * class to hold the metadata information about the rule. the json file present
 * at /org/sonar/l10n/java/rules/squid *.json parsed into this class. the
 * data helps configuring the rule metadata and externalizing the values from
 * the rule itself. hence making the tweak to the representation of the metadata
 * is much simpler.
 */
public class RuleMetadata {
    public String title;
    public String status;
    @Nullable
    public Remediation remediation;

    public String type;
    public String[] tags;
    public String defaultSeverity;
}
