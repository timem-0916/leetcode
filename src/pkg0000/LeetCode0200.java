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
     * 292. Nim 游戏
     * @param n
     * @return
     */
    public boolean canWinNim(int n) {
        return n % 4 != 0;
    }
}
