import java.io.*;
import java.util.*;

/**
 * ЗАДАЧА: Система управления базой данных студентов
 *
 * B+ дерево используется для эффективного хранения и поиска записей студентов
 * по их ID. Это типичная задача для B+ дерева в реальных СУБД.
 *
 * Требования:
 * - Быстрый поиск студента по ID (логарифмическая сложность)
 * - Эффективная вставка новых студентов
 * - Удаление записей с сохранением баланса дерева
 * - Возможность последовательного перебора всех записей (через листья)
 */

// Класс, представляющий запись студента
class StudentRecord {
    int studentId;
    String name;
    String group;
    double gpa;

    public StudentRecord(int studentId, String name, String group, double gpa) {
        this.studentId = studentId;
        this.name = name;
        this.group = group;
        this.gpa = gpa;
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Name: %s, Group: %s, GPA: %.2f",
                studentId, name, group, gpa);
    }
}

// Расширенный узел B+ дерева для хранения записей
class BPlusTreeNodeWithData extends BPlusTreeNode {
    protected List<StudentRecord> records;  // Для листовых узлов храним записи

    public BPlusTreeNodeWithData(boolean isLeaf) {
        super(isLeaf);
        if (isLeaf) {
            this.records = new ArrayList<>();
        }
    }
}

// Расширенное B+ дерево для работы со студентами
class StudentDatabase {
    private BPlusTreeIndex index;
    private Map<Integer, StudentRecord> dataStore;  // Для простоты храним данные отдельно

    public StudentDatabase(int order) {
        this.index = new BPlusTreeIndex(order);
        this.dataStore = new HashMap<>();
    }

    // Вставка студента
    public void insert(StudentRecord student) {
        dataStore.put(student.studentId, student);
        index.insert(student.studentId);
    }

    // Поиск студента по ID
    public StudentRecord search(int studentId) {
        if (index.search(studentId)) {
            return dataStore.get(studentId);
        }
        return null;
    }

    // Удаление студента
    public boolean delete(int studentId) {
        if (index.delete(studentId)) {
            dataStore.remove(studentId);
            return true;
        }
        return false;
    }

    // Получение всех студентов в порядке ID (последовательный обход)
    public List<StudentRecord> getAllStudentsOrdered() {
        List<StudentRecord> result = new ArrayList<>();
        // Получаем все ID в порядке возрастания через обход листьев
        List<Integer> orderedIds = index.getAllKeysInOrder();
        for (int id : orderedIds) {
            result.add(dataStore.get(id));
        }
        return result;
    }

    // Статистика использования
    public void printStatistics() {
        System.out.println("Database Statistics:");
        System.out.println("  Total students: " + dataStore.size());
        System.out.println("  Tree height: " + index.getHeight());
        System.out.println("  Tree nodes: " + index.getNodeCount());
    }

    // Внутренний класс для B+ дерева, расширяющий существующую реализацию
    private class BPlusTreeIndex extends BPlusTree {
        public BPlusTreeIndex(int order) {
            super(order);
        }

        // Получить все ключи в порядке возрастания (обход листьев)
        public List<Integer> getAllKeysInOrder() {
            List<Integer> result = new ArrayList<>();
            BPlusTreeNode leaf = findLeftmostLeaf(root);
            while (leaf != null) {
                result.addAll(leaf.keys);
                leaf = leaf.next;
            }
            return result;
        }

        private BPlusTreeNode findLeftmostLeaf(BPlusTreeNode node) {
            if (node.isLeaf) return node;
            return findLeftmostLeaf(node.children.get(0));
        }

        public int getHeight() {
            return calculateHeight(root);
        }

        private int calculateHeight(BPlusTreeNode node) {
            if (node.isLeaf) return 1;
            return 1 + calculateHeight(node.children.get(0));
        }

        public int getNodeCount() {
            return countNodes(root);
        }

        private int countNodes(BPlusTreeNode node) {
            int count = 1;
            if (!node.isLeaf) {
                for (BPlusTreeNode child : node.children) {
                    count += countNodes(child);
                }
            }
            return count;
        }
    }
}

