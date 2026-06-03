# 📓 Obsidian Daily Note

> Ferramenta CLI em Java que automatiza a criação da nota diária no Obsidian — verificando se ela já existe e, caso contrário, copiando o seu template para a pasta correta com o nome baseado na data de hoje.

---

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Como Funciona](#como-funciona)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Configuração](#configuração)
- [Compilação e Execução](#compilação-e-execução)
- [Conceitos Java Aplicados](#conceitos-java-aplicados)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [Próximos Passos](#próximos-passos)

---

## Sobre o Projeto

Quem usa o [Obsidian](https://obsidian.md/) para fazer anotações diárias conhece a rotina: abrir o cofre, criar a pasta do mês se ela não existir, criar o arquivo com o nome no formato correto, colar o template... **Este projeto elimina tudo isso com um único comando.**

A ferramenta lê duas variáveis de ambiente, calcula o caminho correto para a nota de hoje e, em segundos:

- ✅ Informa se a nota já existe (nenhuma ação tomada).
- 📁 Cria a estrutura de pastas `ano/mês/` automaticamente, se necessário.
- 📋 Copia o template configurado para o local correto com o nome `AAAAMMDD.md`.

---

## Como Funciona

O fluxo de decisão da aplicação pode ser visualizado assim:

```
Início
  │
  ├─► Lê variáveis de ambiente (diario, diario_template)
  │       └─► Ausentes? → Exibe instruções e encerra.
  │
  ├─► Calcula o caminho da nota: {diario}/{ano}/{mês}/{AAAAMMDD}.md
  │
  ├─► Nota já existe?
  │       ├─► SIM  → "✅ Nota já existe." → Encerra.
  │       └─► NÃO  → Prossegue para criação.
  │
  ├─► Template existe no disco?
  │       └─► NÃO → Lança erro com caminho inválido → Encerra.
  │
  └─► Cria pastas (se necessário) + Copia template → "✨ Nota criada!"
```

---

## Arquitetura

O projeto é intencionalmente dividido em **duas classes** para respeitar o **Princípio da Responsabilidade Única (SRP)** — cada classe tem apenas um motivo para mudar.

```
src/main/java/
├── Main.java               # Ponto de entrada: configuração, orquestração e erros.
└── GerenciadorDiario.java  # Núcleo: toda a lógica de datas, paths e arquivos.
```

### `Main.java` — Orquestrador

Responsável por **três coisas apenas**:

1. Ler as variáveis de ambiente do sistema operacional.
2. Validar se a configuração mínima está presente.
3. Instanciar o `GerenciadorDiario` e delegar a execução.

Não contém nenhuma lógica de arquivo ou data. É a "porta de entrada" limpa da aplicação.

### `GerenciadorDiario.java` — Núcleo de Negócio

Onde a lógica real vive. Esta classe:

- Recebe a configuração via construtor (injeção de dependência simples).
- Calcula os caminhos de forma multiplataforma com `java.nio.file.Paths`.
- Verifica existência, cria diretórios e copia o template.
- Lança `IOException` para que o chamador decida como tratar o erro (separação de preocupações).

---

## Pré-requisitos

| Requisito | Versão mínima |
|-----------|--------------|
| Java (JDK) | 8+ |
| Sistema Operacional | Linux, macOS ou Windows |
| Shell | Zsh, Bash ou qualquer shell que suporte `export` |

Para verificar sua instalação:

```bash
java -version
javac -version
```

---

## Configuração

A aplicação usa **variáveis de ambiente** para não ter nenhum caminho fixo no código — isso a torna portável entre máquinas sem necessidade de recompilação.

Adicione as linhas abaixo ao seu arquivo de configuração do shell (`~/.zshrc`, `~/.bashrc` ou equivalente):

```bash
# Caminho raiz do seu cofre no Obsidian
export diario="/Users/seu-usuario/Obsidian/MeuCofre/Diário"

# Caminho completo para o arquivo de template
export diario_template="/Users/seu-usuario/Obsidian/MeuCofre/Templates/diario.md"
```

Após editar o arquivo, recarregue as configurações:

```bash
source ~/.zshrc   # ou source ~/.bashrc
```

---

## Compilação e Execução

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/obsidian-daily-note-initializer.git
cd obsidian-daily-note-initializer
```

### 2. Compile os dois arquivos juntos

```bash
cd src/main/java
javac GerenciadorDiario.java Main.java
```

> **Por que compilar juntos?** `Main.java` depende de `GerenciadorDiario.java`. Compilá-los na mesma chamada garante que o `javac` resolva essa dependência automaticamente.

### 3. Execute

```bash
java Main
```

### Exemplos de saída

**Quando a nota ainda não existe:**
```
📄 Template   : /Users/voce/Obsidian/Templates/diario.md
🔍 Nota do dia: /Users/voce/Obsidian/Diário/2026/06/20260603.md
📭 Nota ainda não existe. Iniciando criação...
⚙️  Criando estrutura de pastas...
✨ Nota criada com sucesso: 20260603.md
```

**Quando a nota já existe:**
```
📄 Template   : /Users/voce/Obsidian/Templates/diario.md
🔍 Nota do dia: /Users/voce/Obsidian/Diário/2026/06/20260603.md
✅ A nota diária de hoje já existe. Nenhuma ação necessária.
```

### Automação com cron (opcional)

Para executar a ferramenta automaticamente toda manhã, adicione uma entrada no `crontab`:

```bash
crontab -e
```

```
# Roda todos os dias às 07:00
0 7 * * * cd /caminho/para/src/main/java && java Main
```

---

## Conceitos Java Aplicados

Este projeto foi desenvolvido como exercício prático dos seguintes fundamentos de Java:

| Conceito | Onde é Usado | Por quê |
|----------|-------------|---------|
| **`java.nio.file` (NIO)** | `GerenciadorDiario.java` | API moderna para I/O de arquivos — mais segura e expressiva que `java.io.File`. |
| **`Path` e `Paths`** | Resolução de caminhos | `Path` é uma *interface* (contrato); `Paths.get()` é o método fábrica que instancia a implementação correta para cada SO. |
| **`LocalDate` e `DateTimeFormatter`** | Cálculo da data atual | API imutável do Java 8+ para datas sem fuso horário — mais segura que `java.util.Date`. |
| **Variáveis de ambiente** | `Main.java` | `System.getenv()` acessa o ambiente do SO, tornando a configuração externa ao código. |
| **Tratamento de exceções** | Bloco `try-catch` em `Main` | Operações de I/O podem falhar (disco cheio, permissões). O Java *obriga* o tratamento de `IOException` em tempo de compilação. |
| **SRP (Single Responsibility)** | Divisão `Main` / `GerenciadorDiario` | Cada classe tem apenas um motivo para mudar — facilita manutenção e testes. |
| **Encapsulamento** | Métodos `private` no gerenciador | `resolverCaminhoNota()` e `criarNota()` são detalhes internos — apenas `executar()` é público. |
| **Constantes de classe** | `FMT_ANO`, `FMT_MES`, `FMT_ARQUIVO` | `static final` garante que os formatadores são criados uma única vez e reutilizados. |

---

## Estrutura de Pastas

```
obsidian-daily-note-initializer/
├── src/
│   └── main/
│       └── java/
│           ├── Main.java               # Ponto de entrada
│           └── GerenciadorDiario.java  # Núcleo da aplicação
└── README.md
```

A estrutura `src/main/java/` segue a convenção do **Maven** — o gerenciador de build mais usado no ecossistema Java — facilitando a migração para um projeto gerenciado por Maven ou Gradle no futuro.

---

## Próximos Passos

Possíveis evoluções para versões futuras:

- [ ] **Suporte a argumentos CLI** — permitir passar uma data específica como argumento (`java Main 2026-06-10`).
- [ ] **Múltiplos templates** — selecionar o template com base no dia da semana.
- [ ] **Testes unitários** — adicionar JUnit 5 para testar `GerenciadorDiario` com um sistema de arquivos em memória.
- [ ] **Build com Maven/Gradle** — empacotar em um `.jar` executável com um único comando.
- [ ] **Integração com shell** — criar um alias `nota` no `.zshrc` que compila e executa automaticamente.

---

*Desenvolvido como projeto de aprendizado de Java — do script funcional à arquitetura orientada a objetos.*