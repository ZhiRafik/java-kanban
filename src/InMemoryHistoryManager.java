import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    HashMap<Integer, Node> taskHistory = new HashMap<>();
    LinkedList<Node> linkedTaskHistory = new LinkedList<>();
    protected int newID = -1;
    // создаём первый ID для taskHistory, изначально ставим -1, т.к. при первом добавлении ID будет увеличен на 1, до 0

    @Override
    public void add(Task task) {
        if (taskHistory.containsValue(task)) {
            Node node = taskHistory.get(task.getID());
        }
        newID++;
        Node node = new Node(task, newID);
        taskHistory.put(newID, node); //следи, чтобы дважды не добавил
        linkedTaskHistory.linkLast(node);
    }

    @Override
    public void remove(int id) {
        Node toRemoveNode = taskHistory.get(id); // узел, подлежащий удалению
        Node nextNode = toRemoveNode.getNext(); // следующий от него узел
        Node prevNode = toRemoveNode.getPrev(); // предыдущий узел
        nextNode.setPrev(prevNode); // меням связь с (prevNode -> toRemoveNode -> NextNode)
        prevNode.setNext(nextNode); // на связь (prevNode -> NextNode) - удаляем toRemoveNode из цепи LinkedTaskHistory
        taskHistory.remove(id); // окончательно удалили Node из taskHistory
    }

    @Override
    public ArrayList<Task> getHistory() {

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
