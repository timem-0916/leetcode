package pkg2000;

public class LeetCode2900 {
    /**
     * 2904. 最短且字典序最小的美丽子字符串
     * @param s
     * @param k
     * @return
     */
    public String shortestBeautifulSubstring(String s, int k) {
        // 特判：如果整个字符串中 1 的个数都不够 k，直接返回空
        if (s.replace("0", "").length() < k) {
            return "";
        }

        int n = s.length(), cnt1 = 0;
        // 初始化为整个字符串，作为最坏情况下的兜底
        String ans = s;

        for (int l = 0, r = 0; r < n; r++) {
            // 1. 扩大窗口：将右边界字符加入统计
            if (s.charAt(r) == '1') {
                cnt1++;
            }

            // 2. 收缩窗口：如果 1 的个数超过 k，或者左边界是 '0'（为了追求最短）
            while (cnt1 > k || s.charAt(l) == '0') {
                if (s.charAt(l++) == '1') {
                    cnt1--;
                }
            }

            // 3. 更新答案：当窗口内恰好有 k 个 1 时
            if (cnt1 == k) {
                String temp = s.substring(l, r + 1);
                // 优先比较长度，长度相同再比较字典序（Java 的 compareTo 天然支持字典序比较）
                if (temp.length() < ans.length() || (temp.length() == ans.length() && temp.compareTo(ans) < 0)) {
                    ans = temp;
                }
            }
        }

        return ans;
    }
}
