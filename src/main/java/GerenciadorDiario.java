import java.io.IOException; // DECLARAÇÃO de classe concreta de exceção checada. Usada para instanciar objetos de erro na Heap em falhas de I/O.
import java.nio.file.Files; // Referencia classe utilitária com Métodos Estáticos (métodos de classe) para operações diretas no disco.
import java.nio.file.Path; // Referencia uma INTERFACE (Contrato Polimórfico). Define comportamento sem instanciar objetos diretamente via 'new'.
import java.nio.file.Paths; // Referencia classe de fábrica com Métodos Estáticos para instanciar classes concretas que implementam Path.
import java.nio.file.StandardCopyOption; // Referencia tipo ENUM. Seus elementos são instanciados como objetos estáticos na inicialização da JVM.
import java.time.LocalDate; // Referencia classe de dados temporais imutáveis. Instâncias na Heap são geradas por métodos estáticos de fábrica.
import java.time.format.DateTimeFormatter; // Referencia classe que armazena na Heap o estado dos interpretadores e formatadores de padrões de data.

public class GerenciadorDiario {

    // --- CONSTANTES DE CLASSE (Escopo Estático) ---

    // DECLARAÇÃO de referência estática na Metaspace. INSTANCIAÇÃO de um DateTimeFormatter na Heap via método de classe .ofPattern(). final fixa o ponteiro.
    private static final DateTimeFormatter FMT_ANO      = DateTimeFormatter.ofPattern("yyyy");

    // DECLARAÇÃO de segunda referência estática. Nova INSTANCIAÇÃO na Heap de um objeto DateTimeFormatter independente.
    private static final DateTimeFormatter FMT_MES      = DateTimeFormatter.ofPattern("MM");

    // DECLARAÇÃO de terceira referência estática. Terceira INSTANCIAÇÃO de DateTimeFormatter isolada na Heap.
    private static final DateTimeFormatter FMT_ARQUIVO  = DateTimeFormatter.ofPattern("yyyyMMdd");

    // --- ESTADO DA INSTÂNCIA (Atributos de Objeto) ---

    // DECLARAÇÃO de atributo por Interface (Polimorfismo). O ponteiro existirá dentro do corpo de cada objeto GerenciadorDiario alocado na Heap.
    private final Path caminhoBase;

    // DECLARAÇÃO de segundo atributo por Interface. Espaço reservado na Heap (dentro do objeto) para armazenar endereço de classe concreta.
    private final Path caminhoTemplate;

    // --- CONSTRUTOR ---

    // Chamada aloca um Frame na Stack. Variáveis locais (parâmetros) recebem cópias dos ponteiros das Strings vindas do String Pool (Heap).
    public GerenciadorDiario(String caminhoBaseStr, String caminhoTemplateStr) {

        // Chamada de método estático de fábrica (Paths.get). A JDK avalia o S.O. e instancia uma CLASSE CONCRETA (ex: UnixPath) na Heap.
        // O endereço retornado é gravado no ponteiro "this.caminhoBase" localizado dentro do objeto na Heap.
        this.caminhoBase     = Paths.get(caminhoBaseStr);

        // Nova chamada ao método estático. Segunda instanciação de CLASSE CONCRETA (ex: UnixPath) na Heap.
        // O ponteiro resultante é gravado em "this.caminhoTemplate" na Heap. O Frame do construtor é desalocado da Stack após o fechamento da chave.
        this.caminhoTemplate = Paths.get(caminhoTemplateStr);
    }

    // --- MÉTODOS DE INSTÂNCIA PRINCIPAIS ---

