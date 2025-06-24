package com.prm392_g1.sneakerhub.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;
import com.prm392_g1.sneakerhub.R;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private LineChart salesChart;
    private TextView totalSales;
    private TextView totalOrders;
    private Spinner periodSpinner;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        salesChart = view.findViewById(R.id.sales_chart);
        totalSales = view.findViewById(R.id.total_sales);
        totalOrders = view.findViewById(R.id.total_orders);
        periodSpinner = view.findViewById(R.id.period_spinner);
        tabLayout = view.findViewById(R.id.statistics_tab_layout);

        // Setup the tab layout
        setupTabLayout();

        // Setup the chart
        setupChart();

        // Setup period spinner
        setupPeriodSpinner();

        // Load initial data
        loadStatisticsData("Daily");
    }

    private void setupTabLayout() {
        // Add tabs for different statistics views
        tabLayout.addTab(tabLayout.newTab().setText("Sales"));
        tabLayout.addTab(tabLayout.newTab().setText("Products"));
        tabLayout.addTab(tabLayout.newTab().setText("Customers"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Update the chart based on selected tab
                String period = periodSpinner.getSelectedItem().toString();
                String tabName = tab.getText().toString();
                updateChartData(tabName, period);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });
    }

    private void setupChart() {
        // Configure the chart appearance
        salesChart.getDescription().setEnabled(false);
        salesChart.setDrawGridBackground(false);
        salesChart.setDrawBorders(false);
        salesChart.setTouchEnabled(true);
        salesChart.setDragEnabled(true);
        salesChart.setScaleEnabled(true);
        salesChart.setPinchZoom(true);
        salesChart.getLegend().setEnabled(false);

        // Configure X axis
        XAxis xAxis = salesChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
    }

    private void setupPeriodSpinner() {
        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String period = parent.getItemAtPosition(position).toString();
                String tabName = tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString();
                updateChartData(tabName, period);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not needed
            }
        });
    }

    private void loadStatisticsData(String period) {
        // In a real app, this would fetch data from a database or API
        // For demonstration, we'll use sample data

        // Update summary
        totalSales.setText("$10,245");
        totalOrders.setText("124");

        // Update chart with dummy data
        updateChartData("Sales", period);
    }

    private void updateChartData(String tabName, String period) {
        // In a real app, you would fetch different data based on the selected tab and period
        // For demonstration, we'll use sample data
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Generate dummy data based on period
        int dataPoints = 0;
        switch (period) {
            case "Daily":
                dataPoints = 7;  // Last 7 days
                for (int i = 0; i < dataPoints; i++) {
                    entries.add(new Entry(i, (float) (Math.random() * 1000)));
                    labels.add("Day " + (i + 1));
                }
                break;
            case "Weekly":
                dataPoints = 4;  // Last 4 weeks
                for (int i = 0; i < dataPoints; i++) {
                    entries.add(new Entry(i, (float) (Math.random() * 5000)));
                    labels.add("Week " + (i + 1));
                }
                break;
            case "Monthly":
                dataPoints = 12;  // Last 12 months
                for (int i = 0; i < dataPoints; i++) {
                    entries.add(new Entry(i, (float) (Math.random() * 20000)));
                    labels.add("Month " + (i + 1));
                }
                break;
            case "Yearly":
                dataPoints = 5;  // Last 5 years
                for (int i = 0; i < dataPoints; i++) {
                    entries.add(new Entry(i, (float) (Math.random() * 100000)));
                    labels.add("Year " + (2021 + i));
                }
                break;
        }

        // Format X axis with appropriate labels
        XAxis xAxis = salesChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        // Create dataset and set appearance
        LineDataSet dataSet = new LineDataSet(entries, tabName);
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#80D6EAFF"));

        // Apply to chart
        LineData lineData = new LineData(dataSet);
        salesChart.setData(lineData);
        salesChart.invalidate(); // Refresh
    }
}
