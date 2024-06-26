import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class tangent {
    public static class Point implements Comparable<Point> {
        double x;
        double y;
        static Point p0;

        Point() {
            x = 0;
            y = 0;
        }

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        static int ccw(Point p, Point q, Point r) {
            double res = (q.y - p.y) * (r.x - q.x) -
                    (r.y - q.y) * (q.x - p.x);

            if (res == 0)
                return 0;
            if (res > 0)
                return 1;
            return -1;
        }

        public static void swap(Point p1, Point p2) {
            double tempX = p1.x;
            double tempY = p1.y;
            p1.x = p2.x;
            p1.y = p2.y;
            p2.x = tempX;
            p2.y = tempY;
        }

        public static double distSq(Point p1, Point p2) {
            return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
        }

        public int compareTo(Point p) {
            int o = ccw(p0, this, p);
            if (o == 0)
                return (distSq(p0, p) >= distSq(p0, this)) ? -1 : 1;
            return o;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Point point = (Point) obj;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            double result = x;
            result = 31 * result + y;
            return (int) result;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    public static boolean isBInsideA(Point[] a, Point[] b) {
        double maxX = Integer.MIN_VALUE;
        double minX = Integer.MAX_VALUE;
        double maxY = Integer.MIN_VALUE;
        double minY = Integer.MAX_VALUE;
        for (Point point : a) {
            double x = point.x;
            double y = point.y;
            if (maxX < x)
                maxX = x;
            if (x < minX)
                minX = x;
            if (maxY < y)
                maxY = y;
            if (y < minY)
                minY = y;
        }
        for (Point point : b) {
            if (!pointInPolygon(point, a)) {
                return false;
            }
        }
        return true;
    }

    public static boolean pointInPolygon(Point point, Point[] polygon) {
        Path2D path = new Path2D.Double();

        path.moveTo(polygon[0].x, polygon[0].y);

        for (int i = 1; i < polygon.length; i++) {
            path.lineTo(polygon[i].x, polygon[i].y);
        }

        path.closePath();

        Point2D testPoint = new Point2D.Double(point.x, point.y);
        if (isPointOnEdgeOfPolygon(point, polygon)) {
            return true;
        }
        return path.contains(testPoint);
    }

    private static boolean isPointOnEdgeOfPolygon(Point point, Point[] polygon) {
        for (int i = 0; i < polygon.length; i++) {
            Point p1 = polygon[i];
            Point p2 = polygon[(i + 1) % polygon.length];

            if (isPointOnLineSegment(point, p1, p2)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPointOnLineSegment(Point p, Point p1, Point p2) {
        double minX = Math.min(p1.x, p2.x);
        double maxX = Math.max(p1.x, p2.x);
        double minY = Math.min(p1.y, p2.y);
        double maxY = Math.max(p1.y, p2.y);

        if (p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY) {
            double dy = p2.y - p1.y;
            double dx = p2.x - p1.x;
            if (dx == 0) {
                return p.x == p1.x;
            }
            double slope = dy / dx;
            double intercept = p1.y - (slope * p1.x);
            return p.y == slope * p.x + intercept;
        }

        return false;
    }

    static double polygonArea(Point[] points, int N) {
        double area = 0;
        for (int i = 0, j = N - 1; i < N; j = i++) {
            area += (points[i].x * points[j].y) - (points[i].y * points[j].x);
        }
        area /= 2;
        return area < 0 ? -area : area;
    }

    public static Point[] deepCopyArray(Point[] original) {
        Point[] copy = new Point[original.length];
        for (int i = 0; i < original.length; i++) {
            copy[i] = new Point(original[i].x, original[i].y);
        }
        return copy;
    }

    public static void determinePosition(Point[] points1, Point[] points2) {
        double[] centroidA = calculateCentroid(points1);
        double[] centroidB = calculateCentroid(points2);
        isAAboveB = centroidA[1] > centroidB[1] ? 1 : centroidA[1] < centroidB[1] ? 0 : -1;
        isALeftB = centroidA[0] < centroidB[0];
    }

    private static double[] calculateCentroid(Point[] points) {
        double sumX = 0;
        double sumY = 0;
        for (Point p : points) {
            sumX += p.x;
            sumY += p.y;
        }
        return new double[] { sumX / points.length, sumY / points.length };
    }

    public static int[] findInitialPoints() {
        int ia;
        int ib;

        if (isAAboveB == 1) {
            ia = findIndexWithMinY(polygon1Hull);
            ib = findIndexWithMaxY(polygon2Hull);
        } else if (isAAboveB == 0) {
            ia = findIndexWithMaxY(polygon1Hull);
            ib = findIndexWithMinY(polygon2Hull);
        } else {
            ia = findIndexWithMaxX(polygon1Hull);
            ib = findIndexWithMinX(polygon2Hull);
        }

        return new int[] { ia, ib };
    }

    private static int findIndexWithMinY(Point[] points) {
        int index = 0;
        for (int i = 1; i < points.length; i++) {
            if (points[i].y < points[index].y) {
                index = i;
            }
        }
        return index;
    }

    private static int findIndexWithMaxY(Point[] points) {
        int index = 0;
        for (int i = 1; i < points.length; i++) {
            if (points[i].y > points[index].y) {
                index = i;
            }
        }
        return index;
    }

    private static int findIndexWithMaxX(Point[] points) {
        int index = 0;
        for (int i = 1; i < points.length; i++) {
            if (points[i].x > points[index].x) {
                index = i;
            }
        }
        return index;
    }

    private static int findIndexWithMinX(Point[] points) {
        int index = 0;
        for (int i = 1; i < points.length; i++) {
            if (points[i].x < points[index].x) {
                index = i;
            }
        }
        return index;
    }

    public static Point[] findTangent(boolean lower) {
        int[] init = findInitialPoints();
        int index_a = init[0];
        int index_b = init[1];
        boolean done = false;
        boolean findParallel_A = false;
        int parallelIndex_A = -1;
        boolean findParallel_B = false;
        int parallelIndex_B = -1;
        while (!done) {
            done = true;
            while (true) {
                int next_index_A = (index_a + (lower ? -1 : 1) + polygon1Hull.length) % polygon1Hull.length;
                int ori = Point.ccw(polygon2Hull[index_b], polygon1Hull[index_a], polygon1Hull[next_index_A]);
                if ((lower && (ori <= 0)) || (!lower && (ori >= 0))) {
                    if (ori == 0 && !findParallel_A) {
                        parallelIndex_A = index_a;
                        findParallel_A = true;
                    } else {
                        findParallel_A = false;
                    }
                    index_a = next_index_A;
                    done = false;
                } else {
                    break;
                }
            }
            while (true) {
                int next_index_B = (index_b + (lower ? 1 : -1) + polygon2Hull.length) % polygon2Hull.length;
                int ori = Point.ccw(polygon1Hull[index_a], polygon2Hull[index_b], polygon2Hull[next_index_B]);
                if ((lower && (ori >= 0)) || (!lower && (ori <= 0))) {
                    if (ori == 0 && !findParallel_B) {
                        parallelIndex_B = index_b;
                        findParallel_B = true;
                    } else {
                        findParallel_B = false;
                    }
                    index_b = next_index_B;
                    done = false;
                } else {
                    break;
                }
            }
            if (done) {
                break;
            }
        }
        if (findParallel_A) {
            index_a = parallelIndex_A;
        }
        if (findParallel_B) {
            index_b = parallelIndex_B;
        }
        return new Point[] { polygon1Hull[index_a], polygon2Hull[index_b] };
    }

    public static void mergePolygonsWithTangents() {
        int upperAIdx = findIndex(polygon1, upperTangent[0]);
        int upperBIdx = findIndex(polygon2, upperTangent[1]);
        int lowerAIdx = findIndex(polygon1, lowerTangent[0]);
        int lowerBIdx = findIndex(polygon2, lowerTangent[1]);
        List<Point> mergedPolygonList = new ArrayList<>();

        int currentIndex = upperAIdx;
        while (true) {
            mergedPolygonList.add(polygon1[currentIndex]);
            if (currentIndex == lowerAIdx) {
                break;
            }
            currentIndex = (currentIndex - 1 + polygon1.length) % polygon1.length;
        }
        currentIndex = lowerBIdx;
        while (true) {
            mergedPolygonList.add(polygon2[currentIndex]);
            if (currentIndex == upperBIdx) {
                break;
            }
            currentIndex = (currentIndex - 1 + polygon2.length) % polygon2.length;
        }
        mergedPolygon = new Point[mergedPolygonList.size()];
        mergedPolygon = mergedPolygonList.toArray(mergedPolygon);
    }

    public static int findIndex(Point[] polygon, Point target) {
        for (int i = 0; i < polygon.length; i++) {
            if (polygon[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }

    protected static Point[] polygon1;
    protected static Point[] polygon1Hull;

    protected static Point[] polygon2;
    protected static Point[] polygon2Hull;

    protected static Point[] mergedPolygon;

    protected static int isAAboveB;
    protected static boolean isALeftB;

    protected static Point[] upperTangent;
    protected static Point[] lowerTangent;

    public static void main(String[] args) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("tangent.out"));
        BufferedReader br = new BufferedReader(new FileReader("tangent.inp"));
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = null;
        int T = Integer.parseInt(br.readLine().trim());
        for (int i = 0; i < T; i++) {
            int polygon1Count = Integer.parseInt(br.readLine().trim());
            polygon1 = new Point[polygon1Count];
            for (int j = 0; j < polygon1Count; j++) {
                st = new StringTokenizer(br.readLine().trim());
                int x = Integer.parseInt(st.nextToken());
                int y = Integer.parseInt(st.nextToken());
                polygon1[j] = new Point(x, y);
            }

            int polygon2Count = Integer.parseInt(br.readLine().trim());
            polygon2 = new Point[polygon2Count];
            for (int j = 0; j < polygon2Count; j++) {
                st = new StringTokenizer(br.readLine().trim());
                int x = Integer.parseInt(st.nextToken());
                int y = Integer.parseInt(st.nextToken());
                polygon2[j] = new Point(x, y);
            }

            determinePosition(polygon1, polygon2);
            if (!isALeftB) {
                Point[] temp1 = deepCopyArray(polygon1);
                polygon1 = polygon2;
                polygon2 = temp1;
                determinePosition(polygon1, polygon2);
            }
            polygon1Hull = convexHull(polygon1);
            polygon2Hull = convexHull(polygon2);

            upperTangent = findTangent(false);
            lowerTangent = findTangent(true);

            mergePolygonsWithTangents();
            double totalArea = polygonArea(mergedPolygon, mergedPolygon.length);
            if (isBInsideA(mergedPolygon, polygon1)) {
                totalArea -= polygonArea(polygon1, polygon1.length);
            }
            if (isBInsideA(mergedPolygon, polygon2)) {
                totalArea -= polygonArea(polygon2, polygon2.length);
            }
            sb.append(String.format("%.1f",
                    Math.round(totalArea * 10)
                            / 10.0))
                    .append('\n');
        }
        bw.write(sb.toString());
        bw.close();
        br.close();
    }

    public static Point[] convexHull(Point[] points) {
        Point[] copy = deepCopyArray(points);
        double minX = copy[0].x;
        double minY = copy[0].y;
        int minIndex = 0;
        int n = copy.length;
        for (int i = 1; i < n; i++) {
            double x = copy[i].x;
            double y = copy[i].y;
            if (x < minX || (x == minX && y < minY)) {
                minX = x;
                minY = y;
                minIndex = i;
            }
        }
        Point.swap(copy[0], copy[minIndex]);
        Point.p0 = copy[0];
        Arrays.sort(copy, 1, n);
        int resize = 1;
        for (int i = 1; i < n; i++) {
            while ((i < n - 1) &&
                    Point.ccw(Point.p0, copy[i], copy[i + 1]) == 0)
                i++;
            copy[resize++] = copy[i];
        }

        Deque<Point> stack = new ArrayDeque<>();
        stack.addLast(copy[0]);
        stack.addLast(copy[1]);
        stack.addLast(copy[2]);
        for (int i = 3; i < resize; i++) {
            while ((stack.size() > 1) &&
                    Point.ccw(underTheTop(stack), stack.peekLast(), copy[i]) != -1)
                stack.pollLast();
            stack.addLast(copy[i]);
        }
        int hullSize = stack.size();
        Point[] hull = new Point[hullSize];
        for (int i = 0; i < hullSize; i++) {
            hull[i] = stack.pollFirst();
        }
        return hull;
    }

    public static Point underTheTop(Deque<Point> stack) {
        Point p = stack.pollLast();
        Point result = stack.peekLast();
        stack.addLast(p);
        return result;
    }
}