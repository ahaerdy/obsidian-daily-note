import java.io.IOException;

/**
 * Ponto de entrada da aplicação.
 *
 * Esta classe é responsável exclusivamente por:
 *   1. Inicializar o GerenciadorDiario com as configurações do ambiente.
 *   2. Delegar a execução da lógica de negócio.
 *   3. Tratar erros de I/O que possam surgir em tempo de execução.
 *
 * Seguindo o Princípio da Responsabilidade Única (SRP), esta classe
 * não contém nenhuma lógica de manipulação de arquivos ou datas.
 */
public class Main {

    /**
     * Porta de entrada do programa.
     *
     * @param args Argumentos de linha de comando (não utilizados nesta versão).
     */
    public static void main(String[] args) {

        // 1. LÊ AS VARIÁVEIS DE AMBIENTE
        // System.getenv() consulta o ambiente do Sistema Operacional (ex: Zsh/Bash).
        String caminhoBase      = System.getenv("diario");
        String caminhoTemplate  = System.getenv("diario_template");

        // 2. VALIDA A CONFIGURAÇÃO ANTES DE QUALQUER OPERAÇÃO
        if (caminhoBase == null || caminhoTemplate == null) {
            System.out.println("❌ Erro: variáveis de ambiente 'diario' ou 'diario_template' não encontradas.");
            System.out.println("   Configure-as no seu shell antes de executar:");
            System.out.println("   export diario=\"/caminho/para/seu/cofre\"");
            System.out.println("   export diario_template=\"/caminho/para/seu/template.md\"");
            System.exit(1); // Encerra com código de erro (convencional em CLIs Unix)
            return;
        }

        // 3. INSTANCIA O GERENCIADOR E EXECUTA A LÓGICA PRINCIPAL
        // O bloco try-catch fica aqui pois é responsabilidade do ponto de entrada
        // apresentar erros críticos ao usuário de forma amigável.
        try {
            GerenciadorDiario gerenciador = new GerenciadorDiario(caminhoBase, caminhoTemplate);
            gerenciador.executar();
        } catch (IOException e) {
            System.out.println("💥 Erro de I/O ao manipular os arquivos: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }
}