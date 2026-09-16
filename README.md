# Baseball Segment Tree
세그먼트 트리 기반 실시간 야구 경기 구간 득점 조회 및 갱신 시스템

## 프로젝트 소개
 야구 경기 진행 중 실시간으로 변경되는 이닝별 득점 데이터를 효율적으로 관리하고, 특정 이닝 구간의 누적 득점을 빠르게 조회하기 위해 개발된 Java 기반 세그먼트 트리 시스템입니다.

### 사용한 기술
- Java (JDK 25)
- Gemini 3.6 Flash
- 세그먼트 트리

### 주요 알고리즘
- 득점 조회
```
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
```

- 득점 갱신
```
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
```
---
- **과목명**: 소프트웨어프로젝트
- **작성자**: 고효빈