package pkg1000;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class LeetCode1000 {

    /**
     * 1021. 删除最外层的括号
     * @param s
     * @return
     */
    public String removeOuterParentheses(String s) {
        // 用于拼接最终结果，避免字符串频繁拼接产生的性能开销
        StringBuilder ans = new StringBuilder();

        // 栈：用于跟踪当前括号的嵌套层级
        // 栈非空时说明当前字符处于某个外层括号的「内部」，需要保留
        Deque<Character> stack = new ArrayDeque<>();

        int n = s.length();

        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);

            if (c == ')') {
                // 遇到右括号：先弹出栈顶元素（匹配一个左括号）
                // 这样如果当前 ')' 是某个原语的最外层右括号，
                // 弹出后栈会变空，后续的 append 判断就会跳过它
                stack.pop();
            }

            // 核心判断：栈非空时，说明当前字符处于某个括号的内部（非最外层）
            // 只有「内部字符」才需要保留到结果中
            if (!stack.isEmpty()) {
                ans.append(c);
            }

            if (c == '(') {
                // 遇到左括号：压入栈中，表示进入新的一层嵌套
                // 注意：如果是某个原语的最外层 '('，
                // 此时栈从空变为非空，但上面的 append 已经执行过了（那时栈还是空的），
                // 所以这个最外层 '(' 不会被加入结果
                stack.push(c);
            }
        }
        
        return ans.toString();
    }

    /**
     * 1096. 花括号展开 II
     * @param expression
     * @return
     */
    public List<String> braceExpansionII(String expression) {
        // 全局索引指针，标记当前正在解析的字符位置，随解析过程逐步右移
        int[] idx = {0};
        // 从表达式开头开始递归解析，得到所有展开后的结果（自动去重排序）
        Set<String> result = expr(expression, idx);
        // 题目要求返回 List，将 TreeSet 转为 ArrayList 即可
        return new ArrayList<>(result);
    }

    // ============================================================
    // 解析「表达式」expr
    // 文法规则：expr → term | term ',' expr（即用逗号分隔的多个 term 取并集）
    // 作用：处理花括号内的逗号分隔逻辑，将所有 term 的结果合并
    // 例如：{a,b,c} → 解析为 term(a) ∪ term(b) ∪ term(c) = {a, b, c}
    // ============================================================
    private Set<String> expr(String expression, int[] idx) {
        Set<String> ret = new TreeSet<String>();
        while (true) {
            // 解析一个 term，将其结果加入总结果集（并集操作）
            ret.addAll(term(expression, idx));

            // 检查当前位置是否为逗号 ','
            if (idx[0] < expression.length() && expression.charAt(idx[0]) == ',') {
                // 跳过逗号，继续解析下一个 term
                idx[0]++;
                continue;
            } else {
                // 不是逗号（遇到 '}' 或到达字符串末尾），表达式解析结束
                break;
            }
        }

        return ret;
    }

    // ============================================================
    // 解析「拼接项」term
    // 文法规则：term → item | item term（即多个 item 连续拼接）
    // 作用：将连续排列的多个 item 做笛卡尔积拼接
    // 例如：a{b,c}d → 解析为 item(a) × item({b,c}) × item(d) = {abd, acd}
    // ============================================================
    private Set<String> term(String expression, int[] idx) {
        // 初始化结果集，放入空字符串 "" 作为笛卡尔积的初始单位元
        // 这样第一次拼接时："" + item结果 = item结果本身，逻辑正确
        Set<String> ret = new TreeSet<String>() {{
            add("");
        }};

        // 循环解析连续的 item：
        // 只要当前位置是小写字母或 '{'（即 item 的合法开头），就继续拼接
        while (idx[0] < expression.length() && (expression.charAt(idx[0]) == '{' || Character.isLetter(expression.charAt(idx[0])))) {
            // 解析下一个 item，得到它的展开结果集
            Set<String> sub = item(expression, idx);
            // 将已有的拼接结果 ret 与当前 item 的结果 sub 做笛卡尔积
            Set<String> tmp = new TreeSet<String>();
            for (String left : ret) {
                for (String right : sub) {
                    // 字符串拼接
                    tmp.add(left + right);
                }
            }
            // 更新拼接结果
            ret = tmp;
        }
        return ret;
    }

    // ============================================================
    // 解析「原子项」item
    // 文法规则：item → letter | '{' expr '}'
    // 作用：解析一个最小单元，要么是单个小写字母，要么是花括号包裹的完整子表达式
    // ============================================================
    private Set<String> item(String expression, int[] idx) {
        // 使用 TreeSet 保证结果自动去重、按字典序排序
        Set<String> ret = new TreeSet<String>();

        if (expression.charAt(idx[0]) == '{') {
            // 情况1：遇到左花括号 '{'，说明是一个嵌套子表达式
            // 跳过 '{' 字符
            idx[0]++;
            // 递归解析花括号内部的完整表达式
            ret = expr(expression, idx);
            // 解析完成后，跳过对应的右花括号 '}'
            idx[0]++;
        } else {
            // 情况2：普通小写字母，直接作为单字符字符串加入结果集
            StringBuilder sb = new StringBuilder();
            sb.append(expression.charAt(idx[0]));
            ret.add(sb.toString());
            // 指针右移，跳过当前字母
            idx[0]++;
        }
        
        return ret;
    }
}
