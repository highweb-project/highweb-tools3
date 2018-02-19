package org.highweb.webclsdk.views.dialog.fpsLookup;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.highweb.webclsdk.views.dialog.fpsLookup.FpsLookupView.Device;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleInsets;

public class FpsLookupChart {
    public static final int TIME_SERIES_FPS = 0;
    public static final int TIME_SERIES_FLOPS = 1;
    public static final int TIME_SERIES_UNKNOWN = 2;

    public static final int UPDATE_CHART_INTERVAL_DEFAULT = 1000;
    public static final int UPDATE_CHART_INTERVAL_CHANGE = 100;

    private TimeSeries mTsFps;
    private TimeSeries mTsFlops;
    private Composite mComposite;
    private Device device;

    private XYLineAndShapeRenderer mXYLineAndShapeRenderer;

    private static JFreeChart mChart;

    private StringBuilder mSbFpsFlopsData;

    private DataGenerator mDataGenerator;
    private boolean mChartUpdateState;
    private long mInterval = UPDATE_CHART_INTERVAL_DEFAULT;

    private final Color AXIS_COLOR  = Color.BLUE;
    private final Color BAKCGROUND_COLOR = Color.WHITE;
    private final Color GRAPH_COLOR = new Color(81, 86, 88);
    
    public FpsLookupChart(int itemAge, Composite composite, Device device) {
        mTsFps = new TimeSeries("FPS");
        mTsFps.setMaximumItemAge(itemAge);
        mTsFlops = new TimeSeries("MFLOPS");
        mTsFlops.setMaximumItemAge(itemAge);
        mComposite = composite;
        this.device = device;
        mSbFpsFlopsData = new StringBuilder();
    }

    public JFreeChart createChart() {
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        timeSeriesCollection.addSeries(mTsFps);
        timeSeriesCollection.addSeries(mTsFlops);

        // x axis
        DateAxis xAxis = new DateAxis(""/*"Time"*/);
        xAxis.setTickLabelFont(new Font("SansSerif", 0, 12));
        xAxis.setLabelFont(new Font("SansSerif", 0, 14));
        xAxis.setAutoRange(true);
        xAxis.setLowerMargin(0.0D);
        xAxis.setUpperMargin(0.0D);
        xAxis.setTickLabelsVisible(true);
        xAxis.setLabelPaint(GRAPH_COLOR);
        xAxis.setTickLabelPaint(AXIS_COLOR);

        // y axis
        NumberAxis yAxis = new NumberAxis(""/* "Fps, Flops(M)" */);
        yAxis.setTickLabelFont(new Font("SansSerif", 0, 12));
        yAxis.setLabelFont(new Font("SansSerif", 0, 14));
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        yAxis.setLabelPaint(AXIS_COLOR);
        yAxis.setTickLabelPaint(AXIS_COLOR);

        // plot line
        mXYLineAndShapeRenderer = new XYLineAndShapeRenderer(true, false);
        mXYLineAndShapeRenderer.setSeriesPaint(TIME_SERIES_FPS, Color.red);
        mXYLineAndShapeRenderer.setSeriesPaint(TIME_SERIES_FLOPS, Color.blue);
        mXYLineAndShapeRenderer.setSeriesPaint(TIME_SERIES_UNKNOWN, Color.orange);
        mXYLineAndShapeRenderer.setBaseStroke(new BasicStroke(3F, 0, 2));

        XYPlot xyPlot = new XYPlot(timeSeriesCollection, xAxis, yAxis, mXYLineAndShapeRenderer);
        xyPlot.setBackgroundPaint(BAKCGROUND_COLOR);
        xyPlot.setDomainGridlinePaint(AXIS_COLOR);
        xyPlot.setRangeGridlinePaint(AXIS_COLOR);
        xyPlot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

        JFreeChart jFreeChart = new JFreeChart("", new Font("SansSerif", 1, 24), xyPlot, true);
        jFreeChart.setBackgroundPaint(BAKCGROUND_COLOR);
        return jFreeChart;
    }

