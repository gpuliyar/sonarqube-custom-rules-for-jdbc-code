package com.gp.sonarqube.custom.rules.checks;

import static com.google.common.collect.ImmutableList.of;
import static org.sonar.plugins.java.api.tree.Tree.Kind.METHOD_INVOCATION;

import org.sonar.check.Rule;
import org.sonar.java.matcher.MethodMatcher;
import org.sonar.java.matcher.MethodMatcherCollection;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import java.util.List;

/**
 * @author GP
 * <p>
 * java check class to check if the source code invokes any sql Connection
 * close method
 */
@Rule(key = "ConnectionCloseCheckRule")
public class ConnectionCloseCheckRule extends IssuableSubscriptionVisitor {
    private final String CONNECTION_CLASS = java.sql.Connection.class.getCanonicalName();
    private final String CLOSE_METHOD = "close";

    /**
     * defining the method matcher collection with Connection class and
     * close method
     */
    private final MethodMatcherCollection CONNECTION_CLOSE = MethodMatcherCollection.create(new MethodMatcher[]{
            MethodMatcher.create().typeDefinition(CONNECTION_CLASS).name(CLOSE_METHOD).withoutParameter()
    });

    /**
     * invoke the check class whenever the parser encounters any statement
     * in the source code that invokes a method
     *
     * @return source code nodes that invoke a method
     */
    @Override
    public List<Kind> nodesToVisit() {
        return of(METHOD_INVOCATION);
    }

    /**
     * the method will be triggered whenever the code has met the criteria
     * of what the nodesToVisit defines. in this case, the visitNode gets
     * invoked for all method invocation. the check done by the below method
     * is to see if the method invocation is the sql Connection close. if
     * so, then raise an issue.
     *
     * @param tree the ast tree of the source code whenever the method
     *             invocation happens
     */
    @Override
    public void visitNode(Tree tree) {
        MethodInvocationTree methodInvocationTree = MethodInvocationTree.class.cast(tree);
        if (CONNECTION_CLOSE.anyMatch(methodInvocationTree))
            reportIssue(tree, "Do not invoke the java.sql.Connection#close() method.");
    }
}
