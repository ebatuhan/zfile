package com.batu.zfile.node.event;

import java.util.UUID;

public record NodeCreatedEvent(
    UUID nodeId
) {

}
