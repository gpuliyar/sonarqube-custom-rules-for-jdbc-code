package com.gp.sonarqube.custom.rules;

import org.apache.commons.lang.StringUtils;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.Tree;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author GP
 * <p>
 * Class responsible to print the AST of the Java class as tree hierarchy
 */
public class PrinterTreeVisitor extends BaseTreeVisitor {
    private static final int INDENT_SPACES = 4;
    private final StringBuilder stringBuilder;
    private int indentLevel;

    private PrinterTreeVisitor() {
        stringBuilder = new StringBuilder();
        indentLevel = 0;
    }

    /**
     * the static method to invoke to print a AST tree as a string in a tree
     * format. the logic internally relies on the BaseTreeVisitor class
     * functionality to recursively loop through the AST nodes.
     *
     * @param tree the tree to print as a string (in tree format)
     * @return the tree string
     */
    public static String print(Tree tree) {
        PrinterTreeVisitor printerTreeVisitor = new PrinterTreeVisitor();
        printerTreeVisitor.scan(tree);
        return printerTreeVisitor.stringBuilder.toString();
    }

    /**
     * method that creates the necessary indents to the string based on the
     * level of the hierarchy.
     *
     * @return the indented string
     */
    private StringBuilder indent() {
        return stringBuilder.append(StringUtils.leftPad("", INDENT_SPACES * indentLevel));
    }

    /**
     * method overrides the BaseTreeVisitor scan method to scan through the
     * AST tree list and loop through them to create a tree representation in
     * a string format
     *
     * @param trees the list of AST trees
     */
    @Override
    protected void scan(List<? extends Tree> trees) {
        if (!trees.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append(" : [\n");
            super.scan(trees);
            indent().append("]\n");
        }
    }

    /**
     * method overrides the BaseTreeVisitor scan method to scan through the
     * AST tree and create a tree representation in a string format
     *
     * @param tree the AST tree
     */
    @Override
    protected void scan(@Nullable Tree tree) {
        if (tree != null && tree.getClass().getInterfaces() != null && tree.getClass().getInterfaces().length > 0) {
            String nodeName = tree.getClass().getInterfaces()[0].getSimpleName();
            indent().append(nodeName).append("\n");
        }
        indentLevel++;
        super.scan(tree);
        indentLevel--;
    }
}
