package timsort;

import java.util.Comparator;

public class TimSort<T> {

    private static final int MIN_MERGE = 32;
    private static final int MIN_GALLOP = 7;

    private final T[] a;
    private final Comparator<? super T> c;

    private int minGallop = MIN_GALLOP;
    private T[] tmp;
    private int stackSize = 0;
    private final int[] runBase;
    private final int[] runLen;

  
    private TimSort(T[] a, Comparator<? super T> c) {
        this.a = a;
        this.c = c;

        int len = a.length;
        // Временный массив: выделяем память под половину исходного массива (максимум)
        int tmpSize = len < 512 ? len >>> 1 : 256;
        this.tmp = (T[]) new Object[tmpSize];

        // Безопасный размер стека для массивов любой длины в Java (до Integer.MAX_VALUE)
        int stackLen = (len < 120 ? 5 : len < 1542 ? 10 : len < 119151 ? 24 : 49);
        this.runBase = new int[stackLen];
        this.runLen = new int[stackLen];
    }

    /**
     * Главная точка входа для сортировки всего массива.
     */
    public static <T> void sort(T[] a, Comparator<? super T> c) {
        if (a == null || c == null) {
            throw new IllegalArgumentException("Массив и компаратор не должны быть null");
        }
        sort(a, 0, a.length, c);
    }

    /**
     * Сортировка диапазона массива от lo (включительно) до hi (исключительно).
     */
    public static <T> void sort(T[] a, int lo, int hi, Comparator<? super T> c) {
        int nRemaining = hi - lo;
        if (nRemaining < 2) {
            return;
        }

        // Если массив маленький, используем бинарные вставки
        if (nRemaining < MIN_MERGE) {
            int initRunLen = countRunAndMakeAscending(a, lo, hi, c);
            binarySort(a, lo, hi, lo + initRunLen, c);
            return;
        }

        TimSort<T> ts = new TimSort<>(a, c);
        int minRun = minRunLength(nRemaining);

        // Основной цикл разделения на серии и их слияния
        do {
            int runLen = countRunAndMakeAscending(a, lo, hi, c);

            if (runLen < minRun) {
                int force = Math.min(nRemaining, minRun);
                binarySort(a, lo, lo + force, lo + runLen, c);
                runLen = force;
            }

            ts.pushRun(lo, runLen);
            ts.mergeCollapse();

            lo += runLen;
            nRemaining -= runLen;
        } while (nRemaining > 0);

        ts.mergeForceCollapse();
    }

    /**
     * Сортировка бинарными вставками для маленьких массивов или ранов.
     */
    private static <T> void binarySort(T[] a, int lo, int hi, int start, Comparator<? super T> c) {
        if (start == lo) {
            start++;
        }
        for (; start < hi; start++) {
            T pivot = a[start];
            int left = lo;
            int right = start;

            while (left < right) {
                int mid = (left + right) >>> 1;
                if (c.compare(pivot, a[mid]) < 0) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            }

            int n = start - left;
            switch (n) {
                case 2:  a[left + 2] = a[left + 1];
                case 1:  a[left + 1] = a[left];
                    break;
                default: System.arraycopy(a, left, a, left + 1, n);
            }
            a[left] = pivot;
        }
    }

    /**
     * Находит длину естественной серии. Если серия убывающая, разворачивает её.
     */
    private static <T> int countRunAndMakeAscending(T[] a, int lo, int hi, Comparator<? super T> c) {
        int runHi = lo + 1;
        if (runHi == hi) {
            return 1;
        }

        if (c.compare(a[runHi++], a[lo]) < 0) { // Убывающая
            while (runHi < hi && c.compare(a[runHi], a[runHi - 1]) < 0) {
                runHi++;
            }
            reverseRange(a, lo, runHi);
        } else { //Возрастающая
            while (runHi < hi && c.compare(a[runHi], a[runHi - 1]) >= 0) {
                runHi++;
            }
        }
        return runHi - lo;
    }

    private static void reverseRange(Object[] a, int lo, int hi) {
        hi--;
        while (lo < hi) {
            Object t = a[lo];
            a[lo++] = a[hi];
            a[hi--] = t;
        }
    }

    /**
     * Вычисление минимальной длины серии (minRun).
     */
    private static int minRunLength(int n) {
        int r = 0;
        while (n >= MIN_MERGE) {
            r |= (n & 1);
            n >>= 1;
        }
        return n + r;
    }

    private void pushRun(int runBase, int runLen) {
        this.runBase[stackSize] = runBase;
        this.runLen[stackSize] = runLen;
        stackSize++;
    }

    /**
     * Слияние серий для сохранения строгих инвариантов Timsort (исправленная логика OpenJDK).
     */
    private void mergeCollapse() {
        while (stackSize > 1) {
            int n = stackSize - 2;
            if (n > 0 && runLen[n - 1] <= runLen[n] + runLen[n + 1]) {
                if (runLen[n - 1] < runLen[n + 1]) {
                    n--;
                }
                mergeAt(n);
            } else if (n >= 1 && runLen[n - 1] <= runLen[n] + runLen[n + 1]) {
                // Дополнительная проверка инварианта для 4-х верхних элементов стека
                if (runLen[n - 1] < runLen[n + 1]) {
                    n--;
                }
                mergeAt(n);
            } else if (runLen[n] <= runLen[n + 1]) {
                mergeAt(n);
            } else {
                break;
            }
        }
    }

