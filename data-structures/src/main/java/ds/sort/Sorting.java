package ds.sort;

/**
 * 정렬 알고리즘 + 이분 탐색
 *
 * 자료구조는 아니지만 자료구조와 뗄 수 없는 짝이다. 정렬돼 있어야 이분 탐색이 되고,
 * 트리·힙은 "정렬 상태를 유지하는 자료구조"라고도 볼 수 있다.
 *
 *  알고리즘      평균         최악         공간      안정*   특징
 *  ──────────────────────────────────────────────────────────────────────────────
 *  버블          O(n²)        O(n²)        O(1)      O      교육용. 실무 사용 X
 *  선택          O(n²)        O(n²)        O(1)      X      교환 횟수 최소 (n번)
 *  삽입          O(n²)        O(n²)        O(1)      O      거의 정렬된 데이터에 O(n). 작은 배열에 빠름
 *  병합          O(n log n)   O(n log n)   O(n)      O      항상 일정. 연결 리스트·외부 정렬에 적합
 *  퀵            O(n log n)   O(n²)        O(log n)  X      실전 최속. 캐시 친화적
 *
 *  * 안정(stable): 같은 값의 원래 순서가 유지되는가. "이름순 정렬 후 나이순 정렬"에서 중요.
 *
 * 자바의 Arrays.sort는?
 *  - 기본형(int[] 등): 듀얼 피벗 퀵 정렬 (안정성 불필요)
 *  - 객체(Object[]): TimSort = 병합 + 삽입의 하이브리드 (안정 정렬이어야 하므로)
 *  → 두 가지가 다른 이유가 바로 위 표의 "안정" 열이다.
 */
public class Sorting {

    private Sorting() {
    }

    // ───────────────────────── O(n²) 셋 ─────────────────────────

    /**
     * 버블 정렬: 이웃끼리 비교해서 큰 것을 뒤로 "거품처럼" 밀어낸다.
     * 한 바퀴 돌면 가장 큰 값이 맨 뒤에 확정된다. n바퀴 돌면 끝.
     */
    public static void bubbleSort(int[] a) {
        int n = a.length;
        for (int end = n - 1; end > 0; end--) {       // 확정된 뒤쪽은 다시 안 본다
            boolean swapped = false;
            for (int i = 0; i < end; i++) {
                if (a[i] > a[i + 1]) {
                    swap(a, i, i + 1);
                    swapped = true;
                }
            }
            if (!swapped) break;                        // 한 바퀴 동안 교환이 없었으면 이미 정렬됨
        }
    }

