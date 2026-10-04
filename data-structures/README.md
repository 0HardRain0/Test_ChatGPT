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
    │   └── map/MyHashMap.java        해시맵, 체이닝
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

## 한눈에 비교

| | 인덱스 접근 | 앞 삽입/삭제 | 뒤 삽입/삭제 | 중간 삽입/삭제 | 키로 검색 |
|---|---|---|---|---|---|
| MyArrayList | **O(1)** | O(n) | **O(1)** | O(n) | O(n) |
| MyLinkedList | O(n) | **O(1)** | **O(1)** | O(1)* | O(n) |
| MyStack | — | — | **O(1)** (push/pop) | — | — |
| MyQueue | — | **O(1)** (poll) | **O(1)** (offer) | — | — |
| MyHashMap | — | — | — | — | **O(1)** 평균 |

\* 노드를 이미 알고 있을 때. 인덱스로 찾아가면 O(n).

## 각 자료구조를 실무 코드와 연결하면

- **ArrayList**: `List<PostResponse>` — 게시판 목록. 사실상 모든 "목록".
- **LinkedList**: 직접 쓰는 일은 드물지만, `LinkedHashMap`(순서 있는 맵), LRU 캐시 내부가 이것.
- **Stack**: 재귀 호출(콜 스택), 실행 취소, JSON 파서의 중괄호 추적.
- **Queue**: 스레드 풀 작업 대기열, 메시지 큐(Kafka/RabbitMQ의 개념적 기초), BFS.
- **HashMap**: 세션 저장소, 캐시, `Map<String, Object>`로 받는 JSON, 중복 제거, 카운팅.
  DB의 해시 인덱스도 같은 원리. (B-Tree 인덱스는 다음 단계인 트리에서.)

## 디버깅으로 공부하는 법

IDE에서 테스트에 중단점을 걸고 한 줄씩 실행하며 내부 배열/노드가 어떻게 바뀌는지 **직접 눈으로 보는 것**이
설명을 열 번 읽는 것보다 낫다. 특히:
- `MyArrayList.add(1, x)` 에서 `System.arraycopy` 전후의 `elements`
- `MyLinkedList.unlink()` 에서 참조 4개가 바뀌는 순간
- `MyQueue` 에서 `rear`가 7 → 0 으로 돌아가는 순간
- `MyHashMap.resize()` 에서 체인이 흩어지는 모습

## 다음 단계 (요청하면 추가)

- **트리**: 이진 탐색 트리(BST) → 왜 DB 인덱스가 B-Tree인지
- **힙 / 우선순위 큐**: 배열로 트리를 표현하는 트릭, Top-K 문제
- **그래프**: 인접 리스트, BFS/DFS, 최단 경로
- **정렬**: 퀵/병합 정렬을 직접 구현하고 `Arrays.sort`와 비교