    // Invocação exige um objeto ativo na Heap. Cria um Frame de execução na Stack, contendo o ponteiro implícito "this".
    public void executar() throws IOException {

        // DECLARAÇÃO da referência "hoje" na Stack. Método estático .now() captura dados do S.O., INSTANCIA um LocalDate na Heap e devolve o endereço.
        LocalDate hoje = LocalDate.now();

        // DECLARAÇÃO de referência "caminhoNota" na Stack. Método de instância é chamado, gerando um novo objeto Path na Heap e retornando seu ponteiro.
        Path caminhoNota = resolverCaminhoNota(hoje);

        // Acesso ao campo estático System.out (PrintStream na Heap). Método de instância .toAbsolutePath() do objeto caminhoTemplate cria e retorna um NOVO objeto Path (caminho absoluto) na Heap.
        // A JVM instancia implicitamente um StringBuilder na Heap para concatenar o literal com a String do objeto Path, enviando a String final para o println.
        System.out.println("📄 Template   : " + caminhoTemplate.toAbsolutePath());
        System.out.println("🔍 Nota do dia: " + caminhoNota.toAbsolutePath());

        // Chamada de método estático (Files.exists) passando o ponteiro "caminhoNota". Ele varre o disco e RETORNA UM TIPO PRIMITIVO (boolean) direto para avaliação na Stack.
        if (Files.exists(caminhoNota)) {
            System.out.println("✅ A nota diária de hoje já existe. Nenhuma ação necessária.");
            // Controle de fluxo. Interrompe a execução e limpa imediatamente o Frame de "executar" da Stack, liberando as referências locais.
            return;
        }

        System.out.println("📭 Nota ainda não existe. Iniciando criação...");

        // Chamada de método de instância privado. Passa uma cópia do ponteiro "caminhoNota" da Stack. Aloca novo Frame para "criarNota" na Stack.
        criarNota(caminhoNota);
    }

    // --- MÉTODOS PRIVADOS DE SUPORTE (Mecânica de Instância) ---

    // Cria Frame na Stack. O parâmetro "data" recebe o endereço do objeto LocalDate que está na Heap.
    private Path resolverCaminhoNota(LocalDate data) {

        // DECLARAÇÃO de referência "ano" na Stack. Método de instância .format() lê o LocalDate na Heap usando a constante estática FMT_ANO e RETORNA UM NOVO OBJETO String na Heap.
        String ano          = data.format(FMT_ANO);

        // DECLARAÇÃO de "mes" na Stack. Segunda execução de método de instância gerando uma NOVA String na Heap.
        String mes          = data.format(FMT_MES);

        // DECLARAÇÃO de "nomeArquivo" na Stack. O método de instância gera uma String. O operador "+" força a JVM a instanciar um StringBuilder na Heap para fundir o resultado com o literal ".md", gerando uma String final na Heap.
        String nomeArquivo  = data.format(FMT_ARQUIVO) + ".md";

        // Método de instância caminhoBase.toString() gera uma String. Método estático Paths.get() recebe os 4 ponteiros de Strings da Stack, instancia uma nova CLASSE CONCRETA de Path na Heap e RETORNA o endereço.
        // O Frame atual é destruído na Stack; strings locais intermediárias sem ponteiro viram alvo do Garbage Collector.
        return Paths.get(caminhoBase.toString(), ano, mes, nomeArquivo);
    }

    // Cria Frame na Stack. O parâmetro "destino" é uma referência local que aponta para o Path gerado no método anterior.
    private void criarNota(Path destino) throws IOException {

        // Chamada de método estático Files.exists. Passa o ponteiro do atributo "caminhoTemplate" (buscado na Heap via "this"). Retorna primitivo boolean negado pelo operador "!" na Stack.
        if (!Files.exists(caminhoTemplate)) {
            // Concatenação gera String na Heap. A palavra-chave "new" força a INSTANCIAÇÃO real de um objeto IOException na Heap.
            // O "throw" interrompe o fluxo e ejeta o endereço desse objeto de erro para desempilhar a Stack até achar um catch.
            throw new IOException(
                    "Template não encontrado em: " + caminhoTemplate.toAbsolutePath()
            );
        }

        System.out.println("⚙️  Criando estrutura de pastas...");

        // Método de instância destino.getParent() avalia o objeto na Heap e RETORNA UM NOVO objeto Path (apenas os diretórios pai).
        // Esse ponteiro temporário alimenta o método estático Files.createDirectories, que cria as pastas físicas no disco (sem atribuição de retorno).
        Files.createDirectories(destino.getParent());

        // Chamada de método estático utilitário. Recebe três referências: o atributo da Heap, a variável local da Stack e o Enum StandardCopyOption (objeto estático pré-alocado na inicialização da JVM).
        // Files.copy extrai as classes concretas por trás das interfaces de Path e opera direto a nível de sistema operacional.
        Files.copy(caminhoTemplate, destino, StandardCopyOption.COPY_ATTRIBUTES);

        // Método de instância destino.getFileName() RETORNA UM NOVO objeto Path isolando o nome do arquivo na Heap. Ele é convertido/concatenado e enviado ao PrintStream.
        System.out.println("✨ Nota criada com sucesso: " + destino.getFileName());

        // Fim do escopo do método. Frame de "criarNota" é desempilhado da Stack. O fluxo retorna e encerra "executar()", limpando a Stack restante.
    }
}