package coffeehouse.modules.order.domain.message;

import coffeehouse.modules.order.domain.OrderId;

public record BrewRequestCommand(OrderId orderId) {}
