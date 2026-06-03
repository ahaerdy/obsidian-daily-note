import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Núcleo do sistema — gerencia todo o ciclo de vida da nota diária.
 *
 * <p>Esta classe encapsula três responsabilidades coesas:
 * <ol>
 *   <li>Calcular os caminhos de origem (template) e destino (nota do dia).</li>
 *   <li>Verificar se a nota diária já existe no cofre do Obsidian.</li>
 *   <li>Criar a estrutura de pastas e copiar o template, se necessário.</li>
 * </ol>
 *
 * <p><b>Estrutura de pastas gerada no cofre:</b>
 * <pre>
 *   {diario}/
 *   └── 2026/
 *       └── 06/
 *           └── 20260603.md
 * </pre>
 */
public class GerenciadorDiario {

    // --- Formatadores de data (constantes de classe, criadas uma única vez) ---

    /** Formata o ano como string de 4 dígitos. Ex: "2026" */
    private static final DateTimeFormatter FMT_ANO      = DateTimeFormatter.ofPattern("yyyy");

    /** Formata o mês como string de 2 dígitos com zero à esquerda. Ex: "06" */
    private static final DateTimeFormatter FMT_MES      = DateTimeFormatter.ofPattern("MM");

    /** Formata a data completa para compor o nome do arquivo. Ex: "20260603" */
    private static final DateTimeFormatter FMT_ARQUIVO  = DateTimeFormatter.ofPattern("yyyyMMdd");

    // --- Estado da instância ---

    /** Caminho raiz do cofre do Obsidian, lido da variável de ambiente {@code diario}. */
    private final Path caminhoBase;

    /** Caminho completo para o arquivo de template, lido da variável {@code diario_template}. */
    private final Path caminhoTemplate;

    // -------------------------------------------------------------------------

    /**
     * Constrói o gerenciador com as configurações do ambiente.
     *
     * @param caminhoBaseStr     String com o caminho raiz do cofre (variável {@code diario}).
     * @param caminhoTemplateStr String com o caminho do template (variável {@code diario_template}).
     */
    public GerenciadorDiario(String caminhoBaseStr, String caminhoTemplateStr) {
        // Paths.get() é um método fábrica multiplataforma: converte Strings em objetos Path
        // que funcionam corretamente em Linux, macOS e Windows.
        this.caminhoBase     = Paths.get(caminhoBaseStr);
        this.caminhoTemplate = Paths.get(caminhoTemplateStr);
    }

    // -------------------------------------------------------------------------

    /**
     * Executa o fluxo completo de verificação e criação da nota diária.
     *
     * <p>Fluxo de decisão:
     * <pre>
     *   Nota já existe? ──► SIM  ──► Informa o usuário e encerra.
     *        │
     *       NÃO
     *        │
     *   Template existe? ──► NÃO ──► Lança erro crítico e encerra.
     *        │
     *       SIM
     *        │
     *   Cria pastas + copia template ──► Confirma criação.
     * </pre>
     *
     * @throws IOException se ocorrer falha de leitura/escrita no sistema de arquivos.
     */
    public void executar() throws IOException {
        LocalDate hoje = LocalDate.now();

        // Resolve o caminho completo da nota com base na data atual
        Path caminhoNota = resolverCaminhoNota(hoje);

        System.out.println("📄 Template   : " + caminhoTemplate.toAbsolutePath());
        System.out.println("🔍 Nota do dia: " + caminhoNota.toAbsolutePath());

        if (Files.exists(caminhoNota)) {
            System.out.println("✅ A nota diária de hoje já existe. Nenhuma ação necessária.");
            return;
        }

        System.out.println("📭 Nota ainda não existe. Iniciando criação...");
        criarNota(caminhoNota);
    }

    // -------------------------------------------------------------------------
    // MÉTODOS PRIVADOS DE SUPORTE
    // -------------------------------------------------------------------------

    /**
     * Monta o caminho completo para a nota do dia a partir de uma data.
     *
     * <p>Exemplo: {@code /cofre/2026/06/20260603.md}
     *
     * @param data A data para a qual o caminho deve ser calculado.
     * @return Um objeto {@link Path} com o caminho absoluto da nota.
     */
    private Path resolverCaminhoNota(LocalDate data) {
        String ano          = data.format(FMT_ANO);
        String mes          = data.format(FMT_MES);
        String nomeArquivo  = data.format(FMT_ARQUIVO) + ".md";

        // Paths.get(base, partes...) constrói o caminho de forma segura e multiplataforma,
        // equivalente a: caminhoBase + "/" + ano + "/" + mes + "/" + nomeArquivo
        return Paths.get(caminhoBase.toString(), ano, mes, nomeArquivo);
    }

    /**
     * Valida o template, cria os diretórios necessários e copia o arquivo.
     *
     * @param destino O caminho onde a nova nota deve ser criada.
     * @throws IOException se o template não existir ou se a cópia falhar.
     */
    private void criarNota(Path destino) throws IOException {
        if (!Files.exists(caminhoTemplate)) {
            // Lança uma exceção com mensagem clara em vez de apenas imprimir e retornar,
            // permitindo que o chamador (Main) decida como tratar o erro.
            throw new IOException(
                    "Template não encontrado em: " + caminhoTemplate.toAbsolutePath()
            );
        }

        System.out.println("⚙️  Criando estrutura de pastas...");

        // getParent() extrai apenas o diretório pai do arquivo (ex: /cofre/2026/06/).
        // createDirectories() cria toda a árvore de pastas de uma vez, sem lançar
        // exceção caso os diretórios já existam (comportamento idempotente).
        Files.createDirectories(destino.getParent());

        // Copia o template para o destino final.
        // COPY_ATTRIBUTES preserva os metadados do arquivo original (timestamps, permissões).
        Files.copy(caminhoTemplate, destino, StandardCopyOption.COPY_ATTRIBUTES);

        System.out.println("✨ Nota criada com sucesso: " + destino.getFileName());
    }
}