public class Node {
    protected Task task;
    protected Node nextE; //next Element - ID следующего элемента
    protected Node prevE; //previous Element - ID предыдущего элемента
    protected int nodeId;

    public Node (Task task, int nodeId) {
        this.task = task;
        this.nodeId = nodeId;
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

    public void setNext(Node node) {
        this.nextE = node;
    }

    public void setPrev(Node node) {
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
