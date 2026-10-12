package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/*
 ===============================================================================
 PASSO 1: PLANO INICIAL (10 MINUTOS)
 ===============================================================================
 - Domínio: Sistema de Cadastro de Produtos para e-Commerce.
 - Classes de Domínio: Produto (representa a entidade e valida dados).
 - Classes de Tela: TelaCadastroProduto (interface gráfica Swing).
 - Dados Validados & Exceções:
   1. Nome do Produto: Não pode ser vazio ou nulo (Lança IllegalArgumentException).
   2. Preço: Deve ser estritamente maior que zero (Lança IllegalArgumentException).
   3. Estoque: Deve ser maior ou igual a zero (Lança IllegalArgumentException).
 - Padrões de Projeto Aplicados:
   1. Factory Method: Utilizado para a criação centralizada e padronizada de instâncias de Produto.
   2. Observer (via Listener/Event Driven do Swing): A interface gráfica reage às ações do usuário através do ActionListener, separando o disparo da ação da execução do domínio.
 - Uso de IA: Opcional (Não utilizado nesta implementação).
 
 - Registros de Ajuste durante o desenvolvimento (Passo 2):
   * Parada dos 35 min: Janela montada via GridBagLayout com campos e botão acionável.
   * Parada dos 70 min: Exceções capturadas com sucesso e exibidas em JOptionPane sem travar a interface.
 ===============================================================================
*/

// =============================================================================
// DOMÍNIO E EXCEÇÕES
// =============================================================================

class Produto {
    private String nome;
    private double preco;
    private int estoque;

    public Produto(String nome, double preco, int estoque) {
        validarNome(nome);
        validarPreco(preco);
        validarEstoque(estoque);
        
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto não pode ser vazio.");
        }
    }

    private void validarPreco(double preco) {
        if (preco <= 0) {
            throw new IllegalArgumentException("O preço do produto deve ser maior que zero.");
        }
    }

    private void validarEstoque(int estoque) {
        if (estoque < 0) {
            throw new IllegalArgumentException("A quantidade em estoque não pode ser negativa.");
        }
    }

    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public int getEstoque() { return estoque; }
}

// =============================================================================
// PADRÃO DE PROJETO 1: FACTORY METHOD
// =============================================================================
/*
 * PADRÃO: Factory Method (Criacional)
 * PROBLEMA: A criação de objetos de domínio com validações de tipos e regras pode poluir
 *           a classe da interface gráfica com parse de strings e lógica de instanciação.
 * SOLUÇÃO: Encapsular a lógica de criação de instâncias de Produto na classe ProdutoFactory,
 *          responsável por converter os dados textuais da GUI e instanciar o objeto com segurança.
 * CONSEQUÊNCIAS NO CÓDIGO: A tela fica isolada da lógica de instanciação direta. Caso o processo
 *                          de criação de produtos mude no futuro, apenas a fábrica precisa ser alterada.
 */
class ProdutoFactory {
    public static Produto criarProduto(String nome, String precoTexto, String estoqueTexto) {
        double preco;
        int estoque;

        try {
            preco = Double.parseDouble(precoTexto.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O campo Preço deve conter um valor numérico válido.");
        }

        try {
            estoque = Integer.parseInt(estoqueTexto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O campo Estoque deve conter um valor inteiro válido.");
        }

        return new Produto(nome, preco, estoque);
    }
}

// =============================================================================
// INTERFACE GRÁFICA E PADRÃO DE PROJETO 2: OBSERVER (ACTIONLISTENER)
// =============================================================================
/*
 * PADRÃO: Observer (Comportamental)
 * PROBLEMA: A interface precisa notificar quando o usuário interage com o botão,
 *           sem acoplar rigidamente o componente visual à lógica do evento.
 * SOLUÇÃO: Utilização da interface ActionListener/Event Listener nativa do Java Swing,
 *          onde a tela se registra como ouvinte do botão.
 * CONSEQUÊNCIAS NO CÓDIGO: Permite que eventos de clique acionem a validação e o processamento
 *                          de forma desacoplada da construção visual dos componentes.
 */
public class TelaCadastroProduto extends JFrame {

    private JTextField txtNome;
    private JTextField txtPreco;
    private JTextField txtEstoque;
    private JButton btnSalvar;
    private List<Produto> repositorioProdutos = new ArrayList<>();

    public TelaCadastroProduto() {
        super("Cadastro de Produtos - e-Commerce");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Uso de Gerenciador de Layout (GridBagLayout) - Sem coordenadas fixas (Critério 1)
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNome = new JLabel("Nome do Produto:");
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblNome, gbc);

        txtNome = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(txtNome, gbc);

        JLabel lblPreco = new JLabel("Preço (R$):");
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblPreco, gbc);

        txtPreco = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(txtPreco, gbc);

        JLabel lblEstoque = new JLabel("Estoque:");
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblEstoque, gbc);

        txtEstoque = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(txtEstoque, gbc);

        btnSalvar = new JButton("Cadastrar Produto");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnSalvar, gbc);

        // Registro do Listener (Padrão Observer)
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processarCadastro();
            }
        });

        add(panel);
    }

    private void processarCadastro() {
        // Captura e tradução de exceções sem repetir regras de validação na tela (Critério 3 e 6)
        try {
            Produto produto = ProdutoFactory.criarProduto(
                txtNome.getText(),
                txtPreco.getText(),
                txtEstoque.getText()
            );

            repositorioProdutos.add(produto);
            JOptionPane.showMessageDialog(this,
                    "Produto '" + produto.getNome() + "' cadastrado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparCampos();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtPreco.setText("");
        txtEstoque.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaCadastroProduto().setVisible(true);
        });
    }
}

/*
 ===============================================================================
 PASSO 3: AUTOAVALIAÇÃO (10 MINUTOS)
 ===============================================================================
 1. Critérios atingidos:
    - Critério 1: Formulário montado com GridBagLayout sem uso de coordenadas fixas.
    - Critério 2: Classe de domínio Produto valida nome, preço e estoque lançando exceções.
    - Critério 3: O ouvinte captura as exceções e exibe via JOptionPane sem duplicar validações na tela.
    - Critério 4: Aplicados os padrões Factory Method e Observer.
    - Critério 5: Padrões documentados com nome, problema, solução e consequências.
    - Critério 6: Entradas válidas persistem no domínio e inválidas geram mensagem sem travar a janela.

 2. Trecho que deu mais trabalho e solução:
    - O manuseio dos layouts com GridBagLayout exigiu ajuste nos espaçamentos (Insets) para manter a janela esteticamente agradável e responsiva.

 3. Análise das consequências dos Padrões no Código:
    - As consequências escritas realmente se confirmaram: a tela permaneceu limpa e focada em exibir os componentes e tratar diálogos de erro, deixando a instanciação para a fábrica e a regra de negócio para a classe de domínio.

 4. Uso de Inteligência Artificial:
    - Não foi utilizada ferramenta de IA nesta resolução.
 ===============================================================================
*/