    public void addFpsObservation(final double d) {
        mTsFps.addOrUpdate(new Millisecond(), d);
    }

    public void addFlopsObservation(final double d) {
        mTsFlops.addOrUpdate(new Millisecond(), d);
    }

    public void saveFpsFlopsData() {
        XYDataset dataset = mChart.getXYPlot().getDataset();
        // Number fps = dataset.getY(TIME_SERIES_FPS, dataset.getItemCount(TIME_SERIES_FPS) - 1);
        // Number flops = dataset.getY(TIME_SERIES_FLOPS, dataset.getItemCount(TIME_SERIES_FLOPS) - 1);
        // Number time = dataset.getX(TIME_SERIES_FLOPS, dataset.getItemCount(TIME_SERIES_FLOPS) - 1);
        Number fps = mTsFps.getValue(mTsFps.getItemCount() -1);
        Number flops = mTsFlops.getValue(mTsFlops.getItemCount() -1);
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        //String date = dayTime.format(new Date(time.longValue()));
        String date = dayTime.format(new Date());
        
        mSbFpsFlopsData.append(date);
        mSbFpsFlopsData.append(" / device model: " + device.getDeviceModel());
        mSbFpsFlopsData.append(" / android version: " + device.getDeviceVersion());
        mSbFpsFlopsData.append(" / fps: " + fps);
        mSbFpsFlopsData.append(" / mflops: " + flops);
        mSbFpsFlopsData.append("\n\t");

        // System.out.println(date+" / fps: "+fps+" / other: "+other+"\n");
        // System.out.println("mTsOther.getValue(mTsOther.getItemCount()-1):
        // "+mTsOther.getValue(mTsOther.getItemCount()-1));
    }

    public void setTimeSeriesVisible(int timeSeriesNum, boolean visible) {
        if (timeSeriesNum == TIME_SERIES_FPS) {
            if (visible) {
                mXYLineAndShapeRenderer.setSeriesVisible(TIME_SERIES_FPS, true);
            } else {
                mXYLineAndShapeRenderer.setSeriesVisible(TIME_SERIES_FPS, false);
            }
        } else {
            if (visible) {
                mXYLineAndShapeRenderer.setSeriesVisible(TIME_SERIES_FLOPS, true);
            } else {
                mXYLineAndShapeRenderer.setSeriesVisible(TIME_SERIES_FLOPS, false);
            }
        }
    }

    public StringBuilder getFpsFlopsData() {
        return mSbFpsFlopsData;
    }

    public void setInterval(long interval) {
        mInterval = interval;
        mDataGenerator.setInterval(interval);
    }

    public void init() {
        // mComposite.setLayoutData(gridData);
        // mComposite.setLayout(new FillLayout());
        // mComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
        // true));

        mChart = createChart();
        // y축 라벨 각도
        // chart.getXYPlot().getRangeAxis().setLabelAngle(90 * (Math.PI /
        // 180.0));

        ChartComposite chartComposite = new ChartComposite(mComposite, SWT.NONE, mChart, true);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        //gridData.horizontalSpan = 5;
        chartComposite.setLayoutData(gridData);
        chartComposite.setDisplayToolTips(true);
        chartComposite.setHorizontalAxisTrace(false);
        chartComposite.setVerticalAxisTrace(false);
        mDataGenerator = new DataGenerator(mComposite, this, UPDATE_CHART_INTERVAL_DEFAULT);
        mDataGenerator.start();
    }

    public boolean getChartUpdateState() {
        return mChartUpdateState;
    }

    public void setChartUpdateState(boolean state) {
        mChartUpdateState = state;
        if (state) {
            wakeup();
        }
    }

    public void wakeup() {
        mDataGenerator.wakeUp();
    }
    
    public void interrupt(){
    	mDataGenerator.interrupt();
    }

    public int getFps() {
        return device.getFps();
    }

    public int getFlops() {
        return device.getFlops();
    }

    public static JFreeChart getChart() {
        return mChart;
    }
}
