package coffeehouse.modules.brew.data.message;

import coffeehouse.modules.brew.domain.OrderId;
import coffeehouse.modules.brew.domain.service.OrderSheetSubmission;
import coffeehouse.modules.order.domain.message.BrewRequestCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrewRequestConsumer {
  private final OrderSheetSubmission orderSheetSubmission;

  @ServiceActivator(inputChannel = "brewRequestChannel")
  public void handle(BrewRequestCommand brewRequestCommand) {
    OrderId brewOrderId = new OrderId(brewRequestCommand.orderId().value());
    orderSheetSubmission.submit(new OrderSheetSubmission.OrderSheetForm(brewOrderId));
  }
}
