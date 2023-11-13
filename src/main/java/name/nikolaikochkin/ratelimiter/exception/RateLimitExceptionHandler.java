package name.nikolaikochkin.ratelimiter.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class RateLimitExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(RateLimitKeyException.class)
    ResponseEntity<String> handleRateLimitKeyException(RateLimitKeyException e) {
        log.error("Something went wrong: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    @ExceptionHandler(RateLimitExceededException.class)
    ResponseEntity<String> handleRateLimitExceededException(RateLimitExceededException e) {
        log.error("Something went wrong: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .build();
    }
}
