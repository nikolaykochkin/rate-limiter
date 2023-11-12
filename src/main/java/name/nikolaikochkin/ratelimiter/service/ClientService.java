package name.nikolaikochkin.ratelimiter.service;

import name.nikolaikochkin.ratelimiter.exception.ClientNotFoundException;
import name.nikolaikochkin.ratelimiter.model.Client;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * The {@code ClientService} class is a Spring service that provides functionality
 * to retrieve and handle client-related information in the context of rate limiting.
 *
 * @see Client
 */
@Service
public class ClientService {

    /**
     * Extracts a {@link Client} instance from the provided {@link ServerHttpRequest}.
     * The method retrieves the IP address of the client from the request, wraps it
     * into a {@code Client} object, and returns it as a {@code Mono<Client>}.
     * If the client's IP address cannot be determined from the request, a
     * {@link ClientNotFoundException} is thrown.
     *
     * @param request the server HTTP request from which the client's information is to be extracted
     * @return a {@code Mono<Client>} containing the extracted client, or an error if the client cannot be identified
     */
    public Mono<Client> clientFromHttpRequest(ServerHttpRequest request) {
        return Optional.ofNullable(request)
                .map(ServerHttpRequest::getRemoteAddress)
                .map(InetSocketAddress::getAddress)
                .map(InetAddress::getHostAddress)
                .map(Client::new)
                .map(Mono::just)
                .orElseGet(() -> Mono.error(new ClientNotFoundException()));
    }
}
