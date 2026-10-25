package pkg1000;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LeetCode1200 {
    /**
     * 1220. 统计元音字母序列的数目（动态规划：空间优化）
     * @param n
     * @return
     */
    public int countVowelPermutation(int n) {
        // 数据太大，需要对 MOD 取余
        final int MOD = 1_000_000_007;

        // 元英字母
        final char[] VOWEL = {'a', 'e', 'i', 'o', 'u'};

        // dp[i][j] -> 第 i 个字符以 VOWEL[j] 开始时的序列数目
        int[] dp = new int[5];

        // dp 边界初始化
        Arrays.fill(dp, 1);

        // dp 状态转移循环
        for (int i = n - 2; i >= 0; i--) {
            int[] prevDp = dp.clone();
            for (int j = 0; j < 5; j++) {
                // 当前序列结尾的字符
                char c = VOWEL[j];
                switch (c) {
                    case 'a' -> dp[j] = prevDp[1];
                    case 'e' -> dp[j] = (prevDp[0] + prevDp[2]) % MOD;
                    case 'i' -> dp[j] = ((prevDp[0] + prevDp[1]) % MOD + (prevDp[3] + prevDp[4]) % MOD) % MOD;
                    case 'o' -> dp[j] = (prevDp[2] + prevDp[4]) % MOD;
                    case 'u' -> dp[j] = prevDp[0];
                    default -> dp[j] = 0;
                }
            }
        }

        int ans = 0;
        for (int j = 0; j < 5; j++) {
            ans = (ans + dp[j]) % MOD;
        }
        return ans;
    }

    /**
     * 1254. 统计封闭岛屿的数目
     * 
     * 计算网格中"封闭岛屿"的数量。
     *
     * 定义：
     *   - 0 表示陆地，1 表示水。
     *   - 岛屿是由 0 组成的四连通区域。
     *   - 封闭岛屿：岛屿中的所有陆地都不与网格边界接触（即完全被水包围）。
     *
     * 核心思路（并查集）：
     *   1. 将所有边界上的陆地都合并到同一个"虚拟集合"中（以 grid[0][0] 为代表）。
     *   2. 将内部相邻的陆地互相合并。
     *   3. 统计所有陆地所属的连通分量个数，再减去与边界相连的那一个分量。
     * 
     * @param grid
     * @return
     */
    public int closedIsland(int[][] grid) {
        int m = grid.length, n = grid[0].length;

        // 创建并查集，总节点数为 m * n，每个格子对应一个节点
        // 节点编号：grid[i][j] 对应编号 i * n + j
        UnionFind uf = new UnionFind(m * n);

        // ========== 第一步：将边界上的陆地全部合并到 grid[0][0] 所在的集合 ==========
        // 这样所有与边界相连的陆地都会拥有相同的根节点，便于后续排除

        // 处理左右两列的边界
        for (int i = 0; i < m; i++) {
            // 左边界：第 0 列
            if (grid[i][0] == 0) {
                // 将 (i, 0) 与 (0, 0) 合并，编号分别为 i*n+0 和 0
                uf.uni(i * n, 0);
            }
            // 右边界：第 n-1 列
            if (grid[i][n - 1] == 0) {
                // 将 (i, n-1) 与 (0, 0) 合并，编号分别为 i*n+(n-1) 和 0
                uf.uni(i * n + n - 1, 0);
            }
        }

        // 处理上下两行的边界（注意左右两角已处理过，所以 j 从 1 到 n-2）
        for (int j = 1; j < n - 1; j++) {
            // 上边界：第 0 行
            if (grid[0][j] == 0) {
                // 将 (0, j) 与 (0, 0) 合并，编号分别为 j 和 0
                uf.uni(j, 0);
            }
            // 下边界：第 m-1 行
            if (grid[m - 1][j] == 0) {
                // 将 (m-1, j) 与 (0, 0) 合并，编号分别为 (m-1)*n+j 和 0
                uf.uni((m - 1) * n + j, 0);
            }
        }

        // ========== 第二步：遍历内部，将相邻的陆地合并 ==========
        // 只需检查上方和左方邻居即可（避免重复合并）
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                // 当前格子是陆地
                if (grid[i][j] == 0) {
                    // 如果上方也是陆地，合并
                    if (i > 0 && grid[i - 1][j] == 0) {
                        uf.uni(i * n + j, (i - 1) * n + j);
                    }
                    // 如果左方也是陆地，合并
                    if (j > 0 && grid[i][j - 1] == 0) {
                        uf.uni(i * n + j, i * n + j - 1);
                    }
                }
            }
        }

        // ========== 第三步：统计封闭岛屿数量 ==========
        // 收集所有陆地格子所属的根节点（即连通分量代表）
        Set<Integer> cnt = new HashSet<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0) {
                    // 将该陆地所属连通分量的根节点加入集合
                    cnt.add(uf.find(i * n + j));
                }
            }
        }

        // 所有连通分量的总数
        int total = cnt.size();

        // 如果存在与边界相连的陆地（即 grid[0][0] 是陆地），
        // 那么它所在的那个连通分量不是封闭岛屿，需要排除
        if (cnt.contains(uf.find(0))) {
            total--;
        }

        return total;
    }
}

/**
 * 并查集（Union-Find）数据结构，支持路径压缩和按秩合并。
 *
 * 功能：
 *   - find(x)：查找 x 所在集合的代表元素（根节点），带路径压缩。
 *   - uni(x, y)：合并 x 和 y 所在的集合，按秩（树高）优化。
 */
class UnionFind {

    // parent[i] = i 的父节点，根节点的父节点是自身
    private int[] parent;
    // rank[i] = 以 i 为根的树的高度（近似秩）
    private int[] rank;

    /**
     * 初始化并查集，共 n 个节点，每个节点自成一个集合。
     */
    public UnionFind(int n) {
        this.parent = new int[n];
        for (int i = 0; i < n; i++) {
            // 初始时每个节点的父节点是自身
            parent[i] = i;
        }
        // 初始秩全部为 0
        this.rank = new int[n];
    }

    /**
     * 合并 x 和 y 所在的集合。
     *
     * 按秩合并策略：
     *   - 秩大的树作为新根，保持树的高度尽可能小。
     *   - 两树秩相同时，任选一个作为根，并将根的秩 +1。
     */
    public void uni(int x, int y) {
        // 找到 x 的根
        int rootX = find(x);
        // 找到 y 的根
        int rootY = find(y);
        // 不在同一集合才需要合并
        if (rootX == rootY) {
            return;
        }

        if (rank[rootX] > rank[rootY]) {
            // rootx 的树更高，让 rooty 挂到 rootx 下
            parent[rootY] = rootX;
        } else if (rank[rootX] < rank[rootY]) {
            // rooty 的树更高，让 rootx 挂到 rooty 下
            parent[rootX] = rootY;
        } else {
            // 两树高度相同，rootx 作为新根，rootx 的秩 +1
            parent[rootY] = rootX;
            rank[rootX]++;
        }
    }

    /**
     * 查找 x 所在集合的根节点，同时进行路径压缩。
     *
     * 路径压缩：将查找路径上的所有节点直接挂到根节点下，
     * 使后续查找操作接近 O(1)。
     */
    public int find(int x) {
        if (parent[x] != x) {
            // 递归压缩路径
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }
    
}
