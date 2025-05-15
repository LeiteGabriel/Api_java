# API de Usuários e Endereços (Api_java)

Esta é uma aplicação Spring Boot para gerenciamento de usuários e seus endereços, com autenticação JWT para APIs e autenticação baseada em sessão para a interface web.

## Pré-requisitos

Antes de começar, garanta que você tem os seguintes softwares instalados:

1.  **Java Development Kit (JDK):**
    *   Versão: Java 17 ou superior (verifique a versão especificada no arquivo `pom.xml`).
    *   Para verificar sua versão: `java -version`
    *   Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) ou [OpenJDK](https://openjdk.java.net/)

2.  **Apache Maven:**
    *   Versão: 3.6.x ou superior.
    *   Para verificar sua versão: `mvn -version`
    *   Download: [Apache Maven](https://maven.apache.org/download.cgi)

3.  **PostgreSQL Server:**
    *   Versão: 12 ou superior.
    *   Download: [PostgreSQL](https://www.postgresql.org/download/)
    *   Certifique-se de que o servidor PostgreSQL esteja em execução.

4.  **Git (Opcional):**
    *   Para clonar o repositório.
    *   Download: [Git](https://git-scm.com/downloads)

## Configuração do Projeto

1.  **Clonar o Repositório (se aplicável):**
    ```bash
    git clone <URL_DO_SEU_REPOSITORIO_GIT>
    cd Api_java
    ```
    Se você já possui os arquivos do projeto, pule esta etapa.

2.  **Configuração do Banco de Dados PostgreSQL:**
    *   Conecte-se ao seu servidor PostgreSQL (usando `psql` ou uma ferramenta gráfica como pgAdmin).
    *   Crie o banco de dados que será utilizado pela aplicação. O nome padrão configurado é `userapi`:
        ```sql
        CREATE DATABASE userapi;
        ```
    *   Verifique as credenciais de acesso ao banco no arquivo `src/main/resources/application.properties`:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/userapi
        spring.datasource.username=postgres
        spring.datasource.password=postgres 
        ```
        Se o seu usuário e senha do PostgreSQL forem diferentes do padrão (`postgres`/`postgres`), atualize este arquivo ou certifique-se de que o usuário `postgres` com a senha `postgres` tenha as permissões necessárias para acessar e modificar o banco de dados `userapi`.

3.  **Revisar Outras Configurações (Opcional):**
    *   O arquivo `src/main/resources/application.properties` também contém configurações para o segredo JWT, porta do servidor (padrão `8080`), e níveis de logging.

## Compilando o Projeto

Navegue até o diretório raiz do projeto (onde o arquivo `pom.xml` está localizado) e execute o seguinte comando Maven para compilar o projeto e baixar todas as dependências:

```bash
mvn clean install
```
Este comando gerará um arquivo `.jar` executável no diretório `target/` (ex: `Api_java-0.0.1-SNAPSHOT.jar`).

## Executando a Aplicação

Você pode executar a aplicação de duas maneiras:

1.  **Usando o Plugin Maven Spring Boot (Recomendado para desenvolvimento):**
    No diretório raiz do projeto, execute:
    ```bash
    mvn spring-boot:run
    ```

2.  **Executando o Arquivo JAR Gerado:**
    Após compilar com `mvn clean install`, o arquivo JAR estará no diretório `target/`.
    Execute-o com o comando:
    ```bash
    java -jar target/Api_java-0.0.1-SNAPSHOT.jar
    ```
    *Nota: Substitua `Api_java-0.0.1-SNAPSHOT.jar` pelo nome exato do arquivo JAR gerado no seu diretório `target/`. O nome e a versão são definidos no `pom.xml`.*

## Acessando a Aplicação

Após a inicialização bem-sucedida, a aplicação estará disponível em:

*   **URL Base:** `http://localhost:8080` (ou a porta configurada em `server.port`).

Você pode acessar:
*   A página de login em `http://localhost:8080/login`.
*   Após o login, o dashboard em `http://localhost:8080/dashboard`.
*   Os endpoints da API estarão sob `/api/**` (ex: `/api/auth/login`, `/api/addresses`).

## Estrutura do Projeto (Principais Diretórios)

*   `src/main/java`: Código fonte Java da aplicação.
    *   `com.userapi.config`: Configurações (ex: Spring Security).
    *   `com.userapi.controller`: Controladores Spring MVC e REST.
    *   `com.userapi.dto`: Data Transfer Objects.
    *   `com.userapi.entity`: Entidades JPA.
    *   `com.userapi.repository`: Repositórios Spring Data JPA.
    *   `com.userapi.security`: Classes relacionadas à segurança (JWT, UserDetailsService).
    *   `com.userapi.service`: Lógica de negócios (serviços).
*   `src/main/resources`: Arquivos de configuração e templates.
    *   `application.properties`: Configurações da aplicação.
    *   `templates/`: Templates HTML (Thymeleaf).
*   `pom.xml`: Arquivo de configuração do Maven (dependências, build).
*   `logs/`: Diretório onde os logs da aplicação são salvos (ex: `application.log`).

## Solução de Problemas Comuns

*   **Erro de Conexão com Banco de Dados:**
    *   Verifique se o servidor PostgreSQL está em execução.
    *   Confirme se as credenciais (`spring.datasource.username`, `spring.datasource.password`) e a URL (`spring.datasource.url`) em `application.properties` estão corretas.
    *   Certifique-se de que o banco de dados (`userapi`) foi criado.
*   **Porta 8080 já em uso:**
    *   Se outra aplicação estiver usando a porta 8080, você pode alterá-la em `application.properties` (ex: `server.port=8081`) ou parar a outra aplicação.

