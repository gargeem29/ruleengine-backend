package com.example.ruleengine;

import java.util.Map;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/rules")  // Base URL for the API
public class RuleController {

    // Endpoint to create a rule from a rule string
    @PostMapping("/create_rule")
    public ResponseEntity<Node> createRule(@RequestBody Map<String, String> payload) {
        try {
            String ruleString = payload.get("rule"); // Extract the rule from the payload
            if (ruleString == null || ruleString.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null); // Return error if input is invalid
            }
            // Create an AST from the rule string
            Node rule = RuleEngine.createRule(ruleString);
            return ResponseEntity.ok(rule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Return error response if the rule is invalid
        }
    }

    // Endpoint to evaluate a rule from JSON payload
   @PostMapping("/evaluate_rule")
public ResponseEntity<Object> evaluateRule(@RequestBody Map<String, Object> payload) {
    // Check if the required fields are present in the payload
    if (!payload.containsKey("rules") || !payload.containsKey("data")) {
        return ResponseEntity.badRequest().body("Missing required fields: 'rules' or 'data'"); // More informative error
    }

    // Validate structure of rules and data
    if (!(payload.get("rules") instanceof Map) || !(payload.get("data") instanceof Map)) {
        return ResponseEntity.badRequest().body("Invalid payload structure");
    }

    Map<String, Object> rules = (Map<String, Object>) payload.get("rules");
    Map<String, Object> data = (Map<String, Object>) payload.get("data");

    try {
        // Create the rule from the JSON rules structure
        Node rule = RuleEngine.createRuleFromJson(rules); // Ensure this method handles errors properly

        // Evaluate the rule with the given data
        boolean result = RuleEngine.evaluateRule(rule, data);
        return ResponseEntity.ok(result);

    } catch (IllegalArgumentException e) {
        // Log exception for debugging
        return ResponseEntity.badRequest().body("Invalid rule: " + e.getMessage());
    } catch (ClassCastException e) {
        // Log exception for debugging
        return ResponseEntity.badRequest().body("Invalid payload structure: " + e.getMessage());
    } catch (Exception e) {
        // Handle unexpected exceptions
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
    }
}

    // Endpoint to combine two rules
    @PostMapping("/combine_rules")
    public ResponseEntity<Node> combineRules(@RequestBody Map<String, Object> payload) {
        if (payload == null) {
            return ResponseEntity.badRequest().body(null); // Payload is null
        }
    
        try {
            // Validate input payload
            if (!payload.containsKey("rule1") || !payload.containsKey("rule2") || !payload.containsKey("operator")) {
                return ResponseEntity.badRequest().body(null); // Missing required fields
            }
    
            String rule1String = payload.get("rule1") instanceof String ? (String) payload.get("rule1") : null;
            String rule2String = payload.get("rule2") instanceof String ? (String) payload.get("rule2") : null;
            String operator = payload.get("operator") instanceof String ? (String) payload.get("operator") : null;
    
            // Validate the input rules and operator
            if (rule1String == null || rule2String == null || operator == null) {
                return ResponseEntity.badRequest().body(null); // Invalid input
            }
    
            // Create rules from the strings
            Node rule1 = RuleEngine.createRule(rule1String); 
            Node rule2 = RuleEngine.createRule(rule2String); 
    
            // Combine the rules using the specified operator (AND/OR)
            Node combinedRule = RuleEngine.combineRules(rule1, rule2, operator);
            return ResponseEntity.ok(combinedRule); // Return combined rule
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Return error if rules are invalid
        } catch (ClassCastException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid payload structure
        } catch (NullPointerException e) {
            // Log the exception for debugging
            return ResponseEntity.badRequest().body(null); // Handle unexpected nulls gracefully
        } catch (Exception e) {
            // Log the unexpected exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Handle other unexpected exceptions
        }
    }



    // New GET method to retrieve rules
    @GetMapping  // This maps to GET requests at /api/rules
    public ResponseEntity<List<Node>> getRules() {
        // Sample logic to retrieve rules; replace with actual implementation
        List<Node> rules = List.of();  // Replace with your actual logic to fetch rules
        return ResponseEntity.ok(rules);
    }
    
}
