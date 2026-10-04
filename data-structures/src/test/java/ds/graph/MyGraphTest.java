package ds.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyGraphTest {

    /**
     *   A --- B
     *   |     |
     *   C --- D --- E
     */
    private MyGraph<String> sample() {
        MyGraph<String> g = new MyGraph<>();
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");
        g.addEdge("C", "D");
        g.addEdge("D", "E");
        return g;
    }

    @Test
    void 무방향_그래프는_양쪽에_간선이_생긴다() {
        MyGraph<String> g = sample();
        assertEquals(5, g.vertexCount());
        assertTrue(g.neighbors("A").contains("B"));
        assertTrue(g.neighbors("B").contains("A"));
        assertEquals(3, g.neighbors("D").size());   // B, C, E
    }

    @Test
    void BFS는_가까운_것부터() {
        // A(거리0) → B, C(거리1) → D(거리2) → E(거리3)
        assertEquals("[A, B, C, D, E]", sample().bfs("A").toString());
    }

    @Test
    void DFS는_한_방향으로_끝까지() {
        // A → B → D → C (D의 이웃: B, C, E 순서. B는 방문했으니 C로) → E
        assertEquals("[A, B, D, C, E]", sample().dfs("A").toString());
        // 반복문 버전도 올바른 DFS지만 순서는 재귀와 같게 맞춰 두었다 (이웃을 거꾸로 push)
        assertEquals("[A, B, D, C, E]", sample().dfsIterative("A").toString());
    }

    @Test
    void BFS_최단_거리() {
        MyGraph<String> g = sample();
        assertEquals(0, g.shortestDistance("A", "A"));
        assertEquals(1, g.shortestDistance("A", "B"));
        assertEquals(2, g.shortestDistance("A", "D"));   // A-B-D 또는 A-C-D
        assertEquals(3, g.shortestDistance("A", "E"));
    }

    @Test
    void 경로_없음() {
        MyGraph<String> g = sample();
        g.addVertex("Z");   // 외딴 정점

        assertFalse(g.hasPath("A", "Z"));
        assertEquals(-1, g.shortestDistance("A", "Z"));
        assertEquals(2, g.connectedComponents());   // {A,B,C,D,E} 와 {Z}
    }

    @Test
    void 방향_그래프는_한쪽으로만() {
        MyGraph<String> g = new MyGraph<>(true);
        g.addEdge("A", "B");   // A → B 만

        assertTrue(g.hasPath("A", "B"));
        assertFalse(g.hasPath("B", "A"));
        assertEquals(1, g.shortestDistance("A", "B"));
        assertEquals(-1, g.shortestDistance("B", "A"));
    }

    @Test
    void 사이클이_있어도_무한루프에_빠지지_않는다() {
        MyGraph<Integer> g = new MyGraph<>();
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 1);   // 1-2-3-1 삼각형

        assertEquals(3, g.bfs(1).size());
        assertEquals(3, g.dfs(1).size());
        assertEquals(1, g.shortestDistance(1, 3));   // 1-3 직접 연결
    }
}
