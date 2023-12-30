package jpashop.jpabook.jpashop.api;

import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import jpashop.jpabook.jpashop.domain.Address;
import jpashop.jpabook.jpashop.domain.Order;
import jpashop.jpabook.jpashop.domain.OrderStatus;
import jpashop.jpabook.jpashop.repository.OrderRepository;
import jpashop.jpabook.jpashop.repository.OrderSearch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne ( ManyToOne, OneToOne )
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {

        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            System.out.println("order.getMember().getName() = " + order.getMember().getName()); // Lazy 강제 초기화
            System.out.println("order.getDelivery().getAddress() = " + order.getDelivery().getAddress());
        }

        return all;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @GetMapping("/api/v2/simple-orders")
    public Result ordersV2() {

        return new Result(orderRepository.findAllByString(new OrderSearch()).stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList()));
    }

    @GetMapping("/api/v3/simple-orders")
    public Result ordersV3() {

        return new Result(orderRepository.findAllWithMemberDelivery().stream() // join fetch
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList()));
    }

    @GetMapping("/api/v4/simple-orders")
    public Result ordersV4() {
        return new Result(orderRepository.findOrderDtos());
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
        }
    }
}
