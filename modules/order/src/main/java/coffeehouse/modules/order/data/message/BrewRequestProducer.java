package coffeehouse.modules.order.data.message;

import coffeehouse.modules.order.domain.OrderId;
import coffeehouse.modules.order.domain.message.BrewRequestCommand;
import coffeehouse.modules.order.domain.service.BarCounter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrewRequestProducer implements BarCounter {
  private final MessageChannel barCounterChannel;

  @Override
  public void brew(OrderId orderId) {
    BrewRequestCommand command = new BrewRequestCommand(orderId);

    GenericMessage<BrewRequestCommand> message = new GenericMessage<>(command);

    barCounterChannel.send(message);
  }
}
