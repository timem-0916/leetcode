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

    /**
     * 3734. 大于目标字符串的最小字典序回文排列
     * @param s
     * @param target
     * @return
     */
    public String lexPalindromicPermutation(String s, String target) {
        int n = s.length();
        // 特殊情况：长度为 1
        if (n == 1) {
            return s.compareTo(target) > 0 ? s : "";
        }

        // 统计每个字符的出现次数
        int[] cnt = new int[26];
        for (char c : s.toCharArray()) {
            cnt[c - 'a']++;
        }

        // 检查是否能构成回文串，并记录奇数个的字符
        String oddChar = "";
        for (int i = 0; i < 26; i++) {
            if ((cnt[i] & 1) == 1) {
                // 超过一个字符出现奇数次，无法构成回文
                if (oddChar != "") {
                    return "";
                }
                oddChar = String.valueOf((char) ('a' + i));
            }
            // 只需要一半的字符来构造左半部分
            cnt[i] >>= 1;
        }

        StringBuilder prefix = new StringBuilder();
        // 贪心构造左半部分的每一位
        for (int i = 0; i < n / 2; i++) {
            boolean found = false;
            // 尝试放置字典序最小的字符
            for (int j = 0; j < 26; j++) {
                if (cnt[j] == 0) {
                    continue;
                }
                cnt[j]--;
                if (lexPalindromicPermutationCheck(prefix.toString(), (char) ('a' + j), cnt, oddChar, target)) {
                    // 如果构造的回文串大于target，则选择该字符
                    prefix.append((char) ('a' + j));
                    found = true;
                    break;
                } else {
                    // 不满足条件，恢复计数
                    cnt[j]++;
                }
            }
            if (!found) {
                return "";
            }
            // prefix已经大于target
            if (prefix.charAt(i) > target.charAt(i)) {
                StringBuilder left = new StringBuilder(prefix);
                for (int j = 0; j < 26; j++) {
                    if (cnt[j] == 0) {
                        continue;
                    }
                    left.append(String.valueOf((char) ('a' + j)).repeat(cnt[j]));
                }
                String palindrome = left.toString() + oddChar + left.reverse().toString();
                return palindrome;
            }
        }

        String palindrome = prefix.toString() + oddChar + prefix.reverse().toString();
        return palindrome;
    }

    /**
     * 检查能否构成满足要求的字符串
     * @param prefix
     * @param c
     * @param cnt
     * @param oddChar
     * @param target
     * @return
     */
    private boolean lexPalindromicPermutationCheck(String prefix, char c, int[] cnt, String oddChar, String target) {
        StringBuilder left = new StringBuilder(prefix);
        left.append(c);
        for (int i = 25; i >= 0; i--) {
            if (cnt[i] == 0) {
                continue;
            }
            left.append(String.valueOf((char) ('a' + i)).repeat(cnt[i]));
        }
        String palindrome = left.toString() + oddChar + left.reverse().toString();
        return palindrome.compareTo(target) > 0;
    }

}
