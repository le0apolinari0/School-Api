package com.study.view.rs;

import com.study.dto.CorDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CorControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Map<Integer, CorDto> repository;

    private String url;

    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    void setUp() {
        // GIVEN
        url = "http://localhost:" + port + "/cores";

        repository.put(1, new CorDto(1, "Verde"));
        repository.put(2, new CorDto(2, "Amarelo"));
        repository.put(3, new CorDto(3, "Vermelho"));
        repository.put(4, new CorDto(4, "Cinza"));
        repository.put(5, new CorDto(5, "Branco"));
    }

    @AfterEach
    void tearDown() {
        // AFTER
        repository.clear();
    }
    /**
     * Testa se o método obterListaCorDtoTestes retorna uma lista de CorDto.
     */
    @Test
    void obterListaCorDtoTeste() {
        // GIVEN
        // O repositório está populado com dados de teste (configurado no setUp).

        // WHEN
        var response = restTemplate.getForEntity(url, CorDto[].class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(repository.size(), response.getBody().length);
    }
    /**
     * Testa se o método buscarDadosSemCorDtoTeste retorna uma lista vazia de CorDto.
     */
    @Test
    void buscarDadosSemCorDtoTeste() {
        // GIVEN
        repository.clear();

        // WHEN
        var response = restTemplate.getForEntity(url, CorDto[].class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(repository.size(), response.getBody().length);
    }

    /**
     * Testa se o método buscarCorDtoPorIdTeste retorna um CorDto por ID.
     */
    @Test
    void buscarCorDtoPorIdTeste() {
        // GIVEN
        // O repositório está populado com dados de teste (configurado no setUp)
        var counter = repository.size();
        CorDto expected = repository.values().stream().findFirst().get();
        var id = expected.getId();

        // WHEN
        var response = restTemplate.getForEntity(url + "/{id}", CorDto.class, id);
        var actual = response.getBody();

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(repository.size(), counter);
        // Verifica se o objeto retornado não é nulo.
        assertNotNull(actual);
    }
    /**
     * Testa se o método buscarTestePorId retorna um erro 404 quando o ID não é encontrado.
     */
    @Test
    void buscarTestePorIdNaoEncontrado() {
        // GIVEN
        // O repositório está populado com dados de teste (configurado no setUp)
        var counter = repository.size();
        var id = 6; // ID que não existe no repositório

        // WHEN
        var response = restTemplate.getForEntity(url + "/{id}", CorDto.class, id);

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(repository.size(), counter);
        assertNull(response.getBody());
    }
    /**
     * Testa se o método excluirTesteNaoEncontrado retorna um erro 404 quando o ID não é encontrado.
     */
    @Test
    void excluirTesteNaoEncontrado() {
        // GIVEN
        // O repositório está populado com dados de teste (configurado no setUp)
        var counter = repository.size();
        var id = 9999; // ID que não existe no repositório

        // WHEN
        var response = restTemplate.exchange(url + "/{id}", HttpMethod.DELETE, null, Void.class, id);

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(repository.size(), counter);
        assertNull(response.getBody());
    }

    /**
     * Testa se o método excluirTeste exclui um CorDto corretamente.
     */
    @Test
    void excluirTeste() {
        // GIVEN
        // O repositório está populado com dados de teste (configurado no setUp)
        var counter = repository.size();
        CorDto expected = repository.values().stream().findFirst().get();
        var id = expected.getId();

        // WHEN
        var response = restTemplate.exchange(url + "/{id}", HttpMethod.DELETE, null, Void.class, id);

        // THEN
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(repository.size(), counter - 1);
        // Verifica se o objeto foi removido do repositório
        assertFalse(repository.containsKey(id));
    }

}