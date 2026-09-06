package pkg0000;

import java.util.Arrays;

public class LeetCode0900 {
    /**
     * 940. 不同的子序列 II
     * @param s
     * @return
     */
    public int distinctSubseqII(String s) {
        final int MOD = 1000000007;
        int[] g = new int[26];
        int n = s.length();
        for (int i = 0; i < n; i++) {
            int total = 1;
            for (int j = 0; j < 26; j++) {
                total = (total + g[j]) % MOD;
            }
            g[s.charAt(i) - 'a'] = total;
        }

        int ans = 0;
        for (int i = 0; i < 26; i++) {
            ans = (ans + g[i]) % MOD;
        }
        return ans;
    }
}
