package ds.graph;

import ds.list.MyArrayList;
import ds.map.MyHashMap;
import ds.queue.MyQueue;
import ds.stack.MyStack;

/**
 * 그래프 — 정점(vertex)과 간선(edge)으로 "관계"를 표현
 *
 * 트리는 그래프의 특수한 경우(사이클 없음, 루트 있음)다. 그래프는 아무렇게나 연결될 수 있다.
 *
 *     A --- B
 *     |     |
 *     C --- D --- E
 *
 * 저장 방식: 인접 리스트 (adjacency list)
 *   정점마다 "나와 연결된 정점 목록"을 둔다.   A: [B, C]   B: [A, D]   C: [A, D]   D: [B, C, E]   E: [D]
 *   간선이 적은(sparse) 현실 그래프에 적합. SNS 친구 관계, 지도 도로망, 웹 링크 전부 이쪽.
 *   (반대편은 인접 행렬: n×n 2차원 배열. 간선 유무 확인은 O(1)이지만 메모리 O(n²))
 *
 * 두 가지 기본 탐색
 *
 *   BFS (너비 우선, Breadth-First)  — 큐 사용
 *     가까운 것부터 차례로. A에서 시작하면 A → (B, C) → (D) → (E) 순서로 "물결처럼" 퍼진다.
 *     ⇒ 가중치 없는 그래프에서 **최단 경로** (몇 다리 건너면 되나). "3촌 이내 친구", 미로 최단 거리.
 *
 *   DFS (깊이 우선, Depth-First)  — 스택(또는 재귀) 사용
 *     한 방향으로 끝까지 파고들다 막히면 돌아온다. A → B → D → E (막힘) → C.
 *     ⇒ 경로 존재 여부, 사이클 탐지, 연결 요소 개수, 미로의 "모든 길" 탐색, 위상 정렬.
 *
 * 시간 복잡도: BFS, DFS 모두 O(V + E)  (정점 수 + 간선 수. 모든 정점과 간선을 한 번씩 본다)
 *
 * 어디에 쓰이나
 *  - 지도·내비게이션 (다익스트라는 BFS에 가중치를 더한 것)
 *  - 추천 시스템 ("친구의 친구"), 소셜 네트워크 분석
 *  - 빌드 도구의 의존성 순서 (Maven이 라이브러리 설치 순서를 정하는 법 = 위상 정렬)
 *  - 네트워크 라우팅, 컴파일러의 데드 코드 제거, 가비지 컬렉터의 도달 가능성 분석
 */
public class MyGraph<V> {

    /** 정점 → 인접 정점 목록. 우리가 만든 MyHashMap + MyArrayList를 그대로 쓴다 */
    private final MyHashMap<V, MyArrayList<V>> adjacency = new MyHashMap<>();
    private final MyArrayList<V> vertices = new MyArrayList<>();   // 삽입 순서 기억용 (HashMap은 순서가 없으므로)
    private final boolean directed;

    /** directed=false 면 무방향 (A-B 추가 시 B-A도 추가), true 면 방향 그래프 */
    public MyGraph(boolean directed) {
        this.directed = directed;
    }

    public MyGraph() {
        this(false);
    }

    public void addVertex(V v) {
        if (!adjacency.containsKey(v)) {
            adjacency.put(v, new MyArrayList<>());
            vertices.add(v);
        }
    }

    public void addEdge(V from, V to) {
        addVertex(from);
        addVertex(to);
        adjacency.get(from).add(to);
        if (!directed) adjacency.get(to).add(from);
    }

    public MyArrayList<V> neighbors(V v) {
        MyArrayList<V> list = adjacency.get(v);
        return list == null ? new MyArrayList<>() : list;
    }

    public int vertexCount() {
        return vertices.size();
    }

    // ───────────────────────── BFS ─────────────────────────

    /**
     * 너비 우선 탐색. 방문 순서를 돌려준다.
     *
     *   1. 시작점을 큐에 넣고 방문 표시
     *   2. 큐에서 하나 꺼낸다
     *   3. 그 이웃 중 아직 안 간 곳을 전부 큐에 넣고 방문 표시
     *   4. 큐가 빌 때까지 2~3 반복
     *
     * "방문 표시"를 큐에 넣을 때 하는 게 중요하다. 꺼낼 때 하면 같은 정점이 큐에 여러 번 들어간다.
     */
    public MyArrayList<V> bfs(V start) {
        MyArrayList<V> order = new MyArrayList<>();
        MyHashMap<V, Boolean> visited = new MyHashMap<>();
        MyQueue<V> queue = new MyQueue<>();

        queue.offer(start);
        visited.put(start, true);

        while (!queue.isEmpty()) {
            V cur = queue.poll();
            order.add(cur);
            MyArrayList<V> nbrs = neighbors(cur);
            for (int i = 0; i < nbrs.size(); i++) {
                V next = nbrs.get(i);
                if (!visited.containsKey(next)) {
                    visited.put(next, true);
                    queue.offer(next);
                }
            }
        }
        return order;
    }

