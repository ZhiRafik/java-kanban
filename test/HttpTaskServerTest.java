import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {

    private static HttpTaskServer server;
    private static HttpClient client;
    private static Gson gson = new Gson();

    @BeforeAll
    static void setUp() throws IOException {
        server = new HttpTaskServer();
        server.start();
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    @AfterAll
    static void tearDown() {
        server.stop();
    }

    @Test
    void testGetAllTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testCreateTask() throws IOException, InterruptedException {
        String jsonTask = gson.toJson(new Task("Тестовая задача", "Описание", Status.NEW));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void testDeleteTaskById() throws IOException, InterruptedException {
        String jsonTask = gson.toJson(new Task("Тестовая задача", "Описание", Status.NEW));
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .header("Content-Type", "application/json")
                .build();

        client.send(createRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode());
        assertEquals("\"Задача успешно удалена\"", deleteResponse.body());
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testInvalidId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/invalid"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("\"Некорректный ID\"", response.body());
    }

    @Test
    void testUnsupportedMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
        assertEquals("\"Метод не поддерживается\"", response.body());
    }

    static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter out, Duration value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public Duration read(JsonReader in) throws IOException {
            String durationString = in.nextString();
            return Duration.parse(durationString);
        }
    }

    static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(FORMATTER));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            String value = in.nextString();
            return LocalDateTime.parse(value, FORMATTER);
        }
    }
}