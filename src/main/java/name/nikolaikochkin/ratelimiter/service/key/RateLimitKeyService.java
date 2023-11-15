package name.nikolaikochkin.ratelimiter.service.key;

import name.nikolaikochkin.ratelimiter.service.key.model.RateLimitKey;
import name.nikolaikochkin.ratelimiter.service.key.provider.RateLimitKeyProvider;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RateLimitKeyService {
    private final Map<Class<? extends RateLimitKeyProvider>, RateLimitKeyProvider> keyProviderMap;

    public RateLimitKeyService(List<RateLimitKeyProvider> providers) {
        keyProviderMap = new HashMap<>();
        providers.forEach(rateLimitKeyProvider -> keyProviderMap.put(rateLimitKeyProvider.getClass(), rateLimitKeyProvider));
    }

    public Flux<RateLimitKey> getRateLimitKeys(Class<? extends RateLimitKeyProvider>[] classes, ProceedingJoinPoint proceedingJoinPoint) {
        return Flux.fromArray(classes)
                .map(keyProviderMap::get)
                .flatMap(rateLimitKeyProvider -> rateLimitKeyProvider.getRateLimitKey(proceedingJoinPoint))
                .filter(Objects::nonNull);
    }
}
