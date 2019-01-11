# sonarqube-custom-rules-for-jdbc-code
Project implements custom Sonarqube Code Quality Rules to check JDBC related code

## Pre-requisite
* You should know SonarQube;
* You should know how to code in Java or any other JVM language (the article uses Java, you can change it to other languages like Groovy or Kotlin, etc.);
* You should know SonarQube Code Quality Profile and how to set it up and how to add more Code Quality checks to your profile;

## What is this Project about?
As you already know, SonarQube is a Static Code Quality Analysis Tool. The Tool checks the static code and reports those code doesn't comply to the standard. The SonarQube offers a bunch of standard checks for Java code. If you have use-cases where you would like to add more rules other than what offered default by the tool, then this project can give you a sample of how to do it. In the project, I'm adding more rules to check if any Java JDBC code uses the close() statement. Then, the Tool will report such usage and recommend the developer to use "try (with resources)" approach, as it internally implements calling the "close()".

## Below code is a sample to check if the close() method of ResultSet invoked
```java
import org.sonar.check.Rule;
import org.sonar.java.matcher.MethodMatcher;
import org.sonar.java.matcher.MethodMatcherCollection;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import java.util.List;
 
import static com.google.common.collect.ImmutableList.of;
import static org.sonar.plugins.java.api.tree.Tree.Kind.METHOD_INVOCATION;
 
@Rule(key = "ResultSetCloseCheckRule")  ........................................................................................ (1)
public class ResultSetCloseCheckRule extends IssuableSubscriptionVisitor { ..................................................... (2)
    private final String RESULT_SET_CLASS = java.sql.ResultSet.class.getCanonicalName();
    private final String CLOSE_METHOD = "close";
 
    private final MethodMatcherCollection RESULT_SET_CLOSE = MethodMatcherCollection.create(new MethodMatcher[]{
            MethodMatcher.create().typeDefinition(RESULT_SET_CLASS).name(CLOSE_METHOD).withoutParameter()
    });
 
    @Override
    public List<Kind> nodesToVisit() {  ........................................................................................ (3)
        return of(METHOD_INVOCATION);
    }
 
    @Override
    public void visitNode(Tree tree) { ......................................................................................... (4)
        MethodInvocationTree methodInvocationTree = MethodInvocationTree.class.cast(tree);
        if (RESULT_SET_CLOSE.anyMatch(methodInvocationTree))
            reportIssue(tree, "Do not invoke the java.sql.ResultSet#close() method."); ......................................... (5)
    }
}
```

## What this code means?
* (1) → Give a unique name to your rule. The name is for internal purpose. Ensure that you don't alter the name of the Rule in future.
* (2) → Extend the relevant `Visitor` Class. As per my use-case I had to extend the Class `IssuableSubscriptionVisitor`. 

> Note: You should know and understand what Visitor Class to extend based on your use-case. The document will not elaborate or explain what Visitor Class to choose and why to select it.

> Note: Take a look at the Visitor Classes inside the SonarQube Plugin Package `org.sonar.plugins.java.api`;

* (3) → State which code that you are interested. As per my use-case, I was interested in the `close()` method. As you can see, the `METHOD_INVOCATION` will enable me to do that.

> Note: Look the Enumeration `org.sonar.plugins.java.api.tree.Tree.Kind` to know more what the SonarQube Plugin supports.

* (4) → Method `visitNode` invoked for every line of code that has a Method Invocation.
* (5) → As you can see, I'm interested only in the Method Invocation of `ResultSet#close()`. In this section, I check whether the Method Invocation is for `ResultSet#close`, if yes, then raise an issue.

## What's in the JSON and HTML file?
> Note:
> 1. Create a new JSON file in the package path "org.sonar.l10n.java.rules.squid" inside resources
> 2. Make sure that the JSON file name is same as the Rule name suffixed with "_java.json"
> 3. Create a new HTML file in the package path "org.sonar.l10n.java.rules.squid" inside resources
> 4. Make sure that the HTML file name is same as the Rule name suffixed with "_java.html"

## JSON
```json
{
  "title": "", ................................ (1)
  "type": "", ................................. (2)
  "status": "", ............................... (3)
  "remediation": { ............................ (4)
    "func": "", ............................... (5)
    "constantCost": "", ....................... (6)
    "linearDesc": "", ......................... (7)
    "linearOffset": "", ....................... (8)
    "linearFactor": "" ........................ (9)
  },
  "tags": [ "", "" ], ......................... (10)
  "defaultSeverity": "" ....................... (11)
}
```

## What the JSON structure mean?

* (1) → The Title of the Rule. It is a free text.
* (2) → The Type of the Rule. Valid Values are → `CODE_SMELL`, `BUG`, `VULNERABILITY`
* (3) → The Status of the Rule. Valid Values are → `beta`, `deprecated`, `ready`, `removed`
* (4) → The Debt Remediation Function information related to the Rule. Check the interface `org.sonar.api.server.debt.DebtRemediationFunction` for more information
* (5) → The Debt Remediation Function of the Rule. Valid Values are → `Constant`, or `Linear`
* (6) → If the Debt Remediation Function is `Constant`, then specify the cost of it. Example: `5min` to denote `5 minutes`
* (7) → The Gap Description of the Rule. It is a free text.
* (8) → If the Debt Remediation Function is `Linear`, then specify the `Linear Offset` value. (deprecated).
* (9) → If the Debt Remediation Function is `Linear`, then specify the `Linear Factor` value
* (10) → List the tags to associate with the Rule
* (11) → The Severity of the Rule. Valid Values are → `Info`, `Minor`, `Major`, `Critical`, `Blocker`

## HTML File
```html
<p>Explain why you do this check and what is the benefit of the check</p>
<h2>Non-Compliant Approach</h2> <!-- for uniformity reasons, don't change this text -->
<pre>
    Place the code that is non compliant
</pre>
<h2>Compliant Solution</h2> <!-- for uniformity reasons, don't change this text -->
<pre>
    Place the code that can help the developer to understand what is the right way to code
</pre>
```

## Whats RulesList class for?
Add the list of Rules to the RulesList class
```java
package com.gp.sonarqube.custom.rules;
...
public final class RulesList {
  ...
 
  public static List<Class<? extends JavaCheck>> getJavaChecks() {
    return ImmutableList.<Class<? extends JavaCheck>>builder()
        .add(ResultSetCloseCheckRule.class)
        .add(StatementCloseCheckRule.class)
        .add(ConnectionCloseCheckRule.class)
        .build();
  }
 
  ...
}
```

## Plugin Build
```
mvn clean install
```

## Where to Deploy?
Once you built the Binary, Unit Testing the functionality, the next step would be to deploy the binary in a local SonarQube Setup to see if the newly created Rule Plugin is doing its job.

## Copy the library to SonarQube Setup
```
/<path where sonarqube installed>/sonarqube-7.2/extensions/plugins
```

## Restart the SonarQube
```
[user@localhost linux-x86-64]$ ./sonar.sh status
SonarQube is running (4043).
```

## Stop SonarQube
```
[user@localhost linux-x86-64]$ ./sonar.sh stop
Stopping SonarQube...
Waiting for SonarQube to exit...
Stopped SonarQube.
```

## Start SonarQube
```
[user@localhost linux-x86-64]$ ./sonar.sh start
Starting SonarQube...
Started SonarQube.
```
