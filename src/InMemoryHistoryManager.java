import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    HashMap<Integer, Node> taskHistory = new HashMap<>();
    LinkedList<Node> linkedTaskHistory = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (taskHistory.containsValue(task)) { // если узел уже есть в истории, перезаписываем его в LinkedTaskHistory
            Node containedNode = taskHistory.get(task.getID());
            removeFromLinkedHistory(containedNode);
            linkedTaskHistory.linkLast(containedNode);
            return;
        }
        Node node = new Node(task); // если узла ещё нет, то добавляем в taskHistory и LinkedTaskHistory
        taskHistory.put(task.getID(), node);
        linkedTaskHistory.linkLast(node);
    }

    @Override
    public void removeNode(Node node) {
        removeFromLinkedHistory(node);
        taskHistory.remove(node); // окончательно удалили Node из taskHistory
    }

    @Override
    public ArrayList<Task> getHistory() {
        return linkedTaskHistory.getTasks();
    }

    public void removeFromLinkedHistory(Node toRemoveNode) {
        Node nextNode = toRemoveNode.getNext(); // следующий от него узел
        Node prevNode = toRemoveNode.getPrev(); // предыдущий узел
        nextNode.setPrev(prevNode); // меням связь с (prevNode -> toRemoveNode -> NextNode)
        prevNode.setNext(nextNode); // на связь (prevNode -> NextNode) => удаляем toRemoveNode из цепи LinkedTaskHistory
    }

    protected class LinkedList<E> {
        private Node head;
        protected void linkLast(Node node) { // добавить узел, как новый ведущий (head)
            if (head == null) {
                head = node;
            } else {
                head.setNext(node);
                node.setPrev(head);
                head = node;
            }
        }

        protected ArrayList<Task> getTasks() { // получить все узлы в виде списка с помощью перебора от head
            ArrayList<Task> tasks = new ArrayList<>();
            tasks.add(head.getTask());
            Node current = head;
            while (current.getPrev() != null) {
                current = current.getPrev();
                tasks.add(current.getTask());
            }
            return tasks;
        }
    }
}
