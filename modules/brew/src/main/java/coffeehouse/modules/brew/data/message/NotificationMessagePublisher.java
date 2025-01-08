package coffeehouse.modules.brew.data.message;

import coffeehouse.modules.brew.domain.OrderId;
import coffeehouse.modules.brew.domain.message.BrewCompletedEvent;
import coffeehouse.modules.brew.domain.service.BrewNotifier;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMessagePublisher implements BrewNotifier {
  private final MessageChannel brewCompletedChannel;

  @Override
  public void notify(OrderId orderId) {
    BrewCompletedEvent brewCompletedEvent = new BrewCompletedEvent(orderId);
    GenericMessage<BrewCompletedEvent> message = new GenericMessage<>(brewCompletedEvent);
    brewCompletedChannel.send(message);
  }
}
