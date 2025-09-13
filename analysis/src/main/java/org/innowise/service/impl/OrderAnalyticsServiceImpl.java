package org.innowise.service.impl;

import org.innowise.model.Customer;
import org.innowise.model.Order;
import org.innowise.model.OrderItem;
import org.innowise.model.OrderStatus;
import org.innowise.service.OrderAnalyticsService;

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
    private static final int MIN_ORDER_COUNT = 5;

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
     * @throws NullPointerException   if orders is null
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

        return orders.stream()
                .filter(Objects::nonNull)
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .map(order -> Optional.ofNullable(order.getItems()).orElse(List.of()).stream()
                        .filter(Objects::nonNull)
                        .map(item -> BigDecimal.valueOf(item.getPrice())
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .filter(check -> check.compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.teeing(
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add),
                        Collectors.counting(),
                        (sum, count) -> count > 0 ?
                                sum.divide(BigDecimal.valueOf(count), ROUNDING_SCALE, RoundingMode.HALF_UP) :
                                BigDecimal.ZERO
                ));
    }

    /**
     * Returns a list of customers who have more than {@link #MIN_ORDER_COUNT} orders.
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
                .filter(entry -> entry.getValue() > MIN_ORDER_COUNT)
                .map(Map.Entry::getKey)
                .toList();
    }
}
