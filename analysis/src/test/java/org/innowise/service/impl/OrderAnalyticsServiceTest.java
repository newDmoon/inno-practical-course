package org.innowise.service.impl;

import org.innowise.model.Category;
import org.innowise.model.Customer;
import org.innowise.model.Order;
import org.innowise.model.OrderItem;
import org.innowise.model.OrderStatus;
import org.innowise.service.impl.OrderAnalyticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderAnalyticsServiceTest {

    private OrderAnalyticsServiceImpl service;
    private Customer dmitry, kirill, vasilii;
    private OrderItem laptop, tshirt, book, sofa, cream, lego;

    @BeforeEach
    void setUp() {
        service = new OrderAnalyticsServiceImpl();

        dmitry = new Customer("C1", "Dmitry", "dmitry@mail.com", LocalDateTime.now(), 30, "Minsk");
        kirill = new Customer("C2", "Kirill", "kirill@mail.com", LocalDateTime.now(), 25, "Pinsk");
        vasilii = new Customer("C3", "Vasilii", "vasilii@mail.com", LocalDateTime.now(), 28, "Pinsk");

        laptop = new OrderItem("Laptop", 1, 1200.0, Category.ELECTRONICS);
        tshirt = new OrderItem("T-Shirt", 2, 25.0, Category.CLOTHING);
        book = new OrderItem("Clean Code", 1, 40.0, Category.BOOKS);
        sofa = new OrderItem("Sofa", 1, 500.0, Category.HOME);
        cream = new OrderItem("Face Cream", 3, 15.0, Category.BEAUTY);
        lego = new OrderItem("Lego Set", 1, 100.0, Category.TOYS);
    }

    private Order createOrder(Customer customer, List<OrderItem> items, OrderStatus status) {
        Order order = new Order();
        order.setOrderId("O");
        order.setOrderDate(LocalDateTime.now());
        order.setCustomer(customer);
        order.setItems(items);
        order.setStatus(status);
        return order;
    }

    @Test
    void testGetUniqueCitiesFromOrders() {
        Order order1 = createOrder(dmitry, List.of(laptop, tshirt), OrderStatus.NEW);
        Order order2 = createOrder(kirill, List.of(book, lego), OrderStatus.SHIPPED);
        Order order3 = createOrder(vasilii, List.of(sofa, cream), OrderStatus.SHIPPED);
        Order order4 = createOrder(dmitry, List.of(book), OrderStatus.DELIVERED);

        Order nullOrder = null;
        Order orderWithNullCustomer = createOrder(null, List.of(book), OrderStatus.DELIVERED);
        Customer customerWithNullCity = new Customer("C4", "Unknown", "u@mail.com", LocalDateTime.now(), 50, null);
        Order orderWithNullCity = createOrder(customerWithNullCity, List.of(lego), OrderStatus.SHIPPED);

        List<String> cities = service.getUniqueCitiesFromOrders(
                Arrays.asList(order1, order2, order3, order4, nullOrder, orderWithNullCustomer, orderWithNullCity)
        );

        assertEquals(List.of("Minsk", "Pinsk"), cities);
    }


    @Test
    void testGetTotalIncomeFromCompletedOrders() {
        Order order1 = createOrder(dmitry, List.of(laptop, tshirt), OrderStatus.DELIVERED);
        Order order2 = createOrder(kirill, List.of(book), OrderStatus.DELIVERED);
        Order order3 = createOrder(vasilii, List.of(sofa), OrderStatus.SHIPPED);
        Order order4 = createOrder(dmitry, null, OrderStatus.DELIVERED);

        BigDecimal income = service.getTotalIncomeFromCompletedOrders(List.of(order1, order2, order3, order4));

        assertEquals(new BigDecimal("1290.00"), income.setScale(2));
    }

    @Test
    void testGetMostPopularProductBySales() {
        Order order1 = createOrder(dmitry, List.of(laptop, tshirt), OrderStatus.SHIPPED);
        Order order2 = createOrder(kirill, List.of(tshirt, book), OrderStatus.DELIVERED);
        Order order3 = createOrder(vasilii, List.of(sofa, cream), OrderStatus.NEW);
        Order order4 = createOrder(dmitry, List.of(tshirt), OrderStatus.SHIPPED);

        String popular = service.getMostPopularProductBySales(List.of(order1, order2, order3, order4));

        assertEquals("T-Shirt", popular);
    }

    @Test
    void testGetAvgCheckForDeliveredOrders() {
        Order order1 = createOrder(dmitry, List.of(laptop), OrderStatus.DELIVERED);
        Order order2 = createOrder(kirill, List.of(book, cream), OrderStatus.DELIVERED);
        Order order3 = createOrder(vasilii, List.of(sofa), OrderStatus.SHIPPED);

        BigDecimal avgCheck = service.getAvgCheckForDeliveredOrders(List.of(order1, order2, order3));

        assertEquals(new BigDecimal("642.50"), avgCheck);
    }

    @Test
    void testGetAvgCheckForDeliveredOrdersEmpty() {
        BigDecimal avgCheck = service.getAvgCheckForDeliveredOrders(List.of());
        assertEquals(BigDecimal.ZERO, avgCheck);
    }

    @Test
    void testGetCustomersWithMoreThan5Orders() {
        List<Order> orders = IntStream.range(0, 6)
                .mapToObj(i -> createOrder(dmitry, List.of(book), OrderStatus.DELIVERED))
                .collect(Collectors.toList());

        orders.addAll(IntStream.range(0, 3)
                .mapToObj(i -> createOrder(kirill, List.of(lego), OrderStatus.NEW))
                .toList());

        orders.add(createOrder(vasilii, List.of(sofa), OrderStatus.SHIPPED));

        List<Customer> result = service.getCustomersWithMoreThan5Orders(orders);

        assertEquals(1, result.size());
        assertEquals(dmitry, result.get(0));
    }

    @Test
    void testNullOrdersThrows() {
        assertThrows(NullPointerException.class, () -> service.getUniqueCitiesFromOrders(null));
        assertThrows(NullPointerException.class, () -> service.getTotalIncomeFromCompletedOrders(null));
        assertThrows(NullPointerException.class, () -> service.getMostPopularProductBySales(null));
        assertThrows(NullPointerException.class, () -> service.getAvgCheckForDeliveredOrders(null));
        assertThrows(NullPointerException.class, () -> service.getCustomersWithMoreThan5Orders(null));
    }
}
