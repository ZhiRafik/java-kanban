public final class Managers {
    private Managers() {
        //закрытый конструктор, чтобы запретить создание объектов этого класса
    }
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager(); // Возвращает объект реализации истории по умолчанию
    }
}
