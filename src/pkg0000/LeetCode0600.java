package pkg0000;


import util.TreeNode;

public class LeetCode0600 {
    /**
     * 669. 修剪二叉搜索树
     * @param root
     * @param low
     * @param high
     * @return
     */
    public TreeNode trimBST(TreeNode root, int low, int high) {
        // 1. 寻找新的根节点：
        // 循环跳过那些不在 [low, high] 范围内的根节点。
        // 如果当前根节点值小于 low，说明其左子树的所有节点也都小于 low，因此直接向右子树寻找；
        // 如果当前根节点值大于 high，说明其右子树的所有节点也都大于 high，因此直接向左子树寻找。
        while (root != null && (root.val < low || root.val > high)) {
            if (root.val < low) {
                root = root.right;
            } else {
                root = root.left;
            }
        }

        // 如果整棵树都不在范围内，直接返回 null
        if (root == null) {
            return null;
        }

        // 2. 修剪左子树：
        // 遍历左子树，寻找并剔除值小于 low 的节点。
        // 因为这是 BST，如果 node.left.val < low，那么 node.left 的左子树肯定也都小于 low，
        // 所以直接将 node.left 指向 node.left.right（即保留其右子树部分），然后继续检查新的 node.left。
        // 如果 node.left.val >= low，说明该节点合法，继续向左下遍历。
        for (TreeNode node = root; node.left != null; ) {
            if (node.left.val < low) {
                node.left = node.left.right;
            } else {
                node = node.left;
            }
        }

        // 3. 修剪右子树：
        // 遍历右子树，寻找并剔除值大于 high 的节点。
        // 同理，如果 node.right.val > high，那么 node.right 的右子树肯定也都大于 high，
        // 所以直接将 node.right 指向 node.right.left（即保留其左子树部分），然后继续检查新的 node.right。
        // 如果 node.right.val <= high，说明该节点合法，继续向右下遍历。
        for (TreeNode node = root; node.right != null; ) {
            if (node.right.val > high) {
                node.right = node.right.left;
            } else {
                node = node.right;
            }
        }

        // 返回修剪后的根节点
        return root;
    }

    /**
     * 678. 有效的括号字符串
     * @param s
     * @return
     */
    public boolean checkValidString(String s) {
        // minCount：当前未匹配的左括号「最少」可能数量（下界）
        // 代表一种最悲观的估计：尽可能多地把 '*' 当作 ')' 来抵消 '('
        int minCount = 0;

        // maxCount：当前未匹配的左括号「最多」可能数量（上界）
        // 代表一种最乐观的估计：尽可能多地把 '*' 当作 '(' 来增加左括号
        int maxCount = 0;

        int n = s.length();

        // 从左到右扫描字符串，动态维护 [minCount, maxCount] 这个区间
        // 核心思想：只要 0 始终落在 [minCount, maxCount] 区间内，就说明存在一种 '*' 的用法使括号合法
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);

            if (c == '(') {
                // 遇到左括号：无论怎么解释，未匹配的左括号数量都必须 +1
                minCount++;
                maxCount++;
            } else if (c == ')') {
                // 遇到右括号：需要消耗一个左括号来匹配
                // minCount - 1：最悲观情况下，也尝试用右括号抵消一个左括号
                // Math.max(..., 0)：下界不能为负，因为「未匹配左括号数」最少为 0
                //   （负数意味着右括号过剩，但 '*' 可以充当左括号来补救，所以下界截断到 0）
                minCount = Math.max(minCount - 1, 0);

                // maxCount - 1：最乐观情况下，也必须用这个右括号消耗一个左括号
                maxCount--;

                // 如果最乐观估计下左括号都不够用了（maxCount < 0），
                // 说明即使把所有 '*' 都当作 '(' 也无法匹配当前右括号，直接返回 false
                if (maxCount < 0) {
                    return false;
                }
            } else {
                // 遇到星号 '*'：它可以是 '('、')' 或空字符串，三种选择
                // minCount - 1：把 '*' 当作 ')'，抵消一个左括号（下界降低）
                // Math.max(..., 0)：下界不能为负，同理截断到 0
                minCount = Math.max(minCount - 1, 0);

                // maxCount + 1：把 '*' 当作 '('，增加一个左括号（上界升高）
                maxCount++;

                // 注意：'*' 当作空字符串的情况隐含在区间内，不需要单独处理
            }
        }

        // 扫描结束后，检查 minCount 是否为 0
        // minCount == 0：说明存在一种解释方式，使得所有左括号都被完全匹配，字符串有效
        // minCount > 0：说明即使把所有 '*' 都当作 ')'，仍有左括号剩余，字符串无效
        return minCount == 0;
    }

    /**
     * 687. 最长同值路径
     * @param root
     * @return
     */
    public int longestUnivaluePath(TreeNode root) {
        // 使用数组来模拟“引用传递”，用于在递归过程中记录全局最大值
        // 相比于类成员变量，这种写法没有副作用，更加安全
        int[] ans = {0};
        longestUnivaluePathDfs(root, ans);
        return ans[0];
    }

    /**
     * 辅助 DFS 函数：自底向上计算同值路径
     * @param root 当前遍历到的节点
     * @param ans 用于记录全局最长同值路径长度的数组
     * @return 返回从当前节点 root 向下延伸，所能形成的最长同值路径的边数（注意：只能选左或右的一条单线）
     */
    private int longestUnivaluePathDfs(TreeNode root, int[] ans) {
        // 1. 递归终止条件：如果当前节点为空，返回 0
        if (root == null) {
            return 0;
        }

        // 2. 递归处理左右子树，获取它们能向上提供的最长同值边数
        int left = longestUnivaluePathDfs(root.left, ans), right = longestUnivaluePathDfs(root.right, ans);
        
        // 3. 计算当前节点向左、向右的“有效延伸”长度
        // 只有当子节点存在，且子节点的值与当前节点相同时，才能将路径延长
        int left1 = 0, right1 = 0;
        if (root.left != null && root.val == root.left.val) {
            left1 = left + 1;
        }
        if (root.right != null && root.val == root.right.val) {
            right1 = right + 1;
        }

        // 4. 【更新全局答案】
        // 以当前节点 root 为最高点（转折点），向左右两边延伸形成的完整路径长度为 left1 + right1
        // 这种“V”型路径在当前节点内部是合法的，所以用它来更新全局最大值
        ans[0] = Math.max(ans[0], left1 + right1);

        // 5. 【向上层父节点汇报】
        // 因为合法的路径不能分叉，所以只能挑选左边和右边中较长的那条，返回给父节点
        // 父节点拿到这个值后，如果值相同，就可以在此基础上继续 +1
        return Math.max(left1, right1);
    }
}
