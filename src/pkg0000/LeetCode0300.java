package pkg0000;

public class LeetCode0300 {

    /**
     * 307. 区域和检索 - 数组可修改
     * NumArray
     */
    class NumArray {
        // 原始数组的长度
        private int n;
        // 线段树底层存储数组
        private int[] segmentTree;

        // 构造函数：根据初始数组构建线段树
        public NumArray(int[] nums) {
            n = nums.length;
            // 线段树通常开 4 倍空间，以防止极端情况下数组越界
            segmentTree = new int[n * 4];
            // 从根节点（索引为0）开始，递归构建整棵树，管理的区间是 [0, n-1]
            build(0, 0, n - 1, nums);
        }

        /**
         * 递归构建线段树
         * @param node 当前节点在 segmentTree 数组中的索引
         * @param s 当前节点管理的区间的左端点 (start)
         * @param e 当前节点管理的区间的右端点 (end)
         * @param nums 原始数组
         */
        private void build(int node, int s, int e, int[] nums) {
            // 递归终止条件：如果区间长度为1（s == e），说明到达了叶子节点
            if (s == e) {
                // 叶子节点的值就是原数组对应的值
                segmentTree[node] = nums[s];
                return;
            }

            // 计算当前区间的中点，使用位运算 >> 1 等价于除以 2，效率更高
            int m = (s + e) >> 1;

            // 递归构建左子树：管理的区间是 [s, m]，左子节点索引为 node * 2 + 1
            build(node * 2 + 1, s, m, nums);
            // 递归构建右子树：管理的区间是 [m + 1, e]，右子节点索引为 node * 2 + 2
            build(node * 2 + 2, m + 1, e, nums);

            // 核心逻辑：当前非叶子节点的值 = 左子树的值 + 右子树的值（区间求和）
            segmentTree[node] = segmentTree[node * 2 + 1] + segmentTree[node * 2 + 2];
        }
        
        /**
         * 更新接口：将原数组中 index 位置的值修改为 val
         * @param index
         * @param val
         */
        public void update(int index, int val) {
            // 从根节点开始向下寻找需要修改的叶子节点
            change(index, val, 0, 0, n - 1);
        }

        /**
         * 递归修改线段树中的某个叶子节点，并自底向上更新父节点的值
         * @param index
         * @param val
         * @param node
         * @param s
         * @param e
         */
        private void change(int index, int val, int node, int s, int e) {
            // 找到目标叶子节点
            if (s == e) {
                // 直接修改该节点的值
                segmentTree[node] = val;
                return;
            }


            int m = (s + e) >> 1;
            // 根据目标索引 index 与中点 m 的关系，决定向左还是向右递归
            if (index <= m) {
                change(index, val, node * 2 + 1, s, m);
            } else {
                change(index, val, node * 2 + 2, m + 1, e);
            }

            // 【自底向上回溯】：子节点的值改变后，必须重新计算当前父节点的区间和
            segmentTree[node] = segmentTree[node * 2 + 1] + segmentTree[node * 2 + 2];
        }
        
        /**
         * 查询接口：返回原数组中 [left, right] 区间的元素总和
         * @param left
         * @param right
         * @return
         */
        public int sumRange(int left, int right) {
            return range(left, right, 0, 0, n - 1);
        }

        /**
         * 递归查询区间和
         * @param left 查询区间的左端点
         * @param right 查询区间的右端点
         * @param node 当前节点索引
         * @param s 当前节点管理的区间左端点
         * @param e 当前节点管理的区间右端点
         * @return
         */
        private int range(int left, int right, int node, int s, int e) {
            // 情况1：当前节点管理的区间 [s, e] 完全被查询区间 [left, right] 包含
            // 直接返回当前节点的值，无需继续向下递归（这是线段树高效的核心）
            if (left == s && right == e) {
                return segmentTree[node];
            }

            int m = (s + e) >> 1;

            if (right <= m) {
                // 情况2：查询区间完全在左半部分
                return range(left, right, node * 2 + 1, s, m);
            } else if (left > m) {
                // 情况3：查询区间完全在右半部分
                return range(left, right, node * 2 + 2, m + 1, e);
            } else {
                // 情况4：查询区间跨越了中点 m，需要同时查询左右子树并将结果相加
                // 左半部分查 [left, m]，右半部分查 [m + 1, right]
                return range(left, m, node * 2 + 1, s, m) + range(m + 1, right, node * 2 + 2, m + 1, e);
            }
        }
    }
}
