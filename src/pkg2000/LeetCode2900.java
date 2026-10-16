package pkg2000;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.apple.laf.resources.aqua;

public class LeetCode2900 {
    /**
     * 2904. 最短且字典序最小的美丽子字符串
     * @param s
     * @param k
     * @return
     */
    public String shortestBeautifulSubstring(String s, int k) {
        // 特判：如果整个字符串中 1 的个数都不够 k，直接返回空
        if (s.replace("0", "").length() < k) {
            return "";
        }

        int n = s.length(), cnt1 = 0;
        // 初始化为整个字符串，作为最坏情况下的兜底
        String ans = s;

        for (int l = 0, r = 0; r < n; r++) {
            // 1. 扩大窗口：将右边界字符加入统计
            if (s.charAt(r) == '1') {
                cnt1++;
            }

            // 2. 收缩窗口：如果 1 的个数超过 k，或者左边界是 '0'（为了追求最短）
            while (cnt1 > k || s.charAt(l) == '0') {
                if (s.charAt(l++) == '1') {
                    cnt1--;
                }
            }

            // 3. 更新答案：当窗口内恰好有 k 个 1 时
            if (cnt1 == k) {
                String temp = s.substring(l, r + 1);
                // 优先比较长度，长度相同再比较字典序（Java 的 compareTo 天然支持字典序比较）
                if (temp.length() < ans.length() || (temp.length() == ans.length() && temp.compareTo(ans) < 0)) {
                    ans = temp;
                }
            }
        }

        return ans;
    }

    /**
     * 2913. 子数组不同元素数目的平方和 I
     * @param heights
     * @param queries
     * @return
     */
    public int sumCounts(List<Integer> nums) {
        int n = nums.size(), ans = 0;

        // 枚举 nums[i...j] 子数组
        for (int i = 0; i < n; i++) {
            // 存储当前子数组中的元素
            Set<Integer> set = new HashSet<>();
            for (int j = i; j < n; j++) {
                set.add(nums.get(j));
                int s = set.size();
                ans += s * s;
            }
        }

        return ans;
    }

    /**
     * 2916. 子数组不同元素数目的平方和 II
     * 求所有子数组的“不同元素个数”之和。
     *
     * 思路：按右端点 i 从左到右扫描，维护以 i 结尾的所有子数组的不同元素个数之和 cur。
     *
     * 关键观察：
     *   当新元素 A[i] 加入时，只有那些“左端点在上一次 A[i] 出现位置之后”的子数组，
     *   才会因为 A[i] 而新增一个不同元素。
     *
     *   设 A[i] 上一次出现的位置是 prev，则左端点 j ∈ [prev+1, i] 的子数组
     *   都会 +1。用 BIT 对这些位置区间加 1，就能快速求出增量 q。
     *
     * 递推：
     *   q = bit.query(j, i)          // 有多少个以 i 结尾的子数组新增了 A[i]
     *   cur = cur + 2*q + (i - j + 1) // 展开 (v+1)^2 - v^2 = 2v + 1 对所有受影响位置求和
     *   res += cur
     *
     * 时间复杂度 O(n log n)，空间 O(n)。
     */
    public int sumCounts(int[] nums) {
        int n = nums.length;
        // 最终答案：所有子数组不同元素个数之和
        long res = 0;
        // 当前以 i 结尾的所有子数组的不同元素个数之和
        long cur = 0;

        // BIT 大小要能覆盖下标 0..n，所以开 n+10 留余量
        BIT bit = new BIT(n + 10);

        // 记录每个值上一次出现的下标
        Map<Integer, Integer> seen = new HashMap<>();

        long mod = 1_000_000_007;

        for (int i = 0; i < n; i++) {
            int num = nums[i];

            // j = 上一次出现位置 + 1，即“新增贡献的左端点起点”
            // 如果 num 没出现过，seen 返回 -1，j = 0，表示所有左端点都受影响
            int j = seen.getOrDefault(num, -1) + 1;

            // q = 当前 BIT 中 [j, i] 的和，也就是这些位置当前的“不同元素个数”之和
            // 但注意：此时还没把 A[i] 加进去，所以 q 其实是“加之前”的值
            long q = bit.query(j, i);

            // 把 A[i] 的影响加进去：
            //   对每个受影响的左端点 v，贡献从 v^2 变成 (v+1)^2，增量 = 2v + 1
            //   对所有受影响位置求和：2 * Σv + 个数 = 2*q + (i - j + 1)
            cur = (cur + q * 2 + i - j + 1) % mod;

            // 累加到总答案
            res = (res + cur) % mod;

            // 真正在 BIT 上把 [j, i] 区间加 1，表示这些左端点的子数组现在多了一个不同元素 A[i]
            bit.add(j, i, 1);

            // 更新 A[i] 的最新出现位置
            seen.put(num, i);
        }

        return (int) res;
    }

