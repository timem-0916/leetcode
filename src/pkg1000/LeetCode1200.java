package pkg1000;

import java.util.Arrays;

public class LeetCode1200 {
    /**
     * 1220. 统计元音字母序列的数目（动态规划：空间优化）
     * @param n
     * @return
     */
    public int countVowelPermutation(int n) {
        // 数据太大，需要对 MOD 取余
        final int MOD = 1_000_000_007;

        // 元英字母
        final char[] VOWEL = {'a', 'e', 'i', 'o', 'u'};

        // dp[i][j] -> 第 i 个字符以 VOWEL[j] 开始时的序列数目
        int[] dp = new int[5];

        // dp 边界初始化
        Arrays.fill(dp, 1);

        // dp 状态转移循环
        for (int i = n - 2; i >= 0; i--) {
            int[] prevDp = dp.clone();
            for (int j = 0; j < 5; j++) {
                // 当前序列结尾的字符
                char c = VOWEL[j];
                switch (c) {
                    case 'a' -> dp[j] = prevDp[1];
                    case 'e' -> dp[j] = (prevDp[0] + prevDp[2]) % MOD;
                    case 'i' -> dp[j] = ((prevDp[0] + prevDp[1]) % MOD + (prevDp[3] + prevDp[4]) % MOD) % MOD;
                    case 'o' -> dp[j] = (prevDp[2] + prevDp[4]) % MOD;
                    case 'u' -> dp[j] = prevDp[0];
                    default -> dp[j] = 0;
                }
            }
        }

        int ans = 0;
        for (int j = 0; j < 5; j++) {
            ans = (ans + dp[j]) % MOD;
        }
        return ans;
    }
}
