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
     * 858. 镜面反射
     * @param p
     * @param q
     * @return
     */
    public int mirrorReflection(int p, int q) {
        // 浮点数比较的精度阈值，用于判断光线是否已经到达墙壁或接收器
        double EPS = 1e-6;

        // (x, y) 表示光线当前的坐标位置，初始从左下角 (0, 0) 出发
        double x = 0.0, y = 0.0;

        // (rx, ry) 表示光线当前的运动方向向量
        // 初始方向为向右 p、向上 q，即从 (0,0) 射向 (p, q)
        double rx = p, ry = q;

        // 循环模拟光线的反射过程，直到光线到达某个接收器为止
        // 接收器位置：
        //   0号接收器：右下角 (p, 0)
        //   1号接收器：右上角 (p, p)
        //   2号接收器：左上角 (0, p)
        // 循环继续的条件是：光线尚未到达任何一个接收器
        while (!
            // 光线到达右墙 x == p，且同时到达 y == 0 或 y == p（即接收器 0 或 1）
            (close(x, p, EPS) && (close(y, 0.0, EPS) || close(y, p, EPS))
            // 或者光线到达左墙 x == 0，且同时到达 y == p（即接收器 2）
            || close(x, 0.0, EPS) && close(y, p, EPS))
        ) {
            // 计算光线沿当前方向运动时，到达任意一面墙壁所需的最小时间 t
            // t 是一个比例系数，表示沿方向向量 (rx, ry) 前进的倍数
            double t = 1e9; // 初始化为一个极大值

            // 四面墙壁分别对应四个方程，求解 t：
            // 左墙  x + rx * t == 0  =>  t = -x / rx
            if ((-x / rx) > EPS) {
                t = Math.min(t, -x / rx);
            }
            // 下墙  y + ry * t == 0  =>  t = -y / ry
            if ((-y / ry) > EPS) {
                t = Math.min(t, -y / ry);
            }
            // 右墙  x + rx * t == p  =>  t = (p - x) / rx
            if (((p - x) / rx) > EPS) {
                t = Math.min(t, (p - x) / rx);
            }
            // 上墙  y + ry * t == p  =>  t = (p - y) / ry
            if (((p - y) / ry) > EPS) {
                t = Math.min(t, (p - y) / ry);
            }

            // 沿方向向量前进 t 倍，更新光线位置到碰到的那面墙壁上
            x += rx * t;
            y += ry * t;

            // 如果光线碰到了左墙或右墙（x == 0 或 x == p），水平方向反转
            if (close(x, p, EPS) || close(x, 0.0, EPS)) {
                rx *= -1;
            }
            // 如果光线碰到了上墙或下墙（y == 0 或 y == p），垂直方向反转
            if (close(y, p, EPS) || close(y, 0.0, EPS)) {
                ry *= -1;
            }
        }

        // 循环结束后，光线已经到达某个接收器，根据最终坐标判断是哪一个
        // 右上角 -> 接收器 1
        if (close(x, p, EPS) && close(y, p, EPS)) {
            return 1;
        }

        // 右下角 -> 0，左上角 -> 2
        return close(x, p, EPS) ? 0 : 2;
    }

    // 判断两个浮点数是否在精度范围内相等
    private boolean close(double x, double y, double EPS) {
        return Math.abs(x - y) < EPS;
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
}
