package pkg1000;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeetCode1500 {

    /**
     * 模数，防止大数溢出，题目通常要求对 10^9+7 取模
     */
    private static final int MOD = 1_000_000_007;

    /**
     * 1502. 判断能否形成等差数列
     * @param arr
     * @return
     */
    public boolean canMakeArithmeticProgression(int[] arr) {
        if (arr == null || arr.length < 2) {
            return false;
        }
        Arrays.sort(arr);
        int n = arr.length, d = arr[1] - arr[0];
        for (int i = 2; i < n; i++) {
            if (arr[i] - arr[i - 1] != d) {
                return false;
            }
        }
        return true;
    }

    /**
     * 1520. 最多的不重叠子字符串
     * @param s
     * @return
     */
    public List<String> maxNumOfSubstrings(String s) {
        // ============================================================
        // 第一步：预处理每个字符的第一次和最后一次出现位置
        // 用 HashMap 记录每个字符的出现范围：
        //   key   -> 字符本身
        //   value -> int[2]，value[0] 为第一次出现位置，value[1] 为最后一次出现位置
        // ============================================================
        Map<Character, int[]> charIdx = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!charIdx.containsKey(ch)) {
                // 第一次遇到该字符，起始位置和结束位置都初始化为当前下标 i
                charIdx.put(ch, new int[]{i, i});
            } else {
                // 该字符已存在，只更新结束位置为当前下标 i（最终即为最后一次出现的位置）
                charIdx.get(ch)[1] = i;
            }
        }

        // ============================================================
        // 第二步：构建所有合法的区间
        // 合法区间的定义：
        //   如果子字符串中包含某个字符，那么该子字符串必须包含该字符在整串中的所有出现位置
        //   换句话说，区间内不能出现"部分包含"的情况
        // 构建方法：对每个字符的初始区间 [l, r] 进行扩展，
        //   扫描区间内的每个字符，如果该字符的出现范围超出了当前区间，就扩展区间，
        //   直到区间内所有字符的完整出现都被包含为止
        // ============================================================
        List<int[]> valid = new ArrayList<>();

        for (Map.Entry<Character, int[]> entry : charIdx.entrySet()) {
            // 获取当前字符的初始区间 [l, r]
            int[] range = entry.getValue();
            int l = range[0], r = range[1];

            // nl 和 nr 是两个扫描指针，都从 l 开始
            // nl 向左扫描（递减），nr 向右扫描（递增）
            // 通过双指针逐步扩展区间，确保区间内所有字符的完整出现都被包含
            int nl = l, nr = l;

            // 循环条件：只要还有未扫描的位置（nl >= l 表示左边还有未扫描的，nr <= r 表示右边还有未扫描的）
            while (nl >= l || nr <= r) {
                // 优先处理左边的指针，如果左边还有未扫描的位置，取 nl；否则取 nr
                int idx = (nl >= l) ? nl : nr;

                // 获取当前扫描位置字符的起始和结束下标
                int[] currIdx = charIdx.get(s.charAt(idx));
                // 该字符的起始和结束位置
                int l_t = currIdx[0], r_t = currIdx[1];

                // 如果该字符的起始位置在当前区间左侧之外，说明区间需要向左扩展
                // 扩展后 l 变小，可能会覆盖更多字符，需要继续检查
                if (l_t < l) {
                    l = l_t;
                }

                // 如果该字符的结束位置在当前区间右侧之外，说明区间需要向右扩展
                // 扩展后 r 变大，可能会覆盖更多字符，需要继续检查
                if (r_t > r) {
                    r = r_t;
                }

                // 如果当前处理的是左指针位置，左指针向左移动一位（确保 nl 在合法范围内）
                if (idx == nl) {
                    nl--;
                }

                // 如果当前处理的是右指针位置，右指针向右移动一位（确保 nr 在合法范围内）
                if (idx == nr) {
                    nr++;
                }
            }

            // 区间不再扩展，得到一个合法区间 [l, r]
            // 该区间内的所有字符的完整出现都被包含在区间内
            valid.add(new int[]{l, r});
        }

        // ============================================================
        // 第三步：按右端点升序排序
        // 为后续贪心算法做准备：优先选择结束位置早的区间，可以留出更多空间给后续区间
        // ============================================================
        valid.sort((a, b) -> a[1] - b[1]);

        // ============================================================
        // 第四步：贪心选择互不重叠的区间
        // 经典的活动选择问题：按结束时间排序后，每次选择与上一个选中区间不重叠且结束最早的区间
        // 这样可以最大化选中区间的数量
        // ============================================================
        List<String> ans = new ArrayList<>();
        // 记录上一个选中区间的结束位置，初始为 -1 表示还没有选中任何区间
        int end = -1;

        for (int[] interval : valid) {
            int l = interval[0], r = interval[1];
            // 如果当前区间的起始位置 > 上一个选中区间的结束位置，说明不重叠，可以选中
            if (l > end) {
                // 截取子字符串并加入结果
                ans.add(s.substring(l, r + 1));
                // 更新上一个选中区间的结束位置
                end = r;
            }
        }

        return ans;
    }

    /**
     * 1541. 平衡括号字符串的最少插入次数
     * @param s
     * @return
     */
    public int minInsertions(String s) {
        // 总共需要插入的括号数（最终答案）
        int insertions = 0;

        // 当前「待匹配」的左括号 '(' 数量
        // 每遇到一个 '(' 就 +1，每成功配对一对括号就 -1
        int leftCnt = 0;

        int len = s.length();
        int idx = 0;  // 当前扫描的下标

        while (idx < len) {
            char c = s.charAt(idx);

            if (c == '(') {
                // ========== 遇到左括号 ==========
                // 直接计数，等待后续的右括号来匹配
                leftCnt++;
                idx++;
            } else {
                // ========== 遇到右括号 ')' ==========
                // 题目要求每个 '(' 必须匹配两个连续的 ')'，即 "(...))"
                // 所以遇到右括号时，需要先看它后面是否还有另一个 ')'

                // 第一步：处理「左括号是否够用」
                if (leftCnt > 0) {
                    // 有待匹配的左括号，消耗一个来配对
                    leftCnt--;
                } else {
                    // 没有左括号可用，说明这个右括号「无主」
                    // 必须插入一个 '(' 来配对，插入数 +1
                    insertions++;
                }

                // 第二步：处理「右括号是否凑够两个」
                if (idx < len - 1 && s.charAt(idx + 1) == ')') {
                    // 下一个字符也是 ')'，凑齐了两个，构成完整的 "))"
                    // 一次跳过两个字符，不需要额外插入
                    idx += 2;
                } else {
                    // 下一个字符不是 ')'（或者是字符串末尾）
                    // 只有一个右括号，缺少一个，必须插入一个 ')' 来凑齐
                    insertions++;
                    idx++;  // 只跳过当前这一个字符
                }
            }
        }

        // ========== 循环结束后，处理剩余的左括号 ==========
        // leftCount > 0 说明还有左括号没有被匹配
        // 每个左括号需要两个右括号 "))" 来闭合
        // 所以需要插入 leftCount * 2 个右括号
        insertions += leftCnt * 2;

        return insertions;
    }

    /**
     * 1569. 将子数组重新排序得到同一个二叉搜索树的方案数
     * 
     * 计算与给定数组生成相同二叉搜索树（BST）的不同排列方案数（不含自身）。
     *
     * 核心思路：
     *   1. 根据数组 nums 构建一棵二叉搜索树（BST）。
     *   2. 对于每个节点，其左右子树的插入顺序可以交错，但必须保持各自子树内部的相对顺序。
     *      若左子树有 L 个节点，右子树有 R 个节点，则交错方案数为 C(L+R, L)。
     *   3. 递归地对每个节点计算：当前子树的合法排列数 = C(L+R, L) × 左子树方案数 × 右子树方案数。
     *   4. 最终答案需减去 1（排除掉原数组本身这一种排列）。
     * 
     * @param nums
     * @return
     */
    public int numOfWays(int[] nums) {
        int n = nums.length;

        // 组合数表（杨辉三角），c[i][j] 表示 C(i, j)，即从 i 个元素中选 j 个的方案数
        long[][] C = new long[n][n];

        // 只有一个节点，不存在其他排列，返回 0
        if (n == 1) {
            return 0;
        }

        // 第一步：预计算组合数表（杨辉三角），用于后续快速查询 C(n, k)
        C[0][0] = 1;
        for (int i = 1; i < n; i++) {
            // C(i, 0) = 1
            C[i][0] = 1;
            for (int j = 1; j < n; j++) {
                // 递推公式：C(i, j) = C(i-1, j-1) + C(i-1, j)
                C[i][j] = (C[i - 1][j - 1] + C[i - 1][j]) % MOD;
            }
        }

        // 第二步：根据数组顺序构建二叉搜索树
        // 第一个元素作为根节点
        TreeNode root = new TreeNode(nums[0]);
        for (int i = 1; i < n; i++) {
            // 将每个元素按 BST 规则插入树中
            numOfWaysInsert(root, nums[i]);
        }

        // 第三步：后序遍历（DFS），自底向上计算每个子树的合法排列数
        numOfWaysDfs(root, C);

        // 第四步：root.ans 包含了原数组本身，题目要求排除自身，所以减 1
        // 加 MOD 再取模是为了防止减 1 后出现负数
        return (root.ans - 1 + MOD) % MOD;
    }

    /**
     * 将 value 插入到以 root 为根的 BST 中。
     * 插入过程中，沿途经过的每个节点的 size 都加 1（用于记录子树节点总数）。
     */
    private void numOfWaysInsert(TreeNode root, int value) {
        TreeNode cur = root;
        while (true) {
            // 每经过一个节点，该节点的子树节点数 +1
            cur.size++;

            if (value < cur.size) {
                // 值比当前节点小，应插入左子树
                if (cur.left == null) {
                    // 左子节点为空，直接插入
                    cur.left = new TreeNode(value);
                    return;
                }
                // 继续向左子树深入
                cur = cur.left;
            } else {
                // 值比当前节点大（或相等），应插入右子树
                if (cur.right == null) {
                    // 右子节点为空，直接插入
                    cur.right = new TreeNode(value);
                    return;
                }
                // 继续向右子树深入
                cur = cur.right;
            }
        }
    }

    /**
     * 后序遍历 DFS，计算以 node 为根的子树中，能生成相同 BST 结构的排列方案数。
     *
     * 递归逻辑：
     *   1. 先递归计算左子树和右子树各自的方案数。
     *   2. 当前节点的方案数 = C(左子树节点数 + 右子树节点数, 左子树节点数)
     *                          × 左子树方案数 × 右子树方案数。
     *   3. 组合数 C(L+R, L) 表示：在保持左右子树内部相对顺序不变的前提下，
     *      将左右子树的插入序列交错合并的方案数。
     */
    private void numOfWaysDfs(TreeNode node, long[][] C) {
        if (node == null) {
            return;
        }

        // 后序遍历：先处理左右子树
        numOfWaysDfs(node.left, C);
        numOfWaysDfs(node.right, C);

        // 获取左子树的节点数和方案数
        int lSize = node.left == null ? 0 : node.left.size;
        // 空子树方案数为 1（乘法单位元）
        int lAns = node.left == null ? 1 : node.left.ans;

        // 获取右子树的节点数和方案数
        int rSize = node.right == null ? 0 : node.right.size;
        // 空子树方案数为 1
        int rAns = node.right == null ? 0 : node.right.ans;

        // 当前子树的总方案数 = 交错合并方案数 × 左子树方案数 × 右子树方案数
        // c[lsize + rsize][lsize] 即 C(lsize + rsize, lsize)
        node.ans = (int) (C[lSize + rSize][lSize] % MOD * lAns % MOD * rAns % MOD);
    }

    /**
     * 1584. 连接所有点的最小费用
     * @param points
     * @return
     */
    public int minCostConnectPoints(int[][] points) {
        int n = points.length, m = (n * (n - 1)) >> 1;
        int[][] edges = new int[m][3];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int w = Math.abs(points[i][0] - points[j][0]) + Math.abs(points[i][1] - points[j][1]);
                edges[idx][0] = i;
                edges[idx][1] = j;
                edges[idx++][2] = w;
            }
        }
        Arrays.sort(edges, (a, b) -> a[2] - b[2]);

        UnionFind uf = new UnionFind(n);
        int v = 0;
        for (int i = 0; i < m; i++) {
            if (uf.union(edges[i][0], edges[i][1])) {
                v += edges[i][2];
            }
        }
        return uf.getCount() == 1 ? v : -1;
    }

    /**
     * 内部类：并查集 (Union-Find)
     * UnionFind
     */
    class UnionFind {
        private int[] parent;
        // 记录当前连通分量的数量
        private int count;

        public UnionFind(int n) {
            parent = new int[n];
            count = n;
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        /**
         * 查找根节点，带路径压缩
         * @param x
         * @return
         */
        public int find(int x) {
            return parent[x] == x ? x : (parent[x] = find(parent[x]));
        }

        /**
         * 合并两个集合，如果原本不连通则合并并返回 true，否则返回 false
         * @param x
         * @param y
         * @return
         */
        public boolean union(int x, int y) {
            x = find(x);
            y = find(y);
            // 已经在同一个集合，加入会形成环
            if (x == y) {
                return false;
            }

            parent[x] = y;
            count--;
            return true;
        }

        public int getCount() {
            return count;
        }
    }
}

/**
 * 二叉搜索树节点定义。
 * value：节点的值
 * size：以该节点为根的子树中的节点总数（包括自身）
 * ans：以该节点为根的子树中，能生成相同 BST 结构的排列方案数
 */
class TreeNode {
    // 左子节点
    TreeNode left;
    // 右子节点
    TreeNode right;
    // 节点值
    int value;
    // 子树节点总数
    int size;
    // 该子树的合法排列方案数
    int ans;

    TreeNode(int value) {
        this.value = value;
        // 初始化时子树只有自身一个节点
        this.size = 1;
        // ans 将在 DFS 中计算
        this.ans = 0;
    }
}
