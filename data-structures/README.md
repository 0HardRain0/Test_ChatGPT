# 자료구조 직접 구현하며 공부하기 (Java)

`java.util`의 컬렉션을 **쓰지 않고 바닥부터** 만들어 본다.
직접 만들어 보면 "왜 ArrayList는 중간 삽입이 느린가", "왜 equals와 hashCode를 같이 재정의해야 하는가" 같은
질문이 외우는 게 아니라 당연한 게 된다.

```
data-structures/
├── pom.xml
└── src/
    ├── main/java/ds/
    │   ├── list/MyArrayList.java     동적 배열
    │   ├── list/MyLinkedList.java    이중 연결 리스트
    │   ├── stack/MyStack.java        스택 (LIFO) + 괄호 검사 예제
    │   ├── queue/MyQueue.java        큐 (FIFO), 원형 배열
    │   ├── map/MyHashMap.java        해시맵, 체이닝
    │   ├── tree/MyBinarySearchTree.java  이진 탐색 트리, 3가지 순회
    │   ├── heap/MyMinHeap.java       최소 힙 (우선순위 큐), 배열로 트리 표현 + Top-K
    │   ├── graph/MyGraph.java        인접 리스트, BFS / DFS / 최단 거리
    │   └── sort/Sorting.java         버블·선택·삽입·병합·퀵 정렬 + 이분 탐색
    └── test/java/ds/...              각 자료구조 테스트 = 사용 예시
```

## 실행

```bash
cd data-structures
mvn test                                    # 전체 테스트
mvn test -Dtest=MyHashMapTest               # 하나만
```

## 공부 순서

읽는 순서대로 난이도가 올라간다. **한 파일을 읽고 → 테스트를 돌려 보고 → 파일 맨 아래 TODO를 직접 해 보고** 다음으로.

| 순서 | 파일 | 핵심 질문 | 소요 |
|---|---|---|---|
| 1 | `MyArrayList` | 배열은 크기가 고정인데 어떻게 "동적"인가? 왜 1.5배로 늘리나? | 1~2시간 |
| 2 | `MyLinkedList` | 노드를 참조로 잇는다는 게 뭔가? 왜 get(i)가 느린가? | 2~3시간 |
| 3 | `MyStack` | 배열 끝을 "위"로 쓰면 왜 O(1)인가? 괄호 검사가 왜 스택인가? | 1시간 |
| 4 | `MyQueue` | 배열 앞에서 빼면 왜 느린가? 원형 배열이 그걸 어떻게 해결하나? | 2시간 |
| 5 | `MyHashMap` | 키를 어떻게 배열 인덱스로 바꾸나? 충돌은 어떻게 처리하나? | 3~4시간 |
| 6 | `Sorting` | 왜 O(n²)과 O(n log n)으로 나뉘나? 이분 탐색은 왜 정렬이 전제인가? | 3시간 |
| 7 | `MyBinarySearchTree` | 비교 한 번에 후보가 왜 절반이 되나? 중위 순회가 왜 정렬인가? 삭제가 왜 어렵나? | 3~4시간 |
| 8 | `MyMinHeap` | 트리를 어떻게 배열에 넣나? 왜 "최솟값만" 빠른가? | 2~3시간 |
| 9 | `MyGraph` | 큐를 쓰면 BFS, 스택을 쓰면 DFS — 왜? BFS가 왜 최단 거리인가? | 3~4시간 |

6~9는 1~5에서 만든 것을 **재사용**한다 (`MyGraph`는 `MyHashMap` + `MyArrayList` + `MyQueue` + `MyStack`을 전부 쓴다).
앞 단계가 탄탄해야 뒷 단계가 읽힌다.

## 한눈에 비교

| | 인덱스 접근 | 앞 삽입/삭제 | 뒤 삽입/삭제 | 중간 삽입/삭제 | 키로 검색 |
|---|---|---|---|---|---|
| MyArrayList | **O(1)** | O(n) | **O(1)** | O(n) | O(n) |
| MyLinkedList | O(n) | **O(1)** | **O(1)** | O(1)* | O(n) |
| MyStack | — | — | **O(1)** (push/pop) | — | — |
| MyQueue | — | **O(1)** (poll) | **O(1)** (offer) | — | — |
| MyHashMap | — | — | — | — | **O(1)** 평균 |