    /**
     * BFS로 최단 거리 (간선 수 기준). 도달 불가면 -1.
     * BFS는 "거리 1인 것들 → 거리 2인 것들 → ..." 순으로 방문하므로, 처음 도착했을 때가 최단이다.
     */
    public int shortestDistance(V from, V to) {
        if (from.equals(to)) return 0;
        MyHashMap<V, Integer> distance = new MyHashMap<>();   // 방문 표시 + 거리를 한 번에
        MyQueue<V> queue = new MyQueue<>();

        queue.offer(from);
        distance.put(from, 0);

        while (!queue.isEmpty()) {
            V cur = queue.poll();
            int d = distance.get(cur);
            MyArrayList<V> nbrs = neighbors(cur);
            for (int i = 0; i < nbrs.size(); i++) {
                V next = nbrs.get(i);
                if (!distance.containsKey(next)) {
                    if (next.equals(to)) return d + 1;   // 처음 도착 = 최단
                    distance.put(next, d + 1);
                    queue.offer(next);
                }
            }
        }
        return -1;
    }

    // ───────────────────────── DFS ─────────────────────────

    /** 깊이 우선 탐색 — 재귀 버전. 콜 스택이 "스택" 역할을 한다 */
    public MyArrayList<V> dfs(V start) {
        MyArrayList<V> order = new MyArrayList<>();
        dfs(start, new MyHashMap<>(), order);
        return order;
    }

    private void dfs(V cur, MyHashMap<V, Boolean> visited, MyArrayList<V> order) {
        visited.put(cur, true);
        order.add(cur);
        MyArrayList<V> nbrs = neighbors(cur);
        for (int i = 0; i < nbrs.size(); i++) {
            V next = nbrs.get(i);
            if (!visited.containsKey(next)) {
                dfs(next, visited, order);   // 이웃 하나로 끝까지 파고든다
            }
        }
    }

    /**
     * 깊이 우선 탐색 — 반복문 버전. BFS 코드에서 큐를 스택으로 바꾸면 DFS가 된다.
     * (정점 수가 수만 개면 재귀는 StackOverflowError가 나므로 실무에서는 이 버전)
     * 방문 순서가 재귀 버전과 다를 수 있다 — 이웃을 거꾸로 꺼내기 때문. 둘 다 올바른 DFS다.
     */
    public MyArrayList<V> dfsIterative(V start) {
        MyArrayList<V> order = new MyArrayList<>();
        MyHashMap<V, Boolean> visited = new MyHashMap<>();
        MyStack<V> stack = new MyStack<>();

        stack.push(start);
        while (!stack.isEmpty()) {
            V cur = stack.pop();
            if (visited.containsKey(cur)) continue;   // 스택에는 중복이 들어갈 수 있어 꺼낼 때 검사
            visited.put(cur, true);
            order.add(cur);
            MyArrayList<V> nbrs = neighbors(cur);
            for (int i = nbrs.size() - 1; i >= 0; i--) {   // 거꾸로 넣어야 첫 이웃이 먼저 pop된다
                V next = nbrs.get(i);
                if (!visited.containsKey(next)) stack.push(next);
            }
        }
        return order;
    }

    /** from에서 to로 갈 수 있나 (DFS 응용) */
    public boolean hasPath(V from, V to) {
        return dfs(from).contains(to);
    }

    /** 연결 요소(서로 이어진 덩어리) 개수. 아직 안 간 정점에서 DFS를 시작할 때마다 +1 */
    public int connectedComponents() {
        MyHashMap<V, Boolean> visited = new MyHashMap<>();
        int count = 0;
        for (int i = 0; i < vertices.size(); i++) {
            V v = vertices.get(i);
            if (!visited.containsKey(v)) {
                dfs(v, visited, new MyArrayList<>());
                count++;
            }
        }
        return count;
    }

    // ───────────────────────── 직접 해볼 과제 ─────────────────────────
    // TODO 1. 2차원 미로(char[][] 에서 '#'은 벽)를 그래프로 보고 BFS로 최단 거리 구하기. 정점 = (행, 열) 좌표.
    // TODO 2. 방향 그래프에서 사이클 탐지 — DFS 중 "현재 경로에 있는 정점"을 다시 만나면 사이클.
    // TODO 3. 위상 정렬 (topological sort) — 선수과목 순서, 빌드 순서. 진입 차수가 0인 정점부터 큐로 처리 (Kahn 알고리즘).
    // TODO 4. (심화) 간선에 가중치를 붙이고 MyMinHeap으로 다익스트라 최단 경로 구현하기.
}
