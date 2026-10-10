package com.dio.geopatterns.controller;

import com.dio.geopatterns.dto.ProcessarPessoaRequest;
import com.dio.geopatterns.model.Pessoa;
import com.dio.geopatterns.service.PessoaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pessoas")
@CrossOrigin(origins = "http://localhost:5173")
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @PostMapping
    public ResponseEntity<Pessoa> cadastrar(@Valid @RequestBody Pessoa pessoa) {
        Pessoa pessoaSalva = pessoaService.cadastrar(pessoa);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pessoaSalva);
    }

    @GetMapping
    public ResponseEntity<List<Pessoa>> buscarTodas() {
        List<Pessoa> pessoas = pessoaService.buscarTodas();

        return ResponseEntity.ok(pessoas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pessoa> buscarPorId(@PathVariable Long id) {
        Pessoa pessoa = pessoaService.buscarPorId(id);

        return ResponseEntity.ok(pessoa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pessoa> atualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody Pessoa pessoa) {

        Pessoa pessoaAtualizada = pessoaService.atualizar(id, pessoa);

        return ResponseEntity.ok(pessoaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        pessoaService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            value = "/{id}/processar",
            produces = "application/geo+json"
    )
    public ResponseEntity<Map<String, Object>> processar(
            @PathVariable Long id,
            @Valid @RequestBody ProcessarPessoaRequest request) {

        Map<String, Object> geoJson = pessoaService.processar(id, request);

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=\"pessoa-" + id + ".geojson\""
                )
                .body(geoJson);
    }
}
