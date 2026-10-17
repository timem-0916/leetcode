package pkg0000;

public class LeetCode0200 {

    /**
     * 221. 最大正方形
     * @param matrix
     * @return
     */
    public int maximalSquare(char[][] matrix) {
        // 记录全局最大正方形的边长，初始为 0
        int maxSide = 0;

        // 边界判断：矩阵为空或行列长度为 0 时直接返回 0
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return maxSide;
        }

        // 获取矩阵的行数和列数
        int rows = matrix.length, columns = matrix[0].length;

        // dp[i][j] 表示以 (i, j) 为右下角的全 1 正方形的最大边长
        // 例如 dp[2][3] = 2 表示以 (2,3) 为右下角可以构成一个 2×2 的全 1 正方形
        int[][] dp = new int[rows][columns];

        // 逐行逐列填充 dp 表
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // 当前格子是 '0' 时，不可能作为正方形的右下角
                if (matrix[i][j] == '0') {
                    continue;
                }

                // 边界情况：第一行或第一列的格子，最多只能构成边长为 1 的正方形
                // 因为正方形需要向左和向上扩展，边界上没有足够的空间
                if (i == 0 || j == 0) {
                    dp[i][j] = 1;
                } else {
                    // 核心状态转移方程：
                    // 以 (i,j) 为右下角的正方形边长，取决于其上方、左方、左上方三个位置的最小值 + 1
                    //
                    // 为什么取 min？因为正方形要求所有格子都是 '1'：
                    //   • dp[i-1][j]   → 上方能构成的最大边长（决定了向上能扩展多少）
                    //   • dp[i][j-1]   → 左方能构成的最大边长（决定了向左能扩展多少）
                    //   • dp[i-1][j-1] → 左上方能构成的最大边长（决定了左上角是否完整）
                    // 三者取最小值，才能保证新扩展的正方形四条边都是完整的 '1'
                    // 最后 +1 是因为当前格子 (i,j) 本身也是 '1'，可以作为新的一层
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }

                // 每计算一个 dp 值，就更新全局最大边长
                maxSide = Math.max(maxSide, dp[i][j]);
            }
        }

        // 最大正方形的面积 = 最大边长 × 最大边长
        return maxSide * maxSide;
    }

    /**
     * 256. 粉刷房子（动态规划：空间优化）
     * @param costs
     * @return
     */
    public int minCost(int[][] costs) {
        // 一共有 n 个房子
        int n = costs.length;

        // dp[i][j] -> 前 i 个房子中，第 i 个房子粉刷为 j 颜色的最少花费成本
        int[] dp = new int[3];

        // dp 边界情况
        for (int j = 0; j < 3; j++) {
            dp[j] = costs[0][j];
        }

        // dp 状态转移方程
        for (int i = 1; i < n; i++) {
            int[] dpPre = new int[3];
            dpPre[0] = dp[0];
            dpPre[1] = dp[1];
            dpPre[2] = dp[2];
            dp[0] = Math.min(dpPre[1], dpPre[2]) + costs[i][0];
            dp[1] = Math.min(dpPre[0], dpPre[2]) + costs[i][1];
            dp[2] = Math.min(dpPre[0], dpPre[1]) + costs[i][2];
        }

        return Math.min(Math.min(dp[0], dp[1]), dp[2]);
    }

    /**
     * 265. 粉刷房子 II（动态规划：空间优化）
     * @param costs
     * @return
     */
    public int minCostII(int[][] costs) {
        // 边界：没有房子，花费为 0
        if (costs.length == 0) {
            return 0;
        }

        // 一共有 n 个房子，k 种粉刷颜色
        int n = costs.length, k = costs[0].length;

        /* 首先，我们需要确定第一行的两个最小值。我们还需要记住最小的那个颜色。*/
        // min[0] -> 最小值，min[1] -> 次小值
        int[] min = {-1, -1};
        // 最小值对应的颜色
        int minColor = -1;

        // dp 边界
        for (int color = 0; color < k; color++) {
            int cost = costs[0][color];
            if (min[0] == -1 || cost < min[0]) {
                min[1] = min[0];
                minColor = color;
                min[0] = cost;
            } else if (min[1] == -1 || cost < min[1]) {
                min[1] = cost;
            }
        }

        // 现在，我们需要一路往下走，维护最小值。
        for (int house = 1; house < n; house++) {
            int[] min1 = {-1, -1};
            int minColor1 = -1;

            for (int color = 0; color < k; color++) {
                // 当前房子刷 color 颜色的原始花费
                int cost = costs[house][color];
                if (color == minColor) {
                    // 当前颜色与上一行最小值颜色相同，不能相邻同色，
                    // 所以只能接上一行的次小值
                    cost += min[1];
                } else {
                    // 当前颜色与上一行最小值颜色不同，可以接上一行的最小值
                    cost += min[0];
                }

                // 用当前累计花费更新本行的最小值和次小值
                if (min1[0] == -1 || cost < min1[0]) {
                    // 当前值比已知最小值还小：
                    //   原来的最小值降级为次小值
                    //   当前值成为新的最小值
                    min1[1] = min1[0];
                    minColor1 = color;
                    min1[0] = cost;
                } else if (min1[1] == -1 || cost < min1[1]) {
                    // 当前值不小于最小值，但比次小值小：
                    //   更新次小值
                    min1[1] = cost;
                }
            }

            // 当前行处理完毕，把当前行的结果变成“上一行”的结果，供下一轮使用
            min = min1;
            minColor = minColor1;
        }

        // 最终 min 就是刷完所有房子的最小总花费
        return min[0];
    }

    /**
     * 292. Nim 游戏
     * @param n
     * @return
     */
    public boolean canWinNim(int n) {
        return n % 4 != 0;
    }
}
