package com.onclass.bootcamp.domain.usecase;

import com.onclass.bootcamp.domain.constants.Constants;
import com.onclass.bootcamp.domain.enums.TechnicalMessage;
import com.onclass.bootcamp.domain.exceptions.BusinessException;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.spi.BootcampPersistencePort;
import com.onclass.bootcamp.domain.spi.CapacidadClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.LongStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class BootcampUseCaseTest {

    @Mock
    private BootcampPersistencePort bootcampPersistencePort;

    @Mock
    private CapacidadClientPort capacidadClientPort;

    @InjectMocks
    private BootcampUseCase bootcampUseCase;

    private Bootcamp bootcampValido;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        bootcampValido = new Bootcamp(
                1L,
                "Bootcamp Backend",
                "Formación en desarrollo con Spring WebFlux",
                LocalDate.now().plusDays(10),
                8,
                List.of(1L, 2L, 3L)
        );
    }

    @Test
    void registrarBootcamp_exito() {
        when(bootcampPersistencePort.existByNombre(anyString())).thenReturn(Mono.just(false));
        when(bootcampPersistencePort.saveBootcamp(any(Bootcamp.class))).thenReturn(Mono.just(bootcampValido));
        when(capacidadClientPort.associateBootcampWithCapacidades(anyLong(), anyList())).thenReturn(Mono.empty());

        StepVerifier.create(bootcampUseCase.registrarBootcamp(bootcampValido, "msg-1"))
                .expectNext(bootcampValido)
                .verifyComplete();
    }

    @Test
    void registrarBootcamp_nombreRequerido() {
        Bootcamp invalido = new Bootcamp(1L, " ", "desc", LocalDate.now().plusDays(5), 4, List.of(1L));

        StepVerifier.create(bootcampUseCase.registrarBootcamp(invalido, "msg"))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getMessage().equals(TechnicalMessage.BOOTCAMP_NOMBRE_REQUIRED.getDescription()))
                .verify();
    }

    @Test
    void registrarBootcamp_nombreDemasiadoLargo() {
        String nombreLargo = "A".repeat(Constants.MAX_NOMBRE_BOOTCAMP + 1);
        Bootcamp invalido = new Bootcamp(1L, nombreLargo, "desc", LocalDate.now().plusDays(5), 4, List.of(1L));

        StepVerifier.create(bootcampUseCase.registrarBootcamp(invalido, "msg"))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getMessage().equals(TechnicalMessage.BOOTCAMP_NOMBRE_TOO_LONG.getDescription()))
                .verify();
    }

    @Test
    void registrarBootcamp_descripcionRequerida() {
        Bootcamp invalido = new Bootcamp(1L, "Bootcamp", " ", LocalDate.now().plusDays(5), 4, List.of(1L));

        StepVerifier.create(bootcampUseCase.registrarBootcamp(invalido, "msg"))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getMessage().equals(TechnicalMessage.BOOTCAMP_DESCRIPCION_REQUIRED.getDescription()))
                .verify();
    }

    @Test
    void registrarBootcamp_fechaInvalida() {
        Bootcamp invalido = new Bootcamp(1L, "Bootcamp", "desc", LocalDate.now().minusDays(1), 4, List.of(1L));

        StepVerifier.create(bootcampUseCase.registrarBootcamp(invalido, "msg"))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getMessage().equals(TechnicalMessage.BOOTCAMP_FECHA_INVALID.getDescription()))
                .verify();
    }

    @Test
    void registrarBootcamp_duracionInvalida() {
        Bootcamp invalido = new Bootcamp(1L, "Bootcamp", "desc", LocalDate.now().plusDays(5), 0, List.of(1L));

        StepVerifier.create(bootcampUseCase.registrarBootcamp(invalido, "msg"))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getMessage().equals(TechnicalMessage.BOOTCAMP_DURACION_REQUIRED.getDescription()))
                .verify();
    }

    @Test
    void registrarBootcamp_minimoCapacidades() {
        Bootcamp invalido = new Bootcamp(1L, "Bootcamp", "desc", LocalDate.now().plusDays(5), 5, List.of());

        StepVerifier.create(bootcampUseCase.registrarBootcamp(invalido, "msg"))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getMessage().equals(TechnicalMessage.BOOTCAMP_MIN_CAPACIDADES.getDescription()))
                .verify();
    }

    @Test
    void registrarBootcamp_masDeMaximoCapacidades() {
        List<Long> muchas = LongStream.range(1, Constants.MAX_CAPACIDADES + 5).boxed().toList();
        Bootcamp invalido = new Bootcamp(1L, "Bootcamp", "desc", LocalDate.now().plusDays(5), 5, muchas);

        StepVerifier.create(bootcampUseCase.registrarBootcamp(invalido, "msg"))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getMessage().equals(TechnicalMessage.BOOTCAMP_MAX_CAPACIDADES.getDescription()))
                .verify();
    }

    @Test
    void registrarBootcamp_capacidadesDuplicadas() {
        Bootcamp invalido = new Bootcamp(1L, "Bootcamp", "desc", LocalDate.now().plusDays(5), 5, List.of(1L, 2L, 2L));

        StepVerifier.create(bootcampUseCase.registrarBootcamp(invalido, "msg"))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getMessage().equals(TechnicalMessage.BOOTCAMP_CAPACIDADES_DUPLICADAS.getDescription()))
                .verify();
    }

    @Test
    void registrarBootcamp_yaExisteNombre() {
        when(bootcampPersistencePort.existByNombre(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(bootcampUseCase.registrarBootcamp(bootcampValido, "msg"))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getMessage().equals(TechnicalMessage.BOOTCAMP_ALREADY_EXISTS.getDescription()))
                .verify();
    }
}