\* 노드를 이미 알고 있을 때. 인덱스로 찾아가면 O(n).

| | 검색 | 삽입 | 삭제 | 최솟값 | 정렬된 순회 |
|---|---|---|---|---|---|
| MyBinarySearchTree | O(log n)* | O(log n)* | O(log n)* | O(log n) | **O(n)** (중위 순회) |
| MyMinHeap | O(n) | **O(log n)** | O(log n) (루트만) | **O(1)** | ✗ |
| 정렬된 배열 + 이분탐색 | **O(log n)** | O(n) | O(n) | O(1) | O(n) |

\* 균형 잡혔을 때. 한쪽으로 쏠리면 O(n). → 그래서 실무는 자가 균형 트리(`TreeMap`)를 쓴다.

**셋의 관계**: "정렬 상태를 어디까지 유지할 것인가"의 선택이다.
배열은 완전히 정렬하지만 삽입이 느리고, BST는 삽입도 빠르지만 균형이 깨질 수 있고, 힙은 "최솟값만" 보장하는 대신 가장 단순하다.

## 각 자료구조를 실무 코드와 연결하면

- **ArrayList**: `List<PostResponse>` — 게시판 목록. 사실상 모든 "목록".
- **LinkedList**: 직접 쓰는 일은 드물지만, `LinkedHashMap`(순서 있는 맵), LRU 캐시 내부가 이것.
- **Stack**: 재귀 호출(콜 스택), 실행 취소, JSON 파서의 중괄호 추적.
- **Queue**: 스레드 풀 작업 대기열, 메시지 큐(Kafka/RabbitMQ의 개념적 기초), BFS.
- **HashMap**: 세션 저장소, 캐시, `Map<String, Object>`로 받는 JSON, 중복 제거, 카운팅.
  DB의 해시 인덱스도 같은 원리.
- **BST**: `TreeMap`/`TreeSet`. **DB 인덱스(B-Tree)의 원리** — `WHERE created_at BETWEEN`이 빠른 이유,
  한방쿼리 튜닝에서 "이 조건이 인덱스를 타나?"의 정체가 이것.
- **Heap**: `PriorityQueue`. 스케줄러, 작업 큐 우선순위, "상위 10개" 쿼리를 메모리에서 할 때.
- **Graph**: 추천("친구의 친구"), 의존성 해석(Maven이 라이브러리 순서를 정하는 법), 권한 상속 트리, 워크플로 엔진.
- **정렬**: `Arrays.sort`/`Collections.sort`가 내부에서 뭘 하는지. `ORDER BY`가 인덱스 없을 때 왜 느린지(filesort).

## 디버깅으로 공부하는 법

IDE에서 테스트에 중단점을 걸고 한 줄씩 실행하며 내부 배열/노드가 어떻게 바뀌는지 **직접 눈으로 보는 것**이
설명을 열 번 읽는 것보다 낫다. 특히:
- `MyArrayList.add(1, x)` 에서 `System.arraycopy` 전후의 `elements`
- `MyLinkedList.unlink()` 에서 참조 4개가 바뀌는 순간
- `MyQueue` 에서 `rear`가 7 → 0 으로 돌아가는 순간
- `MyHashMap.resize()` 에서 체인이 흩어지는 모습
- `MyBinarySearchTree.remove()` 에서 자식이 둘인 노드가 후계자로 교체되는 순간
- `MyMinHeap.siftDown()` 에서 루트로 올라간 큰 값이 아래로 가라앉는 모습
- `MyGraph.bfs()` 에서 큐에 쌓이는 정점들 — 거리별로 층이 지는 게 보인다

## 다음 단계 (요청하면 추가)

- **자가 균형 트리**: AVL 회전 → 왜 `TreeMap`은 쏠리지 않나
- **트라이(Trie)**: 자동완성, 접두사 검색
- **다익스트라**: 가중치 그래프 최단 경로 = BFS + MyMinHeap
- **LRU 캐시**: MyHashMap + MyLinkedList 조합 (유명한 면접 문제)
- **Union-Find**: 네트워크 연결 여부를 거의 O(1)에
