package pkg3000;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class LeetCode3500 {

    /**
     * 3568. 清理教室的最少移动
     * @param classroom
     * @param energy
     * @return
     */
    public int minMoves(String[] classroom, int energy) {
        // 定义四个移动方向：右、左、上、下
        final int[][] dict = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        // 网格行数，列数
        int m = classroom.length, n = classroom[0].length();

        // id[i][j] 用于存储该位置如果是灯(L)，对应的二进制掩码位
        // 例如：第0盏灯对应 1<<0 (001)，第1盏灯对应 1<<1 (010)
        int[][] id = new int[m][n];
        // 起点坐标，灯的总数量
        int startI = 0, startJ = 0, cnt = 0;

        // 1. 预处理：扫描地图，记录起点位置，并为每盏灯分配唯一的二进制位
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                char c = classroom[i].charAt(j);
                if (c == 'S') {
                    startI = i;
                    startJ = j;
                } else if (c == 'L') {
                    // 使用位运算 1 << cnt 为当前灯分配一个独立的位
                    id[i][j] = 1 << cnt++;
                }
            }
        }

        // full 代表所有灯都被收集时的目标状态掩码
        // 例如有3盏灯，full = 1<<3 = 8 (二进制 1000)，目标状态是 full-1 = 7 (二进制 111)
        int full = 1 << cnt;

        // 2. 定义状态访问数组 bestEnergy[i][j][mask]
        // 记录到达坐标 (i, j) 且收集状态为 mask 时，剩余的最大能量值
        // 初始化为 -1 表示该状态尚未被访问过
        int[][][] bestEnergy = new int[m][n][full];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                Arrays.fill(bestEnergy[i][j], -1);
            }
        }

        // 初始化起点状态：位于起点，未收集任何灯(mask=0)，能量满格
        bestEnergy[startI][startJ][0] = energy;

        // 定义 BFS 队列中的节点信息类
        class Info {
            // 当前坐标，当前已收集灯的状态掩码，当前剩余能量，当前已走的步数
            int i, j, mask, e, steps;
            Info(int i, int j, int mask, int e, int steps) {
                this.i = i;
                this.j = j;
                this.mask = mask;
                this.e = e;
                this.steps = steps;
            }
        }

        // 3. 开始 BFS 搜索
        Queue<Info> q = new ArrayDeque<>();
        q.offer(new Info(startI, startJ, 0, energy, 0));
        while (!q.isEmpty()) {
            Info curr = q.poll();

            // 终止条件：如果当前掩码等于所有灯都收集的状态，直接返回步数
            // 因为 BFS 的特性，第一次达到该状态时步数一定是最小的
            if (curr.mask == full - 1) {
                return curr.steps;
            }

            // 剪枝：如果当前能量耗尽，无法继续移动，跳过该节点
            if (curr.e == 0) {
                continue;
            }

            // 尝试向四个方向移动
            for (int i = 0; i < 4; i++) {
                int ni = curr.i + dict[i][0], nj = curr.j + dict[i][1];

                // 边界检查与障碍物检查：越界或遇到墙壁 'X' 则跳过
                if (ni < 0 || ni >= m || nj < 0 || nj >= n || classroom[ni].charAt(nj) == 'X') {
                    continue;
                }

                // 计算移动后的新能量
                // 如果遇到充电站 'R'，能量重置为满格 energy；否则消耗 1 点能量
                int ne = (classroom[ni].charAt(nj) == 'R') ? energy : curr.e - 1;

                // 计算移动后的新掩码
                // 如果该位置是灯，通过位或运算 | 将对应的位点亮；如果不是灯，id[ni][nj]为0，mask不变
                int nmask = curr.mask | id[ni][nj];

                // 核心剪枝逻辑：
                // 只有当新状态的剩余能量 ne 大于之前记录的 bestEnergy 时才入队
                // 这意味着我们只保留到达同一位置、同一收集状态下“能量最充沛”的路径
                if (ne > bestEnergy[ni][nj][nmask]) {
                    bestEnergy[ni][nj][nmask] = ne;
                    q.offer(new Info(ni, nj, nmask, ne, curr.steps + 1));
                }
            }
        }

        // 如果队列为空仍未找到解，说明无法收集所有灯，返回 -1
        return -1;
    }
}
