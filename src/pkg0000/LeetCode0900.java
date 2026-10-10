package pkg0000;

import java.util.List;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

public class LeetCode0900 {

    /**
     * 913. 猫和老鼠
     * @param graph
     * @return
     */
    public int catMouseGame(int[][] graph) {
        int n = graph.length;

        // 游戏角色与结果常量
        final int[] TURN = {0, 1};    // 当前轮到谁移动
        final int[] WIN = {0, 1, 2};  // 三种游戏结果

        int[][][] degrees = new int[n][n][2];
        int[][][] results = new int[n][n][2];

        // 用队列进行 BFS 反向传播，从已知的终止状态往回推
        Queue<int[]> queue = new ArrayDeque<>();

        // ========== 第一步：初始化每个状态的出度 ==========
        // degrees[mouse][cat][turn] 表示该状态下当前移动方有多少个可走的下一步
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < n; j++) {
                // 老鼠移动时，可以走到任意邻居节点
                degrees[i][j][TURN[0]] = graph[i].length;
                // 猫移动时，可以走到任意邻居节点（先按全部邻居计数，后面再减去节点 0）
                degrees[i][j][TURN[1]] = graph[j].length;
            }
        }

        // 猫不能进入节点 0（洞），所以猫移动时的出度要减去能走到节点 0 的边
        // 对每个猫位置 node（node 是节点 0 的邻居），所有 (mouse=node, cat=任意, turn=CAT_TURN) 的出度 -1
        for (int node : graph[0]) {
            for (int i = 0; i < n; i++) {
                degrees[i][node][TURN[1]]--;
            }
        }

        // ========== 第二步：将所有已知终止状态入队 ==========

        // 终止状态一：老鼠在节点 0（洞），老鼠获胜
        // 无论轮到谁，只要老鼠已经在洞里，结果就是老鼠赢
        for (int j = 1; j < n; j++) {
            results[0][j][TURN[0]] = WIN[1];
            results[0][j][TURN[1]] = WIN[1];
            queue.offer(new int[]{0, j, TURN[0]});
            queue.offer(new int[]{0, j, TURN[1]});
        }

        // 终止状态二：猫和老鼠在同一节点，猫获胜
        // 无论轮到谁，只要猫抓住了老鼠，结果就是猫赢
        // 注意 i 从 1 开始，因为猫不能进入节点 0，所以 (0,0) 不会出现
        for (int i = 1; i < n; i++) {
            results[i][i][TURN[0]] = WIN[2];
            results[i][i][TURN[1]] = WIN[2];
            queue.offer(new int[]{i, i, TURN[0]});
            queue.offer(new int[]{i, i, TURN[1]});
        }

        // ========== 第三步：BFS 反向传播 ==========
        // 从已知的终止状态出发，反向推导前驱状态的结果
        // 核心思想：如果一个状态的所有后继都确定了，那么这个状态的结果也确定了
        while (!queue.isEmpty()) {
            int[] state = queue.poll();
            int mouse = state[0], cat = state[1], turn = state[2];
            // 当前已确定的结果
            int result = results[mouse][cat][turn];

            // 获取所有能转移到当前状态的前驱状态
            List<int[]> prevStates = getPrevStates(TURN, WIN, graph, mouse, cat, turn);

            for (int[] prevState : prevStates) {
                int prevMouse = prevState[0], prevCat = prevState[1], prevTurn = prevState[2];
                // 只处理尚未确定结果的前驱状态
                if (results[prevMouse][prevCat][prevTurn] == WIN[0]) {
                    // 判断：前驱状态的移动方能否通过走到当前状态而「必胜」
                    //   result == MOUSE_WIN && prevTurn == MOUSE_TURN
                    //     → 前驱轮到老鼠走，且下一步是鼠胜 → 老鼠会选这条路，前驱也是鼠胜
                    //   result == CAT_WIN && prevTurn == CAT_TURN
                    //     → 前驱轮到猫走，且下一步是猫胜 → 猫会选这条路，前驱也是猫胜
                    boolean canWin = (result == WIN[1] && prevTurn == TURN[0]) || (result == WIN[2] && prevTurn == TURN[1]);
                    if (canWin) {
                        // 前驱移动方找到了必胜走法，直接确定结果并入队
                        results[prevMouse][prevCat][prevTurn] = result;
                        queue.offer(new int[]{prevMouse, prevCat, prevTurn});
                    } else {
                        // 当前后继对前驱移动方不利（是对手的胜局）
                        // 前驱移动方不会主动选这条路，所以只是把「未探索后继数」减 1
                        degrees[prevMouse][prevCat][prevTurn]--;

                        // 如果所有后继都探索完了（出度归零），说明前驱移动方无路可走
                        // 所有走法都通向对手的胜利，前驱只能接受失败
                        if (degrees[prevMouse][prevCat][prevTurn] == 0) {
                            // 老鼠的所有后继都是猫胜 → 前驱是猫胜
                            // 猫的所有后继都是鼠胜 → 前驱是鼠胜
                            int loseResult = prevTurn == TURN[0] ? WIN[2] : WIN[1];
                            results[prevMouse][prevCat][prevTurn] = loseResult;
                            queue.offer(new int[]{prevMouse, prevCat, prevTurn});
                        }
                    }
                }
            }
        }

        // 返回初始状态 (mouse=1, cat=2, turn=MOUSE_TURN) 的结果
        return results[1][2][TURN[0]];
    }

    /**
     * 获取所有能转移到 (mouse, cat, turn) 的前驱状态
     * 即：从哪些状态走一步可以到达当前状态
     * 
     * @param TURN   当前轮到谁移动
     * @param WIN    三种游戏结果
     * @param graph  图
     * @param mouse  当前老鼠位置
     * @param cat    当前猫位置
     * @param turn   当前轮到谁
     * @return       前驱状态列表，每个元素为 [prevMouse, prevCat, prevTurn]
     */
    private List<int[]> getPrevStates(final int[] TURN, final int[] WIN, int[][] graph, int mouse, int cat, int turn) {
        List<int[]> prevStates = new ArrayList<>();

        // 前驱的轮次与当前相反
        int prevTurn = turn == TURN[0] ? TURN[1] : TURN[0];

        if (prevTurn == TURN[0]) {
            // 前驱轮到老鼠走，说明老鼠从某个邻居 prev 走到了当前的 mouse
            // 猫的位置不变
            for (int prev : graph[mouse]) {
                prevStates.add(new int[]{prev, cat, prevTurn});
            }
        } else {
            // 前驱轮到猫走，说明猫从某个邻居 prev 走到了当前的 cat
            // 老鼠的位置不变；猫不能从节点 0 出发（猫不在洞里）
            for (int prev : graph[cat]) {
                if (prev != 0) {
                    prevStates.add(new int[]{mouse, prev, prevTurn});
                }
            }
        }
        return prevStates;
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

    /**
     * 948. 令牌放置
     * @param tokens
     * @param power
     * @return
     */
    public int bagOfTokensScore(int[] tokens, int power) {
        // 第一步：排序，为双指针贪心做准备
        // 排序后，左边是最小的令牌（换分数时消耗最少能量）
        // 右边是最大的令牌（换能量时获得最多能量）
        Arrays.sort(tokens);

        int n = tokens.length;  // 令牌个数
        int left = 0;           // 左指针，指向当前最小的未使用令牌
        int right = n - 1;      // 右指针，指向当前最大的未使用令牌
        int score = 0;          // 当前分数
        int maxScore = 0;       // 记录过程中的最大分数（最终答案）

        // 第二步：双指针交替操作，直到左右指针相遇
        while (left <= right) {
            // 情况一：能量足够，优先用最小令牌「面朝上」换分数
            // 为什么选最小？因为消耗最少能量，保留大令牌后续换能量
            if (power >= tokens[left]) {
                power -= tokens[left++];                 // 左指针右移，该令牌已使用; 消耗令牌对应的能量
                maxScore = Math.max(maxScore, ++score);  // 分数 +1, 更新历史最高分
            }
            // 情况二：能量不够，但有分数，用最大令牌「面朝下」换能量
            // 为什么选最大？因为获得最多能量，为后续换更多分数做准备
            else if (score > 0) {
                power += tokens[right--];                // 右指针左移，该令牌已使用; 消耗令牌对应的能量
                score--;                                 // 分数 -1
            }
            // 情况三：既没能量换分数，也没分数换能量，游戏结束
            else {
                break;
            }
        }

        // 返回过程中出现过的最大分数
        // 注意：不是返回最终的 score，因为最后可能为了换能量而降低了分数
        return maxScore;
    }

}
