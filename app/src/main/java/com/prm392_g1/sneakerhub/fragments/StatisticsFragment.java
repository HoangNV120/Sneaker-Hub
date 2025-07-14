package com.prm392_g1.sneakerhub.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.prm392_g1.sneakerhub.entities.ProductVariant;
import com.prm392_g1.sneakerhub.entities.User;
import com.prm392_g1.sneakerhub.repositories.OrderRepository;
import com.prm392_g1.sneakerhub.repositories.ProductRepository;
import com.prm392_g1.sneakerhub.repositories.UserRepository;
import com.prm392_g1.sneakerhub.utils.ProductUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private Map<String, List<ProductVariant>> productVariants = new HashMap<>();

    private String[] periodOptions = {"Daily", "Weekly", "Monthly", "Yearly"};

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
        // Clear existing tabs and add new ones
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("Sales"));
        tabLayout.addTab(tabLayout.newTab().setText("Products"));
        tabLayout.addTab(tabLayout.newTab().setText("Customers"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateChartData(tab.getText().toString(), periodSpinner.getSelectedItem().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
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
        xAxis.setTextColor(Color.BLACK);

        // Configure Y axes
        salesChart.getAxisLeft().setTextColor(Color.BLACK);
        salesChart.getAxisRight().setEnabled(false);
    }

    private void setupPeriodSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
            android.R.layout.simple_spinner_item, periodOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(adapter);
        periodSpinner.setSelection(2); // Default to "Monthly"

        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String period = periodOptions[position];
                TabLayout.Tab selectedTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());
                if (selectedTab != null) {
                    updateChartData(selectedTab.getText().toString(), period);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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
                // Show empty data
                allOrders = new ArrayList<>();
                updateSummaryStatistics();
            }
        });
    }

    private void loadProducts() {
        productRepository.getAllProducts(new ProductRepository.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                allProducts = products;
                loadProductVariants();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading products: " + error, Toast.LENGTH_SHORT).show();
                allProducts = new ArrayList<>();
            }
        });
    }

    private void loadProductVariants() {
        if (allProducts.isEmpty()) return;

        for (Product product : allProducts) {
            productRepository.getVariantsByProductId(product.id, new ProductRepository.VariantListCallback() {
                @Override
                public void onSuccess(List<ProductVariant> variants) {
                    productVariants.put(product.id, variants);
                }

                @Override
                public void onError(String error) {
                    // Continue without variants for this product
                    productVariants.put(product.id, new ArrayList<>());
                }
            });
        }
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
                allUsers = new ArrayList<>();
            }
        });
    }

    private void updateSummaryStatistics() {
        double totalSalesAmount = 0;
        int validOrderCount = 0;

        for (Order order : allOrders) {
            if (order.status != null && !"cancelled".equalsIgnoreCase(order.status)) {
                totalSalesAmount += order.total_price;
                validOrderCount++;
            }
        }

        totalSales.setText(NumberFormat.getCurrencyInstance(Locale.US).format(totalSalesAmount));
        totalOrders.setText(String.valueOf(validOrderCount));
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
            default:
                generateSalesData(entries, labels, period);
                break;
        }

        // Handle empty data
        if (entries.isEmpty()) {
            // Add default empty data points
            for (int i = 0; i < 7; i++) {
                entries.add(new Entry(i, 0));
                labels.add("No Data");
            }
        }

        // Format X axis with appropriate labels
        XAxis xAxis = salesChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(Math.min(labels.size(), 7), false);

        // Create dataset and set appearance
        LineDataSet dataSet = new LineDataSet(entries, tabName);
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setCircleColor(Color.parseColor("#2196F3"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(6f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#802196F3"));

        // Apply to chart
        LineData lineData = new LineData(dataSet);
        salesChart.setData(lineData);
        salesChart.animateX(1000);
        salesChart.invalidate(); // Refresh
    }

    private void generateSalesData(List<Entry> entries, List<String> labels, String period) {
        Map<String, Double> salesByPeriod = createPeriodMap(period);
        SimpleDateFormat labelFormat = getLabelFormat(period);

        // Calculate actual sales for each period
        for (Order order : allOrders) {
            if (order.status != null && !"cancelled".equalsIgnoreCase(order.status) && order.created_date > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(order.created_date);
                String periodKey = formatPeriodKey(calendar, period, labelFormat);

                if (salesByPeriod.containsKey(periodKey)) {
                    salesByPeriod.put(periodKey, salesByPeriod.get(periodKey) + order.total_price);
                }
            }
        }

        convertToChartData(salesByPeriod, entries, labels);
    }

    private void generateProductData(List<Entry> entries, List<String> labels, String period) {
        Map<String, Integer> productsByPeriod = new HashMap<>();
        initializePeriodMapForProducts(productsByPeriod, period);

        SimpleDateFormat labelFormat = getLabelFormat(period);

        // Count products added in each period
        for (Product product : allProducts) {
            if (product.created_date > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(product.created_date);
                String periodKey = formatPeriodKey(calendar, period, labelFormat);

                if (productsByPeriod.containsKey(periodKey)) {
                    productsByPeriod.put(periodKey, productsByPeriod.get(periodKey) + 1);
                }
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
        Map<String, Integer> customersByPeriod = new HashMap<>();
        initializePeriodMapForCustomers(customersByPeriod, period);

        SimpleDateFormat labelFormat = getLabelFormat(period);

        // Count unique customers per period based on their first order
        Map<String, Long> firstOrderByUser = new HashMap<>();
        for (Order order : allOrders) {
            if (order.user_id != null && order.created_date > 0) {
                if (!firstOrderByUser.containsKey(order.user_id) ||
                    order.created_date < firstOrderByUser.get(order.user_id)) {
                    firstOrderByUser.put(order.user_id, order.created_date);
                }
            }
        }

        for (Long firstOrderDate : firstOrderByUser.values()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(firstOrderDate);
            String periodKey = formatPeriodKey(calendar, period, labelFormat);

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

    private Map<String, Double> createPeriodMap(String period) {
        Map<String, Double> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat labelFormat = getLabelFormat(period);

        int periodCount = getPeriodCount(period);
        int calendarField = getCalendarField(period);

        for (int i = periodCount - 1; i >= 0; i--) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(calendarField, -i);
            String key = formatPeriodKey(calendar, period, labelFormat);
            map.put(key, 0.0);
        }

        return map;
    }

    private void initializePeriodMapForProducts(Map<String, Integer> map, String period) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat labelFormat = getLabelFormat(period);

        int periodCount = getPeriodCount(period);
        int calendarField = getCalendarField(period);

        for (int i = periodCount - 1; i >= 0; i--) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(calendarField, -i);
            String key = formatPeriodKey(calendar, period, labelFormat);
            map.put(key, 0);
        }
    }

    private void initializePeriodMapForCustomers(Map<String, Integer> map, String period) {
        initializePeriodMapForProducts(map, period); // Same logic
    }

    private SimpleDateFormat getLabelFormat(String period) {
        switch (period) {
            case "Daily":
                return new SimpleDateFormat("MMM dd", Locale.getDefault());
            case "Weekly":
                return new SimpleDateFormat("MMM dd", Locale.getDefault());
            case "Monthly":
                return new SimpleDateFormat("MMM yyyy", Locale.getDefault());
            case "Yearly":
                return new SimpleDateFormat("yyyy", Locale.getDefault());
            default:
                return new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        }
    }

    private int getPeriodCount(String period) {
        switch (period) {
            case "Daily":
                return 7;
            case "Weekly":
                return 4;
            case "Monthly":
                return 6;
            case "Yearly":
                return 3;
            default:
                return 6;
        }
    }

    private int getCalendarField(String period) {
        switch (period) {
            case "Daily":
                return Calendar.DAY_OF_YEAR;
            case "Weekly":
                return Calendar.WEEK_OF_YEAR;
            case "Monthly":
                return Calendar.MONTH;
            case "Yearly":
                return Calendar.YEAR;
            default:
                return Calendar.MONTH;
        }
    }

    private String formatPeriodKey(Calendar calendar, String period, SimpleDateFormat labelFormat) {
        switch (period) {
            case "Weekly":
                return "W" + calendar.get(Calendar.WEEK_OF_YEAR);
            default:
                return labelFormat.format(calendar.getTime());
        }
    }

    private void convertToChartData(Map<String, Double> dataMap, List<Entry> entries, List<String> labels) {
        int index = 0;
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            entries.add(new Entry(index, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            index++;
        }
    }
}
