import java.util.Arrays;
import java.util.Scanner;

public class BaseballSegmentTree {
    private final int[] scores;
    private final int[] tree;
    private final int totalInnings;

    // 경기 데이터 입력 받은 후 배열 저장 및 세그먼트 트리 생성
    public BaseballSegmentTree(int[] scores) {
        if (scores == null || scores.length == 0) {
            throw new IllegalArgumentException("경기 데이터는 1이닝 이상 존재해야 합니다.");
        }

        this.totalInnings = scores.length;
        this.scores = scores.clone();
        this.tree = new int[4 * totalInnings];

        buildTree(1, 1, totalInnings);
    }

    public int queryScore(int startInning, int endInning) {
        validateInningRange(startInning, endInning);
        return queryRecursive(1, 1, totalInnings, startInning, endInning);
    }

    public void updateScore(int inning, int newScore) {
        validateSingleInning(inning);
        validateScore(newScore);

        updateRecursive(1, 1, totalInnings, inning, newScore);
    }

    public int[] getScores() {
        return scores.clone();
    }

    public int getTotalInnings() {
        return totalInnings;
    }

    // 세그먼트 트리 생성
    private int buildTree(int node, int start, int end) {
        if (start == end) {
            return tree[node] = scores[start - 1];
        }
        int mid = (start + end) / 2;
        int leftSum = buildTree(node * 2, start, mid);
        int rightSum = buildTree(node * 2 + 1, mid + 1, end);
        return tree[node] = leftSum + rightSum;
    }

    // 구간 득점 조회
    private int queryRecursive(int node, int start, int end, int qStart, int qEnd) {
        // 1. 겹치지 않을 경우
        if (qEnd < start || end < qStart) {
            return 0;
        }
        // 2. 완전히 겹치는 경우
        if (qStart <= start && end <= qEnd) {
            return tree[node];
        }
        // 일부만 겹치는 경우
        int mid = (start + end) / 2;
        int leftVal = queryRecursive(node * 2, start, mid, qStart, qEnd);
        int rightVal = queryRecursive(node * 2 + 1, mid + 1, end, qStart, qEnd);
        return leftVal + rightVal;
    }

    // 득점 갱신
    private void updateRecursive(int node, int start, int end, int inning, int newScore) {
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
            updateRecursive(node * 2, start, mid, inning, newScore);
        } else {
            updateRecursive(node * 2 + 1, mid + 1, end, inning, newScore);
        }
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    // 예외 검증
    private void validateInningRange(int startInning, int endInning) {
        if (startInning < 1 || endInning > totalInnings || startInning > endInning) {
            throw new IllegalArgumentException(
                    String.format("잘못된 이닝 범위 입니다: %d회 ~ %d회 (전체 %d이닝)", startInning, endInning, totalInnings));
        }
    }

    private void validateSingleInning(int inning) {
        if (inning < 1 || inning > totalInnings) {
            throw new IllegalArgumentException(
                    String.format("존재하지 않는 이닝입니다: %d회 (전체 %d이닝)", inning, totalInnings));
        }
    }