// Класс для генерации наборов данных
class DataSetGenerator {
    private static final String[] NAMES = {
            "Улучай", "Панагушина", "Цыб", "Кожевников", "Бырка", "Меджидов", "Гареев",
            "Моисеева", "Кравченко", "Ахметова", "Романов", "Лебедев", "Валиулин", "Чугун",
            "Чехов", "Осадчая", "Рахматулина", "Кондратьев", "Тарелова", "Силович"
    };

    private static final String[] GROUPS = {
            "11-501", "11-502", "11-503", "11-504", "11-403", "11-506", "11-541"
    };

    private static final Random random = new Random(42); // Фиксированный seed для воспроизводимости

    // Генерация случайного студента
    public static StudentRecord generateRandomStudent(int id) {
        String name = NAMES[random.nextInt(NAMES.length)] + " " +
                (char)('А' + random.nextInt(32)) + ".";
        String group = GROUPS[random.nextInt(GROUPS.length)];
        double gpa = 2.0 + random.nextDouble() * 3.0;
        return new StudentRecord(id, name, group, gpa);
    }

    // Генерация набора данных указанного размера
    public static List<StudentRecord> generateDataSet(int size) {
        List<StudentRecord> dataset = new ArrayList<>();
        Set<Integer> usedIds = new HashSet<>();

        for (int i = 0; i < size; i++) {
            int id;
            do {
                id = random.nextInt(size * 10) + 1;
            } while (usedIds.contains(id));
            usedIds.add(id);

            dataset.add(generateRandomStudent(id));
        }

        return dataset;
    }

    // Генерация набора ID для поиска (существующих и несуществующих)
    public static List<Integer> generateSearchKeys(List<StudentRecord> dataset, int count) {
        List<Integer> keys = new ArrayList<>();
        List<Integer> existingIds = new ArrayList<>();
        for (StudentRecord record : dataset) {
            existingIds.add(record.studentId);
        }

        for (int i = 0; i < count; i++) {
            if (random.nextBoolean() && !existingIds.isEmpty()) {
                // Существующий ID
                keys.add(existingIds.get(random.nextInt(existingIds.size())));
            } else {
                // Несуществующий ID
                keys.add(random.nextInt(dataset.size() * 15) + dataset.size() * 10);
            }
        }
        return keys;
    }
}

// Основной класс для тестирования с множеством наборов данных
public class BPlusTreeExtendedTest {
    private static final int ORDER = 4;
    private static final int NUM_DATASETS = 50;  // 50 наборов данных
    private static final int MIN_SIZE = 100;
    private static final int MAX_SIZE = 10000;

