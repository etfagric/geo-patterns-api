package com.dio.geopatterns.service;

import com.dio.geopatterns.dto.ProcessarPessoaRequest;
import com.dio.geopatterns.exception.PessoaNaoEncontradaException;
import com.dio.geopatterns.model.Pessoa;
import com.dio.geopatterns.repository.PessoaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final GeoJsonService geoJsonService;

    private static final double RAIO_PROCESSAMENTO_METROS = 500.0;

    public PessoaService(PessoaRepository pessoaRepository, GeoJsonService geoJsonService) {
        this.pessoaRepository = pessoaRepository;
        this.geoJsonService = geoJsonService;
    }

    public Pessoa cadastrar(Pessoa pessoa) {
        return pessoaRepository.salvar(pessoa);
    }

    public List<Pessoa> buscarTodas() {
        return pessoaRepository.buscarTodas();
    }

    public Pessoa buscarPorId(Long id) {
        return buscarPessoaOuFalhar(id);
    }

    private Pessoa buscarPessoaOuFalhar(Long id) {
        return pessoaRepository.buscarPorId(id)
                .orElseThrow(() -> new PessoaNaoEncontradaException(id));
    }

    public Pessoa atualizar(Long id, Pessoa pessoa) {
        pessoa.setId(id);

        pessoaRepository.atualizar(pessoa);

        return pessoaRepository.buscarPorId(id)
                .orElseThrow(() -> new PessoaNaoEncontradaException(id));
    }

    public void excluir(Long id) {
        pessoaRepository.excluir(id);
    }

    public Map<String, Object> processar(Long id, ProcessarPessoaRequest request) {
        Pessoa pessoa = buscarPessoaOuFalhar(id);

        pessoaRepository.atualizarLocalizacao(
                id,
                request.getLatitude(),
                request.getLongitude(),
                RAIO_PROCESSAMENTO_METROS
        );

        return geoJsonService.gerarCirculo(
                pessoa.getId(),
                request.getLatitude(),
                request.getLongitude(),
                RAIO_PROCESSAMENTO_METROS
        );
    }


}
