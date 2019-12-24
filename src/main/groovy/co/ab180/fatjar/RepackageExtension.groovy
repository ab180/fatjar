package co.ab180.fatjar

import com.tonicsystems.jarjar.Rule
import org.gradle.api.Project

class RepackageExtension {

    private List<Rule> rules = new ArrayList<>()

    public RepackageExtension(Project project) {

    }

    public void relocate(String pattern, String result) {
        Rule rule = new Rule()
        rule.pattern = pattern
        rule.result = result
        rules.add(rule)
    }

    public List<Rule> getRules() {
        return new ArrayList<Rule>(rules)
    }
}
