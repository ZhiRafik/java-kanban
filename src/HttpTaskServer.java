import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.TreeSet;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HttpTaskServer {
    private final HttpServer server;
    private InMemoryTaskManager taskManager;
    private InMemoryHistoryManager historyManager;
    private final Gson gson;

    public HttpTaskServer() throws IOException {
        this.taskManager = new InMemoryTaskManager();
        this.historyManager = new InMemoryHistoryManager();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(historyManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    static class PrioritizedHandler implements HttpHandler {
        private final InMemoryTaskManager taskManager;
        private final Gson gson;

        public PrioritizedHandler(InMemoryTaskManager taskManager, Gson gson) {
            this.taskManager = taskManager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (method.equals("GET")) {
                try {
                    TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                    sendResponse(exchange, gson.toJson(prioritizedTasks), 200);
                } catch (Exception e) {
                    sendResponse(exchange, gson.toJson("Внутренняя ошибка сервера"), 500);
                }
            } else {
                sendResponse(exchange, gson.toJson("Метод не поддерживается"), 405);
            }
        }
    }

    static class HistoryHandler implements HttpHandler {
        private final InMemoryHistoryManager historyManager;
        private final Gson gson;

        public HistoryHandler(InMemoryHistoryManager historyManager, Gson gson) {
            this.historyManager = historyManager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (method.equals("GET")) {
                try {
                    ArrayList<Task> tasks = historyManager.getHistory();
                    sendResponse(exchange, gson.toJson(tasks), 200);
                } catch (Exception e) {
                    sendResponse(exchange, gson.toJson("Внутренняя ошибка сервера"), 500);
                }
            } else {
                sendResponse(exchange, gson.toJson("Метод не поддерживается"), 405);
            }
        }
    }

    static class EpicsHandler implements HttpHandler {
        private final InMemoryTaskManager taskManager;
        private final Gson gson;

        public EpicsHandler(InMemoryTaskManager taskManager, Gson gson) {
            this.taskManager = taskManager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] splitPath = path.split("/");

            switch (method) {
                case "GET":
                    if (splitPath.length == 2) { // /epics
                        handleGetEpics(exchange);
                    } else if (splitPath.length == 3) { // /epics/{id}
                        handleGetEpicById(exchange, splitPath[2]);
                    } else if (splitPath.length == 4 && "subtasks".equals(splitPath[3])) { // /epics/{id}/subtasks
                        handleGetEpicSubtasks(exchange, splitPath[2]);
                    } else {
                        sendResponse(exchange, gson.toJson("Некорректный путь"), 400);
                    }
                    break;
                case "POST":
                    if (splitPath.length == 2) {
                        handleCreateEpic(exchange);
                    } else {
                        sendResponse(exchange, gson.toJson("Некорректный путь"), 400);
                    }
                    break;
                case "DELETE":
                    if (splitPath.length == 3) { // /epics/{id}
                        handleDeleteEpic(exchange, splitPath[2]);
                    } else {
                        sendResponse(exchange, gson.toJson("Некорректный путь"), 400);
                    }
                    break;
                default:
                    sendResponse(exchange, gson.toJson("Метод не поддерживается"), 405);
            }
        }

        private void handleDeleteEpic(HttpExchange exchange, String id) throws IOException {
            try {
                int epicId = Integer.parseInt(id);
                Epic epic = taskManager.getEpic(epicId);
                if (epic == null) {
                    sendResponse(exchange, gson.toJson("Эпик не найден"), 404);
                    return;
                }
                taskManager.removeEpicByID(epicId);
                sendResponse(exchange, gson.toJson("Эпик успешно удалён"), 200);
            } catch (NumberFormatException e) {
                sendResponse(exchange, gson.toJson("Некорректный ID"), 400);
            }
        }

        private void handleGetEpics(HttpExchange exchange) throws IOException {
            ArrayList<Epic> epics = taskManager.getAllEpics();
            sendResponse(exchange, gson.toJson(epics), 200);
        }

        private void handleGetEpicById(HttpExchange exchange, String id) throws IOException {
            try {
                int epicId = Integer.parseInt(id);
                Epic epic = taskManager.getEpic(epicId);
                if (epic == null) {
                    sendResponse(exchange, gson.toJson("Эпик не найден"), 404);
                } else {
                    sendResponse(exchange, gson.toJson(epic), 200);
                }
            } catch (NumberFormatException e) {
                sendResponse(exchange, gson.toJson("Некорректный ID"), 400);
            }
        }

        private void handleGetEpicSubtasks(HttpExchange exchange, String id) throws IOException {
            try {
                int epicId = Integer.parseInt(id);
                Epic epic = taskManager.getEpic(epicId);
                if (epic == null) {
                    sendResponse(exchange, gson.toJson("Эпик не найден"), 404);
                    return;
                }
                ArrayList<Subtask> subtasks = taskManager.getSubtasksFromEpic(epic);
                sendResponse(exchange, gson.toJson(subtasks), 200);
            } catch (NumberFormatException e) {
                sendResponse(exchange, gson.toJson("Некорректный ID"), 400);
            }
        }

        private void handleCreateEpic(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes());
            Epic epic = gson.fromJson(body, Epic.class);
            taskManager.createNewEpic(epic);
            sendResponse(exchange, gson.toJson("Эпик успешно создан"), 201);
        }
    }

    static class SubtasksHandler implements HttpHandler {

        private final InMemoryTaskManager taskManager;
        private final Gson gson;

        public SubtasksHandler(InMemoryTaskManager taskManager, Gson gson) {
            this.taskManager = taskManager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] splitPath = path.split("/");

            switch (method) {
                case "GET":
                    if (splitPath.length == 2) {
                        handleGetSubtasks(exchange);
                    } else if (splitPath.length == 3) {
                        handleGetSubtaskById(exchange, splitPath[2]);
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateSubtask(exchange);
                    break;
                case "DELETE":
                    if (splitPath.length == 3) {
                        handleDeleteSubtaskById(exchange, splitPath[2]);
                    }
                    break;
                default:
                    sendResponse(exchange, gson.toJson("Метод не поддерживается"), 405);
            }
        }

        private void handleDeleteSubtaskById(HttpExchange exchange, String id) throws IOException {
            try {
                int subtaskId = Integer.parseInt(id);
                Subtask subtask = taskManager.getSubtask(subtaskId);
                if (subtask == null) {
                    sendResponse(exchange, gson.toJson("Подзадача не найдена"), 404);
                    return;
                }
                taskManager.removeSubtaskByID(subtaskId);
                sendResponse(exchange, gson.toJson("Подзадача успешно удалена"), 200);
            } catch (NumberFormatException e) {
                sendResponse(exchange, gson.toJson("Некорректный ID"), 400);
            }
        }

        private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes());
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (subtask.getID() == 0) {
                taskManager.createNewSubtask(subtask);
                sendResponse(exchange, gson.toJson("Подзадача успешно создана"), 201);
            } else {
                taskManager.updateSubtask(subtask);
                sendResponse(exchange, gson.toJson("Подзадача успешно обновлена"), 200);
            }
        }

        private void handleGetSubtaskById(HttpExchange exchange, String id) throws IOException {
            try {
                int subtaskId = Integer.parseInt(id);
                Subtask subtask = taskManager.getSubtask(subtaskId);
                if (subtask == null) {
                    sendResponse(exchange, gson.toJson("Подзадача не найдена"), 404);
                    return;
                }
                sendResponse(exchange, gson.toJson(subtask), 200);
            } catch (NumberFormatException e) {
                sendResponse(exchange, gson.toJson("Некорретный ID"), 400);
            }
        }

        private void handleGetSubtasks(HttpExchange exchange) throws IOException {
            ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();
            String response = gson.toJson(subtasks);
            sendResponse(exchange, response, 200);
        }
    }

    static class TasksHandler implements HttpHandler {
        private final InMemoryTaskManager taskManager;
        private final Gson gson;

        public TasksHandler(InMemoryTaskManager taskManager, Gson gson) {
            this.taskManager = taskManager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] splitPath = path.split("/");

            switch (method) {
                case "GET":
                    if (splitPath.length == 2) {
                        handleGetTasks(exchange);
                    } else if (splitPath.length == 3) {
                        handleGetTaskById(exchange, splitPath[2]);
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateTask(exchange);
                    break;
                case "DELETE":
                    if (splitPath.length == 3) { // /tasks/{id}
                        handleDeleteTaskById(exchange, splitPath[2]);
                    }
                    break;
                default:
                    sendResponse(exchange, gson.toJson("Метод не поддерживается"), 405);
            }
        }

        private void handleDeleteTaskById(HttpExchange exchange, String id) throws IOException {
            try {
                int taskId = Integer.parseInt(id);
                Task task = taskManager.getTask(taskId);
                if (task == null) {
                    sendResponse(exchange, gson.toJson("Задача не найдена"), 404);
                    return;
                }
                taskManager.removeTaskByID(taskId);
                sendResponse(exchange, gson.toJson("Задача успешно удалена"), 200);
            } catch (NumberFormatException e) {
                sendResponse(exchange, gson.toJson("Некорретный ID"), 400);
            }
        }

        private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes());
            Task task = gson.fromJson(body, Task.class);
            if (task.getID() == 0) {
                taskManager.createNewTask(task);
                sendResponse(exchange, gson.toJson("Задача создана"), 201);
            } else {
                taskManager.updateTask(task);
                sendResponse(exchange, gson.toJson("Задача обновлена"), 201);
            }
        }

        private void handleGetTasks(HttpExchange exchange) throws IOException {
            ArrayList<Task> tasks = taskManager.getAllTasks();
            String response = gson.toJson(tasks);
            sendResponse(exchange, response, 200);
        }

        private void handleGetTaskById(HttpExchange exchange, String id) throws NumberFormatException, IOException {
            try {
                int taskId = Integer.parseInt(id);
                Task task = taskManager.getTask(taskId);
                if (task == null) {
                    sendResponse(exchange, gson.toJson("Задача не была найдена"), 404);
                } else {
                    sendResponse(exchange, gson.toJson(task), 200);
                }
            } catch (NumberFormatException e) {
                sendResponse(exchange, gson.toJson("Некорретный ID"), 400);
            }
        }
    }

    private static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    public static void main(String[] args) {

    }
}
