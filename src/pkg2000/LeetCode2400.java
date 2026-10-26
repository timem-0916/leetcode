package pkg2000;

public class LeetCode2400 {

    /**
     * 2409. 统计共同度过的日子数
     * 
     * 计算 Alice 和 Bob 在罗马共同停留的天数。
     *
     * 输入格式：日期字符串为 "MM-DD" 形式（如 "08-15" 表示 8月15日）。
     * 核心思路：将每个日期转换为"一年中的第几天"，然后求两个区间的交集长度。
     * 
     * @param arriveAlice
     * @param leaveAlice
     * @param arriveBob
     * @param leaveBob
     * @return
     */
    public int countDaysTogether(String arriveAlice, String leaveAlice, String arriveBob, String leaveBob) {
        // 每月天数表，索引 0~11 对应 1~12 月
        // 本题假设为非闰年，2月按 28 天计算
        final int[] MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        // 每月天数的"前缀和"数组，MONTH_PRESUM[i] 表示前 i 个月的总天数
        // 例如 MONTH_PRESUM[3] = 31(1月) + 28(2月) + 31(3月) = 90
        final int[] MONTH_PRESUM = new int[13];
        for (int i = 0; i < 12; i++) {
            MONTH_PRESUM[i + 1] = MONTH_PRESUM[i] + MONTH[i];
        }

        // 将 Alice 的到达日和离开日转换为"一年中的第几天"
        // alice[0] = 到达日是一年中的第几天，alice[1] = 离开日是一年中的第几天
        int[] alice = {calculateDayOfYear(arriveAlice, MONTH_PRESUM), calculateDayOfYear(leaveAlice, MONTH_PRESUM)};
        // 将 Bob 的到达日和离开日转换为"一年中的第几天"
        int[] bob = {calculateDayOfYear(arriveBob, MONTH_PRESUM), calculateDayOfYear(leaveBob, MONTH_PRESUM)};

        // 求两个区间 [alice[0], alice[1]] 和 [bob[0], bob[1]] 的交集长度
        // 交集的左端点 = 两个区间左端点的较大值（晚到的人的到达日）
        // 交集的右端点 = 两个区间右端点的较小值（早走的人的离开日）
        // 交集长度 = 右端点 - 左端点 + 1（两端都包含，所以要 +1）
        // 若交集长度为负，说明没有重叠，取 max(0, ...) 保证结果非负
        return Math.max(0, Math.min(alice[1], bob[1]) - Math.max(alice[0], bob[0]) + 1);
    }

    /**
     * 将 "MM-DD" 格式的日期字符串转换为"一年中的第几天"。
     *
     * 例如："03-01" → 前两个月共 31+28=59 天，再加 1 天 = 第 60 天
     *
     * @param day           日期字符串，格式为 "MM-DD"
     * @param MONTH_PRESUM  每月天数的前缀和数组
     * @return 该日期是一年中的第几天（1 月 1 日为第 1 天）
     */
    private int calculateDayOfYear(String day, final int[] MONTH_PRESUM) {
        // 截取前两个字符解析为月份（如 "08-15" → "08" → 8）
        int m = Integer.parseInt(day.substring(0, 2));

        // 截取第 3 个字符之后的部分解析为日期（如 "08-15" → "15" → 15）
        // substring(3) 跳过 "MM-" 三个字符，直接取日期部分
        int d = Integer.parseInt(day.substring(3));

        // 前 m-1 个月的总天数 + 当月第 d 天 = 一年中的第几天
        // 例如 m=3, d=1 → MONTH_PRESUM[2] + 1 = 59 + 1 = 60（3月1日是第60天）
        return MONTH_PRESUM[m - 1] + d;
    }

    /**
     * 2472. 不重叠回文子字符串的最大数目
     * @param s
     * @param k
     * @return
     */
    public int maxPalindromes(String s, int k) {
        int n = s.length();

        // ============================================
        // 第一步：预处理 —— 用动态规划标记所有回文子串
        // ============================================
        // isPalindrome[left][right] = true 表示 s[left...right] 是回文串
        boolean[][] isPalindrome = new boolean[n][n];

        // 按子串长度从小到大枚举（len = 1, 2, 3, ..., n）
        // 必须按长度递增的顺序，因为长串的回文判断依赖短串的结果
        for (int len = 1; len <= n; len++) {
            // 枚举所有长度为 len 的子串的起始位置
            for (int left = 0; left + len <= n; left++) {
                // 子串的右边界
                int right = left + len - 1;

                // 判断 s[left...right] 是否为回文：
                // 条件1：首尾字符相同 s[left] == s[right]
                // 条件2：去掉首尾后的内部子串也是回文
                //        - 如果 len <= 2（即长度为1或2），首尾相同就是回文，无需检查内部
                //        - 如果 len > 2，需要检查 isPalindrome[left+1][right-1]
                isPalindrome[left][right] = s.charAt(left) == s.charAt(right) && (len <= 2 || isPalindrome[left + 1][right - 1]);
            }
        }

        // ============================================
        // 第二步：动态规划 —— 求最多不重叠回文子串数量
        // ============================================
        // dp[i] 表示字符串前 i 个字符（s[0...i-1]）中，
        // 最多能选出多少个长度 >= k 的互不重叠的回文子串
        int[] dp = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            // 默认情况：不选以 i-1 结尾的回文子串
            // 那么 dp[i] 继承 dp[i-1] 的结果（前 i-1 个字符的最优解）
            dp[i] = dp[i - 1];

            // 枚举所有可能的回文子串的起始位置 j
            // 回文子串为 s[j...i-1]，其长度为 i - j
            // 要求长度 >= k，即 i - j >= k，所以 j <= i - k，即 j + k <= i
            for (int j = 0; j + k <= i; j++) {
                // 如果 s[j...i-1] 是回文串
                if (isPalindrome[j][i - 1]) {
                    // 选择这个回文子串，则总数 = 前 j 个字符的最优解 + 1
                    // 取所有可能中的最大值
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
        }

        // dp[n] 就是整个字符串 s[0...n-1] 的最优解
        return dp[n];
    }

    /**
     * 2485. 找出中枢整数
     * @param n
     * @return
     */
    public int pivotInteger(int n) {
        int t = (n * (n + 1)) >>> 1;
        int x = (int) Math.sqrt(t);
        return x * x == t ? x : -1;
    }
}