    public static void main(String[] args) throws IOException {
        System.out.println("=== Система управления базой данных студентов ===");
        System.out.println("Используется B+ дерево для индексации записей");
        System.out.println("Порядок дерева: " + ORDER);
        System.out.println("Количество наборов данных: " + NUM_DATASETS);
        System.out.println("Диапазон размеров: " + MIN_SIZE + " - " + MAX_SIZE);
        System.out.println();

        // Создаем папку для результатов
        new File("datasets").mkdir();
        new File("results").mkdir();

        // Генерируем различные размеры наборов данных
        List<Integer> sizes = generateDatasetSizes(NUM_DATASETS, MIN_SIZE, MAX_SIZE);
        Collections.sort(sizes);

        // Хранилища результатов
        List<TestResult> insertResults = new ArrayList<>();
        List<TestResult> searchResults = new ArrayList<>();
        List<TestResult> deleteResults = new ArrayList<>();

        // Список всех сгенерированных наборов (для сохранения)
        List<DataSetInfo> dataSets = new ArrayList<>();

        System.out.println("Генерация наборов данных...");

        // Генерируем и тестируем каждый набор данных
        for (int i = 0; i < NUM_DATASETS; i++) {
            int size = sizes.get(i);
            System.out.printf("Набор %d/%d (размер: %d)...\n", i + 1, NUM_DATASETS, size);

            // Генерируем набор данных
            List<StudentRecord> dataset = DataSetGenerator.generateDataSet(size);
            List<Integer> searchKeys = DataSetGenerator.generateSearchKeys(dataset, size / 2);

            // Сохраняем информацию о наборе
            DataSetInfo info = new DataSetInfo(i + 1, size, dataset);
            dataSets.add(info);

            // Сохраняем набор данных в файл
            saveDataSetToFile(info, "datasets/dataset_" + (i + 1) + ".csv");

            // Тестируем производительность
            TestResults results = testPerformance(dataset, searchKeys);

            insertResults.add(new TestResult(size, results.insertTime));
            searchResults.add(new TestResult(size, results.searchTime));
            deleteResults.add(new TestResult(size, results.deleteTime));

            System.out.printf("  Вставка: %d мкс | Поиск: %d мкс | Удаление: %d мкс\n",
                    results.insertTime, results.searchTime, results.deleteTime);
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("Сохранение результатов...");

        // Сохраняем результаты в CSV файлы
        saveToCSV("results/insert_results.csv", insertResults);
        saveToCSV("results/search_results.csv", searchResults);
        saveToCSV("results/delete_results.csv", deleteResults);

        // Сохраняем сводную информацию о наборах данных
        saveDataSetsSummary(dataSets, "results/datasets_summary.csv");

        // Выводим итоговую таблицу
        printSummaryTable(insertResults, searchResults, deleteResults);

        // Демонстрация работы с конкретным студентом
        demonstrateStudentDatabase();
    }

    private static TestResults testPerformance(List<StudentRecord> dataset, List<Integer> searchKeys) {
        StudentDatabase db = new StudentDatabase(ORDER);

        // Тест вставки
        long start = System.nanoTime();
        for (StudentRecord record : dataset) {
            db.insert(record);
        }
        long insertTime = (System.nanoTime() - start) / 1000;

        // Тест поиска
        start = System.nanoTime();
        for (int key : searchKeys) {
            db.search(key);
        }
        long searchTime = (System.nanoTime() - start) / 1000;

        // Тест удаления
        start = System.nanoTime();
        for (StudentRecord record : dataset) {
            db.delete(record.studentId);
        }
        long deleteTime = (System.nanoTime() - start) / 1000;

        return new TestResults(insertTime, searchTime, deleteTime);
    }

    private static List<Integer> generateDatasetSizes(int count, int min, int max) {
        List<Integer> sizes = new ArrayList<>();
        Random rand = new Random(123);

        // Генерируем размеры, чтобы покрыть весь диапазон
        for (int i = 0; i < count; i++) {
            int size;
            if (i < 10) {
                // Небольшие размеры для детального анализа
                size = min + rand.nextInt(1000);
            } else if (i < 30) {
                // Средние размеры
                size = 1000 + rand.nextInt(4000);
            } else {
                // Крупные размеры
                size = 5000 + rand.nextInt(5000);
            }
            sizes.add(Math.min(size, max));
        }

        Collections.sort(sizes);
        return sizes;
    }

    private static void saveDataSetToFile(DataSetInfo info, String filename) throws IOException {
        PrintWriter writer = new PrintWriter(new File(filename));
        writer.println("StudentID,Name,Group,GPA");
        for (StudentRecord record : info.dataset) {
            writer.printf("%d,%s,%s,%.2f\n",
                    record.studentId, record.name, record.group, record.gpa);
        }
        writer.close();
    }

    private static void saveDataSetsSummary(List<DataSetInfo> dataSets, String filename) throws IOException {
        PrintWriter writer = new PrintWriter(new File(filename));
        writer.println("DatasetID,Size,MinID,MaxID,UniqueNames");
        for (DataSetInfo info : dataSets) {
            int minId = info.dataset.stream().mapToInt(r -> r.studentId).min().orElse(0);
            int maxId = info.dataset.stream().mapToInt(r -> r.studentId).max().orElse(0);
            long uniqueNames = info.dataset.stream().map(r -> r.name).distinct().count();
            writer.printf("%d,%d,%d,%d,%d\n",
                    info.id, info.size, minId, maxId, uniqueNames);
        }
        writer.close();
    }

    private static void saveToCSV(String filename, List<TestResult> results) throws IOException {
        PrintWriter writer = new PrintWriter(new File(filename));
        writer.println("Размер,Время(мкс)");
        for (TestResult r : results) {
            writer.println(r.size + "," + r.timeMicros);
        }
        writer.close();
    }

    private static void printSummaryTable(List<TestResult> insert,
                                          List<TestResult> search,
                                          List<TestResult> delete) {
        System.out.println("\n" + "=".repeat(85));
        System.out.printf("%-15s %-20s %-20s %-20s %-10s\n",
                "Размер", "Вставка (мкс)", "Поиск (мкс)", "Удаление (мкс)", "Кол-во");
        System.out.println("=".repeat(85));

        // Группируем по диапазонам для наглядности
        int[] ranges = {100, 500, 1000, 2000, 5000, 7500, 10000};
        int rangeIndex = 0;

        for (int i = 0; i < insert.size(); i++) {
            int size = insert.get(i).size;
            if (size > ranges[rangeIndex] && rangeIndex < ranges.length - 1) {
                System.out.println("-".repeat(85));
                rangeIndex++;
            }
            System.out.printf("%-15d %-20d %-20d %-20d %-10d\n",
                    size,
                    insert.get(i).timeMicros,
                    search.get(i).timeMicros,
                    delete.get(i).timeMicros,
                    i + 1);
        }
        System.out.println("=".repeat(85));

        // Выводим статистику
        System.out.println("\nСтатистика:");
        System.out.printf("Среднее время вставки: %.2f мкс\n",
                insert.stream().mapToLong(r -> r.timeMicros).average().orElse(0));
        System.out.printf("Среднее время поиска: %.2f мкс\n",
                search.stream().mapToLong(r -> r.timeMicros).average().orElse(0));
        System.out.printf("Среднее время удаления: %.2f мкс\n",
                delete.stream().mapToLong(r -> r.timeMicros).average().orElse(0));
    }

    private static void demonstrateStudentDatabase() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Демонстрация работы базы данных студентов");
        System.out.println("=".repeat(80));

        StudentDatabase db = new StudentDatabase(ORDER);

        // Добавляем несколько студентов
        System.out.println("\n1. Добавление студентов:");
        StudentRecord[] students = {
                new StudentRecord(1001, "Панагушина В.", "11-504", 5.0),
                new StudentRecord(1002, "Романов А.", "11-503", 4.2),
                new StudentRecord(1003, "Валиулин М.", "11-503", 3.8),
                new StudentRecord(1004, "Осадчая А.", "11-501", 4.7),
                new StudentRecord(1005, "Ахметова К.", "11-403", 4.9)
        };

        for (StudentRecord s : students) {
            db.insert(s);
            System.out.println("  Добавлен: " + s);
        }

        // Поиск студентов
        System.out.println("\n2. Поиск студентов:");
        int[] searchIds = {1003, 1001, 1999};
        for (int id : searchIds) {
            StudentRecord found = db.search(id);
            if (found != null) {
                System.out.println("  Найден ID " + id + ": " + found);
            } else {
                System.out.println("  Студент с ID " + id + " не найден");
            }
        }

        // Получение всех студентов в порядке ID
        System.out.println("\n3. Все студенты в порядке ID:");
        List<StudentRecord> allStudents = db.getAllStudentsOrdered();
        for (StudentRecord s : allStudents) {
            System.out.println("  " + s);
        }

        // Удаление студента
        System.out.println("\n4. Удаление студента ID 1002:");
        if (db.delete(1002)) {
            System.out.println("  Студент удален");
        }

        System.out.println("\n5. Обновленный список студентов:");
        allStudents = db.getAllStudentsOrdered();
        for (StudentRecord s : allStudents) {
            System.out.println("  " + s);
        }

        // Статистика
        System.out.println("\n6. Статистика базы данных:");
        db.printStatistics();
    }

    // Вспомогательные классы
    static class TestResults {
        long insertTime;
        long searchTime;
        long deleteTime;

        TestResults(long insertTime, long searchTime, long deleteTime) {
            this.insertTime = insertTime;
            this.searchTime = searchTime;
            this.deleteTime = deleteTime;
        }
    }

    static class DataSetInfo {
        int id;
        int size;
        List<StudentRecord> dataset;

        DataSetInfo(int id, int size, List<StudentRecord> dataset) {
            this.id = id;
            this.size = size;
            this.dataset = dataset;
        }
    }
}
