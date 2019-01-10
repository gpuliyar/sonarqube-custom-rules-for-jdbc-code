package com.gp.sonarqube.custom.rules;

import org.sonar.api.server.debt.DebtRemediationFunction;
import org.sonar.api.server.rule.RulesDefinition.DebtRemediationFunctions;

/**
 * @author GP
 * <p>
 * class to hold the metadata information about the rule. the json file present
 * at /org/sonar/l10n/java/rules/squid *.json parsed into this class. the
 * data helps configuring the rule metadata and externalizing the values from
 * the rule itself. hence making the tweak to the representation of the metadata
 * is much simpler.
 */
public class Remediation {
    public String func;
    public String constantCost;
    public String linearDesc;
    public String linearOffset;
    public String linearFactor;

    /**
     * method to return the DebtRemediationFunction based on the configured
     * function value in the /org/sonar/l10n/java/rules/squid/*.json file. the
     * method determines whether the configured func value is a constant cost
     * or linear cost and based on that, it returns the appropriate
     * DebtRemediationFunction
     *
     * @param drf the debt remediation functions
     * @return the final debt remediation function which the sonarqube displays
     * in its portal
     */
    public DebtRemediationFunction remediationFunction(DebtRemediationFunctions drf) {
        if (func.startsWith("Constant")) {
            return drf.constantPerIssue(constantCost.replace("mn", "min"));
        }
        if ("Linear".equals(func)) {
            return drf.linear(linearFactor.replace("mn", "min"));
        }
        return drf.linearWithOffset(linearFactor.replace("mn", "min"), linearOffset.replace("mn", "min"));
    }

}
