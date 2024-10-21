package com.example.ruleengine;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RuleengineApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuleengineApplication.class, args);

        // Example data to evaluate against
        Map<String, Object> data = Map.of(
                "age", 35,
                "income", 60000
        );

        // Example rule JSON structure (manually constructing as a map)
        Map<String, Object> rulesMap = Map.of(
                "rules", Map.of(
                        "operator", "AND",
                        "left", Map.of("operand", "age > 30"),
                        "right", Map.of("operand", "income > 50000")
                )
        );

        // Create rules from the provided JSON map
        Node ruleTree = RuleEngine.createRuleFromJson(rulesMap);

        // Evaluate the rule against the data
        boolean result = RuleEngine.evaluateRule(ruleTree, data);
        System.out.println("Is the user in the cohort? " + result);
    }
}
