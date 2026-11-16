package com.drivingSchool.app.tickets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Хранилище билетов и прогресса пользователей.
 * Билеты читаются один раз при старте из папки data/tickets.
 * Для каждого chatId держится свой {@link Run}
 */
public class TicketService {
    private final Map<Integer, Ticket> tickets = new TreeMap<>();
    private final Map<Long, Run> runs = new ConcurrentHashMap<>();
    private final Map<Long, String> lastResults = new ConcurrentHashMap<>();

    public TicketService(Path directory) {
        this.loadAll(directory);
    }

    private void loadAll(Path directory) {
        if (!Files.isDirectory(directory)) {
            return;
        }

        try (Stream<Path> files = Files.list(directory)) {
            files.filter(path -> path.toString().endsWith(".json")).forEach(this::loadOne);
        } catch (IOException error) {
            System.out.println("Не удалось прочитать папку с билетами: " + error.getMessage());
        }
    }

    private void loadOne(Path file) {
        try {
            Ticket ticket = new TicketLoader().load(file);

            if (ticket.number() > 0 && ticket.size() > 0) {
                this.tickets.put(ticket.number(), ticket);
            }
        } catch (IOException | RuntimeException error) {
            System.out.println("Билет " + file.getFileName() + " пропущен: " + error.getMessage());
        }
    }

    public boolean isEmpty() {
        return this.tickets.isEmpty();
    }

    public List<Integer> numbers() {
        return new ArrayList<>(this.tickets.keySet());
    }

    public Ticket ticket(int number) {
        return this.tickets.get(number);
    }

    public Run run(long chatId) {
        return this.runs.get(chatId);
    }

    public Run start(long chatId, int number) {
        Ticket ticket = this.tickets.get(number);

        if (ticket == null) {
            return null;
        }

        Run run = new Run(ticket);
        this.runs.put(chatId, run);
        return run;
    }

    /** запомнить последний результат, чтобы показать его в личном кабинете. */
    public void rememberResult(long chatId, String result) {
        this.lastResults.put(chatId, result);
    }

    public String lastResult(long chatId) {
        return this.lastResults.get(chatId);
    }

    public void finish(long chatId) {
        this.runs.remove(chatId);
    }

    /** Прохождение*/
    public static final class Run {
        private final Ticket ticket;
        private int index;
        private int correct;
        private int wrong;

        private Run(Ticket ticket) {
            this.ticket = ticket;
        }

        public Ticket ticket() {
            return this.ticket;
        }

        public int index() {
            return this.index;
        }

        public int correct() {
            return this.correct;
        }

        public int wrong() {
            return this.wrong;
        }

        public boolean finished() {
            return this.index >= this.ticket.size();
        }

        public Question current() {
            return this.finished() ? null : this.ticket.questionAt(this.index);
        }

        public void answer(boolean isCorrect) {
            if (isCorrect) {
                this.correct++;
            } else {
                this.wrong++;
            }

            this.index++;
        }
    }
}