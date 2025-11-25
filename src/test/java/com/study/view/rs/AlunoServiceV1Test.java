package com.study.view.rs;

import com.study.dto.v3.AlunoRequest;
import com.study.dto.v3.AlunoResponse;
import com.study.dto.v3.TutorResponse;
import com.study.mapper.AlunoMapper;
import com.study.model.Aluno;
import com.study.model.Professor;
import com.study.repository.AlunoRepository;
import com.study.repository.ProfessorRepository;
import com.study.service.AlunoServiceV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AlunoServiceV1Test {

    @Mock
    private AlunoMapper mapper;

    @Mock
    private AlunoRepository repository;

    @Mock
    private ProfessorRepository professorRepository;

    @InjectMocks
    private AlunoServiceV1 service;

    private static final String ALUNO_NAME = "João";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    /**
     * Testa se o método retrieveAll retorna uma lista de AlunoResponse.
     */
    @Test
    void testRetrieveAllDeveRetornarListaDeAlunoResponse() {
        // GIVEN
        var aluno = Aluno.builder().name(ALUNO_NAME).build();
        when(repository.findAll()).thenReturn(List.of(aluno));
        var alunoResponse = new AlunoResponse();
        when(mapper.toResponse(List.of(aluno))).thenReturn(List.of(alunoResponse));

        // WHEN
        List<AlunoResponse> result = service.retrieveAll();

        // THEN
        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .containsExactly(alunoResponse);

        verify(repository).findAll();
        verify(mapper).toResponse(List.of(aluno));
    }

    /**
     * Testa se o método save salva um AlunoResponse corretamente.
     */
    @Test
    void salvaAlunoResponse() {
        // GIVEN
        var request = new AlunoRequest();
        request.setName("Maria");
        var aluno = Aluno.builder().name("Maria").build();
        var response = new AlunoResponse();
        when(repository.save(any(Aluno.class))).thenReturn(aluno);
        when(mapper.toResponse(aluno)).thenReturn(response);

        // WHEN
        AlunoResponse result = service.save(request);

        // THEN
        assertThat(result).isEqualTo(response);
        verify(repository).save(any(Aluno.class));
        verify(mapper).toResponse(any(Aluno.class));
    }

    /**
     * Testa se o método buscarAlunoPorId busca um Aluno por ID corretamente.
     */
    @Test
    void buscarAlunoPorId() {
        // GIVEN
        var aluno = Aluno.builder().name("Carlos").build();
        var response = new AlunoResponse();
        when(repository.findById(1)).thenReturn(Optional.of(aluno));
        when(mapper.toResponse(aluno)).thenReturn(response);

        // WHEN
        AlunoResponse result = service.getById(1);

        // THEN
        assertThat(result).isEqualTo(response);
        verify(repository).findById(1);
        verify(mapper).toResponse(aluno);
    }
    /**
     * Testa se o método getByIdAlunoExceptionQuandoNaoEncontrado
     * lança uma exceção quando o Aluno não é encontrado.
     */
    @Test
    void getByIdAlunoExceptionQuandoNaoEncontrado() {
        // GIVEN
        when(repository.findById(99)).thenReturn(Optional.empty());

        // WHEN
        Executable executable = () -> service.getById(99);

        // THEN
        assertThrows(EntityNotFoundException.class, executable);
    }

    /**
     * Testa se o método updateTutor_AtualizarAlunoResponse atualiza o Tutor de um Aluno corretamente.
     */
    @Test
    void updateTutor_AtualizarAlunoResponse() {
        // GIVEN
        var aluno = Aluno.builder().name("Pedro").build();
        var professor = Professor.builder().name("Prof. Silva").build();
        var response = new TutorResponse();
        when(repository.findById(1)).thenReturn(Optional.of(aluno));
        when(professorRepository.findById(2)).thenReturn(Optional.of(professor));
        when(mapper.toResponse(professor)).thenReturn(response);

        // WHEN
        TutorResponse result = service.updateTutor(1, 2);

        // THEN
        assertThat(result).isEqualTo(response);
        verify(repository).save(aluno);
        assertThat(aluno.getTutor()).isEqualTo(professor);
    }
    /**
     * Testa se o método getTutoradosByProfessorIdRetornaListaAlunoResponse retorna uma lista de AlunoResponse.
     */
    @Test
    void getTutoradosByProfessorIdRetornaListaAlunoResponse() {
        // GIVEN
        var professor = Professor.builder().name("Prof. Ana").build();
        var aluno = Aluno.builder().name("Lucas").tutor(professor).build();
        var response = new AlunoResponse();
        when(professorRepository.findById(1)).thenReturn(Optional.of(professor));
        when(repository.findAlunosByTutor(professor)).thenReturn(Arrays.asList(aluno));
        when(mapper.toResponse(Arrays.asList(aluno))).thenReturn(Arrays.asList(response));

        // WHEN
        List<AlunoResponse> result = service.getTutoradosByProfessorId(1);

        // THEN
        assertThat(result).isNotNull().hasSize(1).contains(response);
        verify(repository).findAlunosByTutor(professor);
        verify(mapper).toResponse(Arrays.asList(aluno));
    }
}

