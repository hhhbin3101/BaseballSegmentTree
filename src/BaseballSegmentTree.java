import java.util.Arrays;
import java.util.Scanner;

public class BaseballSegmentTree {
    private int[] scores;
    private int[] tree;
    private int n;

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
    private int build(int node, int start, int end) {
        if (start == end) {
            return tree[node] = scores[start - 1];
        }
        int mid = (start + end) / 2;
        int leftSum = build(node * 2, start, mid);
        int rightSum = build(node * 2 + 1, mid + 1, end);
        return tree[node] = leftSum + rightSum;
    }

    // 구간 득점 조회
    public int ScoreInquiry(int iStart, int iEnd) {
        if (iStart < 1 || iEnd > n || iStart > iEnd) {
            System.out.println("잘못된 이닝 범위입니다.");
            return -1;
        }
        return ScoreInquiry(1, 1, n, iStart, iEnd);
    }

    private int ScoreInquiry(int node, int start, int end, int iStart, int iEnd) {
        // 겹치지 않을 경우
        if (iEnd < start || end < iStart) {
            return 0;
        }
        // 완전히 겹치는 경우
        if (iStart <= start && end <= iEnd) {
            return tree[node];
        }
        // 일부만 겹치는 경우
        int mid = (start + end) / 2;
        int leftVal = ScoreInquiry(node * 2, start, mid, iStart, iEnd);
        int rightVal = ScoreInquiry(node * 2 + 1, mid + 1, end, iStart, iEnd);
        return leftVal + rightVal;
    }

    // 득점 갱신
    public void ScoreUpdate(int inning, int newScore) {
        if (inning < 1 || inning > n) {
            System.out.println("잘못된 이닝입니다.");
            return;
        }
        if (newScore < 0) {
            System.out.println("잘못 입력하셨습니다.");
            return;
        }
        ScoreUpdate(1, 1, n, inning, newScore);
        System.out.println(inning + "회 점수가 " + newScore + "점으로 성공적으로 변경되었습니다.");
    }

    private void ScoreUpdate(int node, int start, int end, int inning, int newScore) {
        if (inning < start || inning > end) {
            return;
        }
        if (start == end) {
            scores[inning - 1] = newScore;
            tree[node] = newScore;
            return;
        }
        int mid = (start + end) / 2;
        if (inning <= mid) {
            ScoreUpdate(node * 2, start, mid, inning, newScore);
        } else {
            ScoreUpdate(node * 2 + 1, mid + 1, end, inning, newScore);
        }
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    public int[] getScores() {
        return scores.clone();
    }

    public int getN() {
        return n;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BaseballSegmentTree bgSystem = null;

        System.out.println("================================================");
        System.out.println(" 실시간 야구 경기 구간 득점 조회 및 갱신 시스템 ");
        System.out.println("================================================");

        // 초기 경기 데이터 설정
        System.out.print("진행할 총 이닝 수를 입력하세요 (기본 9이닝): ");
        int totalInnings = scanner.nextInt();
        while (totalInnings <= 0) {
            System.out.print("1 이상의 이닝 수를 입력해주세요: ");
            totalInnings = scanner.nextInt();
        }

        int[] initialScores = new int[totalInnings];
        System.out.println("\n1회부터 " + totalInnings + "회까지의 득점을 차례로 입력하세요:");
        for (int i = 0; i < totalInnings; i++) {
            System.out.print((i + 1) + "회 득점: ");
            initialScores[i] = scanner.nextInt();
        }

        bgSystem = new BaseballSegmentTree(initialScores);
        System.out.println("\n경기 데이터 세그먼트 트리가 성공적으로 생성되었습니다.");
        System.out.println("현재 이닝별 득점: " + Arrays.toString(bgSystem.getScores()));

        // 대화형 메뉴 루프
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n-------------------------------");
            System.out.println(" 1. 구간 득점 조회");
            System.out.println(" 2. 실시간 득점 갱신");
            System.out.println(" 3. 현재 전체 이닝 현황 출력");
            System.out.println(" 4. 이닝 데이터 전체 재설정");
            System.out.println(" 0. 프로그램 종료");
            System.out.println("-------------------------------");
            System.out.print("메뉴 선택: ");

            int menu = scanner.nextInt();

            switch (menu) {
                case 1:
                    System.out.print("\n조회할 시작 이닝(회): ");
                    int startInning = scanner.nextInt();
                    System.out.print("조회할 종료 이닝(회): ");
                    int endInning = scanner.nextInt();

                    int sum = bgSystem.ScoreInquiry(startInning, endInning);
                    if (sum != -1) {
                        System.out.println("[" + startInning + "회 ~ " + endInning + "회] 누적 득점: " + sum + "점");
                    }
                    break;

                case 2:
                    System.out.print("\n득점을 변경할 이닝(회): ");
                    int targetInning = scanner.nextInt();
                    System.out.print("새로운 득점 입력: ");
                    int newScore = scanner.nextInt();

                    bgSystem.ScoreUpdate(targetInning, newScore);
                    System.out.println("현재 이닝별 득점: " + Arrays.toString(bgSystem.getScores()));
                    break;

                case 3:
                    System.out.println("\n[현재 경기 득점 현황]");
                    int[] currentScores = bgSystem.getScores();
                    for (int i = 0; i < currentScores.length; i++) {
                        System.out.printf("%2d회: %2d점 | ", (i + 1), currentScores[i]);
                        if ((i + 1) % 3 == 0)
                            System.out.println();
                    }
                    System.out.println("\n현재 총 득점: " + bgSystem.ScoreInquiry(1, bgSystem.getN()) + "점");
                    break;

                case 4:
                    System.out.print("\n새로운 총 이닝 수 입력: ");
                    totalInnings = scanner.nextInt();
                    int[] newScores = new int[totalInnings];
                    System.out.println("1회부터 " + totalInnings + "회까지 득점을 입력하세요:");
                    for (int i = 0; i < totalInnings; i++) {
                        System.out.print((i + 1) + "회 득점: ");
                        newScores[i] = scanner.nextInt();
                    }
                    bgSystem = new BaseballSegmentTree(newScores);
                    System.out.println("세그먼트 트리가 다시 생성되었습니다.");
                    System.out.println("현재 이닝별 득점: " + Arrays.toString(bgSystem.getScores()));
                    break;

                case 0:
                    System.out.println("\n프로그램을 종료합니다.");
                    isRunning = false;
                    break;

                default:
                    System.out.println("올바른 메뉴 번호를 입력해주세요.");
                    break;
            }
        }
        scanner.close();
    }
}