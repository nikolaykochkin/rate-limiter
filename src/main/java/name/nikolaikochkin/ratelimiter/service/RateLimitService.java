package name.nikolaikochkin.ratelimiter.service;

import name.nikolaikochkin.ratelimiter.model.Client;

public interface RateLimitService {
    boolean allowClientRequest(Client client);
}
