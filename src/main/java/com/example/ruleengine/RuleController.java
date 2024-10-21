package com.example.ruleengine;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rules")  // Base URL for the API
public class RuleController {

    // Endpoint to create a rule from a rule string
    @PostMapping("/create_rule")
    public ResponseEntity<Node> createRule(@RequestBody String ruleString) {
        try {
            // Create an AST from the rule string
            Node rule = RuleEngine.createRule(ruleString);
            return ResponseEntity.ok(rule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Return error response if the rule is invalid
        }
    }

    // Endpoint to evaluate a rule from JSON payload
    @PostMapping("/evaluate_rule")
public ResponseEntity<Boolean> evaluateRule(@RequestBody Map<String, Object> payload) {
    // Assume the incoming JSON payload contains "rules" (the rules structure) and "data" (the attributes to evaluate)
    Map<String, Object> rules = (Map<String, Object>) payload.get("rules");
    Map<String, Object> data = (Map<String, Object>) payload.get("data");

    try {
        // Create the rule from the JSON rules structure
        Node rule = RuleEngine.createRuleFromJson(rules);  // Assuming you've updated RuleEngine to handle JSON directly
        
        // Evaluate the rule with the given data
        boolean result = RuleEngine.evaluateRule(rule, data);
        return ResponseEntity.ok(result);

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(false); // Return error response if the rule is invalid
    }
}


 // Endpoint to combine two rules
 @PostMapping("/combine_rules")
 public ResponseEntity<Node> combineRules(@RequestBody Map<String, Object> payload) {
     Node rule1 = RuleEngine.createRule((String) payload.get("rule1")); // Create first rule
     Node rule2 = RuleEngine.createRule((String) payload.get("rule2")); // Create second rule
     String operator = (String) payload.get("operator"); // Get operator

     try {
         Node combinedRule = RuleEngine.combineRules(rule1, rule2, operator);
         return ResponseEntity.ok(combinedRule); // Return combined rule
     } catch (IllegalArgumentException e) {
         return ResponseEntity.badRequest().body(null); // Return error if rules are invalid
     }
 }

}











