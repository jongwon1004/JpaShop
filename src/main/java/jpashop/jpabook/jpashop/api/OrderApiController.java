package jpashop.jpabook.jpashop.api;

import jpashop.jpabook.jpashop.domain.Address;
import jpashop.jpabook.jpashop.domain.Order;
import jpashop.jpabook.jpashop.domain.OrderItem;
import jpashop.jpabook.jpashop.domain.OrderStatus;
import jpashop.jpabook.jpashop.repository.OrderRepository;
import jpashop.jpabook.jpashop.repository.OrderSearch;
import jpashop.jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpashop.jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpashop.jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpashop.jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            System.out.println("order.getMember().getName() = " + order.getMember().getName());
            System.out.println("order.getDelivery().getAddress() = " + order.getDelivery().getAddress());

            order.getOrderItems().stream()
                    .map(o -> o.getItem().getName())
                    .forEach(System.out::println);
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public Result ordersV2() {

        return new Result(orderRepository.findAllByString(new OrderSearch()).stream()
                .map(OrderDto::new)
                .collect(toList()));
    }

    @GetMapping("/api/v3/orders")
    public Result ordersV3() {

        return new Result(orderRepository.findAllWithItem().stream()
                .map(o -> new OrderDto(o))
                .collect(toList()));

    }

    @GetMapping("/api/v3.1/orders")
    public Result ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                @RequestParam(value = "limit", defaultValue = "100") int limit) {


        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        return new Result(orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList()));


    }

    @GetMapping("/api/v4/orders")
    public Result<OrderQueryDto> ordersV4() {
        return new Result(orderQueryRepository.findOrderQueryDtos());
    }

    @GetMapping("/api/v5/orders")
    public Result<OrderQueryDto> ordersV5() {
        return new Result(orderQueryRepository.findAllByDto_optimization());
    }

    @GetMapping("/api/v6/orders")
    public Result<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        return new Result(flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(Collectors.toList()));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; // 값타입 정도는 외부에 노출해도된다. 하지만 엔티티 노출은 금지!!!
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
//            order.getOrderItems().stream()
//                    .forEach(o -> o.getItem().getName());
//            orderItems = order.getOrderItems();

            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }

    @Data
    static class OrderItemDto {

        private String itemName; // 상품명
        private int orderPrice; // 주문 가격
        private int orderCount; // 주문 수량
        private int totalPrice; // 총 주문 가격

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            orderCount = orderItem.getCount();
            totalPrice = orderItem.getTotalPrice();
        }
    }
}
