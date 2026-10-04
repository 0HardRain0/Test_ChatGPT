package ds.tree;

import ds.list.MyArrayList;

import java.util.NoSuchElementException;

/**
 * 이진 탐색 트리 (Binary Search Tree, BST)
 *
 * 규칙 하나로 모든 게 결정된다:
 *   "왼쪽 자식 < 부모 < 오른쪽 자식"  (모든 노드에서)
 *
 *             50
 *           /    \
 *         30      70
 *        /  \    /  \
 *      20   40  60   80
 *
 * 찾기: 50보다 작으면 왼쪽, 크면 오른쪽. 한 번 비교할 때마다 후보가 절반으로 줄어든다 → O(log n)
 *       정렬된 배열의 이분탐색과 같은 원리인데, 배열과 달리 삽입·삭제도 O(log n)이다.
 *
 * 시간 복잡도
 *  - contains, insert, remove   평균 O(log n)
 *  - 최악 O(n)  ← 정렬된 순서로 넣으면 한쪽으로만 자라서 연결 리스트가 된다 (아래 그림)
 *
 *      10
 *        \
 *         20
 *           \
 *            30     ← 이걸 막으려고 "스스로 균형을 잡는" AVL, Red-Black 트리가 나왔다.
 *              \       java.util.TreeMap이 Red-Black 트리다.
 *               40
 *
 * 순회 (트리의 모든 노드를 방문하는 순서)
 *  - 중위 (in-order)   : 왼쪽 → 나 → 오른쪽   ⇒ BST에서는 **오름차순 정렬**이 된다 (가장 중요)
 *  - 전위 (pre-order)  : 나 → 왼쪽 → 오른쪽   ⇒ 트리 복사, 직렬화
 *  - 후위 (post-order) : 왼쪽 → 오른쪽 → 나   ⇒ 자식 먼저 처리 (폴더 삭제, 수식 계산)
 *
 * 어디에 쓰이나
 *  - TreeMap / TreeSet: 정렬 유지 + 범위 검색 ("점수 80~90 사이인 사람")
 *  - DB 인덱스: B-Tree는 BST를 디스크용으로 뚱뚱하게(자식 수백 개) 만든 것. 원리는 같다.
 *    → "WHERE created_at BETWEEN ..." 이 빠른 이유
 *  - 파일 시스템, 컴파일러의 구문 트리, 게임의 공간 분할
 */
public class MyBinarySearchTree<E extends Comparable<E>> {

    private static class Node<E> {
        E value;
        Node<E> left;
        Node<E> right;

        Node(E value) {
            this.value = value;
        }
    }

    private Node<E> root;
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // ───────────────────────── 찾기 ─────────────────────────

    /** 평균 O(log n). 반복문으로 내려간다 */
    public boolean contains(E target) {
        Node<E> cur = root;
        while (cur != null) {
            int cmp = target.compareTo(cur.value);
            if (cmp == 0) return true;
            cur = (cmp < 0) ? cur.left : cur.right;   // 작으면 왼쪽, 크면 오른쪽
        }
        return false;
    }

    /** 가장 작은 값 = 왼쪽 끝까지 */
    public E min() {
        if (root == null) throw new NoSuchElementException("트리가 비어 있습니다");
        return minNode(root).value;
    }

    /** 가장 큰 값 = 오른쪽 끝까지 */
    public E max() {
        if (root == null) throw new NoSuchElementException("트리가 비어 있습니다");
        Node<E> cur = root;
        while (cur.right != null) cur = cur.right;
        return cur.value;
    }

    // ───────────────────────── 삽입 ─────────────────────────

    /** 중복은 넣지 않는다 (Set처럼). 넣었으면 true */
    public boolean insert(E value) {
        if (root == null) {
            root = new Node<>(value);
            size++;
            return true;
        }
        Node<E> cur = root;
        while (true) {
            int cmp = value.compareTo(cur.value);
            if (cmp == 0) return false;                    // 이미 있음
            if (cmp < 0) {
                if (cur.left == null) {                    // 왼쪽이 비었으면 거기 붙인다
                    cur.left = new Node<>(value);
                    size++;
                    return true;
                }
                cur = cur.left;
            } else {
                if (cur.right == null) {
                    cur.right = new Node<>(value);
                    size++;
                    return true;
                }
                cur = cur.right;
            }
        }
    }