    private void validateScore(int score) {
        if (score < 0) {
            throw new IllegalArgumentException("득점은 0점 이상이어야 합니다.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        printHeader();

        int totalInnings = readValidTotalInnings(scanner);
        int[] initialScores = readInningScores(scanner, totalInnings);
        BaseballSegmentTree bgSystem = new BaseballSegmentTree(initialScores);

        System.out.println("\n세그먼트 트리가 성공적으로 생성되었습니다.");
        printScoresSummary(bgSystem);

        runMenuLoop(scanner, bgSystem);
        scanner.close();
    }

    private static void runMenuLoop(Scanner scanner, BaseballSegmentTree bgSystem) {
        boolean isRunning = true;
        while (isRunning) {
            printMenu();
            int menu = readInt(scanner);

            switch (menu) {
                case 1:
                    handleQuery(scanner, bgSystem);
                    break;
                case 2:
                    handleUpdate(scanner, bgSystem);
                    break;
                case 3:
                    printScoresSummary(bgSystem);
                    break;
                case 4:
                    bgSystem = handleReset(scanner);
                    break;
                case 0:
                    System.out.println("\n프로그램을 종료합니다. 수고하셨습니다!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("올바른 메뉴 번호를 입력해주세요.");
                    break;
            }
        }
    }

    private static void handleQuery(Scanner scanner, BaseballSegmentTree bgSystem) {
        System.out.print("\n조회할 시작 이닝(회): ");
        int start = readInt(scanner);
        System.out.print("조회할 종료 이닝(회): ");
        int end = readInt(scanner);

        try {
            int sum = bgSystem.queryScore(start, end);
            System.out.printf("[%d회 ~ %d회] 누적 득점: %d점%n", start, end, sum);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void handleUpdate(Scanner scanner, BaseballSegmentTree bgSystem) {
        System.out.print("\n득점을 변경할 이닝(회): ");
        int inning = readInt(scanner);
        System.out.print("새로운 득점 입력: ");
        int newScore = readInt(scanner);

        try {
            bgSystem.updateScore(inning, newScore);
            System.out.printf("%d회 점수가 %d점으로 갱신되었습니다.%n", inning, newScore);
            System.out.println("현재 이닝별 득점: " + Arrays.toString(bgSystem.getScores()));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static BaseballSegmentTree handleReset(Scanner scanner) {
        System.out.println("\n[이닝 데이터 재설정]");
        int totalInnings = readValidTotalInnings(scanner);
        int[] newScores = readInningScores(scanner, totalInnings);
        System.out.println("세그먼트 트리가 성공적으로 재설정되었습니다.");
        return new BaseballSegmentTree(newScores);
    }

    private static int readValidTotalInnings(Scanner scanner) {
        System.out.print("진행할 총 이닝 수를 입력하세요: ");
        int innings = readInt(scanner);
        while (innings <= 0) {
            System.out.print("1 이상의 이닝 수를 입력해주세요: ");
            innings = readInt(scanner);
        }
        return innings;
    }

    private static int[] readInningScores(Scanner scanner, int totalInnings) {
        int[] scores = new int[totalInnings];
        System.out.println("1회부터 " + totalInnings + "회까지의 득점을 차례로 입력하세요:");
        for (int i = 0; i < totalInnings; i++) {
            System.out.print((i + 1) + "회 득점: ");
            scores[i] = readInt(scanner);
            while (scores[i] < 0) {
                System.out.print("득점은 0 이상이어야 합니다. 다시 입력: ");
                scores[i] = readInt(scanner);
            }
        }
        return scores;
    }

    private static int readInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("문자가 아닌 숫자만 입력해주세요: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static void printHeader() {
        System.out.println("================================================");
        System.out.println(" 실시간 야구 경기 구간 득점 조회 및 갱신 시스템 ");
        System.out.println("================================================");
    }

    private static void printMenu() {
        System.out.println("\n-------------------------------");
        System.out.println(" 1. 구간 득점 조회 (queryScore)");
        System.out.println(" 2. 실시간 득점 갱신 (updateScore)");
        System.out.println(" 3. 현재 전체 이닝 현황 출력");
        System.out.println(" 4. 이닝 데이터 전체 재설정");
        System.out.println(" 0. 프로그램 종료");
        System.out.println("-------------------------------");
        System.out.print("메뉴 선택: ");
    }

    private static void printScoresSummary(BaseballSegmentTree bgSystem) {
        System.out.println("\n[현재 경기 득점 현황]");
        int[] scores = bgSystem.getScores();
        for (int i = 0; i < scores.length; i++) {
            System.out.printf("%2d회: %2d점 | ", (i + 1), scores[i]);
            if ((i + 1) % 3 == 0)
                System.out.println();
        }
        if (scores.length % 3 != 0)
            System.out.println();
        System.out.println("현재 총 득점: " + bgSystem.queryScore(1, bgSystem.getTotalInnings()) + "점");
    }
}