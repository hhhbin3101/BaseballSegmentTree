import java.util.Arrays;

public class BaseballSegmentTree {
    private int[] scores;
    private int[] tree;
    private  int n;

    // 경기 데이터 입력 받은 후 배열 저장 및 세그먼트 트리 생성
    public BaseballSegmentTree(int[] scores) {
        this.n = scores.length;
        this.scores = scores.clone();
        this.tree = new int[4 * n];

        if (n > 0) {
            build(1, 1, n);
        }
    }

    // 세그먼트 트리 생성
    private  int build(int node, int start, int end) {
        if (start == end) {
            return tree[node] = scores[start - 1];
        }
        int mid = (start + end) / 2;
        int leftSum = build(node * 2, start, mid);
        int rightSum = build(node * 2 + 1, mid + 1, end);
        return tree[node] = leftSum + rightSum;
    }

    // 구간 득점 조회
    public  int Qurey(int qStart, int qEnd) {
        return Qurey(1, 1, n, qStart, qEnd);
    }

    private int Qurey(int node, int start, int end, int qStart, int qEnd) {
        // 겹치지 않을 경우
        if (qEnd < start || end < qStart) {
            return 0;
        }
        // 완전히 겹치는 경우
        if (qStart <= start && end <= qEnd) {
            return tree[node];
        }
        // 일부만 겹치는 경우
        int mid = (start + end) / 2;
        int leftVal = Qurey(node * 2, start, mid, qStart, qEnd);
        int rightVal = Qurey(node * 2 + 1, mid + 1, end, qStart, qEnd);
        return leftVal + rightVal;
    }

    // 득점 갱신
    public void Update(int inning, int newScore) {
        Update(1, 1, n, inning, newScore);
    }

    private void Update(int node, int start, int end, int inning, int newScore) {
        if (inning < start || inning > end){
            return;
        }
        if (start == end) {
            scores[inning - 1] = newScore;
            tree[node] = newScore;
            return;
        }
        int mid = (start + end) / 2;
        if (inning <= mid) {
            Update(node * 2, start, mid, inning, newScore);
        }
        else {
            Update(node * 2 + 1, mid + 1, end, inning, newScore);
        }
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    public int[] getScores(){
        return scores.clone();
    }

    // 시뮬레이션
    public static void main(String[] args) {
        // 경기 데이터 입력
        int[] kiaScores = {2, 0, 3, 6, 10, 0, 0, 2, 0};
        System.out.println("=== 경기 데이터 입력 및 세그먼트 트리 생성 ===");
        BaseballSegmentTree bgSystem = new BaseballSegmentTree(kiaScores);
        System.out.println("초기 이닝별 득점: " + Arrays.toString(bgSystem.getScores()));

        // 구간 득점 조회
        System.out.println("\n=== 구간 득점 조회 ===");
        int res4to7 = bgSystem.Qurey(4, 7); // 4회 ~ 7회 누적 득점 계산
        System.out.println("4회 ~ 7회 누적 득점 조회 결과: " + res4to7 + "점"); // 결과값: 16점

        int totalScore = bgSystem.Qurey(1, 9);
        System.out.println("1회 ~ 9회 전체 총 득점 조회 결과: " + totalScore + "점"); // 결과값: 23점

        // 득점 갱신
        System.out.println("\n=== 득점 갱신 ===");
        System.out.println("7회 득점 변경: 0점 -> 3점");
        bgSystem.Update(7, 3);
        System.out.println("갱신 후 이닝별 득점: " + Arrays.toString(bgSystem.getScores()));

        System.out.println("\n=== 결과 검증 ===");
        int res4to7Update = bgSystem.Qurey(4, 7); // 결과값: 19점
        System.out.println("갱신 후 4회 ~ 7회 누적 득점 조회: " + res4to7Update + "점");
    }
}