package coffeehouse.modules.brew.domain.message;

import coffeehouse.modules.brew.domain.OrderId;

public record BrewCompletedEvent(OrderId orderId) {}
