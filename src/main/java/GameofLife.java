import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GameofLife implements GameOfLifeI {
    @Override
    public List<String> play(String inputFile) {
        Scanner scanner = getScanner(inputFile);
        if (scanner == null) {
            return null;
        }

        int N = scanner.nextInt();
        int M = scanner.nextInt();
        scanner.nextLine();

        boolean[][] cell1 = new boolean[N][N];
        boolean[][] cell2 = new boolean[N][N];
        List<String> list = new ArrayList<>();
        for (int i = 0; i < N; ++i) {
            list.add(scanner.nextLine());
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                cell1[i][j] = (list.get(i).charAt(j) == '1');
            }
        }

        boolean[][] lastStep = cell1;
        int stepsize = 3;
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < stepsize; j++) {
                for (int k = 0; k < stepsize; k++) {
                    int xfirst = j * N / stepsize;
                    int xlast = (j + 1) * N / stepsize + (j + 1 == (stepsize - 1) ? N % stepsize : 0);
                    int yfirst = k * N / stepsize;
                    int ylast = (k + 1) * N / stepsize + (k + 1 == (stepsize - 1) ? N % stepsize : 0);
                    Thread t = new Thread(new ThreadTask(cell1, cell2, N, xfirst, xlast, yfirst, ylast));
                    threads.add(t);
                    t.start();
                }
            }
            joinThreads(threads);
            boolean[][] buf = cell1; cell1 = cell2; cell2 = buf;
        }

        List<String> result = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            final boolean[] tmp = lastStep[i];
            result.add(IntStream.range(0, N).mapToObj(pos -> Integer.toString(tmp[pos] ? 1 : 0)).collect(Collectors.joining()));
        }

        return result;
    }

    private void joinThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Scanner getScanner(String inputFile) {
        try {
            return new Scanner(new File(inputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class ThreadTask implements Runnable {
        private boolean[][] cell;
        private boolean[][] result;
        private int N;
        private int xfirst, xlast, yfirst, ylast;

        public ThreadTask(boolean[][] cell, boolean[][] result, int N, int xfirst, int xlast, int yfirst, int ylast) {
            this.xfirst = xfirst;
            this.xlast = xlast;
            this.yfirst = yfirst;
            this.ylast = ylast;
            this.cell = cell;
            this.result = result;
            this.N = N;
        }

        @Override
        public void run() {
            int[] xarray = new int[3];
            int[] yarray = new int[3];
            for (int i = xfirst; i < xlast; i++) {
                for (int j = yfirst; j < ylast; j++) {
                    int aliveNbs = 0;

                    aliveNbs += btoi(cell[(i - 1 + N) % N][(j - 1 + N) % N]);
                    aliveNbs += btoi(cell[(i - 1 + N) % N][j]);
                    aliveNbs += btoi(cell[(i - 1 + N) % N][(j + 1) % N]);
                    aliveNbs += btoi(cell[i][(j - 1 + N) % N]) + btoi(cell[i][(j + 1) % N]);
                    aliveNbs += btoi(cell[(i + 1) % N][(j - 1 + N) % N]);
                    aliveNbs += btoi(cell[(i + 1) % N][j]);
                    aliveNbs += btoi(cell[(i + 1) % N][(j + 1) % N]);

                    result[i][j] = (cell[i][j] && aliveNbs == 2 || aliveNbs == 3);
                }
            }
        }

        private int btoi(boolean b) {
            return (b ? 1 : 0);
        }
    }


}