    // ───────────────────────── 삭제 (BST에서 제일 까다로운 부분) ─────────────────────────

    /**
     * 삭제할 노드의 자식 수에 따라 세 경우로 나뉜다.
     *   0개 (잎)      : 그냥 떼어낸다
     *   1개           : 자식을 내 자리로 끌어올린다
     *   2개           : 오른쪽 서브트리의 최솟값(= 나보다 큰 것 중 가장 작은 것, "후계자")으로 내 값을 바꾸고,
     *                   그 후계자 노드를 대신 지운다 (후계자는 왼쪽 자식이 없으므로 0개 or 1개 경우로 떨어진다)
     *
     * 재귀로 구현: remove(node, value)는 "value를 지운 뒤의 서브트리 루트"를 돌려준다.
     * 부모가 그 반환값을 자기 자식 참조에 다시 대입하는 식으로 연결을 고친다.
     */
    public boolean remove(E value) {
        int before = size;
        root = remove(root, value);
        return size < before;
    }

    private Node<E> remove(Node<E> node, E value) {
        if (node == null) return null;                 // 못 찾음

        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = remove(node.left, value);      // 왼쪽에서 지우고 결과를 다시 연결
        } else if (cmp > 0) {
            node.right = remove(node.right, value);
        } else {
            // 찾았다. 자식 수로 분기
            size--;
            if (node.left == null) return node.right;  // 0개 or 오른쪽만 → 오른쪽을 올린다 (null일 수도)
            if (node.right == null) return node.left;  // 왼쪽만 → 왼쪽을 올린다

            // 2개: 후계자 값으로 교체하고 후계자를 지운다
            Node<E> successor = minNode(node.right);
            node.value = successor.value;
            size++;                                    // 아래 재귀에서 다시 size-- 되므로 보정
            node.right = remove(node.right, successor.value);
        }
        return node;
    }

    private Node<E> minNode(Node<E> node) {
        while (node.left != null) node = node.left;
        return node;
    }

    // ───────────────────────── 순회 ─────────────────────────

    /** 중위 순회 → 오름차순. BST의 가장 큰 쓸모 */
    public MyArrayList<E> inOrder() {
        MyArrayList<E> out = new MyArrayList<>();
        inOrder(root, out);
        return out;
    }

    private void inOrder(Node<E> node, MyArrayList<E> out) {
        if (node == null) return;
        inOrder(node.left, out);     // 왼쪽 먼저
        out.add(node.value);         // 나
        inOrder(node.right, out);    // 오른쪽
    }

    public MyArrayList<E> preOrder() {
        MyArrayList<E> out = new MyArrayList<>();
        preOrder(root, out);
        return out;
    }

    private void preOrder(Node<E> node, MyArrayList<E> out) {
        if (node == null) return;
        out.add(node.value);         // 나 먼저
        preOrder(node.left, out);
        preOrder(node.right, out);
    }

    public MyArrayList<E> postOrder() {
        MyArrayList<E> out = new MyArrayList<>();
        postOrder(root, out);
        return out;
    }

    private void postOrder(Node<E> node, MyArrayList<E> out) {
        if (node == null) return;
        postOrder(node.left, out);
        postOrder(node.right, out);
        out.add(node.value);         // 나 마지막
    }

    /** 트리 높이. 균형 잡혔으면 log₂(n) 근처, 한쪽으로 쏠렸으면 n에 가깝다 */
    public int height() {
        return height(root);
    }

    private int height(Node<E> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    // ───────────────────────── 직접 해볼 과제 ─────────────────────────
    // TODO 1. 1~1000을 순서대로 insert한 트리와 섞어서 insert한 트리의 height()를 비교해 보기. 왜 차이가 나나?
    // TODO 2. range(from, to) — from 이상 to 이하인 값을 오름차순으로 반환. 중위 순회에서 가지치기를 하면 O(log n + k).
    //         (DB의 BETWEEN 쿼리가 인덱스를 타면 빠른 이유가 이것)
    // TODO 3. 재귀 없이 스택(MyStack)으로 중위 순회 구현하기.
    // TODO 4. (심화) 삽입 후 균형이 깨지면 회전(rotation)으로 고치는 AVL 트리로 발전시켜 보기.
}
