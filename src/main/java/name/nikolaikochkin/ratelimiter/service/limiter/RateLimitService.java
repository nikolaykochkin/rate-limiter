package name.nikolaikochkin.ratelimiter.service.limiter;

import name.nikolaikochkin.ratelimiter.model.Client;

public interface RateLimitService {
    boolean allowClientRequest(Client client);
}
