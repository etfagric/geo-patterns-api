package com.dio.geopatterns.repository;

import com.dio.geopatterns.model.Pessoa;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class PessoaRepository {

    private final JdbcTemplate jdbcTemplate;

    public PessoaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Pessoa salvar(Pessoa pessoa) {
        String sql = """
                INSERT INTO pessoa (nome)
                VALUES (?)
                RETURNING id
                """;

        Long id = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                pessoa.getNome());

        pessoa.setId(id);

        return pessoa;
    }

    public List<Pessoa> buscarTodas() {

        String sql = """
            SELECT id,
                   nome,
                   latitude,
                   longitude,
                   raio
            FROM pessoa
            ORDER BY id
            """;

        return jdbcTemplate.query(
                sql,
                this::mapearPessoa
        );
    }

    public Optional<Pessoa> buscarPorId(Long id) {

        String sql = """
            SELECT id,
                   nome,
                   latitude,
                   longitude,
                   raio
            FROM pessoa
            WHERE id = ?
            """;

        List<Pessoa> pessoas = jdbcTemplate.query(
                sql,
                this::mapearPessoa,
                id
        );

        return pessoas.stream().findFirst();
    }

    public int atualizar(Pessoa pessoa) {

        String sql = """
            UPDATE pessoa
            SET nome = ?
            WHERE id = ?
            """;

        return jdbcTemplate.update(
                sql,
                pessoa.getNome(),
                pessoa.getId()
        );
    }

    public int excluir(Long id) {

        String sql = """
            DELETE FROM pessoa
            WHERE id = ?
            """;

        return jdbcTemplate.update(sql, id);
    }

    public int atualizarLocalizacao(
            Long id,
            Double latitude,
            Double longitude,
            Double raio) {

        String sql = """
            UPDATE pessoa
            SET latitude = ?,
                longitude = ?,
                raio = ?
            WHERE id = ?
            """;

        return jdbcTemplate.update(
                sql,
                latitude,
                longitude,
                raio,
                id
        );
    }

    private Pessoa mapearPessoa(ResultSet rs, int rowNum) throws SQLException {
        Pessoa pessoa = new Pessoa();

        pessoa.setId(rs.getLong("id"));
        pessoa.setNome(rs.getString("nome"));
        pessoa.setLatitude(rs.getObject("latitude", Double.class));
        pessoa.setLongitude(rs.getObject("longitude", Double.class));
        pessoa.setRaio(rs.getObject("raio", Double.class));

        return pessoa;
    }
}
