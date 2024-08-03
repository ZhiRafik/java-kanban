import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    HashMap<Integer, Node> taskHistory = new HashMap<>();
    LinkedList<Node> linkedTaskHistory = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (taskHistory.containsKey(task.getID())) { // если узел уже есть в истории, перезаписываем его в LinkedTaskHistory
            // проверка идёт по id, поскольку id у узлов и задач - одинаковые, node берёт свой id от task'a
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
            linkedTaskHistory.head = prevNode;
            prevNode.setNext(null);
        } else { // единственный элемент
            return;
        }

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
