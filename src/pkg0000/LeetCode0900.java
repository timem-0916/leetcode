package pkg0000;

import java.util.Arrays;

public class LeetCode0900 {

    /**
     * 913. 猫和老鼠
     * @param graph
     * @return
     */
    public int catMouseGame(int[][] graph) {
        // 三种游戏结果常量
        // WIN[0] -> 平局（双方都无法取胜，陷入循环）
        // WIN[1] -> 老鼠获胜（老鼠到达节点 0）
        // WIN[2] -> 猫获胜（猫抓住老鼠）
        final int[] WIN = {0, 1, 2};

        // 图的节点数
        int n = graph.length;
        // 记忆化搜索表：dp[mouse][cat][turns] 记录该状态的结果
        // dp[mouse][cat][turns]：
        //   mouse —— 老鼠当前所在节点
        //   cat   —— 猫当前所在节点
        //   turns —— 当前回合数（从 0 开始）
        // 初始值 -1 表示该状态尚未计算
        int[][][] dp = new int[n][n][2 * n * (n - 1)];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Arrays.fill(dp[i][j], -1);
            }
        }

        // 初始状态：老鼠在节点 1，猫在节点 2，第 0 回合（老鼠先走）
        return catMouseGameHelp(WIN, graph, dp, 1, 2, 0);
    }

    /**
     * 获取当前状态下的游戏结果
     * @param WIN     三种游戏结果常量
     * @param graph   图
     * @param dp      记忆化搜索表
     * @param mouse   老鼠当前所在节点
     * @param cat     猫当前所在节点
     * @param turns   当前回合数
     * @return        [0, 1, 2]
     */
    private int catMouseGameHelp(final int[] WIN, int[][] graph, int[][][] dp, int mouse, int cat, int turns) {
        int n = graph.length;

        // ========== 终止条件一：回合数上限，判为平局 ==========
        // 最多 2 * n * (n-1) 回合。如果超过这个回合数还没分出胜负，
        // 说明双方陷入了循环，谁也无法取胜，判为平局。
        // 原理：状态总数为 n * n * 2（老鼠位置 × 猫位置 × 轮到谁），
        // 超过 2*n*(n-1) 回合必然出现重复状态。
        if (turns == 2 * n * (n - 1)) {
            return WIN[0];
        }

        // ========== 记忆化：已计算过的状态直接返回 ==========
        if (dp[mouse][cat][turns] < 0) {
            if (mouse == 0) {
                // ========== 终止条件二：老鼠到达节点 0（洞），老鼠胜 ==========
                dp[mouse][cat][turns] = WIN[1];
            } else if (cat == mouse) {
                // ========== 终止条件三：猫和老鼠在同一节点，猫胜 ==========
                dp[mouse][cat][turns] = WIN[2];
            } else {
                // ========== 非终止状态：递归计算下一步 ==========
                catMouseGameNext(WIN, graph, dp, mouse, cat, turns);
            }
        }

        return dp[mouse][cat][turns];
    }

    /**
     * 计算当前状态下一步的所有可能结果，并确定最优策略下的最终结果
     * @param WIN     三种游戏结果常量
     * @param graph   图
     * @param dp      记忆化搜索表
     * @param mouse   老鼠当前所在节点
     * @param cat     猫当前所在节点
     * @param turns   当前回合数
     */
    private void catMouseGameNext(final int[] WIN, int[][] graph, int[][][] dp, int mouse, int cat, int turns) {
        // 根据回合数判断当前轮到谁移动：偶数回合老鼠走，奇数回合猫走
        int curMove = turns % 2 == 0 ? mouse : cat;

        // 「默认结果」：对当前移动方最不利的结果
        //   老鼠移动时，默认结果是 CAT_WIN（假设猫能赢，老鼠要尽力避免）
        //   猫移动时，默认结果是 MOUSE_WIN（假设老鼠能赢，猫要尽力避免）
        // 当前移动方会遍历所有下一步，尝试把结果往对自己有利的方向推
        int defaultResult = curMove == mouse ? WIN[2] : WIN[1];

        // result 初始化为默认结果（对当前移动方最不利的情况）
        int result = defaultResult;

        // 获取当前移动方可以到达的所有邻居节点
        int[] nextNodes = graph[curMove];
        for (int next : nextNodes) {
            // 猫的特殊限制：猫不能进入节点 0（洞），跳过
            if (curMove == cat && next == 0) {
                continue;
            }

            // 根据当前移动方，计算下一步的老鼠位置和猫位置
            int nextMouse = curMove == mouse ? next : mouse;
            int nextCat = curMove == cat ? next : cat;

            // 递归计算下一步状态的结果
            int nextResult = catMouseGameHelp(WIN, graph, dp, nextMouse, nextCat, turns + 1);

            // ========== 核心博弈逻辑：当前移动方选择最优策略 ==========
            // 如果下一步结果不是「默认结果」（即对当前移动方有利），就采纳
            if (nextResult != defaultResult) {
                // 更新为更有利的结果
                result = nextResult;
                // 如果结果已经不是平局（即当前移动方已经找到必胜走法），无需继续遍历
                // 因为：
                //   老鼠找到了 MOUSE_WIN → 直接选它，不用再看其他走法
                //   猫找到了 CAT_WIN → 直接选它，不用再看其他走法
                if (result != WIN[0]) {
                    break;
                }
                // 如果结果是 DRAW，继续看有没有更好的（必胜）走法
            }

            // 如果 nextResult == defaultResult（对当前移动方不利），忽略这个走法
        }

        // 将最终结果写入记忆化表
        dp[mouse][cat][turns] = result;
    }

    /**
     * 921. 使括号有效的最少添加
     * @param s
     * @return
     */
    public int minAddToMakeValid(String s) {
        // ans：记录需要额外补充的左括号 '(' 数量
        // 当遇到右括号 ')' 且没有可匹配的左括号时，说明这个右括号无法被匹配，必须补一个左括号
        int ans = 0;

        // leftCount：记录当前尚未被匹配的左括号 '(' 的数量
        // 每遇到一个 '(' 就加 1，每成功匹配一个 ')' 就减 1
        int leftCount = 0;

        // 获取字符串长度，避免在循环中重复调用 s.length()
        int length = s.length();

        // 从左到右遍历字符串中的每一个字符
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);

            if (c == '(') {
                // 遇到左括号，未匹配的左括号数量加 1
                leftCount++;
            } else {
                // 遇到右括号 ')'
                if (leftCount > 0) {
                    // 如果还有未匹配的左括号，就用当前右括号与其中一个配对
                    // 未匹配的左括号数量减 1
                    leftCount--;
                } else {
                    // 没有可匹配的左括号，这个右括号是多余的
                    // 需要额外补充一个左括号来匹配它
                    ans++;
                }
            }
        }

        // 遍历结束后，leftCount 中剩余的左括号都没有被匹配
        // 每个未匹配的左括号都需要补充一个右括号来配对
        ans += leftCount;

        // 返回最少需要添加的括号总数
        return ans;
    }

    /**
     * 940. 不同的子序列 II
     * @param s
     * @return
     */
    public int distinctSubseqII(String s) {
        final int MOD = 1000000007;
        int[] g = new int[26];
        int n = s.length(), total = 0;
        for (int i = 0; i < n; i++) {
            int oi = s.charAt(i) - 'a';
            int prev = g[oi];
            g[oi] = (total + 1) % MOD;
            total = ((total + g[oi] - prev) % MOD + MOD) % MOD;
        }
        return total;
    }
}
