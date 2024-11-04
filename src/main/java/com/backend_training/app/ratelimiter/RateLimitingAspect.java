package com.backend_training.app.ratelimiter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.EstimationProbe;
import io.github.bucket4j.Bandwidth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitingAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint pjp, RateLimit rateLimit) throws Throwable {
        String clientIp = getClientIpAddress();
        String cacheKey = clientIp + ":" + pjp.getSignature().toShortString();
        Bucket bucket = bucketCache.computeIfAbsent(cacheKey, key -> createBucket(rateLimit.capacity(), rateLimit.refillTokens(), rateLimit.duration()));
        
        if (bucket.tryConsume(1)) {
            return pjp.proceed();
        } else {
            int retryAfter = calculateRetryAfter(bucket);
            response.setHeader("Retry-After", String.valueOf(retryAfter));
            return new ResponseEntity<>("Too many requests. Please try again after " + retryAfter + " seconds.", HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    private Bucket createBucket(int capacity, long refillTokens, long durationInMillis) {
        return Bucket.builder()
                .addLimit(Bandwidth.builder().capacity(capacity).refillGreedy(refillTokens,  Duration.ofMillis(durationInMillis)).build())
                .build();
    }

    private String getClientIpAddress() {
        return request.getRemoteAddr();
    }

    private int calculateRetryAfter(Bucket bucket) {
        if (bucket.getAvailableTokens() !=0) {
            return 0;
        } else {
            EstimationProbe estimationProbe = bucket.estimateAbilityToConsume(1);
            return Duration.ofNanos(estimationProbe.getNanosToWaitForRefill()).toSecondsPart();
        }
    }
}
