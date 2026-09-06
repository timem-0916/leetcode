package pkg0000;

public class LeetCode0100 {
    /**
     * 115. 不同的子序列
     * @param s
     * @param t
     * @return
     */
    public int numDistinct(String s, String t) {
        char[] sArray = s.toCharArray(), tArray = t.toCharArray();
        int n = sArray.length, m = tArray.length;
        // dp[i][j] 表示 s 的前 i 个字符中，包含 t 的前 j 个字符作为子序列的方案数。
        // 使用 long 防止在计算过程中发生整数溢出
        long[][] dp = new long[n + 1][m + 1];
        // 边界条件：任何字符串都可以通过删除所有字符来匹配空串，方案数为 1
        for (int i = 0; i <= n; i++) {
            dp[i][0] = 1L;
        }

        // 填充 DP 表格
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                // 无论字符是否相等，都可以选择跳过 s[i-1]
                dp[i][j] = dp[i - 1][j];
                // 如果字符相等，还可以选择匹配 s[i-1] 和 t[j-1]
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] += dp[i - 1][j - 1];
                }
            }
        }

        return (int) dp[n][m];
    }

}
