package pkg2000;

public class LeetCode2400 {
    /**
     * 2472. 不重叠回文子字符串的最大数目
     * @param s
     * @param k
     * @return
     */
    public int maxPalindromes(String s, int k) {
        int n = s.length();

        // ============================================
        // 第一步：预处理 —— 用动态规划标记所有回文子串
        // ============================================
        // isPalindrome[left][right] = true 表示 s[left...right] 是回文串
        boolean[][] isPalindrome = new boolean[n][n];

        // 按子串长度从小到大枚举（len = 1, 2, 3, ..., n）
        // 必须按长度递增的顺序，因为长串的回文判断依赖短串的结果
        for (int len = 1; len <= n; len++) {
            // 枚举所有长度为 len 的子串的起始位置
            for (int left = 0; left + len <= n; left++) {
                // 子串的右边界
                int right = left + len - 1;

                // 判断 s[left...right] 是否为回文：
                // 条件1：首尾字符相同 s[left] == s[right]
                // 条件2：去掉首尾后的内部子串也是回文
                //        - 如果 len <= 2（即长度为1或2），首尾相同就是回文，无需检查内部
                //        - 如果 len > 2，需要检查 isPalindrome[left+1][right-1]
                isPalindrome[left][right] = s.charAt(left) == s.charAt(right) && (len <= 2 || isPalindrome[left + 1][right - 1]);
            }
        }

        // ============================================
        // 第二步：动态规划 —— 求最多不重叠回文子串数量
        // ============================================
        // dp[i] 表示字符串前 i 个字符（s[0...i-1]）中，
        // 最多能选出多少个长度 >= k 的互不重叠的回文子串
        int[] dp = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            // 默认情况：不选以 i-1 结尾的回文子串
            // 那么 dp[i] 继承 dp[i-1] 的结果（前 i-1 个字符的最优解）
            dp[i] = dp[i - 1];

            // 枚举所有可能的回文子串的起始位置 j
            // 回文子串为 s[j...i-1]，其长度为 i - j
            // 要求长度 >= k，即 i - j >= k，所以 j <= i - k，即 j + k <= i
            for (int j = 0; j + k <= i; j++) {
                // 如果 s[j...i-1] 是回文串
                if (isPalindrome[j][i - 1]) {
                    // 选择这个回文子串，则总数 = 前 j 个字符的最优解 + 1
                    // 取所有可能中的最大值
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
        }

        // dp[n] 就是整个字符串 s[0...n-1] 的最优解
        return dp[n];
    }
}