    /**
     * 2940. 找到 Alice 和 Bob 可以相遇的建筑
     * @param heights
     * @param queries
     * @return
     */
    public int[] leftmostBuildingQueries(int[] heights, int[][] queries) {
        int n = heights.length;
        // 1. 初始化线段树，用于维护区间内的最大高度
        int[] segmentTree = new int[n * 4];
        build(segmentTree, 0, n - 1, 0, heights);

        int m = queries.length;
        int[] ans = new int[m];
        for (int i = 0; i < m; i++) {
            int a = queries[i][0], b = queries[i][1];

            // 2. 逻辑预处理：确保 a 是较小的索引，b 是较大的索引
            // 因为题目要求从 a 到 b，如果 a > b，则相当于在 b 的左侧寻找
            // 统一处理为：在 b 的右侧（或 b 本身）寻找目标
            if (a > b) {
                int temp = a;
                a = b;
                b = temp;
            }

            // 3. 快速判断：如果 a == b，或者 a 的高度已经小于 b 的高度
            // 那么 b 本身就是满足条件的最左侧建筑，无需查询线段树
            if (a == b || heights[a] < heights[b]) {
                ans[i] = b;
                continue;
            }

            // 4. 核心查询：在 [b + 1, n - 1] 区间内，寻找第一个高度 > heights[a] 的建筑
            // 如果找不到，query 方法会返回 -1
            ans[i] = query(segmentTree, b + 1, heights[a], 0, n - 1, 0);
        }

        return ans;
    }

    /**
     * 构建线段树（区间最大值）
     * @param segmentTree 线段树数组
     * @param l 当前节点管理的区间左端点
     * @param r 当前节点管理的区间右端点
     * @param rt 当前节点在 segmentTree 数组中的索引
     * @param heights 原始高度数组
     */
    private void build(final int[] segmentTree, int l, int r, int rt, int[] heights) {
        // 递归终止条件：到达叶子节点，直接存储原数组的高度
        if (l == r) {
            segmentTree[rt] = heights[l];
            return;
        }
        // 计算中点，使用位运算 >> 1 代替除以 2
        int m = (l + r) >> 1;
        // 递归构建左子树和右子树
        build(segmentTree, l, m, rt * 2 + 1, heights);
        build(segmentTree, m + 1, r, rt * 2 + 2, heights);
        // 核心逻辑：当前节点的值 = 左右子树的最大值
        segmentTree[rt] = Math.max(segmentTree[rt * 2 + 1], segmentTree[rt * 2 + 2]);
    }

    /**
     * 在线段树中查询：从 pos 位置开始，寻找第一个高度 > val 的索引
     * @param segmentTree 线段树数组
     * @param pos 查询的起始位置（即 b + 1）
     * @param val 目标高度阈值（即 heights[a]）
     * @param l 当前节点管理的区间左端点
     * @param r 当前节点管理的区间右端点
     * @param rt 当前节点在 segmentTree 数组中的索引
     * @return 满足条件的最左侧索引，如果不存在返回 -1
     */
    private int query(final int[] segmentTree, int pos, int val, int l, int r, int rt) {
        // 1. 剪枝：如果当前区间的最大高度都 <= val，说明这个区间内没有符合条件的建筑
        // 直接返回 -1，避免无效递归
        if (val >= segmentTree[rt]) {
            return -1;
        }

        // 2. 递归终止条件：到达叶子节点，说明该位置的高度 > val，直接返回索引
        if (l == r) {
            return l;
        }

        int m = (l + r) >> 1;
        // 3. 核心查找逻辑：优先在左子树寻找，因为题目要求“最左侧”
        if (pos <= m) {
            // 尝试在左子树 [l, m] 中寻找
            int res = query(segmentTree, pos, val, l, m, rt * 2 + 1);
            // 如果在左子树找到了结果（res >= 0），直接返回，无需再查右子树
            if (res >= 0) {
                return res;
            }
        }
        
        // 4. 如果左子树没找到，或者起始位置 pos 已经在右子树范围内，则去右子树寻找
        return query(segmentTree, pos, val, m + 1, r, rt * 2 + 2);
    }
}

