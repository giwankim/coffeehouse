package coffeehouse.modules.user.data.message;

import coffeehouse.modules.user.data.message.dto.BrewCompletedEvent;
import coffeehouse.modules.user.domain.service.UserBrewCompleted;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBrewCompletedEventListener {
  private final UserBrewCompleted userBrewCompleted;

  @ServiceActivator(inputChannel = "brewCompletedEventPublishSubscribeChannel")
  public void handle(BrewCompletedEvent brewCompletedEvent) {
    userBrewCompleted.notify(brewCompletedEvent.orderId());
  }
}
