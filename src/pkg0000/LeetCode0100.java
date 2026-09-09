package pkg0000;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /**
     * 135. 分发糖果
     * @param ratings
     * @return
     */
    public int candy(int[] ratings) {
        int n = ratings.length;
        int[] candies = new int[n];
        Arrays.fill(candies, 1);

        // 比左边评分高，就比左边多一个糖果
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                candies[i] = candies[i - 1] + 1;
            }
        }
        // 比右边评分高，也要比右边多至少一个糖果
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                candies[i] = Math.max(candies[i], candies[i + 1] + 1);
            }
        }

        return Arrays.stream(candies).sum();
    }

    /**
     * 139. 单词拆分
     * @param s
     * @param wordDict
     * @return
     */
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> set = new HashSet<>(wordDict);
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && set.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[n];
    }

    /**
     * 198. 打家劫舍
     * @param nums
     * @return
     */
    public int rob(int[] nums) {
        int n = nums.length;
        int[][] dp = new int[n][2];
        dp[0][1] = nums[0];
        for (int i = 1; i < n; i++) {
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1]);
            dp[i][1] = dp[i - 1][0] + nums[i];
        }
        return Math.max(dp[n - 1][0], dp[n - 1][1]);
    }

}
