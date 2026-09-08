package pkg0000;

public class LeetCode0000 {

    /**
     * 11. 盛最多水的容器
     * @param height
     * @return
     */
    public int maxArea(int[] height) {
        int n = height.length, l = 0, r = n - 1, max = 0;
        while (l < r) {
            max = Math.max(max, (r - l) * Math.min(height[l], height[r]));
            if (height[l] <= height[r]) {
                l++;
            } else {
                r--;
            }
        }
        return max;
    }

    /**
     * 14. 最长公共前缀
     * @param strs
     * @return
     */
    public String longestCommonPrefix(String[] strs) {
        // 1. 初始化字典树的根节点（根节点不存储字符）
        WordNode root = new WordNode();

        // 2. 遍历所有字符串，将它们插入到字典树中
        for (String str : strs) {
            // 每次插入新单词，都要从根节点出发
            WordNode node = root;
            for (char c : str.toCharArray()) {
                // 计算字符对应的数组下标 (0~25)
                int idx = c - 'a';

                // 【核心修复】：如果当前路径上的节点不存在，才创建新节点
                // 如果已经存在，则直接复用，避免覆盖之前的计数
                if (node.children[idx] == null) {
                    node.children[idx] = new WordNode();
                }

                // 移动到下一个节点，并将该节点的经过次数 +1
                node = node.children[idx];
                node.cnt++;
            }
        }

        // 字符串的总个数
        int n = strs.length;
        StringBuilder ans = new StringBuilder();
        // 使用 curr 指针遍历，避免破坏 root 结构
        WordNode curr = root;

        // 3. 从根节点开始，寻找最长公共前缀
        while (curr != null) {
            // 标记当前层是否找到了公共前缀节点
            boolean found = false;
            // 遍历当前节点的 26 个可能的子节点
            for (int i = 0; i < 26; i++) {
                // 如果子节点存在，且它的经过次数等于字符串总数 n
                // 说明所有字符串都经过了这里，这就是公共前缀的一部分
                if (curr.children[i] != null && curr.children[i].cnt == n) {
                    // 将字符加入结果
                    ans.append((char) ('a' + i));
                    // 指针下移，继续寻找下一层
                    curr = curr.children[i];
                    found = true;
                    // 因为每个节点最多只有一个 cnt==n 的子节点，找到即可跳出循环
                    break;
                }
            }
            // 如果当前层没有找到符合条件的子节点，说明公共前缀已经结束
            if (!found) {
                break;
            }
        }
        return ans.toString();
    }

    /**
     * WordNode
     */
    class WordNode {
        int cnt;
        WordNode[] children;
        WordNode() {
            cnt = 0;
            children = new WordNode[26];
        }
    }
}
