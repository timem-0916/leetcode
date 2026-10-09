package pkg1000;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeetCode1800 {
    /**
     * 1807. 替换字符串中的括号内容
     * @param s
     * @param knowledge
     * @return
     */
    public String evaluate(String s, List<List<String>> knowledge) {
        Map<String, String> knowledgeSet = new HashMap<>();
        for (List<String> list : knowledge) {
            knowledgeSet.put(list.get(0), list.get(1));
        }

        StringBuilder ans = new StringBuilder(), tmp = new StringBuilder();
        boolean start = false;
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (c == '(') {
                start = true;
                tmp = new StringBuilder();
            } else if (c == ')') {
                
                ans.append(knowledgeSet.containsKey(tmp.toString()) ? knowledgeSet.get(tmp.toString()) : '?');
                start = false;
            } else {
                if (start) {
                    tmp.append(c);
                } else {
                    ans.append(c);
                }
            }
        }
        return ans.toString();
    }
}
