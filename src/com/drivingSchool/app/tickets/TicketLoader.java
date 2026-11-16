package com.drivingSchool.app.tickets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Читает файл билета из датасета pdd_russia и превращает его в {@link Ticket}.
 * Формат: массив вопросов, у каждого answers[] с флагом is_correct.
 */
public final class TicketLoader {
    private static final String IMAGE_BASE = "https://raw.githubusercontent.com/etspring/pdd_russia/master/";
    private static final String NO_IMAGE_MARKER = "no_image";

    private final ObjectMapper mapper = new ObjectMapper();

    public Ticket load(Path path) throws IOException {
        JsonNode root = this.mapper.readTree(path.toFile());

        List<Question> questions = new ArrayList<>();
        int ticketNumber = 0;

        for (JsonNode node : root) {
            ticketNumber = this.parseNumber(node.path("ticket_number").asText());

            List<String> options = new ArrayList<>();
            int correctIndex = -1;
            int index = 0;

            for (JsonNode answer : node.path("answers")) {
                options.add(answer.path("answer_text").asText());
                if (answer.path("is_correct").asBoolean()) {
                    correctIndex = index;
                }
                index++;
            }

            questions.add(new Question(
                    node.path("question").asText(),
                    List.copyOf(options),
                    correctIndex,
                    node.path("answer_tip").asText(),
                    this.parseImage(node.path("image").asText())
            ));
        }

        return new Ticket(ticketNumber, List.copyOf(questions));
    }

    private int parseNumber(String raw) {
        String digits = raw.replaceAll("\\D+", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }

    private String parseImage(String raw) {
        if (raw == null || raw.isBlank() || raw.contains(NO_IMAGE_MARKER)) {
            return null;
        }

        String clean = raw.startsWith("./") ? raw.substring(2) : raw;
        return IMAGE_BASE + clean;
    }
}