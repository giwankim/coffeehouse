package coffeehouse.modules.order.data.repository;

import coffeehouse.modules.order.domain.OrderId;
import coffeehouse.modules.order.domain.entity.Order;
import coffeehouse.modules.order.domain.entity.OrderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * @author springrunner.kr@gmail.com
 */
@Repository
public class InMemoryOrderRepository implements OrderRepository {

  private final List<Order> orders = new ArrayList<>();

  @Override
  public Optional<Order> findById(OrderId id) {
    return orders.stream().filter(order -> order.getId().equals(id)).findFirst();
  }

  @Override
  public Order save(Order order) {
    orders.add(order);
    return order;
  }
}
