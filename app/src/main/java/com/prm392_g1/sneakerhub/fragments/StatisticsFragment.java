package com.prm392_g1.sneakerhub.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.prm392_g1.sneakerhub.entities.Order;
import com.prm392_g1.sneakerhub.entities.Product;
import com.prm392_g1.sneakerhub.entities.User;
import com.prm392_g1.sneakerhub.repositories.OrderRepository;
import com.prm392_g1.sneakerhub.repositories.ProductRepository;
import com.prm392_g1.sneakerhub.repositories.UserRepository;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private LineChart salesChart;
    private TextView totalSales;
    private TextView totalOrders;
    private Spinner periodSpinner;
    private TabLayout tabLayout;

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;

    private List<Order> allOrders = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRepositories();
        setupTabLayout();
        setupChart();
        setupPeriodSpinner();
        loadStatisticsData();
    }

    private void initializeViews(View view) {
        salesChart = view.findViewById(R.id.sales_chart);
        totalSales = view.findViewById(R.id.total_sales);
        totalOrders = view.findViewById(R.id.total_orders);
        periodSpinner = view.findViewById(R.id.period_spinner);
        tabLayout = view.findViewById(R.id.statistics_tab_layout);
    }

    private void setupRepositories() {
        orderRepository = new OrderRepository();
        productRepository = new ProductRepository();
        userRepository = new UserRepository();
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

    private void loadStatisticsData() {
        loadOrders();
        loadProducts();
        loadUsers();
    }

    private void loadOrders() {
        orderRepository.getAllOrders(new OrderRepository.OrderListCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                allOrders = orders;
                updateSummaryStatistics();
                updateChartData("Sales", periodSpinner.getSelectedItem().toString());
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading orders: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        productRepository.getAllProducts(new ProductRepository.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                allProducts = products;
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading products: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUsers() {
        userRepository.getAllUsers(new UserRepository.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                allUsers = users;
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading users: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSummaryStatistics() {
        double totalSalesAmount = 0;
        for (Order order : allOrders) {
            if (!"cancelled".equalsIgnoreCase(order.status)) {
                totalSalesAmount += order.total_price;
            }
        }

        totalSales.setText(NumberFormat.getCurrencyInstance(Locale.US).format(totalSalesAmount));
        totalOrders.setText(String.valueOf(allOrders.size()));
    }

    private void updateChartData(String tabName, String period) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        switch (tabName) {
            case "Sales":
                generateSalesData(entries, labels, period);
                break;
            case "Products":
                generateProductData(entries, labels, period);
                break;
            case "Customers":
                generateCustomerData(entries, labels, period);
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

    private void generateSalesData(List<Entry> entries, List<String> labels, String period) {
        Map<String, Double> salesByPeriod = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat labelFormat;

        switch (period) {
            case "Daily":
                labelFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
                // Group sales by day for last 7 days
                for (int i = 6; i >= 0; i--) {
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.DAY_OF_YEAR, -i);
                    String dayKey = labelFormat.format(calendar.getTime());
                    salesByPeriod.put(dayKey, 0.0);
                }
                break;

            case "Weekly":
                labelFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
                // Group sales by week for last 4 weeks
                for (int i = 3; i >= 0; i--) {
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.WEEK_OF_YEAR, -i);
                    String weekKey = "Week " + labelFormat.format(calendar.getTime());
                    salesByPeriod.put(weekKey, 0.0);
                }
                break;

            case "Monthly":
                labelFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                // Group sales by month for last 12 months
                for (int i = 11; i >= 0; i--) {
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.MONTH, -i);
                    String monthKey = labelFormat.format(calendar.getTime());
                    salesByPeriod.put(monthKey, 0.0);
                }
                break;

            case "Yearly":
                labelFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                // Group sales by year for last 5 years
                for (int i = 4; i >= 0; i--) {
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.YEAR, -i);
                    String yearKey = labelFormat.format(calendar.getTime());
                    salesByPeriod.put(yearKey, 0.0);
                }
                break;
        }

        // Calculate actual sales for each period
        for (Order order : allOrders) {
            if (!"cancelled".equalsIgnoreCase(order.status)) {
                calendar.setTimeInMillis(order.created_date);
                String periodKey;

                switch (period) {
                    case "Daily":
                        periodKey = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(calendar.getTime());
                        break;
                    case "Weekly":
                        periodKey = "Week " + new SimpleDateFormat("MMM dd", Locale.getDefault()).format(calendar.getTime());
                        break;
                    case "Monthly":
                        periodKey = new SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(calendar.getTime());
                        break;
                    case "Yearly":
                        periodKey = new SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.getTime());
                        break;
                    default:
                        continue;
                }

                if (salesByPeriod.containsKey(periodKey)) {
                    salesByPeriod.put(periodKey, salesByPeriod.get(periodKey) + order.total_price);
                }
            }
        }

        // Convert to chart data
        int index = 0;
        for (Map.Entry<String, Double> entry : salesByPeriod.entrySet()) {
            entries.add(new Entry(index, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            index++;
        }
    }

    private void generateProductData(List<Entry> entries, List<String> labels, String period) {
        // For products, show number of new products added in each period
        Map<String, Integer> productsByPeriod = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat labelFormat;

        switch (period) {
            case "Daily":
                labelFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
                for (int i = 6; i >= 0; i--) {
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.DAY_OF_YEAR, -i);
                    String dayKey = labelFormat.format(calendar.getTime());
                    productsByPeriod.put(dayKey, 0);
                }
                break;
            case "Monthly":
                labelFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                for (int i = 11; i >= 0; i--) {
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.MONTH, -i);
                    String monthKey = labelFormat.format(calendar.getTime());
                    productsByPeriod.put(monthKey, 0);
                }
                break;
            default:
                // Default to monthly for other periods
                labelFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                for (int i = 5; i >= 0; i--) {
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.MONTH, -i);
                    String monthKey = labelFormat.format(calendar.getTime());
                    productsByPeriod.put(monthKey, 0);
                }
                break;
        }

        // Count products added in each period
        for (Product product : allProducts) {
            calendar.setTimeInMillis(product.created_date);
            String periodKey = labelFormat.format(calendar.getTime());

            if (productsByPeriod.containsKey(periodKey)) {
                productsByPeriod.put(periodKey, productsByPeriod.get(periodKey) + 1);
            }
        }

        // Convert to chart data
        int index = 0;
        for (Map.Entry<String, Integer> entry : productsByPeriod.entrySet()) {
            entries.add(new Entry(index, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            index++;
        }
    }

    private void generateCustomerData(List<Entry> entries, List<String> labels, String period) {
        // For customers, show number of new registrations in each period
        // Since User entity doesn't have created_date, we'll simulate with order data
        Map<String, Integer> customersByPeriod = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat labelFormat;

        switch (period) {
            case "Daily":
                labelFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
                for (int i = 6; i >= 0; i--) {
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.DAY_OF_YEAR, -i);
                    String dayKey = labelFormat.format(calendar.getTime());
                    customersByPeriod.put(dayKey, 0);
                }
                break;
            case "Monthly":
                labelFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                for (int i = 11; i >= 0; i--) {
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.MONTH, -i);
                    String monthKey = labelFormat.format(calendar.getTime());
                    customersByPeriod.put(monthKey, 0);
                }
                break;
            default:
                // Default to monthly
                labelFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                for (int i = 5; i >= 0; i--) {
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.MONTH, -i);
                    String monthKey = labelFormat.format(calendar.getTime());
                    customersByPeriod.put(monthKey, 0);
                }
                break;
        }

        // Count unique customers per period based on their first order
        Map<String, Long> firstOrderByUser = new HashMap<>();
        for (Order order : allOrders) {
            if (!firstOrderByUser.containsKey(order.user_id) ||
                order.created_date < firstOrderByUser.get(order.user_id)) {
                firstOrderByUser.put(order.user_id, order.created_date);
            }
        }

        for (Long firstOrderDate : firstOrderByUser.values()) {
            calendar.setTimeInMillis(firstOrderDate);
            String periodKey = labelFormat.format(calendar.getTime());

            if (customersByPeriod.containsKey(periodKey)) {
                customersByPeriod.put(periodKey, customersByPeriod.get(periodKey) + 1);
            }
        }

        // Convert to chart data
        int index = 0;
        for (Map.Entry<String, Integer> entry : customersByPeriod.entrySet()) {
            entries.add(new Entry(index, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            index++;
        }
    }
}
