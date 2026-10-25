package pkg3000;

public class LeetCode3600 {

    /**
     * 3619. 总价值可以被 K 整除的岛屿数目
     * 
     * 统计总价值能被 k 整除的岛屿数量。
     *
     * 核心思路（DFS 洪水填充）：
     *   1. 遍历网格中的每个格子。
     *   2. 遇到正数格子（陆地）时，用 DFS 探索整个连通岛屿，同时累加岛屿总价值。
     *   3. DFS 过程中将访问过的格子置为 0（标记为已访问），避免重复计算。
     *   4. 如果岛屿总价值能被 k 整除，计数器 +1。
     * 
     * @param grid
     * @param k
     * @return
     */
    public int countIslands(int[][] grid, int k) {
        // 方向数组：表示上下左右四个方向的偏移量
        // dirs[d] 和 dirs[d+1] 组成一个方向向量
        // d=0: (-1, 0) 上
        // d=1: (0, 1)  右
        // d=2: (1, 0)  下
        // d=3: (0, -1) 左
        final int[] DIRS = {-1, 0, 1, 0, -1};

        int m = grid.length, n = grid[0].length, ans = 0;

        // 遍历网格中的每一个格子
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                // 如果当前格子是陆地（值 > 0），说明发现了一个新岛屿
                // 调用 DFS 计算该岛屿的总价值，并判断是否能被 k 整除
                if (grid[i][j] > 0 && countIslandsDfs(grid, i, j, DIRS) % k == 0) {
                    // 满足条件，计数 +1
                    ans++;
                }
            }
        }

        return ans;
    }

    /**
     * 从 (i, j) 出发，用 DFS 探索整个连通岛屿，返回岛屿的总价值。
     *
     * 关键操作：
     *   - 访问当前格子后，将其值置为 0，相当于"淹没"该格子，标记为已访问。
     *   - 这样后续遍历时不会再次进入该格子，保证每个岛屿只被计算一次。
     *
     * @param grid 网格
     * @param i    当前行号
     * @param j    当前列号
     * @return 以 (i, j) 为起点的连通岛屿的总价值
     */
    private long countIslandsDfs(int[][] grid, int i, int j, final int[] DIRS) {
        int m = grid.length, n = grid[0].length;

        // 累加当前格子的价值（注意用 long，防止多个格子价值之和溢出 int）
        long sum = grid[i][j];

        // 将当前格子置为 0，标记为已访问（"淹没"陆地）
        grid[i][j] = 0;

        // 向四个方向探索
        for (int d = 0; d < 4; d++) {
            // 计算邻居格子的坐标
            int x = i + DIRS[d], y = j + DIRS[d + 1];

            // 检查邻居是否在网格范围内，且是未访问的陆地（值 > 0）
            if (x >= 0 && x < m && y >= 0 && y < n && grid[x][y] > 0) {
                // 递归探索邻居格子，将返回的子岛屿价值累加到总和中
                sum += countIslandsDfs(grid, x, y, DIRS);
            }
        }

        // 返回整个连通岛屿的总价值
        return sum;
    }

    /**
     * 3644. 排序排列
     * 
     * 计算将排列排序所需的最小操作代价。
     *
     * 题目约定 nums 是 [0, 1, ..., n-1] 的一个排列。
     * 操作定义：每次可以选择一个"不在正确位置上"的元素 nums[i]（即 nums[i] != i），
     *           将其与任意另一个元素交换，本次操作的代价为 nums[i] 的值。
     * 目标：通过若干次交换使数组变为 [0, 1, 2, ..., n-1]，求最小总代价。
     *
     * 核心思路：
     *   1. 已经在正确位置上的元素（nums[i] == i）不需要移动，跳过。
     *   2. 对于所有错位的元素，只需对其中**值最小**的那个执行一次交换操作，
     *      利用它作为"中转"逐步将所有错位元素归位，总代价即为该最小值。
     *   3. 对所有错位元素的值做按位与（&），最终结果就是这些值的按位与
     * 
     * @param nums
     * @return
     */
    public int sortPermutation(int[] nums) {
        int n = nums.length;

        // res 初始化为最大值，用于后续取错位元素中的按位与
        int res = Integer.MAX_VALUE;

        // 遍历数组，找出所有不在正确位置上的元素
        for (int i = 0; i < n; i++) {
            // 如果 nums[i] 已经在正确位置（值等于下标），无需操作，跳过
            if (nums[i] == i) {
                continue;
            }
            // 对错位元素的值做按位与累积
            // 随着遍历，res 会逐渐收敛到所有错位元素值的"公共二进制位"
            res &= nums[i];
        }

        // 如果 res 仍为初始值 MAX_VALUE，说明没有任何错位元素，数组已经有序
        // 否则返回计算得到的最小代价
        return res == Integer.MAX_VALUE ? 0 : res;
    }

}
