package org.shuangfa114.test.createtrackbuilder.foundation.util.algorithm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class TrackAstarPlanner {

    private static final double KAPPA = 0.55228;

    private static int getHeuristic(int x, int y, int z, int ex, int ey, int ez) {
        return Math.abs(x - ex) + Math.abs(y - ey) * 6 + Math.abs(z - ez);
    }

    public static List<Node> findPath(BlockPos start, BlockPos end) throws NoSolutionException {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<String> closedSet = new HashSet<>();

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs) {
            int h = getHeuristic(start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ());
            openSet.add(new Node(start.getX(), start.getY(), start.getZ(), d[0], d[1], 0, h, null, false));
        }

        int maxIterations = 50000;
        int iterations = 0;

        while (!openSet.isEmpty() && iterations < maxIterations) {
            Node curr = openSet.poll();
            iterations++;

            if (curr.x == end.getX() && curr.y == end.getY() && curr.z == end.getZ()) {
                return reconstructPath(curr);
            }

            if (!closedSet.add(curr.getStateKey())) continue;

            // 1. 直线平地 (长度 2-7)
            for (int L = 2; L <= 7; L++) {
                int nx = curr.x + L * curr.dx;
                int nz = curr.z + L * curr.dz;
                int h = getHeuristic(nx, curr.y, nz, end.getX(), end.getY(), end.getZ());
                openSet.add(new Node(nx, curr.y, nz, curr.dx, curr.dz, curr.gCost + L, h, curr, false));
            }

            // 2. 直线坡道 (基于源码规则: L >= max(|H| * 4, 6))
            int[][] slopes = {{1, 6}, {-1, 6}, {2, 8}, {-2, 8}, {3, 12}, {-3, 12}};
            for (int[] slope : slopes) {
                int H = slope[0], L = slope[1];
                int nx = curr.x + L * curr.dx;
                int nz = curr.z + L * curr.dz;
                int ny = curr.y + H;
                int h = getHeuristic(nx, ny, nz, end.getX(), end.getY(), end.getZ());
                openSet.add(new Node(nx, ny, nz, curr.dx, curr.dz, curr.gCost + L + Math.abs(H) * 2, h, curr, false));
            }

            // 3. 90度弯道 (半径 7-9)
            for (int R = 7; R <= 9; R++) {
                // 左转
                int ldx = -curr.dz, ldz = curr.dx;
                int nxL = curr.x + R * curr.dx + R * ldx;
                int nzL = curr.z + R * curr.dz + R * ldz;
                int hL = getHeuristic(nxL, curr.y, nzL, end.getX(), end.getY(), end.getZ());
                openSet.add(new Node(nxL, curr.y, nzL, ldx, ldz, curr.gCost + (int) (R * 1.5), hL, curr, true));

                // 右转
                int rdx = curr.dz, rdz = -curr.dx;
                int nxR = curr.x + R * curr.dx + R * rdx;
                int nzR = curr.z + R * curr.dz + R * rdz;
                int hR = getHeuristic(nxR, curr.y, nzR, end.getX(), end.getY(), end.getZ());
                openSet.add(new Node(nxR, curr.y, nzR, rdx, rdz, curr.gCost + (int) (R * 1.5), hR, curr, true));
            }
        }

        throw new NoSolutionException("无法找到满足物理限制条件的路径。可能距离过远或高度差过于极端。");
    }

    private static List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    // --- 将寻路结果转换为 Create 需要的贝塞尔段 ---
    public static List<BezierSegment> generateSegments(List<Node> path) {
        List<BezierSegment> segments = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            Node w1 = path.get(i);
            Node w2 = path.get(i + 1);

            Vec3 p1 = Vec3.atBottomCenterOf(w1.getPos());
            Vec3 p2 = Vec3.atBottomCenterOf(w2.getPos());
            double s1, s2;

            if (w2.isTurn) {
                double dist = p1.distanceTo(p2);
                double R = dist / Math.sqrt(2);
                s1 = s2 = R * KAPPA;
            } else {
                double dist = p1.distanceTo(p2);
                s1 = s2 = dist * 0.33;
            }

            Vec3 q1 = p1.add(w1.getDir().scale(s1));
            Vec3 q2 = p2.subtract(w2.getDir().scale(s2)); // 终点控制柄方向取反
            segments.add(new BezierSegment(p1, q1, p2, q2));
        }
        return segments;
    }

    public static class NoSolutionException extends Exception {
        public NoSolutionException(String message) {
            super(message);
        }
    }

    public static class Node implements Comparable<Node> {
        public final int x, y, z;
        public final int dx, dz; // 朝向向量 (仅支持 4 基础方向保证绝对合法)
        public final int gCost, hCost;
        public final Node parent;
        public final boolean isTurn;

        public Node(int x, int y, int z, int dx, int dz, int gCost, int hCost, Node parent, boolean isTurn) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dx = dx;
            this.dz = dz;
            this.gCost = gCost;
            this.hCost = hCost;
            this.parent = parent;
            this.isTurn = isTurn;
        }

        public int fCost() {
            return gCost + hCost;
        }

        public BlockPos getPos() {
            return new BlockPos(x, y, z);
        }

        public Vec3 getDir() {
            return new Vec3(dx, 0, dz);
        }

        @Override
        public int compareTo(Node o) {
            return Integer.compare(this.fCost(), o.fCost());
        }

        // 用于 HashSet 判重
        public String getStateKey() {
            return x + "," + y + "," + z + "," + dx + "," + dz;
        }
        public Segment toSegment() {
            return new Segment(new BlockPos(x,y,z),new Vec3(dx,0,dz));
        }
    }

    public static class BezierSegment {
        public final Vec3 p1, q1, p2, q2;

        public BezierSegment(Vec3 p1, Vec3 q1, Vec3 p2, Vec3 q2) {
            this.p1 = p1;
            this.q1 = q1;
            this.p2 = p2;
            this.q2 = q2;
        }
    }
    public static void main(String[] args) {//just for test

    }
}