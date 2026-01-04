package com.onclass.bootcamp.domain.usecase;

import com.onclass.bootcamp.domain.constants.Constants;
import com.onclass.bootcamp.domain.criteria.BootcampCriteria;
import com.onclass.bootcamp.domain.enums.TechnicalMessage;
import com.onclass.bootcamp.domain.exceptions.BusinessException;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampList;
import com.onclass.bootcamp.domain.spi.BootcampPersistencePort;
import com.onclass.bootcamp.domain.spi.BootcampReporteClientPort;
import com.onclass.bootcamp.domain.spi.CapacidadClientPort;
import com.onclass.bootcamp.domain.spi.TecnologiaClientPort;
import com.onclass.bootcamp.domain.utils.PageResult;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampListDTO;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampReporteDTO;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.CapacidadSummaryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.LongStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BootcampUseCaseTest {

    @Mock
    private BootcampPersistencePort bootcampPersistencePort;

    @Mock
    private CapacidadClientPort capacidadClientPort;

    @Mock
    private TecnologiaClientPort tecnologiaClientPort;

    @Mock
    private BootcampReporteClientPort bootcampReporteClientPort;

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
        doReturn(Mono.just(false)).when(bootcampPersistencePort).existByNombre(anyString());
        doReturn(Mono.just(true)).when(capacidadClientPort).validateCapacidadesExist(anyList());
        doReturn(Mono.just(bootcampValido)).when(bootcampPersistencePort).saveBootcamp(any(Bootcamp.class));
        doReturn(Mono.empty()).when(capacidadClientPort).associateBootcampWithCapacidades(anyLong(), anyList());
        doReturn(Flux.just(1L, 2L)).when(tecnologiaClientPort).findTecnologiaIdsByCapacidades(anyList());
        doReturn(Mono.empty()).when(bootcampReporteClientPort).notificarNuevoBootcamp(any(BootcampReporteDTO.class));

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
        doReturn(Mono.just(true)).when(bootcampPersistencePort).existByNombre(anyString());

        StepVerifier.create(bootcampUseCase.registrarBootcamp(bootcampValido, "msg"))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getMessage().equals(TechnicalMessage.BOOTCAMP_ALREADY_EXISTS.getDescription()))
                .verify();
    }

    @Test
    void listarBootcamps_sortByName_exito() {
        // Arrange
        BootcampList b1 = new BootcampList(1L, "Java", "desc", LocalDate.now(), 6, List.of());
        BootcampList b2 = new BootcampList(2L, "Spring", "desc", LocalDate.now(), 6, List.of());
        PageResult<BootcampList> page = new PageResult<>(
                List.of(b1, b2),
                2L, 1, 0, 10, true, true
        );

        CapacidadSummaryDTO capacidad = new CapacidadSummaryDTO(1L, "Java Avanzado", List.of());

        doReturn(Mono.just(page)).when(bootcampPersistencePort).findAll(any(BootcampCriteria.class));
        doReturn(Flux.empty()).when(capacidadClientPort).findCapacidadesByBootcampId(anyLong());

        // Act & Assert
        StepVerifier.create(bootcampUseCase.listarBootcamps(new BootcampCriteria()))
                .expectNextMatches(result ->
                        result.getContent().size() == 2 &&
                                result.getContent().get(0).nombre().equals("Java") &&
                                result.getContent().get(1).nombre().equals("Spring"))
                .verifyComplete();
    }

    @Test
    void listarBootcamps_sortByCapacidadCount_exito() {
        // Arrange
        BootcampList b1 = new BootcampList(1L, "Java", "desc", LocalDate.now().plusDays(5), 8, List.of());
        BootcampList b2 = new BootcampList(2L, "Angular", "desc", LocalDate.now().plusDays(5), 8, List.of());

        PageResult<BootcampList> page = new PageResult<>(
                List.of(b1, b2),
                2L, 1, 0, 10, true, true
        );

        BootcampCriteria criteria = new BootcampCriteria();
        criteria.setSortBy(Constants.SORT_BY_CAPACIDAD_COUNT);
        criteria.setSortOrder(Constants.SORT_ORDER_DESC);
        criteria.setPage(0);
        criteria.setSize(10);

        // Mock capacidades por bootcamp
        CapacidadSummaryDTO capJava1 = new CapacidadSummaryDTO(1L, "Java Avanzado", List.of());
        CapacidadSummaryDTO capJava2 = new CapacidadSummaryDTO(2L, "Spring Boot", List.of());
        CapacidadSummaryDTO capAngular = new CapacidadSummaryDTO(3L, "Angular", List.of());

        doReturn(Mono.just(page)).when(bootcampPersistencePort).findAll(any(BootcampCriteria.class));
        doReturn(Flux.empty()).when(capacidadClientPort).findCapacidadesByBootcampId(eq(1L));
        doReturn(Flux.empty()).when(capacidadClientPort).findCapacidadesByBootcampId(eq(2L));

        // Act & Assert
        StepVerifier.create(bootcampUseCase.listarBootcamps(criteria))
                .assertNext(result -> {
                    assert result.getContent().size() == 2;
                    assert result.getContent().get(0).nombre().equals("Java");
                    assert result.getTotalElements() == 2;
                })
                .verifyComplete();
    }

    @Test
    void listarBootcamps_parametroInvalido() {
        BootcampCriteria criteria = new BootcampCriteria();
        criteria.setSortBy("otroCampo");

        StepVerifier.create(bootcampUseCase.listarBootcamps(criteria))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getMessage().equals(TechnicalMessage.INVALID_PARAMETERS.getDescription()))
                .verify();
    }
}