    /**
     * Финальное принудительное слияние всех оставшихся серий в стеке.
     */
    private void mergeForceCollapse() {
        while (stackSize > 1) {
            int n = stackSize - 2;
            if (n > 0 && runLen[n - 1] < runLen[n + 1]) {
                n--;
            }
            mergeAt(n);
        }
    }

    /**
     * Запускает слияние двух соседних серий на индексах i и i+1.
     */
    private void mergeAt(int i) {
        int base1 = runBase[i];
        int len1 = runLen[i];
        int base2 = runBase[i + 1];
        int len2 = runLen[i + 1];

        runLen[i] = len1 + len2;
        if (i == stackSize - 3) {
            runBase[i + 1] = runBase[i + 2];
            runLen[i + 1] = runLen[i + 2];
        }
        stackSize--;

        // Обрезаем границы слияния (элементы, которые уже на своих местах, не трогаем)
        int k = gallopRight(a[base2], a, base1, len1, 0, c);
        base1 += k;
        len1 -= k;
        if (len1 == 0) return;

        len2 = gallopLeft(a[base1 + len1 - 1], a, base2, len2, len2 - 1, c);
        if (len2 == 0) return;

        // Выбираем оптимальное направление слияния на основе меньшего подмассива
        if (len1 <= len2) {
            mergeLo(base1, len1, base2, len2);
        } else {
            mergeHi(base1, len1, base2, len2);
        }
    }

    /**
     * Слияние слева направо (используется, если первый подмассив меньше).
     */
    private void mergeLo(int base1, int len1, int base2, int len2) {
        T[] a = this.a;
        T[] tmp = ensureCapacity(len1);
        System.arraycopy(a, base1, tmp, 0, len1);

        int cursor1 = 0;
        int cursor2 = base2;
        int dest = base1;

        a[dest++] = a[cursor2++];
        if (--len2 == 0) {
            System.arraycopy(tmp, cursor1, a, dest, len1);
            return;
        }
        if (len1 == 1) {
            System.arraycopy(a, cursor2, a, dest, len2);
            a[dest + len2] = tmp[cursor1];
            return;
        }

        int minGallop = this.minGallop;
        outer:
        while (true) {
            int count1 = 0;
            int count2 = 0;

            // Прямое слияние по одному элементу
            do {
                if (c.compare(a[cursor2], tmp[cursor1]) < 0) {
                    a[dest++] = a[cursor2++];
                    count2++;
                    count1 = 0;
                    if (--len2 == 0) break outer;
                } else {a[dest++] = tmp[cursor1++];
                    count1++;
                    count2 = 0;
                    if (--len1 == 1) break outer;
                }
            } while ((count1 | count2) < minGallop);

            // Переключение в режим Галопа (Galloping Mode)
            do {
                count1 = gallopRight(a[cursor2], tmp, cursor1, len1, 0, c);
                if (count1 != 0) {
                    System.arraycopy(tmp, cursor1, a, dest, count1);
                    dest += count1;
                    cursor1 += count1;
                    len1 -= count1;
                    if (len1 <= 1) break outer;
                }
                a[dest++] = a[cursor2++];
                if (--len2 == 0) break outer;

                count2 = gallopLeft(tmp[cursor1], a, cursor2, len2, 0, c);
                if (count2 != 0) {
                    System.arraycopy(a, cursor2, a, dest, count2);
                    dest += count2;
                    cursor2 += count2;
                    len2 -= count2;
                    if (len2 == 0) break outer;
                }
                a[dest++] = tmp[cursor1++];
                if (--len1 == 1) break outer;
                minGallop--;
            } while (count1 >= MIN_GALLOP || count2 >= MIN_GALLOP);

            if (minGallop < 0) minGallop = 0;
            minGallop += 2; // Штраф за выход из режима галопа
        }

        this.minGallop = minGallop < 1 ? 1 : minGallop;

        if (len1 == 1) {
            System.arraycopy(a, cursor2, a, dest, len2);
            a[dest + len2] = tmp[cursor1];
        } else {
            System.arraycopy(tmp, cursor1, a, dest, len1);
        }
    }

