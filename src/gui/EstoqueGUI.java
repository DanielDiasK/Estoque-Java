package gui;

import model.Produto;
import service.EstoqueManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

public class EstoqueGUI extends JFrame {
    private EstoqueManager manager;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public EstoqueGUI() {
        manager = new EstoqueManager();
        configurarJanela();
        criarComponentes();
    }

    private void configurarJanela() {
        setTitle("Sistema de Estoque");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));
    }

    private void criarComponentes() {
        // Painel superior com botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdicionar = new JButton("Adicionar Produto");
        JButton btnRemover = new JButton("Remover Produto");
        
        btnAdicionar.addActionListener(e -> mostrarDialogoAdicionar());
        btnRemover.addActionListener(e -> removerProdutoSelecionado());
        
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnRemover);
        
        // Tabela de produtos
        String[] colunas = {"Código", "Nome", "Preço", "Quantidade"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabela = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabela);
        
        add(painelBotoes, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        atualizarTabela();
    }

    private void mostrarDialogoAdicionar() {
        JTextField tfCodigo = new JTextField();
        JTextField tfNome = new JTextField();
        JTextField tfPreco = new JTextField();
        JTextField tfQuantidade = new JTextField();

        Object[] campos = {
            "Código:", tfCodigo,
            "Nome:", tfNome,
            "Preço:", tfPreco,
            "Quantidade:", tfQuantidade
        };

        int resultado = JOptionPane.showConfirmDialog(this, campos, 
                "Adicionar Produto", JOptionPane.OK_CANCEL_OPTION);
                
        if (resultado == JOptionPane.OK_OPTION) {
            try {
                Produto novoProduto = new Produto(
                    tfCodigo.getText(),
                    tfNome.getText(),
                    new BigDecimal(tfPreco.getText()),
                    Integer.parseInt(tfQuantidade.getText())
                );
                manager.adicionarProduto(novoProduto);
                atualizarTabela();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Dados inválidos!");
            }
        }
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        for (Produto p : manager.getProdutos()) {
            modeloTabela.addRow(new Object[]{
                p.getCodigo(),
                p.getNome(),
                p.getPreco(),
                p.getQuantidade()
            });
        }
    }

    private void removerProdutoSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            String codigo = (String) tabela.getValueAt(linha, 0);
            manager.getProdutos().removeIf(p -> p.getCodigo().equals(codigo));
            atualizarTabela();
        }
    }
}
