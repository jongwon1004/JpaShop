package jpashop.jpabook.jpashop.repository.order.simplequery;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    // new 명령어를 사용해서 jpql 결과를 DTO 로 즉시변환
    // 재사용성이 낮음, 쿼리 튜닝이 어려움, 로직이 너무 fit함, 하지만 성능적으로 조금이나마 이점이 있음,
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                        "select new jpashop.jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m " +
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
