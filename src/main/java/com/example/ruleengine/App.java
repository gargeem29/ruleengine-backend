package com.example.ruleengine;

import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        try {
            // Create rule: age > 30 AND department = 'Sales'
            Node rule1 = RuleEngine.createRule("age > 30 AND department = 'Sales'");

            // Create rule: salary > 50000
            Node rule2 = RuleEngine.createRule("salary > 50000");

            // Combine the rules using AND
            Node combinedRule = RuleEngine.combineRules(rule1, rule2, "AND");

            // Create sample data
            Map<String, Object> data = new HashMap<>();
            data.put("age", 35);
            data.put("department", "Sales");
            data.put("salary", 60000);

            // Evaluate the combined rule
            boolean result = RuleEngine.evaluateRule(combinedRule, data);
            System.out.println("Evaluation Result: " + result);  // Expected: true
        } catch (IllegalArgumentException e) {
            System.err.println("Error evaluating rule: " + e.getMessage());
        }
    }
}



