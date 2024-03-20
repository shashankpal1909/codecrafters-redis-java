package com.shashank.redis.protocol;

public record DecodedData<X>(X data, Long bytesCount) {


}
