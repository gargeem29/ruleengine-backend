package com.example.ruleengine;

import java.util.HashMap;  // Import the Node class
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;  // Ensure to import JUnit assertions
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RuleEngineTest {

    private RuleEngine ruleEngine;

    @BeforeEach
    void setUp() {
        ruleEngine = new RuleEngine();
    }

    @Test
    void testCreateRuleFromJson() {
        // Arrange: Sample JSON-like input as a Map
        Map<String, Object> rules = new HashMap<>();
        rules.put("rules", List.of(
                Map.of("operator", "AND", "operands", List.of(
                        Map.of("condition", "age > 30"),
                        Map.of("condition", "income > 50000")
                ))
        ));
    
        // Act: Create rule from JSON-like Map
        Node ruleNode = ruleEngine.createRuleFromJson(rules);
    
        // Assert: Verify the structure of the rule node
        assertNotNull(ruleNode);  // Ensure the node is created
        assertEquals("operator", ruleNode.getNodeType());  // Node should be operator type
        assertEquals("AND", ruleNode.getValue());  // The operator should be "AND"
        assertNotNull(ruleNode.getLeft());  // Left child should not be null
        assertNotNull(ruleNode.getRight());  // Right child should not be null
    }

    @Test
    void testEvaluateRule() {
        // Arrange: Create a sample rule
        Node ruleNode = ruleEngine.createRule("age > 30 AND income > 50000");

        // Sample data to test against
        Map<String, Object> data = Map.of(
                "age", 35,
                "income", 60000
        );

        // Act: Evaluate the rule with the data
        boolean result = ruleEngine.evaluateRule(ruleNode, data);

        // Assert: Verify that the rule evaluation returns true
        assertTrue(result);
    }

    @Test
    void testInvalidRuleEvaluation() {
        // Arrange: Create a rule that should evaluate to false
        Node ruleNode = ruleEngine.createRule("age < 30");

        // Sample data where the rule should fail
        Map<String, Object> data = Map.of(
                "age", 35
        );

        // Act: Evaluate the rule
        boolean result = ruleEngine.evaluateRule(ruleNode, data);

        // Assert: Verify that the evaluation returns false
        assertFalse(result);
    }

    @Test
    void testInvalidJsonInput() {
        // Arrange: Sample invalid JSON-like input as a Map
        Map<String, Object> invalidRules = new HashMap<>();
        invalidRules.put("rules", null);  // This should trigger an error

        // Act & Assert: Expecting an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            ruleEngine.createRuleFromJson(invalidRules);
        });
    }
}
