package pkg3000;

import java.util.Arrays;

public class LeetCode3700 {
    /**
     * 3720. 大于目标字符串的最小字典序排列
     * @param s
     * @param target
     * @return
     */
    public String lexGreaterPermutation(String s, String target) {
        char[] sArray = s.toCharArray();
        char[] tArray = target.toCharArray();
        int n = sArray.length;
        int[] cnt = new int[26];
        for (int i = 0; i < n; i++) {
            cnt[sArray[i] - 'a']++;
            cnt[tArray[i] - 'a']--;
        }

        // 从右往左尝试
        for (int i = n - 1; i >= 0; i--) {
            int p = tArray[i] - 'a';
            // 撤销消耗
            cnt[p]++;
            // 检查前缀能否完全匹配
            if (Arrays.stream(cnt).min().getAsInt() < 0) {
                continue;
            }
            // 找一个比 p 大的最小可用字符
            for (int q = p + 1; q < 26; q++) {
                if (cnt[q] > 0) {
                    cnt[q]--;
                    tArray[i] = (char) ('a' + q);
                    return new String(tArray, 0, i + 1) + getMinString(cnt);
                }
            }
        }

        return "";
    }

    /**
     * 检查剩余字符是否能构成大于 suffix 的字符串
     * @param cnt
     * @param target
     * @param start
     * @return
     */
    private boolean canFormGreater(int[] cnt, String target, int start) {
        String maxStr = getMaxString(cnt);
        String suffix = target.substring(start);
        return maxStr.compareTo(suffix) > 0;
    }

    /**
     * 获取最大字典序字符串（降序排列）
     * @param cnt
     * @return
     */
    private String getMaxString(int[] cnt) {
        StringBuilder res = new StringBuilder();
        for (int i = 25; i >= 0; i--) {
            if (cnt[i] > 0) {
                res.append(String.valueOf((char) ('a' + i)).repeat(cnt[i]));
            }
        }
        return res.toString();
    }

    /**
     * 获取最大字典序字符串（降序排列）
     * @param cnt
     * @return
     */
    private String getMinString(int[] cnt) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            if (cnt[i] > 0) {
                res.append(String.valueOf((char) ('a' + i)).repeat(cnt[i]));
            }
        }
        return res.toString();
    }

}
