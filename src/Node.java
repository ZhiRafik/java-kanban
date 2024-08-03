public class Node {
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
