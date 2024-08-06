import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    HashMap<Integer, Node> taskHistory = new HashMap<>();

    @Override
    public void add(Task task) {
        if (taskHistory.containsKey(task.getID())) { // если узел уже есть в истории, перезаписываем его в LinkedTaskHistory
            // проверка идёт по id, поскольку id у узлов и задач - одинаковые, node берёт свой id от task'a
            Node containedNode = taskHistory.get(task.getID());
            removeFromLinkedHistory(containedNode);
            linkLast(containedNode);
            return;
        }
        Node node = new Node(task); // если узла ещё нет, то добавляем в taskHistory и LinkedTaskHistory
        taskHistory.put(task.getID(), node);
        linkLast(node);
    }

    @Override
    public void removeNode(Node node) {
        removeFromLinkedHistory(node);
        taskHistory.remove(node); // окончательно удалили InMemoryHistoryManager.Node из taskHistory
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    private void removeFromLinkedHistory(Node toRemoveNode) {
        if (toRemoveNode.getPrev() != null && toRemoveNode.getNext() != null) { // если внутри цепи
            Node nextNode = toRemoveNode.getNext(); // следующий от него узел
            Node prevNode = toRemoveNode.getPrev(); // предыдущий узел
            nextNode.setPrev(prevNode); // меням связь с (prevNode -> toRemoveNode -> NextNode)
            prevNode.setNext(nextNode); // на связь (prevNode -> NextNode) => удаляем toRemoveNode из цепи LinkedTaskHistory
        } else if (toRemoveNode.getPrev() == null && toRemoveNode.getNext() != null) { // если первый элемент цепи
            Node nextNode = toRemoveNode.getNext();
            nextNode.setPrev(null);
        } else if (toRemoveNode.getPrev() != null && toRemoveNode.getNext() == null) { // если последний элемент цепи
            Node prevNode = toRemoveNode.getPrev();
            head = prevNode;
            prevNode.setNext(null);
        } else { // единственный элемент
            return;
        }

    }

    private Node head;

    private void linkLast(Node node) { // добавить узел, как новый ведущий (head)
        if (head == null) {
            head = node;
        } else {
            head.setNext(node);
            node.setPrev(head);
            head = node;
        }
    }

    private ArrayList<Task> getTasks() { // получить все узлы в виде списка с помощью перебора от head
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(head.getTask());
        Node current = head;
        while (current.getPrev() != null) {
            current = current.getPrev();
            tasks.add(current.getTask());
        }
        return tasks;
    }


    public static class Node {
        protected Task task;
        protected Node nextE; //next Element - следующий node
        protected Node prevE; //previous Element - предыдущий node
        protected int nodeId;

        public Node(Task task) {
            this.task = task;
            this.nodeId = task.getID();
        }

        public int getID() {
            return nodeId;
        }

        public Task getTask() {
            return task;
        }

        public Node getPrev() {
            return prevE;
        }

        public Node getNext() {
            return nextE;
        }

        protected void setNext(Node node) {
            this.nextE = node;
        }

        protected void setPrev(Node node) {
            this.prevE = node;
        }

        @Override // перепишем метод эквивалентности
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return task.equals(node.task) && nextE == node.nextE && prevE == node.prevE && nodeId == node.getID();
        }
    }
}
