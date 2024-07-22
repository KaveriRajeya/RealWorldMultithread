package realworldexample;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class ProducerConsumer {
    private Queue<Integer> buffer = new LinkedList<>();
    private final int bufferSize = 5;
    private volatile boolean running = true;

    public static void main(String[] args) {
        ProducerConsumer pc = new ProducerConsumer();
        ExecutorService executor = Executors.newFixedThreadPool(4); // creating 4 thread pools

        executor.execute(pc::produce);
        executor.execute(pc::produce);
        executor.execute(pc::consume);
        executor.execute(pc::consume);

        try {
            Thread.sleep(5000);// working in 5 seconds 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pc.stop();

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS); // it can be work in minutes also
            // when current thread is interrupted it will block untill all tasks completes there execution and then it will request for shutdown.
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("System is going to shutdown.");
    }

    public void produce() {
        while (running) {
            synchronized (this) {
                while (buffer.size() == bufferSize) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                int item = (int) (Math.random() * 100);
                buffer.add(item);
                System.out.println("Produced " + item);
                notifyAll();
            }
            try {
                Thread.sleep(100); // Simulate time taken to produce
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void consume() {
        while (running) {
            synchronized (this) {
                while (buffer.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                int item = buffer.poll();
                System.out.println("Consumed " + item);
                notifyAll();
            }
            try {
                Thread.sleep(150); // Simulate time taken to consume
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        running = false;
    }
}