    /**
     * Слияние справа налево (используется, если второй подмассив меньше).
     */
    private void mergeHi(int base1, int len1, int base2, int len2) {
        T[] a = this.a;
        T[] tmp = ensureCapacity(len2);
        System.arraycopy(a, base2, tmp, 0, len2);

        int cursor1 = base1 + len1 - 1;
        int cursor2 = len2 - 1;
        int dest = base2 + len2 - 1;

        a[dest--] = a[cursor1--];
        if (--len1 == 0) {
            System.arraycopy(tmp, 0, a, dest - (len2 - 1), len2);
            return;
        }
        if (len2 == 1) {
            dest -= len1;
            cursor1 -= len1;
            System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
            a[dest] = tmp[cursor2];
            return;
        }

        int minGallop = this.minGallop;
        outer:
        while (true) {
            int count1 = 0;
            int count2 = 0;

            do {
                if (c.compare(tmp[cursor2], a[cursor1]) < 0) {
                    a[dest--] = a[cursor1--];
                    count1++;
                    count2 = 0;
                    if (--len1 == 0) break outer;
                } else {
                    a[dest--] = tmp[cursor2--];
                    count2++;
                    count1 = 0;
                    if (--len2 == 1) break outer;
                }
            } while ((count1 | count2) < minGallop);

            do {
                count1 = len1 - gallopRight(tmp[cursor2], a, base1, len1, len1 - 1, c);
                if (count1 != 0) {
                    dest -= count1;
                    cursor1 -= count1;
                    len1 -= count1;
                    System.arraycopy(a, cursor1 + 1, a, dest + 1, count1);
                    if (len1 == 0) break outer;
                }
                a[dest--] = tmp[cursor2--];
                if (--len2 == 1) break outer;

                count2 = len2 - gallopLeft(a[cursor1], tmp, 0, len2, cursor2, c);
                if (count2 != 0) {
                    dest -= count2;
                    cursor2 -= count2;
                    len2 -= count2;
                    System.arraycopy(tmp, cursor2 + 1, a, dest + 1, count2);
                    if (len2 <= 1) break outer;
                }
                a[dest--] = a[cursor1--];
                if (--len1 == 0) break outer;
                minGallop--;
            } while (count1 >= MIN_GALLOP || count2 >= MIN_GALLOP);

            if (minGallop < 0) minGallop = 0;
            minGallop += 2;
        }

        this.minGallop = minGallop < 1 ? 1 : minGallop;

        if (len2 == 1) {
            dest -= len1;
            cursor1 -= len1;
            System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
            a[dest] = tmp[cursor2];
        } else {
            System.arraycopy(tmp, 0, a, dest - (len2 - 1), len2);
        }
    }

    private static <T> int gallopLeft(T key, T[] a, int base, int len, int hint, Comparator<? super T> c) {
        int lastOfs = 0;
        int ofs = 1;
        if (c.compare(key, a[base + hint]) > 0) {
            int maxOfs = len - hint;
            while (ofs < maxOfs && c.compare(key, a[base + hint + ofs]) > 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0) ofs = maxOfs;
            }
            if (ofs > maxOfs) ofs = maxOfs;
            lastOfs += hint;
            ofs += hint;
        } else {
            final int maxOfs = hint + 1;
            while (ofs < maxOfs && c.compare(key, a[base + hint - ofs]) <= 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0) ofs = maxOfs;
            }
            if (ofs > maxOfs) ofs = maxOfs;
            int tmp = lastOfs;
            lastOfs = hint - ofs;
            ofs = hint - tmp;
        }
        lastOfs++;
        while (lastOfs < ofs) {
            int m = lastOfs + ((ofs - lastOfs) >>> 1);
            if (c.compare(key, a[base + m]) > 0) {
                lastOfs = m + 1;
            } else {
                ofs = m;
            }
        }
        return ofs;
    }

    private static <T> int gallopRight(T key, T[] a, int base, int len, int hint, Comparator<? super T> c) {
        int ofs = 1;
        int lastOfs = 0;
        if (c.compare(key, a[base + hint]) < 0) {
            int maxOfs = hint + 1;
            while (ofs < maxOfs && c.compare(key, a[base + hint - ofs]) < 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0) ofs = maxOfs;
            }
            if (ofs > maxOfs) ofs = maxOfs;
            int tmp = lastOfs;
            lastOfs = hint - ofs;
            ofs = hint - tmp;
        } else {
            int maxOfs = len - hint;
            while (ofs < maxOfs && c.compare(key, a[base + hint + ofs]) >= 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0) ofs = maxOfs;
            }
            if (ofs > maxOfs) ofs = maxOfs;
            lastOfs += hint;
            ofs += hint;
        }
        lastOfs++;
        while (lastOfs < ofs) {
            int m = lastOfs + ((ofs - lastOfs) >>> 1);
            if (c.compare(key, a[base + m]) < 0) {
                ofs = m;
            } else {
                lastOfs = m + 1;
            }
        }
        return ofs;
    }


    private T[] ensureCapacity(int minCapacity) {
        if (tmp.length < minCapacity) {
            int newSize = minCapacity;
            newSize |= newSize >> 1;
            newSize |= newSize >> 2;
            newSize |= newSize >> 4;
            newSize |= newSize >> 8;
            newSize |= newSize >> 16;
            newSize++;
            if (newSize < 0) {
                newSize = minCapacity;
            } else {
                newSize = Math.min(newSize, a.length >>> 1);
            }
            this.tmp = (T[]) new Object[newSize];
        }
        return tmp;
    }
}