    /**
     * 선택 정렬: 남은 것 중 최솟값을 "선택"해서 앞에 놓는다.
     * 비교는 많지만 교환은 딱 n-1번. 쓰기 비용이 비싼 매체(플래시)에서 의미 있음.
     */
    public static void selectionSort(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (a[j] < a[minIdx]) minIdx = j;
            }
            if (minIdx != i) swap(a, i, minIdx);
        }
    }

    /**
     * 삽입 정렬: 카드 정리하듯이. 앞쪽은 정렬돼 있다고 보고, 새 카드를 알맞은 위치에 끼워 넣는다.
     * 거의 정렬된 데이터면 거의 안 움직여서 O(n). 그래서 TimSort, 퀵 정렬이 작은 구간에서 이걸 쓴다.
     */
    public static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > key) {              // key보다 큰 것들을 한 칸씩 뒤로 민다
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;                             // 빈 자리에 key
        }
    }

    // ───────────────────────── O(n log n) 둘 ─────────────────────────

    /**
     * 병합 정렬: 반으로 쪼개고(재귀), 정렬된 두 반쪽을 합친다(merge).
     * "분할 정복"의 교과서. 쪼개는 깊이가 log n, 각 깊이에서 합치는 비용이 n → O(n log n).
     * 단점: 합칠 때 임시 배열이 필요 → O(n) 추가 메모리.
     */
    public static void mergeSort(int[] a) {
        if (a.length < 2) return;
        int[] tmp = new int[a.length];
        mergeSort(a, tmp, 0, a.length - 1);
    }

    private static void mergeSort(int[] a, int[] tmp, int lo, int hi) {
        if (lo >= hi) return;                           // 원소 1개 = 이미 정렬됨
        int mid = (lo + hi) >>> 1;                      // (lo+hi)/2 인데 오버플로 안전
        mergeSort(a, tmp, lo, mid);                     // 왼쪽 반 정렬
        mergeSort(a, tmp, mid + 1, hi);                 // 오른쪽 반 정렬
        merge(a, tmp, lo, mid, hi);                     // 합치기
    }

    /** 정렬된 a[lo..mid]와 a[mid+1..hi]를 합쳐 a[lo..hi]를 정렬 상태로 */
    private static void merge(int[] a, int[] tmp, int lo, int mid, int hi) {
        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) {
            // <= 를 써야 같은 값일 때 왼쪽이 먼저 → 안정 정렬
            tmp[k++] = (a[i] <= a[j]) ? a[i++] : a[j++];
        }
        while (i <= mid) tmp[k++] = a[i++];             // 왼쪽 남은 것
        while (j <= hi) tmp[k++] = a[j++];              // 오른쪽 남은 것
        System.arraycopy(tmp, lo, a, lo, hi - lo + 1);  // 결과를 원본에 복사
    }

    /**
     * 퀵 정렬: 기준값(pivot)을 하나 고르고, 작은 것은 왼쪽·큰 것은 오른쪽으로 나눈 뒤(partition) 양쪽을 재귀.
     * 병합 정렬과 달리 제자리(in-place)에서 하므로 추가 메모리가 거의 없고, 메모리 접근이 연속적이라 실전에서 가장 빠르다.
     *
     * 최악 O(n²): pivot이 매번 최솟값/최댓값이면 (예: 이미 정렬된 배열에서 첫 원소를 pivot으로).
     * 그래서 여기서는 가운데 값을 pivot으로 쓴다. 실무 구현은 "세 값의 중앙값"이나 랜덤을 쓴다.
     */
    public static void quickSort(int[] a) {
        quickSort(a, 0, a.length - 1);
    }

    private static void quickSort(int[] a, int lo, int hi) {
        if (lo >= hi) return;
        int p = partition(a, lo, hi);
        quickSort(a, lo, p - 1);
        quickSort(a, p + 1, hi);
    }

    /**
     * Lomuto 분할: 마지막 원소를 pivot으로 쓰되, 가운데 값을 먼저 마지막으로 옮겨 둔다.
     * i는 "pivot보다 작은 구간의 끝". j가 작은 값을 만날 때마다 i를 늘리고 교환.
     * 끝나면 pivot을 i+1 자리에 놓는다 → 그 자리는 최종 위치로 확정.
     */
    private static int partition(int[] a, int lo, int hi) {
        swap(a, (lo + hi) >>> 1, hi);                   // 가운데 값을 pivot으로 (정렬된 입력에서 O(n²) 방지)
        int pivot = a[hi];
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (a[j] < pivot) swap(a, ++i, j);
        }
        swap(a, i + 1, hi);
        return i + 1;
    }

    // ───────────────────────── 이분 탐색 ─────────────────────────

    /**
     * 정렬된 배열에서 target의 인덱스. 없으면 -1. O(log n)
     * 한 번 비교할 때마다 범위가 절반 → 10억 개도 30번이면 끝난다.
     * BST의 contains, DB 인덱스 검색, 그리고 "git bisect"가 전부 이 아이디어.
     *
     * 흔한 버그 두 가지:
     *   (lo + hi) / 2  → lo + hi 가 int 범위를 넘으면 음수가 된다. >>> 1 또는 lo + (hi - lo) / 2 로.
     *   while (lo < hi) vs (lo <= hi), hi = mid vs mid - 1  → 경계 조건 하나 틀리면 무한 루프.
     */
    public static int binarySearch(int[] sorted, int target) {
        int lo = 0, hi = sorted.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            if (sorted[mid] == target) return mid;
            if (sorted[mid] < target) lo = mid + 1;     // 오른쪽 반
            else hi = mid - 1;                          // 왼쪽 반
        }
        return -1;
    }

    /**
     * lower bound: target 이상인 첫 번째 위치. 없으면 length.
     * 중복이 있을 때 "몇 번째부터인가", 또는 "끼워 넣을 자리"를 찾을 때 쓴다.
     * 범위 검색(from ~ to 개수 세기)은 lowerBound(to+1) - lowerBound(from).
     */
    public static int lowerBound(int[] sorted, int target) {
        int lo = 0, hi = sorted.length;                 // hi가 length인 것에 주의 (답이 length일 수 있음)
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (sorted[mid] < target) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    // ───────────────────────── 직접 해볼 과제 ─────────────────────────
    // TODO 1. 100만 개 랜덤 배열로 5개 정렬의 시간을 재서 표로 만들어 보기. O(n²) 셋은 1만 개로 줄여서.
    //         그리고 "이미 정렬된 배열"로 다시 재 보기 — 삽입 정렬과 퀵 정렬의 결과가 어떻게 달라지나?
    // TODO 2. 안정성 확인: (값, 원래인덱스) 쌍 배열을 값으로만 정렬했을 때, 같은 값끼리 원래 순서가 유지되는지
    //         병합 정렬과 퀵 정렬에서 각각 확인해 보기.
    // TODO 3. upperBound (target 초과인 첫 위치) 구현하고, 정렬된 배열에서 target의 개수를 O(log n)에 세기.
    // TODO 4. 퀵 정렬에서 구간 길이가 16 이하이면 삽입 정렬로 전환하도록 바꿔 보기. 빨라지나?
    // TODO 5. 계수 정렬(counting sort): 값의 범위가 작으면(0~100 점수) 비교 없이 O(n)에 정렬할 수 있다. 왜 가능한가?
}
