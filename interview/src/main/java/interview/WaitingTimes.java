package interview;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * [모의 라이브 코딩 #5] 고객센터 상담 배정 — 각 요청의 대기 시간
 *
 * 문제
 *   상담원 1명. 요청은 들어온 순서대로(FIFO) 처리. 요청마다 처리 시간(분)이 주어진다.
 *   각 요청이 몇 분 기다렸다가 시작되는지 구하라.
 *   {5, 3, 8} → {0, 5, 8}
 *
 * 핵심
 *   "먼저 들어온 것부터" = FIFO = Queue.     ← 이름을 말하기 전에 "가장 먼저? 가장 최근?" 자문 (#4 교훈)
 *   그런데 코드에 Queue 객체는 없다. 배열을 0번부터 순서대로 도는 것이 이미 FIFO이기 때문.
 *   "개념은 Queue, 구현은 배열 순회" — 실무에서 아주 흔하다.
 *
 *   i번 요청의 대기 시간 = 0 ~ i-1 번 처리 시간의 합  → 누적 합(prefix sum) 패턴.
 *   실제 시간이 흐르는 시뮬레이션이 아니다. 문제에 시계가 없으면 산수다.
 *
 * Queue 객체가 진짜 필요해지는 때
 *   - 요청이 실행 중에 계속 들어올 때 (실제 콜센터, 메시지 큐 컨슈머)
 *   - 상담원이 여러 명이라 "다음 빈 상담원에게 다음 요청"을 배정할 때
 *   - 우선순위가 있을 때 → Queue가 아니라 PriorityQueue(힙)
 *   실무의 MQ(Kafka, RabbitMQ, IBM MQ)가 바로 프로세스 사이에 걸친 Queue.
 *
 * 시간 복잡도 O(n) — 한 바퀴, 칸마다 덧셈 하나
 */
public class WaitingTimes {

    private WaitingTimes() {
    }

    /** 누적 합. 면접에서 도달한 풀이 */
    public static int[] waitingTimes(int[] durations) {
        int[] result = new int[durations.length];
        int elapsed = 0;                              // 지금까지 앞사람들이 쓴 시간

        for (int i = 0; i < durations.length; i++) {
            result[i] = elapsed;                      // 내 대기 = 앞사람들 합
            elapsed += durations[i];                  // 내 처리 시간을 더해 다음 사람에게 넘김
        }
        return result;
    }

    /**
     * 비교용: 실제 Queue 객체를 쓰는 버전.
     * 결과는 같지만 "배열 → Queue에 전부 넣기 → 다시 빼기" 로 같은 일을 두 번 한다.
     * 요청이 동적으로 추가되는 상황(아래 Simulator)이 아니면 이렇게 쓸 이유가 없다.
     */
    public static int[] waitingTimesWithQueue(int[] durations) {
        Queue<Integer> queue = new ArrayDeque<>();
        for (int d : durations) queue.offer(d);       // 들어온 순서대로 줄 세우기

        int[] result = new int[durations.length];
        int elapsed = 0;
        int i = 0;
        while (!queue.isEmpty()) {
            int duration = queue.poll();              // 맨 앞(가장 먼저 온) 요청
            result[i++] = elapsed;
            elapsed += duration;
        }
        return result;
    }

    /**
     * Queue 객체가 "진짜" 필요한 모습: 요청이 처리 도중에도 계속 들어온다.
     * 콜센터 서버, MQ 컨슈머가 이 구조다. 배열 하나로는 표현이 안 된다.
     */
    public static class Simulator {
        private final Queue<Integer> pending = new ArrayDeque<>();
        private int elapsed = 0;

        /** 새 요청 도착 (언제든 호출 가능) */
        public void arrive(int duration) {
            pending.offer(duration);
        }

        /** 상담원이 다음 요청 하나를 처리. 그 요청의 대기 시간을 돌려준다. 대기 중인 요청이 없으면 -1 */
        public int serveNext() {
            Integer duration = pending.poll();        // 비어 있으면 null (pop 과 달리 예외 없음)
            if (duration == null) return -1;
            int waited = elapsed;
            elapsed += duration;
            return waited;
        }

        public int pendingCount() {
            return pending.size();
        }
    }

    // ───────────────────────── 직접 해볼 과제 ─────────────────────────
    // TODO 1. 상담원이 k명일 때 각 요청의 대기 시간. "가장 먼저 비는 상담원"을 찾아야 한다 → PriorityQueue<종료시각>.
    //         (#2 TODO 2 "회의실 최소 개수" 와 같은 뼈대)
    // TODO 2. 요청마다 도착 시각이 따로 주어지면? 상담원이 놀고 있다가 요청이 오면 대기 0.
    //         elapsed = max(elapsed, 도착시각) 한 줄이 추가된다.
    // TODO 3. 누적 합 배열 prefix[i] = durations[0..i-1] 합 을 만들어 두고, "a번부터 b번까지 처리 시간 합"을 O(1)에 구하기.
    //         SQL의 SUM(...) OVER (ORDER BY ...) 가 같은 것.
    // TODO 4. 대기 시간의 평균과 최댓값. 콜센터 KPI가 이것이다. 한 바퀴 안에서 같이 구할 수 있나?
}
