import java.io.*;
import java.util.*;

public class BPlusTreeTest {
    private static final int ORDER = 4;  // Порядок B+ дерева

    public static void main(String[] args) throws IOException {
        System.out.println("=== Тестирование производительности B+ Дерева ===");
        System.out.println("Порядок дерева: " + ORDER);
        System.out.println();

        // Создаём папку для результатов
        new File("results").mkdir();

        // Размеры тестовых наборов данных
        int[] sizes = {100, 200, 500, 1000, 2000, 3000, 5000, 7000, 10000};

        // Хранилища результатов
        List<TestResult> insertResults = new ArrayList<>();
        List<TestResult> searchResults = new ArrayList<>();
        List<TestResult> deleteResults = new ArrayList<>();

        // Тестируем каждый размер
        for (int size : sizes) {
            System.out.println("Тестирование размера данных: " + size);

            long totalInsertTime = 0;
            long totalSearchTime = 0;
            long totalDeleteTime = 0;

            int numTests = 5;  // 5 тестов для каждого размера

            for (int test = 0; test < numTests; test++) {
                // Генерируем тестовые данные
                List<Integer> numbers = generateRandomNumbers(size);           // Для вставки и удаления
                List<Integer> searchKeys = generateRandomNumbers(size / 2);   // Для поиска (половина от размера)

                // === ТЕСТ ВСТАВКИ ===
                BPlusTree tree = new BPlusTree(ORDER);
                long start = System.nanoTime();
                for (int num : numbers) {
                    tree.insert(num);
                }
                long end = System.nanoTime();
                totalInsertTime += (end - start);

                // === ТЕСТ ПОИСКА ===
                start = System.nanoTime();
                for (int key : searchKeys) {
                    tree.search(key);
                }
                end = System.nanoTime();
                totalSearchTime += (end - start);

                // === ТЕСТ УДАЛЕНИЯ ===
                start = System.nanoTime();
                for (int num : numbers) {
                    tree.delete(num);
                }
                end = System.nanoTime();
                totalDeleteTime += (end - start);
            }

            // Вычисляем среднее время (переводим в микросекунды)
            long avgInsert = totalInsertTime / numTests / 1000;
            long avgSearch = totalSearchTime / numTests / 1000;
            long avgDelete = totalDeleteTime / numTests / 1000;

            // Сохраняем результаты
            insertResults.add(new TestResult(size, avgInsert));
            searchResults.add(new TestResult(size, avgSearch));
            deleteResults.add(new TestResult(size, avgDelete));

            // Выводим результаты для текущего размера
            System.out.printf("  Вставка: %d мкс%n", avgInsert);
            System.out.printf("  Поиск:   %d мкс%n", avgSearch);
            System.out.printf("  Удаление: %d мкс%n", avgDelete);
            System.out.println();
        }

        // Сохраняем результаты в CSV файлы
        saveToCSV("results/insert_results.csv", insertResults);
        saveToCSV("results/search_results.csv", searchResults);
        saveToCSV("results/delete_results.csv", deleteResults);

        // Выводим итоговую таблицу
        printSummaryTable(insertResults, searchResults, deleteResults);
    }

    private static List<Integer> generateRandomNumbers(int count) {
        List<Integer> numbers = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            numbers.add(rand.nextInt(count * 10));
        }
        return numbers;
    }

    private static void saveToCSV(String filename, List<TestResult> results) throws IOException {
        PrintWriter writer = new PrintWriter(new File(filename));
        writer.println("Размер,Время(мкс)");
        for (TestResult r : results) {
            writer.println(r.size + "," + r.timeMicros);
        }
        writer.close();
    }

    private static void printSummaryTable(List<TestResult> insert, List<TestResult> search, List<TestResult> delete) {
        System.out.println("\n" + "=".repeat(75));
        System.out.printf("%-12s %-20s %-20s %-20s%n", "Размер", "Вставка (мкс)", "Поиск (мкс)", "Удаление (мкс)");
        System.out.println("=".repeat(75));

        for (int i = 0; i < insert.size(); i++) {
            System.out.printf("%-12d %-20d %-20d %-20d%n",
                    insert.get(i).size,
                    insert.get(i).timeMicros,
                    search.get(i).timeMicros,
                    delete.get(i).timeMicros);
        }
        System.out.println("=".repeat(75));
    }
}

class TestResult {
    int size;           // Размер данных
    long timeMicros;    // Время в микросекундах

    TestResult(int size, long timeMicros) {
        this.size = size;
        this.timeMicros = timeMicros;
    }
}
