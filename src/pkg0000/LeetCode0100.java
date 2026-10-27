package pkg0000;

import java.util.ArrayList;
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
     * 118. 杨辉三角
     * @param numRows
     * @return
     */
    public List<List<Integer>> generate(int numRows) {
        // 最终结果列表，用于存放杨辉三角的每一行
        List<List<Integer>> ans = new ArrayList<>();
        
        // 一维滚动数组，用于动态规划计算当前行的数值。
        // 杨辉三角第 i 行（0-based）有 i+1 个元素，数组长度设为 numRows 足够容纳最长的一行。
        int[] dp = new int[numRows];

        // 外层循环：逐行生成杨辉三角，i 代表当前行的行号（从 0 开始）
        for (int i = 0; i < numRows; i++) {
            // 用于暂存当前行计算出的所有元素，最后加入结果集
            List<Integer> list = new ArrayList<>();

            // 内层循环：计算当前行的每一个元素。
            // 关键点：这里采用【从右向左】（j 从 i 递减到 0）的遍历顺序。
            // 这样做是为了在计算 dp[j] 时，dp[j-1] 仍然保留着上一行（i-1行）的旧值，
            // 从而避免数据被提前覆盖，实现空间优化的滚动数组。
            for (int j = i; j >= 0; j--) {
                // 杨辉三角的边界条件：每一行的第一个（j==0）和最后一个（j==i）元素永远是 1
                if (j == 0 || j == i) {
                    dp[j] = 1;
                } else {
                    // 核心状态转移方程：当前元素 = 上一行左上方元素 + 上一行正上方元素
                    // 因为是从右向左遍历，此时 dp[j-1] 还是上一行的值，dp[j] 也是上一行的值
                    dp[j] = dp[j - 1] + dp[j];
                }
                // 将计算好的当前元素加入当前行的列表
                list.add(dp[j]);
            }
            // 将当前行列表添加到最终结果中
            ans.add(list);
        }

        return ans;
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
