package org.example.service;

import org.example.model.Customer;
import org.example.model.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderAnalyticsService {
    List<String> getUniqueCitiesFromOrders(List<Order> orders);

    BigDecimal getTotalIncomeFromCompletedOrders(List<Order> orders);

    String getMostPopularProductBySales(List<Order> orders);

    BigDecimal getAvgCheckForDeliveredOrders(List<Order> orders);

    List<Customer> getCustomersWithMoreThan5Orders(List<Order> orders);
}
