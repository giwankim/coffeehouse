package coffeehouse.modules.order.data.message;

import coffeehouse.modules.order.data.message.dto.BrewCompletedEvent;
import coffeehouse.modules.order.domain.service.BrewOrderCompleted;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderBrewCompletedEventListener {
  private final BrewOrderCompleted brewOrderCompleted;

  @ServiceActivator(inputChannel = "brewCompletedEventPublishSubscribeChannel")
  public void handle(BrewCompletedEvent brewCompletedEvent) {
    brewOrderCompleted.changeOrderStatus(brewCompletedEvent.orderId());
  }
}
