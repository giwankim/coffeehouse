package coffeehouse.modules.user.data.message.dto;

import coffeehouse.modules.user.domain.OrderId;

public record BrewCompletedEvent(OrderId orderId) {}
