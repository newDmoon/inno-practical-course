package org.innowise.service;

import org.innowise.model.Customer;
import org.innowise.model.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderAnalyticsService {
    List<String> getUniqueCitiesFromOrders(List<Order> orders);

    BigDecimal getTotalIncomeFromCompletedOrders(List<Order> orders);

    String getMostPopularProductBySales(List<Order> orders);

    BigDecimal getAvgCheckForDeliveredOrders(List<Order> orders);

    List<Customer> getCustomersWithMoreThan5Orders(List<Order> orders);
}
