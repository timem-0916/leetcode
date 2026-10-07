package pkg0000;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LeetCode0300 {

    /**
     * 301. 删除无效的括号
     * @param s
     * @return
     */
    public List<String> removeInvalidParentheses(String s) {
        // 存储最终结果：所有删除最少括号后得到的有效字符串
        List<String> ans = new ArrayList<>();
        // 存储当前这一层需要检查的所有字符串（同一删除数量下的候选集合）
        // 使用 HashSet 自动去重，避免相同字符串被重复处理
        Set<String> currSet = new HashSet<>();

        // 初始状态：把原始字符串加入第一层
        currSet.add(s);

        // BFS 主循环：每一轮代表"删除 k 个括号"，k 从 0 开始递增
        while (true) {
            // 遍历当前层的所有字符串，检查是否已经是有效括号序列
            for (String str : currSet) {
                if (isValid(str)) {
                    ans.add(str);
                }
            }

            // 如果当前层已经找到了有效字符串，说明已经是最少删除次数
            // 直接返回结果，不再继续删除更多括号
            if (ans.size() > 0) {
                return ans;
            }

            // 当前层没有有效字符串，需要生成下一层：对每个字符串尝试删除一个括号
            Set<String> nextSet = new HashSet<>();
            for (String str : currSet) {
                for (int i = 0; i < str.length(); i++) {
                    // 去重剪枝：连续相同字符只处理第一个
                    // 例如 "())" 中删除第 1 个或第 2 个 ')' 结果相同，避免重复
                    if (i > 0 && str.charAt(i) == str.charAt(i - 1)) {
                        continue;
                    }

                    // 只对括号字符进行删除操作，非括号字符跳过
                    if (str.charAt(i) == '(' || str.charAt(i) == ')') {
                        // 删除下标 i 处的字符，拼接前后两部分生成新字符串
                        // 加入 nextSet，由 HashSet 自动去重
                        nextSet.add(str.substring(0, i) + str.substring(i + 1));
                    }
                }
            }

            // 进入下一层：检查删除 k+1 个括号后的所有候选字符串
            currSet = nextSet;
        }
    }

    /**
     * 判断字符串是否是有效的括号序列
     * 规则：遍历过程中右括号数量不能超过左括号，且最终左右括号数量相等
     * @param str
     * @return
     */
    private boolean isValid(String str) {
        int cnt = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(') {
                cnt++;
            } else if (str.charAt(i) == ')') {
                cnt--;
                // 右括号比左括号多，说明无效
                if (cnt < 0) {
                    return false;
                }
            }
        }

        // 最终 cnt 必须为 0，左右括号完全匹配
        return cnt == 0;
    }

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
