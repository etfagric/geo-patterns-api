# Geolocation API & Design Patterns com Spring Boot

API REST desenvolvida em Java 21 e Spring Boot para gerenciamento e processamento geoespacial de pessoas, aplicando padrões de projeto clássicos (GoF) e boas práticas do ecossistema Spring.

Projeto desenvolvido como parte do Bootcamp Itaú - Java com Inteligência Artificial na plataforma DIO (https://dio.me).

---

## Como a Aplicação Funciona

A aplicação resolve o problema de rastreamento e delimitação de área de cobertura geográfica para pessoas cadastradas:

1. **Cadastro:** O usuário cadastra uma pessoa na base de dados informando o nome através da API.
2. **Processamento de Localização:** Com a pessoa cadastrada, a aplicação recebe as coordenadas de latitude e longitude onde ela se encontra.
3. **Cálculo de Área e Retorno GeoJSON:** A API persiste a localização da pessoa no banco de dados, executa o cálculo de projeção esférica terrestre considerando um raio de cobertura de 500 metros e devolve um arquivo no formato padronizado GeoJSON (`FeatureCollection`).
4. **Visualização:** Esse GeoJSON contém tanto o ponto central exato da pessoa quanto o polígono circular delimitando a sua área de abrangência, permitindo renderizar a visualização em mapas e softwares GIS (como QGIS, Google Maps ou geojson.io).

---

## Padroes de Projeto Aplicados (Design Patterns)

O projeto demonstra na prática a aplicação dos padrões de projeto clássicos do GoF integrados à arquitetura do Spring Framework:

### 1. Singleton (Spring IoC / Container)
* **Conceito:** Garantir que uma classe tenha apenas uma instância em toda a aplicação e fornecer um ponto de acesso global.
* **No Projeto:** O Spring gerencia o ciclo de vida dos componentes como Singletons por padrão. Classes anotadas com `@RestController` (`PessoaController`), `@Service` (`PessoaService`, `GeoJsonService`), `@Repository` (`PessoaRepository`) e `@Component` (`CirculoGeometriaStrategy`) são instanciadas uma única vez e injetadas via construtor (Injeção de Dependências), promovendo baixo acoplamento e testabilidade.

### 2. Facade (Fachada)
* **Conceito:** Prover uma interface unificada e simplificada para um conjunto complexo de subsistemas.
* **No Projeto:** O método `processar(...)` na classe `PessoaService` atua como uma Facade. Ele abstrai toda a complexidade operacional para a camada de controle (`PessoaController`):
  1. Valida e recupera a pessoa no banco de dados (`buscarPessoaOuFalhar`);
  2. Atualiza os dados de geolocalização no PostgreSQL via JDBC;
  3. Aciona o subsistema de cálculo geoespacial (`GeoJsonService`) para geração do GeoJSON.
  O Controller apenas interage com a Facade, sem conhecer detalhes de persistência ou trigonometria esférica.

### 3. Strategy (Algoritmo Intercambiavel)
* **Conceito:** Definir uma família de algoritmos, encapsular cada um deles e torná-los intercambiáveis sem alterar as classes que os utilizam.
* **No Projeto:** 
  * A interface `GeometriaStrategy` estabelece o contrato para geração de geometrias a partir de coordenadas.
  * A classe `CirculoGeometriaStrategy` implementa o algoritmo trigonométrico de cálculo geodésico (projeção esférica da Terra para gerar um polígono circular de 64 vértices).
  * O `GeoJsonService` depende exclusivamente da abstração `GeometriaStrategy`, permitindo plugar facilmente novas estratégias geométricas (como `BoundingBoxStrategy` ou `HexagonStrategy`) sem qualquer alteração na camada de serviço.

---

## Tecnologias Utilizadas

* Java 21 (Features modernas, Text Blocks, Records)
* Spring Boot 4 / Spring Framework (Injeção de Dependência, REST)
* Spring JDBC (JdbcTemplate para controle fino de queries SQL e transações)
* PostgreSQL 17 (Banco de dados relacional)
* Docker e Docker Compose (Containerização do banco de dados)
* GeoJSON (RFC 7946 - Padrão de dados geoespaciais)
* Maven (Gerenciamento de dependências e build)

---

## Como Executar o Projeto

### Pre-requisitos
* Java 21 instalado
* Docker e Docker Compose instalados

### 1. Iniciar o Banco de Dados (PostgreSQL)
Suba o container do PostgreSQL configurado no Docker Compose:

```bash
docker compose up -d
```

O banco estará acessível em `localhost:5433` com as credenciais padrão `postgres`/`postgres`.

### 2. Executar a Aplicacao
Execute o Spring Boot via Maven Wrapper:

```bash
# Linux/macOS
./mvnw spring-boot:run

# Windows PowerShell
.\mvnw.cmd spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

---

## Endpoints da API

| Metodo | Endpoint | Descricao |
| :--- | :--- | :--- |
| `POST` | `/api/pessoas` | Cadastra uma nova pessoa |
| `GET` | `/api/pessoas` | Lista todas as pessoas cadastradas |
| `GET` | `/api/pessoas/{id}` | Busca pessoa por ID |
| `PUT` | `/api/pessoas/{id}` | Atualiza dados da pessoa |
| `DELETE` | `/api/pessoas/{id}` | Remove uma pessoa |
| `POST` | `/api/pessoas/{id}/processar` | Processa latitude/longitude e retorna GeoJSON com a area de 500m |

---

## Testando a Aplicacao na Pratica (Exemplo com cURL)

### 1. Cadastrar a Pessoa
Primeiro, cadastre a pessoa para obter o seu identificador (`id`):

```bash
curl --location 'http://localhost:8080/api/pessoas' \
--header 'Content-Type: application/json' \
--data '{
    "nome": "Campus DIO Araraquara"
}'
```

### 2. Informar Coordenadas e Gerar a Area (Exemplo: Sede da DIO)
Em seguida, envie a latitude e longitude da pessoa cadastrada (exemplo com o ID 1):

```bash
curl --location 'http://localhost:8080/api/pessoas/1/processar' \
--header 'Content-Type: application/json' \
--data '{
    "latitude": -21.7944,
    "longitude": -48.1764
}'
```

A resposta será um arquivo GeoJSON (`pessoa-1.geojson`) com o ponto central e o círculo delimitador.

### Visualizacao do Resultado no Mapa
Você pode copiar o JSON de resposta retornado no passo 2 e colar diretamente na ferramenta web gratuita https://geojson.io para visualizar a pessoa e o raio de 500 metros desenhados no mapa.


