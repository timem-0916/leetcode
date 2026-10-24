package pkg0000;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

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

    /**
     * 352. 将数据流变为多个不相交区间
     * 
     * 数据流的不相交区间总结器。
     *
     * 核心思路：用 TreeMap 维护一组不相交、不重叠的区间 [left, right]，
     * 其中 key 为区间左端点，value 为区间右端点。
     * 每次插入新值时，判断它与相邻区间的关系，决定是忽略、扩展还是合并。
     */
    class SummaryRanges {

        // key = 区间左端点，value = 区间右端点
        // TreeMap 保证区间按左端点有序排列，便于快速查找相邻区间
        TreeMap<Integer, Integer> intervals;

        /** 初始化空的数据结构 */
        public SummaryRanges() {
            intervals = new TreeMap<>();
        }
        
        /**
         * 向数据流中添加一个整数 val，并更新区间集合。
         *
         * 处理逻辑分五种情况：
         *   情况一：val 已经落在某个现有区间内 → 直接忽略。
         *   情况二：val 恰好紧贴在左侧区间的右边（val = r0 + 1）→ 向左扩展该区间。
         *   情况三：val 恰好紧贴在右侧区间的左边（val = l1 - 1）→ 向右扩展该区间（需更新 key）。
         *   情况四：val 同时紧贴左右两个区间（r0 + 1 = val = l1 - 1）→ 合并三个为一个。
         *   情况五：val 与任何区间都不相邻 → 新建一个单点区间 [val, val]。
         */
        public void addNum(int value) {
            // 找到左端点严格大于 val 的第一个区间 interval1 = [l1, r1]
            // 即 l1 是满足 l1 > val 的最小左端点
            // 如果不存在，interval1 为 null
            Map.Entry<Integer, Integer> interval1 = intervals.ceilingEntry(value + 1);
            
            // 找到左端点小于等于 val 的最大区间 interval0 = [l0, r0]
            // 即 l0 是满足 l0 <= val 的最大左端点
            // 如果不存在，interval0 为 null
            Map.Entry<Integer, Integer> interval0 = intervals.floorEntry(value);

            // 情况一：val 已经被 interval0 覆盖（l0 <= val <= r0），无需任何操作
            if (interval0 != null && interval0.getKey() <= value && value <= interval0.getValue()) {
                return;
            }

            // 判断 val 是否与左侧区间相邻：interval0 的右端点 + 1 == val
            boolean leftAside = interval0 != null && interval0.getValue() + 1 == value;
            // 判断 val 是否与右侧区间相邻：interval1 的左端点 - 1 == val
            boolean rightAside = interval1 != null && interval1.getKey() - 1 == value;

            if (leftAside && rightAside) {
                // 情况四：val 同时连接左右两个区间，需要合并
                // 合并后的新区间为 [l0, r1]
                int left = interval0.getKey(), right = interval1.getValue();
                // 删除左区间
                intervals.remove(interval0.getKey());
                // 删除右区间
                intervals.remove(interval1.getKey());
                // 插入合并后的区间
                intervals.put(left, right);
            } else if (leftAside) {
                // 情况二：val 只与左侧区间相邻，将左区间的右端点扩展至 val
                intervals.put(interval0.getKey(), interval0.getValue() + 1);
            } else if (rightAside) {
                // 情况三：val 只与右侧区间相邻
                // 需要将新区间的左端点从 interval1.getKey() 改为 val
                int right = interval1.getValue();
                // 删除旧的右区间
                intervals.remove(interval1.getKey());
                // 以 val 为新左端点插入
                intervals.put(value, right);
            } else {
                // 情况五：val 与任何区间都不相邻，新建一个单点区间 [val, val]
                intervals.put(value, value);
            }
        }
        
        /**
         * 返回当前所有不相交区间的二维数组表示。
         *
         * 由于 TreeMap 已经按左端点有序，直接遍历输出即可。
         *
         * @return 形如 [[l1, r1], [l2, r2], ...] 的区间数组
         */
        public int[][] getIntervals() {
            int size = intervals.size();
            int[][] ans = new int[size][2];
            int index = 0;
            // 按左端点从小到大的顺序遍历所有区间
            for (Map.Entry<Integer, Integer> entry : intervals.entrySet()) {
                int left = entry.getKey(), right = entry.getValue();
                ans[index][0] = left;
                ans[index][1] = right;
                index++;
            }
            return ans;
        }
    }

    /**
     * 380. O(1) 时间插入、删除和获取随机元素
     * RandomizedSet
     */
    class RandomizedSet {
        List<Integer> list;
        Map<Integer, Integer> map;
        Random random;

        public RandomizedSet() {
            list = new ArrayList<>();
            map = new HashMap<>();
            random = new Random();
        }
        
        public boolean insert(int val) {
            if (map.containsKey(val)) {
                return false;
            }
            int size = list.size();
            list.add(val);
            map.put(val, size);
            return true;
        }
        
        public boolean remove(int val) {
            if (!map.containsKey(val)) {
                return false;
            }
            int size = list.size();
            int index = map.get(val);
            int last = list.getLast();
            list.set(index, last);
            map.put(last, index);
            list.remove(size - 1);
            map.remove(val);
            return true;
        }
        
        public int getRandom() {
            int size = list.size();
            int index = random.nextInt(size);
            return list.get(index);
        }
    }

    /**
     * 381. O(1) 时间插入、删除和获取随机元素 - 允许重复
     */
    class RandomizedCollection {

        List<Integer> list;
        Map<Integer, Set<Integer>> map;
        Random random;

        public RandomizedCollection() {
            list = new ArrayList<>();
            map = new HashMap<>();
            random = new Random();
        }
        
        public boolean insert(int val) {
            int size = list.size();
            list.add(val);
            boolean res = false;
            if (!map.containsKey(val)) {
                res = true;
                map.put(val, new HashSet<>());
            }
            map.get(val).add(size);
            return res;
        }
        
        public boolean remove(int val) {
            if (!map.containsKey(val)) {
                return false;
            }

            Iterator<Integer> it = map.get(val).iterator();
            int index = it.next();
            it.remove();
            if (map.get(val).isEmpty()) {
                map.remove(val);
            }

            int lastIdx = list.size() - 1;

            if (index != lastIdx) {
                int lastVal = list.get(lastIdx);
                list.set(index, lastVal);

                Set<Integer> lastSet = map.get(lastVal);
                lastSet.remove(lastIdx);
                lastSet.add(index);
            }

            list.removeLast();
            return true;
        }
        
        public int getRandom() {
            int total = list.size();
            int r = random.nextInt(total);
            for (Map.Entry<Integer, Set<Integer>> entry : map.entrySet()) {
                r -= entry.getValue().size();
                if (r < 0) {
                    return entry.getKey();
                }
            }
            return 0;
        }
    }
}
