package pkg0000;

import util.TreeNode;

public class LeetCode0400 {

    /**
     * 440. 字典序的第K小数字
     * @param n
     * @param k
     * @return
     */
    public int findKthNumber(int n, int k) {
        // 1. 初始化当前数字为 1（字典序的起点）
        int curr = 1;

        // 2. 将 k 减 1，因为 curr = 1 已经占据了第 1 个位置
        // 此时 k 表示我们还需要在字典树中“往下走”多少步
        k--;

        // 3. 循环寻找，直到 k 减为 0
        while (k > 0) {
            // 核心步骤：计算以 curr 为根的子树中，有多少个节点（包含 curr 本身）
            int steps = getSteps(curr, n);

            // 情况 A：以 curr 为根的子树节点总数 <= 剩余的步数 k
            // 说明第 k 个数不在 curr 的子树里，我们需要跳过整个 curr 子树，去兄弟节点 curr + 1 找
            if (steps <= k) {
                // 减去跳过的节点数
                k -= steps;
                // 指针右移，指向下一个兄弟节点（例如从 1 跳到 2）
                curr++;
            } else {
                // 情况 B：以 curr 为根的子树节点总数 > 剩余的步数 k
                // 说明第 k 个数就在 curr 的子树里！我们需要向下深入一层

                // 指针下移，指向 curr 的第一个子节点（例如从 1 变成 10）
                curr *= 10;
                // 向下走了一步，消耗掉 1 个步数
                k--;
            }
        }

        return curr;
    }

    /**
     * 计算以 curr 为前缀的数字，在 [1, n] 范围内一共有多少个
     * 本质上是在计算字典树中，curr 这棵子树的节点总数
     * @param curr 当前前缀（例如 1）
     * @param n 数字的上限
     * @return
     */
    private int getSteps(int curr, long n) {
        int steps = 0;
        // first 表示当前层的最左节点，last 表示当前层的最右节点
        long first = curr, last = curr;

        // 逐层向下遍历字典树，直到最左节点 first 超过了上限 n
        while (first <= n) {
            // 核心计算：当前层的节点数 = 最右节点 - 最左节点 + 1
            // 注意：最右节点不能超过上限 n，所以取 Math.min(last, n)
            steps += Math.min(last, n) - first + 1;

            // 移动到下一层
            first *= 10;
            last *= 10;
            last += 9;
        }
        
        return steps;
    }

    /**
     * 450. 删除二叉搜索树中的节点
     * @param root
     * @param key
     * @return
     */
    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null) {
            return null;
        }

        if (key < root.val) {
            root.left = deleteNode(root.left, key);
        } else if (key > root.val) {
            root.right = deleteNode(root.right, key);
        } else {
            if (root.left == null) {
                return root.right;
            }
            if (root.right == null) {
                return root.left;
            }
            TreeNode successor = root.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            root.right = deleteNode(root.right, successor.val);
            successor.right = root.right;
            successor.left = root.left;
            return successor;
        }

        return root;
    }
}
