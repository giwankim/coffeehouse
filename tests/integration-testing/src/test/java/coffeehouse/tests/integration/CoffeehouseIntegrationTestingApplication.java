package coffeehouse.tests.integration;

import coffeehouse.modules.brew.EnableBrewModule;
import coffeehouse.modules.brew.domain.OrderSheetId;
import coffeehouse.modules.brew.domain.entity.OrderSheet;
import coffeehouse.modules.brew.domain.entity.OrderSheetRepository;
import coffeehouse.modules.brew.domain.entity.OrderSheetStatus;
import coffeehouse.modules.order.EnableOrderModule;
import coffeehouse.modules.order.domain.OrderId;
import coffeehouse.modules.order.domain.UserAccountId;
import coffeehouse.modules.order.domain.entity.Order;
import coffeehouse.modules.order.domain.entity.OrderRepository;
import coffeehouse.modules.order.domain.entity.OrderStatus;
import coffeehouse.modules.user.EnableUserModule;
import coffeehouse.modules.user.domain.entity.UserAccount;
import coffeehouse.modules.user.domain.entity.UserAccountRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.client.RestTemplate;

/**
 * @author springrunner.kr@gmail.com
 */
@SpringBootApplication
@EnableOrderModule
@EnableBrewModule
@EnableUserModule
public class CoffeehouseIntegrationTestingApplication {

  public static void main(String[] args) {
    SpringApplication.run(CoffeehouseIntegrationTestingApplication.class, args);
  }

  @Bean
  RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
    RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
    rabbitAdmin.declareQueue(new Queue("brew"));
    return rabbitAdmin;
  }

  @Bean
  InitializingBean initData(OrderSheetRepository orderSheetRepository, OrderRepository orderRepository, UserAccountRepository userAccountRepository) {
    return () -> {
      var userAccountIdValue = "bb744f5a-2715-488b-aade-ebae5aa8f055";
      userAccountRepository.save(UserAccount.createCustomer(new coffeehouse.modules.user.domain.UserAccountId(userAccountIdValue)));

      var newOrderIdValue = "7438b60b-7c68-4d55-a033-fa933e92832c";

      orderRepository.save(Order.create(new OrderId(newOrderIdValue), new UserAccountId(userAccountIdValue)));


      var acceptedOrderIdValue = "1a176aa8-e834-46e8-b293-0d0208ad1cd8";
      var confirmedOrderSheetIdValue = "e9c17eeb-2bbf-4087-acd3-9675eb6178db";
      orderRepository.save(new Order(new OrderId(acceptedOrderIdValue), new UserAccountId(userAccountIdValue), OrderStatus.ACCEPTED));
      orderSheetRepository.save(new OrderSheet(new OrderSheetId(confirmedOrderSheetIdValue), new coffeehouse.modules.brew.domain.OrderId(acceptedOrderIdValue), OrderSheetStatus.CONFIRMED));
    };
  }

  @Bean
  Jackson2JsonMessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  RestTemplate defaultRestTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder.build();
  }

  @Bean
  MessageChannel barCounterChannel() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow amqpOutboudIntegrationFlow(AmqpTemplate amqpTemplate, MessageChannel barCounterChannel) {
    return IntegrationFlow.from(barCounterChannel)
        .handle(
            Amqp.outboundAdapter(amqpTemplate)
                .routingKey("brew")
        ).get();
  }

  @Bean
  MessageChannel brewRequestChannel() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow ampqInboundIntegrationFlow(ConnectionFactory connectionFactory, MessageChannel brewRequestChannel) {
    return IntegrationFlow.from(
        Amqp.inboundAdapter(connectionFactory, "brew")
    ).handle(
        message -> {
          brewRequestChannel.send(message);
        }
    ).get();
  }
}
