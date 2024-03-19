package com.shashank.redis.storage;

import java.time.Instant;

public record Data(String value, Instant expiry) {
}
