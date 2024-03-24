package com.shashank.redis.storage;

import java.time.Instant;

public record Data<T>(T value, Instant expiry) {
}
