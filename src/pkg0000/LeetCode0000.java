package pkg0000;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class LeetCode0000 {

    /**
     * 11. 盛最多水的容器
     * @param height
     * @return
     */
    public int maxArea(int[] height) {
        int n = height.length, l = 0, r = n - 1, max = 0;
        while (l < r) {
            max = Math.max(max, (r - l) * Math.min(height[l], height[r]));
            if (height[l] <= height[r]) {
                l++;
            } else {
                r--;
            }
        }
        return max;
    }

    /**
     * 14. 最长公共前缀
     * @param strs
     * @return
     */
    public String longestCommonPrefix(String[] strs) {
        // 1. 初始化字典树的根节点（根节点不存储字符）
        WordNode root = new WordNode();

        // 2. 遍历所有字符串，将它们插入到字典树中
        for (String str : strs) {
            // 每次插入新单词，都要从根节点出发
            WordNode node = root;
            for (char c : str.toCharArray()) {
                // 计算字符对应的数组下标 (0~25)
                int idx = c - 'a';

                // 【核心修复】：如果当前路径上的节点不存在，才创建新节点
                // 如果已经存在，则直接复用，避免覆盖之前的计数
                if (node.children[idx] == null) {
                    node.children[idx] = new WordNode();
                }

                // 移动到下一个节点，并将该节点的经过次数 +1
                node = node.children[idx];
                node.cnt++;
            }
        }

        // 字符串的总个数
        int n = strs.length;
        StringBuilder ans = new StringBuilder();
        // 使用 curr 指针遍历，避免破坏 root 结构
        WordNode curr = root;

        // 3. 从根节点开始，寻找最长公共前缀
        while (curr != null) {
            // 标记当前层是否找到了公共前缀节点
            boolean found = false;
            // 遍历当前节点的 26 个可能的子节点
            for (int i = 0; i < 26; i++) {
                // 如果子节点存在，且它的经过次数等于字符串总数 n
                // 说明所有字符串都经过了这里，这就是公共前缀的一部分
                if (curr.children[i] != null && curr.children[i].cnt == n) {
                    // 将字符加入结果
                    ans.append((char) ('a' + i));
                    // 指针下移，继续寻找下一层
                    curr = curr.children[i];
                    found = true;
                    // 因为每个节点最多只有一个 cnt==n 的子节点，找到即可跳出循环
                    break;
                }
            }
            // 如果当前层没有找到符合条件的子节点，说明公共前缀已经结束
            if (!found) {
                break;
            }
        }
        return ans.toString();
    }

    /**
     * WordNode
     */
    class WordNode {
        int cnt;
        WordNode[] children;
        WordNode() {
            cnt = 0;
            children = new WordNode[26];
        }
    }

    /**
     * 20. 有效的括号
     * @param s
     * @return
     */
    public boolean isValid(String s) {
        // 用两个字符串分别存储左括号和右括号，相同下标的字符互为配对
        // set[0] = "([{"  →  左括号集合，下标 0='(', 1='[', 2='{'
        // set[1] = ")]}"  →  右括号集合，下标 0=')', 1=']', 2='}'
        // 配对原理：'(' 和 ')' 下标都是 0，'[' 和 ']' 下标都是 1，以此类推
        final String[] set = {"([{", ")]}"};

        // 栈：存储遇到的左括号在 set[0] 中的下标（0、1 或 2）
        // 遇到右括号时，弹出栈顶下标与当前右括号的下标比较，相等则配对成功
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (set[0].indexOf(c) >= 0) {
                // 当前字符是左括号（在 set[0] 中能找到）
                // 将其在 set[0] 中的下标压入栈，等待后续匹配
                stack.push(set[0].indexOf(c));
            } else if (stack.isEmpty() || stack.pop() != set[1].indexOf(c)) {
                // 当前字符是右括号，需要检查是否能与栈顶的左括号配对
                // 两种无效情况用短路或 (||) 合并判断：
                //   1. 栈为空 → 没有左括号可以匹配，无效
                //   2. 栈顶下标 ≠ 当前右括号在 set[1] 中的下标 → 类型不匹配，无效
                // 注意：stack.pop() 会同时完成「取出栈顶」和「弹出元素」两个操作
                return false;
            }
        }

        // 遍历结束后，栈必须为空才说明所有左括号都被正确匹配
        // 若栈非空，说明有左括号多余未闭合，返回 false
        return stack.isEmpty();
    }

    /**
     * 22. 括号生成
     * @param n
     * @return
     */
    public List<String> generateParenthesis(int n) {
        // 用于存储所有合法的括号组合结果
        List<String> combinations = new ArrayList<>();

        // 创建一个长度为 2n 的字符数组，作为递归过程中构建括号的「画布」
        // 每一层递归会在当前位置填入 '(' 或 ')'，最终填满整个数组
        generateAll(new char[2 * n], 0, 0, combinations);

        return combinations;
    }

    /**
     * 暴力枚举所有可能的括号组合，再筛选出合法的
     * @param current 当前正在构建的字符数组
     * @param pos 当前要填充的位置下标
     * @param leftCnt 已经填充的左括号数目
     * @param result 收集合法组合的结果列表
     */
    private void generateAll(char[] current, int pos, int leftCnt, List<String> result) {
        int n = current.length >> 1;

        // ========== 递归终止：数组已填满 ==========
        if (pos == current.length) {
            // 由于递归过程中已经严格约束了左右括号数量，
            // 能走到这里的一定是恰好 n 个 '(' 和 n 个 ')' 且前缀合法，
            // 直接加入结果，无需 valid() 校验
            result.add(new String(current));
            return;
        }

        // ========== 剪枝一：左括号还没用完，可以填 '(' ==========
        // 已用左括号数 < n，说明还有名额，填入 '(' 不会导致左括号超额
        if (leftCnt < n) {
            current[pos] = '(';
            generateAll(current, pos + 1, leftCnt + 1, result);
        }

        // ========== 剪枝二：右括号数量尚未追平左括号，可以填 ')' ==========
        // 已用右括号数 = pos - leftCnt
        // 只有当 右括号数 < 左括号数 时，填入 ')' 才不会破坏前缀合法性
        // 即 pos - leftCnt < leftCnt  ⟺  leftCnt * 2 > pos
        if (leftCnt << 1 > pos) {
            current[pos] = ')';
            generateAll(current, pos + 1, leftCnt, result);
        }
    }

    /**
     * 70. 爬楼梯
     * @param n
     * @return
     */
    public int climbStairs(int n) {
        if (n == 1) {
            return 1;
        }
        int[] dp = {1, 2};
        for (int i = 3; i <= n; i++) {
            int temp = dp[1];
            dp[1] += dp[0];
            dp[0] = temp;
        }
        return dp[1];
    }

    /**
     * 97. 交错字符串
     * @param s1
     * @param s2
     * @param s3
     * @return
     */
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length(), t = s3.length();
        if (m + n != t) {
            return false;
        }
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                int k = i + j - 1;
                if (i > 0) {
                    dp[j] = dp[j] && s1.charAt(i - 1) == s3.charAt(k);
                }
                if (j > 0 && s2.charAt(j - 1) == s3.charAt(k)) {
                    dp[j] |= dp[j - 1];
                }
            }
        }
        return dp[n];
    }
}
