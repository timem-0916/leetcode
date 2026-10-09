package pkg0000;


public class LeetCode0900 {
    /**
     * 940. 不同的子序列 II
     * @param s
     * @return
     */
    public int distinctSubseqII(String s) {
        final int MOD = 1000000007;
        int[] g = new int[26];
        int n = s.length(), total = 0;
        for (int i = 0; i < n; i++) {
            int oi = s.charAt(i) - 'a';
            int prev = g[oi];
            g[oi] = (total + 1) % MOD;
            total = ((total + g[oi] - prev) % MOD + MOD) % MOD;
        }
        return total;
    }

    /**
     * 921. 使括号有效的最少添加
     * @param s
     * @return
     */
    public int minAddToMakeValid(String s) {
        // ans：记录需要额外补充的左括号 '(' 数量
        // 当遇到右括号 ')' 且没有可匹配的左括号时，说明这个右括号无法被匹配，必须补一个左括号
        int ans = 0;

        // leftCount：记录当前尚未被匹配的左括号 '(' 的数量
        // 每遇到一个 '(' 就加 1，每成功匹配一个 ')' 就减 1
        int leftCount = 0;

        // 获取字符串长度，避免在循环中重复调用 s.length()
        int length = s.length();

        // 从左到右遍历字符串中的每一个字符
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);

            if (c == '(') {
                // 遇到左括号，未匹配的左括号数量加 1
                leftCount++;
            } else {
                // 遇到右括号 ')'
                if (leftCount > 0) {
                    // 如果还有未匹配的左括号，就用当前右括号与其中一个配对
                    // 未匹配的左括号数量减 1
                    leftCount--;
                } else {
                    // 没有可匹配的左括号，这个右括号是多余的
                    // 需要额外补充一个左括号来匹配它
                    ans++;
                }
            }
        }

        // 遍历结束后，leftCount 中剩余的左括号都没有被匹配
        // 每个未匹配的左括号都需要补充一个右括号来配对
        ans += leftCount;

        // 返回最少需要添加的括号总数
        return ans;
    }
}