/**
 * 差分数组 + 双树状数组（BIT）实现区间加、区间求和。
 *
 * 普通树状数组只能做“单点加、前缀和”。
 * 这里用两个树状数组配合差分思想，支持：
 *   - 区间加 delta
 *   - 区间求和 sum[l..r]
 *
 * 核心公式：
 *   设差分数组 d，则原数组 a[i] = d[1] + d[2] + ... + d[i]
 *   前缀和 prefix(r) = Σ a[i] (i=1..r)
 *                     = Σ (r+1 - j) * d[j]   (j=1..r)
 *                     = (r+1) * Σ d[j] - Σ j * d[j]
 *
 * 所以维护两棵树：
 *   tree1 存 d[j]
 *   tree2 存 j * d[j]
 * 查询时 prefix(r) = (r+1) * queryTree1(r) - queryTree2(r)
 */
class BIT {
    // 树状数组大小（对应原数组长度）
    int size;
    // 维护差分值 d[j]
    long[] _tree1;
    // 维护 j * d[j]
    long[] _tree2;

    public BIT(int n) {
        this.size = n;
        // 树状数组下标从 1 开始，多开一点防止越界
        this._tree1 = new long[n + 2];
        this._tree2 = new long[n + 2];
    }

    /**
     * lowbit：取二进制最低位的 1。
     * 例如 lowbit(6) = lowbit(110) = 010 = 2
     * 树状数组用它来定位父节点 / 子节点。
     */
    private int _lowbit(int index) {
        return index & -index;
    }

    /**
     * 对外接口：对原数组区间 [left, right] 每个位置加 delta。
     *
     * 差分技巧：
     *   d[left]   += delta
     *   d[right+1] -= delta
     * 这样前缀和从 left 开始都多了 delta，到 right+1 又减回去，
     * 效果就是 [left, right] 整体 +delta。
     */
    public void add(int left, int right, int delta) {
        _add(left, delta);
        _add(right + 1, -delta);
    }

    /**
     * 内部：在差分数组的 index 位置加 delta，并同步更新两棵树。
     *
     * index += 1 是因为外部传入的下标从 0 开始，
     * 而树状数组内部下标从 1 开始。
     * rawIndex 保存转换后的下标，用于计算 j * delta。
     */
    private void _add(int index, int delta) {
        // 0-based → 1-based
        index += 1;
        long rawIndex = index;
        while (index <= size) {
            // 累加 d[j]
            _tree1[index] += delta;
            // 累加 j * d[j]
            _tree2[index] += rawIndex * delta;
            // 跳到父节点
            index += _lowbit(index);
        }
    }

    /**
     * 对外接口：查询原数组区间 [left, right] 的和。
     * 利用前缀和相减：sum[l..r] = prefix(r) - prefix(l-1)
     */
    public long query(int left, int right) {
        return _query(right) - _query(left - 1);
    }

    /**
     * 内部：查询原数组前缀和 prefix(index)，即 a[0] + a[1] + ... + a[index]。
     *
     * 公式：prefix(r) = (r+1) * Σ d[j] - Σ j*d[j]
     * 对应代码：res += rawIndex * _tree1[index] - _tree2[index]
     *
     * 注意这里的 rawIndex = index + 1 = (外部下标 + 1) + 1 = 外部下标 + 2，
     * 也就是公式里的 (r+1)。
     */
    private long _query(int index) {
        // 0-based → 1-based
        index += 1;
        // 公式里的 (r+1)
        long rawIndex = index + 1;

        // 越界保护
        if (index > size) {
            index = size;
        }

        long res = 0;
        while (index > 0) {
            res += rawIndex * _tree1[index] - _tree2[index];
            // 跳到下一个子节点
            index -= _lowbit(index);
        }

        return res;
    }
    
}