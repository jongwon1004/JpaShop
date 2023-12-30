package jpashop.jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jpashop.jpabook.jpashop.domain.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Test1 {

    @Autowired
    private EntityManager em;

    @Test
    public void test1() {
        List<OrderItem> selectOiFromOrderItemOi = em.createQuery("select oi from OrderItem oi", OrderItem.class)
                .getResultList();
        for (OrderItem orderItem : selectOiFromOrderItemOi) {
            System.out.println("orderItem = " + orderItem.getTotalPrice());

        }
    }
}
