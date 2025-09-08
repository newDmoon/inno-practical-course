package org.example.service;

import org.example.model.Customer;
import org.example.model.Order;
import org.example.model.OrderItem;
import org.example.model.OrderStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of {@link OrderAnalyticsService} that provides
 * analytical operations over customer orders.
 */
public class OrderAnalyticsServiceImpl implements OrderAnalyticsService {
    private static final String ORDERS_LIST_IS_NULL = "orders list is null";
    private static final int ROUNDING_SCALE = 2;

    /**
     * Returns the list of unique cities where orders came from.
     *
     * @param orders list of orders
     * @return list of distinct city names, possibly empty
     * @throws NullPointerException if orders is null
     */
    @Override
    public List<String> getUniqueCitiesFromOrders(List<Order> orders) {
        Objects.requireNonNull(orders, ORDERS_LIST_IS_NULL);

        return orders.stream()
                .filter(Objects::nonNull)
                .map(Order::getCustomer)
                .filter(Objects::nonNull)
                .map(Customer::getCity)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    /**
     * Calculates total income from orders with status {@link OrderStatus#DELIVERED}.
     *
     * @param orders list of orders
     * @return total income as {@link BigDecimal}, zero if no delivered orders
     * @throws NullPointerException if orders is null
     */
    @Override
    public BigDecimal getTotalIncomeFromCompletedOrders(List<Order> orders) {
        Objects.requireNonNull(orders, ORDERS_LIST_IS_NULL);

        return orders.stream()
                .filter(Objects::nonNull)
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .flatMap(order -> Optional.ofNullable(order.getItems()).orElse(List.of()).stream())
                .filter(Objects::nonNull)
                .map(item -> BigDecimal.valueOf(item.getPrice())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Finds the most popular product by total quantity among orders with status
     * {@link OrderStatus#SHIPPED} or {@link OrderStatus#DELIVERED}.
     *
     * @param orders list of orders
     * @return product name with the highest total quantity
     * @throws NullPointerException if orders is null
     * @throws NoSuchElementException if there are no shipped or delivered orders
     */
    @Override
    public String getMostPopularProductBySales(List<Order> orders) {
        Objects.requireNonNull(orders, ORDERS_LIST_IS_NULL);

        Map<String, Integer> mapProductQuantities = orders.stream()
                .filter(Objects::nonNull)
                .filter(order -> order.getStatus() == OrderStatus.SHIPPED
                        || order.getStatus() == OrderStatus.DELIVERED)
                .flatMap(order -> Optional.ofNullable(order.getItems()).orElse(List.of()).stream())
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        OrderItem::getProductName,
                        Collectors.summingInt(OrderItem::getQuantity))
                );

        return mapProductQuantities.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Calculates the average check (average total price per order) for
     * all {@link OrderStatus#DELIVERED} orders.
     *
     * @param orders list of orders
     * @return average check as {@link BigDecimal}
     * @throws NullPointerException if orders is null
     */
    @Override
    public BigDecimal getAvgCheckForDeliveredOrders(List<Order> orders) {
        Objects.requireNonNull(orders, ORDERS_LIST_IS_NULL);

        List<BigDecimal> checks = orders.stream()
                .filter(Objects::nonNull)
                .filter(order -> order.getStatus().equals(OrderStatus.DELIVERED))
                .map(order -> Optional.ofNullable(order.getItems()).orElse(List.of()).stream()
                        .filter(Objects::nonNull)
                        .map(item -> BigDecimal.valueOf(item.getPrice())
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .filter(check -> check.compareTo(BigDecimal.ZERO) > 0)
                .toList();

        if (!checks.isEmpty()) {
            return checks.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(checks.size()), ROUNDING_SCALE, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    /**
     * Returns a list of customers who have more than 5 orders.
     *
     * @param orders list of orders
     * @return list of customers with more than 5 orders, possibly empty
     * @throws NullPointerException if orders null
     */
    @Override
    public List<Customer> getCustomersWithMoreThan5Orders(List<Order> orders) {
        Objects.requireNonNull(orders, ORDERS_LIST_IS_NULL);

        return orders.stream()
                .filter(Objects::nonNull)
                .map(Order::getCustomer)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(customer -> customer, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 5)
                .map(Map.Entry::getKey)
                .toList();
    }
}
