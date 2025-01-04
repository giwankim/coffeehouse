package coffeehouse.modules.brew;

import coffeehouse.modules.brew.domain.OrderId;
import coffeehouse.modules.brew.domain.service.OrderSheetSubmission;
import coffeehouse.modules.order.domain.message.BrewRequestCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Observable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * @author springrunner.kr@gmail.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(EnableBrewModule.BrewModuleConfiguration.class)
public @interface EnableBrewModule {

  @Configuration
  @ComponentScan
  class BrewModuleConfiguration {
    @Bean
    MessageHandler messageHandler(
        OrderSheetSubmission orderSheetSubmission, MessageChannel barCounterChannel) {
      MessageHandler messageHandler = new MessageHandler() {
        @Override
        public void handleMessage(Message<?> message) throws MessagingException {
          BrewRequestCommand command = (BrewRequestCommand) message.getPayload();
          OrderId brewOrderId = new OrderId(command.orderId().value());
          orderSheetSubmission.submit(new OrderSheetSubmission.OrderSheetForm(brewOrderId));
        }
      };

      Observable observable = (Observable) barCounterChannel;
      observable.addObserver((o, arg) -> {
        Message<?> message = (Message<?>) arg;
        messageHandler.handleMessage(message);
      });

      return messageHandler;
    }
  }
}
