package pkg1000;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeetCode1800 {
    /**
     * 1807. 替换字符串中的括号内容
     * @param s
     * @param knowledge
     * @return
     */
    public String evaluate(String s, List<List<String>> knowledge) {
        Map<String, String> knowledgeSet = new HashMap<>();
        for (List<String> list : knowledge) {
            knowledgeSet.put(list.get(0), list.get(1));
        }

        StringBuilder ans = new StringBuilder(), tmp = new StringBuilder();
        boolean start = false;
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (c == '(') {
                start = true;
                tmp = new StringBuilder();
            } else if (c == ')') {
                
                ans.append(knowledgeSet.containsKey(tmp.toString()) ? knowledgeSet.get(tmp.toString()) : '?');
                start = false;
            } else {
                if (start) {
                    tmp.append(c);
                } else {
                    ans.append(c);
                }
            }
        }
        return ans.toString();
    }

    /**
     * 1866. 恰有 K 根木棍可以看到的排列数目
     * @param n
     * @param k
     * @return
     */
    public int rearrangeSticks(int n, int k) {
        // 定义模数，防止结果溢出，题目要求对 10^9 + 7 取模
        final int MOD = 1_000_000_007;

        // 动态规划数组：f[j] 表示当前处理到第 i 根木棍时，恰好能看到 j 根木棍的方案数。
        // 初始状态下（i = 0），看到 0 根木棍的方案数为 1（即空排列）。
        int[] f = new int[k + 1];
        f[0] = 1;

        // 外层循环：枚举木棍的总数 i，从 1 根逐步增加到 n 根。
        for (int i = 1; i <= n; i++) {
            // g 数组用于记录加入第 i 根木棍（即当前长度为 i 的排列中最高的一根）后的新状态。
            int[] g = new int[k + 1];

            // 内层循环：枚举当前能看到的木棍数量 j。
            // 注意：j 必须至少为 1，因为只要排列非空，最高的木棍一定能被看到。
            for (int j = 1; j <= k; j++) {
                // 状态转移方程包含两种情况：
                // 1. 第 i 根木棍（最高的）放在最后面：它一定会被看到。
                //    此时前 i-1 根木棍需要恰好看到 j-1 根，方案数为 f[j - 1]。
                // 2. 第 i 根木棍（最高的）不放在最后面：它会被挡住，看不见。
                //    此时前 i-1 根木棍需要恰好看到 j 根。最高的木棍可以插在除了最后面以外的任意位置，
                //    共有 (i - 1) 种插法。方案数为 f[j] * (i - 1)。
                // 使用 1L 强制转为 long 类型进行乘法运算，防止两个 int 相乘时发生溢出。
                g[j] = (int) (1L * f[j] * (i - 1) % MOD + f[j - 1]) % MOD;
            }

            // 滚动数组优化：将当前轮次计算出的 g 数组赋值给 f，供下一轮 i+1 使用。
            f = g;
        }

        // 返回处理完 n 根木棍后，恰好能看到 k 根木棍的方案数。
        return f[k];
    }
}
