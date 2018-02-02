package org.highweb.webclsdk.views;

import org.eclipse.swt.widgets.Composite;


public class DataGenerator extends Thread {
    private Composite composite;
    private FpsLookupChart fpsLookupChart;
    private long interval;

    public DataGenerator(Composite composite, FpsLookupChart fpsLookupChart, long interval) {
        this.composite = composite;
        this.fpsLookupChart = fpsLookupChart;
        this.interval = interval;
    }

    @Override
    public void run() {
        System.out.println("Thread Begin...");
        while (!composite.isDisposed()) {
            // while (!Thread.currentThread().isInterrupted()) {
            synchronized (this) {
                try {
                    if (!composite.isDisposed()) {
                        System.out.println(Thread.currentThread().getName() + ": wait");
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + ": wait() InterruptedException");
                }
            }

            while (true) {

                if (composite.isDisposed() || !fpsLookupChart.getChartUpdateState())
                    break;

                composite.getDisplay().syncExec(new Runnable() {

                    @Override
                    public void run() {
                        // isDisposed() 컨트롤이 삭제되었는지?
                        if (!composite.isDisposed()) {
//                            System.out.println(Thread.currentThread().getName() + ": run");
                            fpsLookupChart.addFpsObservation(fpsLookupChart.getFps());
                            fpsLookupChart.addFlopsObservation(fpsLookupChart.getFlops());
                            fpsLookupChart.saveFpsFlopsData();
                        }
                    }
                });

                sleep();
            }

        }
        System.out.println("Thread End...");
    }

    private void sleep() {
        try {
//            System.out.println(Thread.currentThread().getName() + ": current interval: " + this.interval + " sleeping...");
            Thread.sleep(interval);
        } catch (InterruptedException e) {
//            System.out.println(Thread.currentThread().getName() + ": sleep() InterruptedException");
        }
    }

    public void setInterval(long interval) {
        System.out.println("================================");
        System.out.println("trying interrupt...");
        System.out.println("old interval: " + this.interval);
        System.out.println("new interval: " + interval);
        System.out.println("================================");
        this.interval = interval;
        interrupt();
    }

    public void wakeUp() {
        synchronized (this) {
            System.out.println(Thread.currentThread().getName() + ": notify");
            this.notify();
        }

    }
}
