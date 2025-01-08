package coffeehouse.modules.order.data.message.dto;

import coffeehouse.modules.order.domain.OrderId;

public record BrewCompletedEvent(OrderId orderId) {}
