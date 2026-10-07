package pkg0000;

import java.util.ArrayList;
import java.util.List;

public class LeetCode0300 {

    /**
     * 301. 删除无效的括号
     * @param s
     * @return
     */
    public List<String> removeInvalidParentheses(String s) {
        // 存储最终结果：所有删除最少括号后得到的有效字符串
        List<String> ans = new ArrayList<>();

        // lremove：需要删除的左括号 '(' 的最少数量
        int lremove = 0;
        // rremove：需要删除的右括号 ')' 的最少数量
        int rremove = 0;

        // 第一遍遍历：统计最少需要删除多少个左括号和右括号
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                // 遇到左括号，先假设它需要被删除（后面如果遇到右括号可以抵消）
                lremove++;
            } else if (s.charAt(i) == ')') {
                if (lremove == 0) {
                    // 没有多余的左括号可以匹配当前右括号，这个右括号必须删除
                    rremove++;
                } else {
                    // 有未匹配的左括号，可以抵消一个，左括号待删除数量减 1
                    lremove--;
                }
            }
        }

        // 调用回溯函数，从下标 0 开始，尝试删除 lremove 个左括号和 rremove 个右括号
        removeInvalidParenthesesHelper(s, 0, lremove, rremove, ans);

        return ans;
    }

    /**
     * 回溯搜索所有可能的删除方案
     * @param str      当前正在处理的字符串
     * @param start    当前遍历的起始下标，避免重复搜索
     * @param lremove  还需要删除的左括号数量
     * @param rremove  还需要删除的右括号数量
     * @param ans      存储的结果
     */
    private void removeInvalidParenthesesHelper(String str, int start, int lremove, int rremove, List<String> ans) {
        // 递归终止条件：需要删除的括号都已删完
        if (lremove == 0 && rremove == 0) {
            // 验证当前字符串是否是有效的括号序列
            if (isValid(str)) {
                ans.add(str);
            }
            return;
        }

        // 从 start 开始遍历字符串，尝试删除每一个括号
        for (int i = start; i < str.length(); i++) {
            // 去重剪枝：如果当前字符和前一个字符相同，跳过
            // 避免对连续相同的括号做重复删除，产生重复结果
            if (i != start && str.charAt(i) == str.charAt(i - 1)) {
                continue;
            }

            // 可行性剪枝：如果剩余需要删除的括号总数超过了字符串剩余长度
            // 说明不可能删够，直接返回
            if (lremove + rremove > str.length() - i) {
                return;
            }

            // 尝试删除一个左括号
            if (lremove > 0 && str.charAt(i) == '(') {
                // 删除下标 i 处的左括号，拼接新字符串
                // 递归时 start 仍传 i（因为删除后后面的字符前移了，i 位置已经是新字符）
                removeInvalidParenthesesHelper(str.substring(0, i) + str.substring(i + 1), i, lremove - 1, rremove, ans);
            }

            // 尝试删除一个右括号
            if (rremove > 0 && str.charAt(i) == ')') {
                removeInvalidParenthesesHelper(str.substring(0, i) + str.substring(i + 1), i, lremove, rremove - 1, ans);
            }
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
