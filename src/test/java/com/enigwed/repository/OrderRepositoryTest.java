package com.enigwed.repository;

import com.enigwed.entity.Order;
import com.enigwed.entity.WeddingOrganizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void existsByBookCode_OrderWithBookCodeExist_ReturnTrue() {
        // Arrange
        Mockito.when(orderRepository.existsByBookCode("ABC")).thenReturn(true);

        // Act & Assert
        assertTrue(orderRepository.existsByBookCode("ABC"));
        Mockito.verify(orderRepository, Mockito.times(1)).existsByBookCode("ABC");
    }

    @Test
    void existsByBookCode_OrderWithBookCodeDoesNotExist_ReturnFalse() {
        // Arrange
        Mockito.when(orderRepository.existsByBookCode("ABC")).thenReturn(false);

        // Act & Assert
        assertFalse(orderRepository.existsByBookCode("ABC"));
        Mockito.verify(orderRepository, Mockito.times(1)).existsByBookCode("ABC");
    }

    @Test
    void findByBookCode_OrderWithBookCodeExist_ReturnOptionalOrder() {
        // Arrange
        Order expect = Order.builder().bookCode("ABC").build();

        Mockito.when(orderRepository.findByBookCode("ABC")).thenReturn(Optional.ofNullable(expect));

        // Act
        Optional<Order> actual = orderRepository.findByBookCode("ABC");

        // Assert
        assertTrue(actual.isPresent());
        assertEquals("ABC", actual.get().getBookCode());
        Mockito.verify(orderRepository, Mockito.times(1)).findByBookCode("ABC");
    }

    @Test
    void findByBookCode_OrderWithBookCodeDoesNotExist_ReturnOptionalEmpty() {
        // Arrange
        Mockito.when(orderRepository.findByBookCode("ABC")).thenReturn(Optional.empty());

        // Act
        Optional<Order> actual = orderRepository.findByBookCode("ABC");

        // Assert
        assertFalse(actual.isPresent());
        Mockito.verify(orderRepository, Mockito.times(1)).findByBookCode("ABC");
    }

    @Test
    void findByWeddingOrganizerId_OrderWithWeddingOrganizerIdExist_ReturnListOfOrder() {
        // Arrange
        WeddingOrganizer weddingOrganizer = WeddingOrganizer.builder().id("123").build();
        Order order = Order.builder().weddingOrganizer(weddingOrganizer).build();
        List<Order> expect = List.of(order);

        Mockito.when(orderRepository.findByWeddingOrganizerId("123")).thenReturn(expect);

        // Act
        List<Order> actual = orderRepository.findByWeddingOrganizerId("123");

        // Assert
        assertFalse(actual.isEmpty());
        assertTrue(actual.contains(order));
        Mockito.verify(orderRepository, Mockito.times(1)).findByWeddingOrganizerId("123");
    }

    @Test
    void findByWeddingOrganizerId_OrderWithWeddingOrganizerIdDoesNotExist_ReturnEmptyList() {
        // Arrange
        Mockito.when(orderRepository.findByWeddingOrganizerId("123")).thenReturn(List.of());

        // Act
        List<Order> actual = orderRepository.findByWeddingOrganizerId("123");

        // Assert
        assertTrue(actual.isEmpty());
        Mockito.verify(orderRepository, Mockito.times(1)).findByWeddingOrganizerId("123");
    }
}