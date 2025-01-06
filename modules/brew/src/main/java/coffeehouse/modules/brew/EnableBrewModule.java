package coffeehouse.modules.brew;

import coffeehouse.modules.brew.domain.OrderId;
import coffeehouse.modules.brew.domain.service.OrderSheetSubmission;
import coffeehouse.modules.order.domain.message.BrewRequestCommand;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.MessageChannel;

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
    public IntegrationFlow requestBrewIntegrationFlow(
        OrderSheetSubmission orderSheetSubmission, MessageChannel brewRequestChannel) {
      return IntegrationFlow.from(brewRequestChannel)
          .handle(
              BrewRequestCommand.class,
              (payload, headers) -> {
                OrderId brewOrderId = new OrderId(payload.orderId().value());
                orderSheetSubmission.submit(new OrderSheetSubmission.OrderSheetForm(brewOrderId));
                return null;
              })
          .get();
    }

    @Bean
    MessageChannel brewCompletedNotifyOrderChannel() {
      return new DirectChannel();
    }

    @Bean
    MessageChannel brewCompletedNotifyUserChannel() {
      return new DirectChannel();
    }

    @Bean
    public IntegrationFlow notifyOrderIntegrationFlow(
        MessageChannel brewCompletedNotifyOrderChannel, Environment environment) {
      URI uri =
          environment.getRequiredProperty("coffeehouse.brew.notify-brew-complete-uri", URI.class);
      return IntegrationFlow.from(brewCompletedNotifyOrderChannel)
          .handle(Http.outboundChannelAdapter(uri).httpMethod(HttpMethod.POST))
          .get();
    }

    @Bean
    public IntegrationFlow notifyUserIntegrationFlow(
        MessageChannel brewCompletedNotifyUserChannel, Environment environment) {
      URI uri =
          environment.getRequiredProperty("coffeehouse.user.notify-brew-complete-uri", URI.class);
      return IntegrationFlow.from(brewCompletedNotifyUserChannel)
          .handle(Http.outboundChannelAdapter(uri).httpMethod(HttpMethod.POST))
          .get();
    }
  }
}
