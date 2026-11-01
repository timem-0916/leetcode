package pkg0000;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class LeetCode0800 {

    /**
     * 812. 最大三角形面积
     * @param points
     * @return
     */
    public double largestTriangleArea(int[][] points) {
        int n = points.length;
        double maxArea = 0.0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    maxArea = Math.max(maxArea, triangleArea(points[i][0], points[i][1], points[j][0], points[j][1], points[k][0], points[k][1]));
                }
            }
        }
        return maxArea;
    }

    /**
     * 根据三个顶点坐标，求三角形面积
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @return
     */
    private double triangleArea(int x1, int y1, int x2, int y2, int x3, int y3) {
        return 0.5 * Math.abs(x1 * y2 + x2 * y3 + x3 * y1 - x1 * y3 - x2 * y1 - x3 * y2);
    }

    /**
     * 835. 图像重叠
     * @param img1
     * @param img2
     * @return
     */
    public int largestOverlap(int[][] img1, int[][] img2) {
        // 图像是 n x n 的正方形
        int n = img1.length;

        // G1 存储 img1 中所有值为 1 的坐标点, G2 存储 img2 中所有值为 1 的坐标点
        List<Point> G1 = new ArrayList<>(), G2 = new ArrayList<>();

        // 遍历整个 n x n 的网格，提取所有值为 1 的点
        for (int i = 0; i < n * n; i++) {
            // i/n 是行号，i%n 是列号（将二维坐标展平为一维遍历）
            if (img1[i/n][i%n] == 1) {
                G1.add(new Point(i/n, i%n));
            }
            if (img2[i/n][i%n] == 1) {
                G2.add(new Point(i/n, i%n));
            }
        }

        // 将 img2 的所有 1 的坐标放入 HashSet，用于 O(1) 快速查找
        // 这样在后续判断"某个坐标在 img2 中是否为 1"时，不需要遍历，直接查表即可
        Set<Point> set = new HashSet<>(G2);

        // 记录最大重叠数
        int ans = 0;

        // seen 用于记录已经计算过的平移向量（delta）
        // 避免对相同的平移向量重复计算，起到剪枝优化的作用
        Set<Point> seen = new HashSet<>();

        // 枚举 img1 中每个 1 和 img2 中每个 1 的组合
        // 它们之间的差值 (g2 - g1) 就是一种"把 img1 平移到 img2 上"的平移向量
        for (Point g1 : G1) {
            for (Point g2 : G2) {
                // 计算平移向量：img1 中的点 g1 需要移动多少才能到达 g2 的位置
                Point delta = new Point(g2.x - g1.x, g2.y - g1.y);

                // 如果这个平移向量还没计算过，才进行计算（去重剪枝）
                if (!seen.contains(delta)) {
                    seen.add(delta);
                    // 当前平移方案下的重叠数
                    int cand = 0;
                    // 将 img1 中所有 1 的点都按 delta 平移，检查平移后是否落在 img2 的 1 上
                    for (Point p: G1) {
                        if (set.contains(new Point(p.x + delta.x, p.y + delta.y))) {
                            cand++;
                        }
                    }
                    // 更新全局最大重叠数
                    ans = Math.max(ans, cand);
                }
            }
        }

        return ans;
    }

    /**
     * 836. 矩形重叠
     * @param rec1
     * @param rec2
     * @return
     */
    public boolean isRectangleOverlap(int[] rec1, int[] rec2) {
        return rec1[2] > rec2[0] && rec1[0] < rec2[2] && rec1[3] > rec2[1] && rec1[1] < rec2[3];
    }

    /**
     * 841. 钥匙和房间
     * @param rooms
     * @return
     */
    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        int n = rooms.size();
        boolean[] open = new boolean[n];
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(0);
        while (!q.isEmpty()) {
            int curr = q.poll();
            open[curr] = true;
            for (int next : rooms.get(curr)) {
                if (!open[next]) {
                    q.offer(next);
                }
            }
        }

        for (int i = 0; i < n; i++) {
            if (!open[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 856. 括号的分数
     * @param s
     * @return
     */
    public int scoreOfParentheses(String s) {
        int bal = 0;         // 平衡计数器：遇到 '(' +1，遇到 ')' -1
        int n = s.length();  // 字符串长度
        int res = 0;         // 最终得分累加器

        for (int i = 0; i < n; i++) {
            // 先更新平衡计数器，反映当前字符对嵌套层数的影响
            bal += (s.charAt(i) == '(' ? 1 : -1);

            // 核心判断：检测是否出现核心单元 "()"
            // 当当前字符是 ')' 且前一个字符是 '(' 时，构成最内层的 "()"
            // 这个 "()" 的得分取决于它外面包裹了多少层括号
            if (s.charAt(i) == ')' && s.charAt(i - 1) == '(') {
                // 此时 bal 的值等于 "()" 外层的括号对数
                // 每层外层括号使得分翻倍，所以贡献为 2^bal
                // 1 << bal 即 2 的 bal 次方（位运算左移）
                // 例如：
                //   "()"        → bal=0, 贡献 2^0 = 1
                //   "(())"      → bal=1, 贡献 2^1 = 2
                //   "((()))"    → bal=2, 贡献 2^2 = 4
                res += 1 << bal;
            }
        }

        return res;
    }

    /**
     * 858. 镜面反射（数学）
     * @param p
     * @param q
     * @return
     */
    public int mirrorReflection(int p, int q) {
        // 第一步：求 p 和 q 的最大公约数，用于将问题化简到最简整数比
        // 光线在正方形房间内的反射路径具有周期性，
        // 将 p 和 q 同时除以它们的最大公约数，相当于把整个反射过程
        // 压缩到最小的"基本单元"中分析，不影响最终到达的接收器编号
        int g = gcd(p, q);

        // 将 p 和 q 化简为互质的最简形式
        p /= g;
        q /= g;

        // 第二步：对化简后的 p 和 q 取模 2，只保留奇偶性信息
        // 这是因为光线最终到达哪个接收器，只取决于 p 和 q 的奇偶组合：
        //   - p 为奇数表示光线在水平方向经过了奇数次反射（最终落在右墙）
        //   - p 为偶数表示光线在水平方向经过了偶数次反射（最终落在左墙）
        //   - q 为奇数表示光线在垂直方向经过了奇数次反射（最终落在上墙）
        //   - q 为偶数表示光线在垂直方向经过了偶数次反射（最终落在下墙）
        p %= 2;
        q %= 2;

        // 第三步：根据奇偶性组合判断接收器编号
        // 三种可能的情况：
        if (p == 1 && q == 1) {
            return 1;
        }

        // p奇q偶 → 右下角 → 接收器 0
        // p偶q奇 → 左上角 → 接收器 2
        // 注意：p偶q偶 的情况不可能出现，因为 p 和 q 已经互质，不可能同时为偶数
        return p == 1 ? 0 : 2;
    }

    /**
     * 辗转相除法（欧几里得算法）求两个整数的最大公约数
     * 原理：gcd(a, b) = gcd(b % a, a)，递归直到 a == 0，此时 b 即为最大公约数
     * 时间复杂度：O(log(min(a, b)))
     *
     * @param a 第一个整数
     * @param b 第二个整数
     * @return a 和 b 的最大公约数
     */
    private int gcd(int a, int b) {
        // 递归终止条件：当 a 为 0 时，b 就是最大公约数
        if (a == 0) {
            return b;
        }
        // 递归调用，用 b % a 和 a 继续求 GCD
        return gcd(b % a, a);
    }

    /**
     * 860. 柠檬水找零
     * @param bills
     * @return
     */
    public boolean lemonadeChange(int[] bills) {
        // cnt[0] -> 5, cnt[1] -> 10, cnt[2] -> 20
        int[] cnt = {0, 0, 0};
        int n = bills.length;
        for (int i = 0; i < n; i++) {
            int income = bills[i];
            if (income == 20) {
                cnt[2]++;
                if (cnt[1] > 0 && cnt[0] > 0) {
                    cnt[1]--;
                    cnt[0]--;
                } else if (cnt[0] > 2) {
                    cnt[0] -= 3;
                } else {
                    return false;
                }
            } else if (income == 10) {
                cnt[1]++;
                if (cnt[0] > 0) {
                    cnt[0]--;
                } else {
                    return false;
                }
            } else {
                cnt[0]++;
            }
        }
        return true;
    }

    /**
     * 877. 石子游戏
     * @param piles
     * @return
     */
    public boolean stoneGame(int[] piles) {
        int n = piles.length;
        // dp[i][j] 表示当剩下的石子堆为下标 i 到下标 j 时，即在下标范围 [i,j] 中，当前玩家与另一个玩家的石子数量之差的最大值，注意当前玩家不一定是先手 Alice
        int[][] dp = new int[n][n];
        for (int i = 0; i < n; i++) {
            dp[i][i] = piles[i];
        }

        for (int l = 2; l <= n; l++) {
            for (int i = 0; i <= n - l; i++) {
                int j = i + l - 1;
                dp[i][j] = Math.max(piles[i] - dp[i + 1][j], piles[j] - dp[i][j - 1]);
            }
        }
        return dp[0][n - 1] > 0;
    }

    /**
     * 883. 三维形体投影面积
     * @param grid
     * @return
     */
    public int projectionArea(int[][] grid) {
        // 获取网格的边长 n（题目保证 grid 为 n x n 的正方形网格）
        // xyArea：俯视图（从 z 轴正方向往下看）的投影面积
        // yzArea：侧视图（从 x 轴正方向往左看）的投影面积
        // xzArea：正视图（从 y 轴正方向往前看）的投影面积
        int n = grid.length, xyArea = 0, yzArea = 0, xzArea = 0;

        // 外层循环遍历每一行/列索引 i
        for (int i = 0; i < n; i++) {
            // yzHeight：在第 i 列中，所有行上的最大高度（用于计算 yz 平面投影）
            // xzHeight：在第 i 行中，所有列上的最大高度（用于计算 xz 平面投影）
            int yzHeight = 0, xzHeight = 0;

            // 内层循环遍历每一列/行索引 j
            for (int j = 0; j < n; j++) {
                // 俯视图投影：只要 grid[i][j] > 0，说明该位置有立方体，
                // 从上往下看会贡献 1 个单位的面积（无论堆多高，俯视只看到一个 1x1 的正方形）
                xyArea += grid[i][j] > 0 ? 1 : 0;

                // 侧视图（yz 平面）投影：从 x 轴方向看过去，
                // 对于第 i 列，看到的是该列中所有行的最大高度
                // grid[j][i] 表示第 j 行、第 i 列的高度（注意行列互换，按列取最大值）
                yzHeight = Math.max(yzHeight, grid[j][i]);

                // 正视图（xz 平面）投影：从 y 轴方向看过去，
                // 对于第 i 行，看到的是该行中所有列的最大高度
                // grid[i][j] 表示第 i 行、第 j 列的高度（按行取最大值）
                xzHeight = Math.max(xzHeight, grid[i][j]);
            }

            // 内层循环结束后，累加第 i 列的侧视投影面积和第 i 行的正视投影面积
            yzArea += yzHeight;
            xzArea += xzHeight;
        }

        // 三个投影面的面积之和即为最终答案
        return xyArea + yzArea + xzArea;
    }
}
