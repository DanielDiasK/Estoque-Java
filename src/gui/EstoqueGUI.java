package gui;

import model.Produto;
import service.EstoqueManager;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class EstoqueGUI extends JFrame {
    private EstoqueManager manager;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblTotalProdutos;
    private JLabel lblValorTotal;
    private JTextField campoPesquisa;
    private JPanel painelPesquisa;

    public EstoqueGUI() {
        manager = new EstoqueManager();
        configurarJanela();
        criarComponentes();
        atualizarTabela(); // Adicionar esta linha para atualizar a tabela ao iniciar
    }

    private void configurarJanela() {
        setTitle("Sistema de Estoque");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(240, 242, 245));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void criarComponentes() {
        // Painel de Cards
        JPanel painelCards = criarPainelCards();

        // Painel de botões e pesquisa
        JPanel painelSuperior = new JPanel(new BorderLayout(10, 10));
        painelSuperior.setOpaque(false);

        // Criar painel de pesquisa
        painelPesquisa = criarPainelPesquisa();

        // Painel de botões
        JPanel painelBotoes = criarPainelBotoes();

        // Adicionar painéis ao painel superior
        JPanel painelBotoesePesquisa = new JPanel(new BorderLayout(10, 0));
        painelBotoesePesquisa.setOpaque(false);
        painelBotoesePesquisa.add(painelBotoes, BorderLayout.WEST);
        painelBotoesePesquisa.add(painelPesquisa, BorderLayout.CENTER);

        painelSuperior.add(painelCards, BorderLayout.NORTH);
        painelSuperior.add(painelBotoesePesquisa, BorderLayout.CENTER);

        // Tabela de produtos
        JPanel painelTabela = criarPainelTabela();

        add(painelSuperior, BorderLayout.NORTH);
        add(painelTabela, BorderLayout.CENTER);

        atualizarCards();
    }

    private JPanel criarPainelCards() {
        JPanel painelCards = new JPanel(new GridLayout(1, 2, 15, 0));
        painelCards.setOpaque(false);

        // Card - Total de Produtos
        JPanel cardProdutos = criarCard("Total de Produtos", "0", lblTotalProdutos = new JLabel());

        // Card - Valor Total em Estoque
        JPanel cardValor = criarCard("Valor Total em Estoque", "R$ 0,00", lblValorTotal = new JLabel());

        painelCards.add(cardProdutos);
        painelCards.add(cardValor);

        return painelCards;
    }

    private JPanel criarCard(String titulo, String valor, JLabel labelValor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
                g2d.setColor(new Color(230, 230, 230));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
                g2d.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(new Color(100, 100, 100));
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));

        labelValor.setText(valor);
        labelValor.setForeground(new Color(51, 51, 51));
        labelValor.setFont(new Font("Arial", Font.BOLD, 24));

        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(5));
        card.add(labelValor);

        return card;
    }

    private JPanel criarPainelBotoes() {
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdicionar = criarBotaoEstilizado("Adicionar Produto", new Color(0, 123, 255));
        JButton btnRemover = criarBotaoEstilizado("Remover Produto", new Color(220, 53, 69));

        btnAdicionar.addActionListener(e -> mostrarDialogoAdicionar());
        btnRemover.addActionListener(e -> removerProdutoSelecionado());

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnRemover);

        return painelBotoes;
    }

    private JPanel criarPainelPesquisa() {
        JPanel painel = new JPanel(new BorderLayout(5, 0));
        painel.setOpaque(false);

        campoPesquisa = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        campoPesquisa.setOpaque(false);
        campoPesquisa.setFont(new Font("Arial", Font.PLAIN, 14));
        campoPesquisa.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel lblPesquisa = new JLabel("🔍");
        lblPesquisa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        lblPesquisa.setBorder(new EmptyBorder(0, 10, 0, 0));

        painel.add(lblPesquisa, BorderLayout.WEST);
        painel.add(campoPesquisa, BorderLayout.CENTER);

        campoPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarProdutos(campoPesquisa.getText());
            }
        });

        return painel;
    }

    private void filtrarProdutos(String texto) {
        modeloTabela.setRowCount(0);
        for (Produto p : manager.getProdutos()) {
            if (texto.isEmpty() || 
                p.getCodigo().toLowerCase().contains(texto.toLowerCase()) ||
                p.getNome().toLowerCase().contains(texto.toLowerCase())) {
                modeloTabela.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNome(),
                    NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(p.getPreco()),
                    p.getQuantidade(),
                    ""
                });
            }
        }
    }

    private JPanel criarPainelTabela() {
        String[] colunas = {"Código", "Nome", "Preço", "Quantidade", "Ações"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        
        tabela = new JTable(modeloTabela);
        estilizarTabela(tabela);
        
        tabela.getColumnModel().getColumn(4).setCellRenderer(new ActionButtonRenderer());
        tabela.getColumnModel().getColumn(4).setCellEditor(new ActionButtonEditor(new JCheckBox()));
        
        // Ajustar larguras das colunas
        tabela.getColumnModel().getColumn(0).setPreferredWidth(100);  // Código
        tabela.getColumnModel().getColumn(1).setPreferredWidth(300);  // Nome
        tabela.getColumnModel().getColumn(2).setPreferredWidth(150);  // Preço
        tabela.getColumnModel().getColumn(3).setPreferredWidth(100);  // Quantidade
        tabela.getColumnModel().getColumn(4).setPreferredWidth(120);  // Ações

        JScrollPane scrollPane = new JScrollPane(tabela) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        scrollPane.setBackground(new Color(255, 255, 255));
        
        // Criar painel com borda arredondada
        JPanel painelTabela = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        painelTabela.setOpaque(false);
        painelTabela.setBorder(new EmptyBorder(0, 0, 0, 0));
        painelTabela.add(scrollPane, BorderLayout.CENTER);

        return painelTabela;
    }

    private JButton criarBotaoEstilizado(String texto, Color cor) {
        JButton botao = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(cor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(cor.brighter());
                } else {
                    g2d.setColor(cor);
                }
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g2d.dispose();

                super.paintComponent(g);
            }
        };
        botao.setForeground(Color.WHITE); // Mudando para branco para melhor contraste
        botao.setOpaque(false);
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private void estilizarTabela(JTable tabela) {
        // Configuração básica
        tabela.setRowHeight(45);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 10));
        
        // Estilo do cabeçalho
        JTableHeader header = tabela.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(33, 37, 41));
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBorder(new MatteBorder(0, 0, 2, 0, new Color(233, 236, 239)));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        // Estilo das células
        tabela.setFont(new Font("Arial", Font.PLAIN, 13));
        tabela.setSelectionBackground(new Color(236, 243, 255));
        tabela.setSelectionForeground(new Color(33, 37, 41));
        tabela.setBackground(Color.WHITE);
        
        // Bordas e espaçamento
        tabela.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Estilo alternado das linhas
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                }
                setBorder(new EmptyBorder(8, 15, 8, 15));
                setHorizontalAlignment(column == 2 || column == 3 ? JLabel.CENTER : JLabel.LEFT);
                return c;
            }
        });
    }

    private void mostrarDialogoAdicionar() {
        JDialog dialog = new JDialog(this, "Adicionar Produto", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField tfCodigo = criarCampoEstilizado();
        JTextField tfNome = criarCampoEstilizado();
        JTextField tfPreco = criarCampoEstilizado();
        JTextField tfQuantidade = criarCampoEstilizado();

        // Adiciona campos com labels
        adicionarCampo(painelCampos, "Código:", tfCodigo, gbc, 0);
        adicionarCampo(painelCampos, "Nome:", tfNome, gbc, 1);
        adicionarCampo(painelCampos, "Preço:", tfPreco, gbc, 2);
        adicionarCampo(painelCampos, "Quantidade:", tfQuantidade, gbc, 3);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = criarBotaoEstilizado("Salvar", new Color(40, 167, 69));
        JButton btnCancelar = criarBotaoEstilizado("Cancelar", new Color(108, 117, 125));

        btnSalvar.addActionListener(e -> {
            try {
                Produto novoProduto = new Produto(
                    tfCodigo.getText(),
                    tfNome.getText(),
                    new BigDecimal(tfPreco.getText().replace(",", ".")),
                    Integer.parseInt(tfQuantidade.getText())
                );
                manager.adicionarProduto(novoProduto);
                atualizarTabela();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Por favor, preencha todos os campos corretamente.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        dialog.add(painelCampos, BorderLayout.CENTER);
        dialog.add(painelBotoes, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JTextField criarCampoEstilizado() {
        JTextField campo = new JTextField(15) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(new Color(206, 212, 218));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        campo.setOpaque(false);
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return campo;
    }

    private void adicionarCampo(JPanel panel, String label, JTextField campo, 
            GridBagConstraints gbc, int linha) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.weightx = 0.1;
        JLabel lblCampo = new JLabel(label);
        lblCampo.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblCampo, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.9;
        panel.add(campo, gbc);
    }

    private void editarProdutoSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            String codigo = (String) tabela.getValueAt(linha, 0);
            Produto produto = manager.getProdutos().stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);

            if (produto != null) {
                mostrarDialogoEditar(produto);
            }
        }
    }

    private void mostrarDialogoEditar(Produto produto) {
        JDialog dialog = new JDialog(this, "Editar Produto", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField tfCodigo = criarCampoEstilizado();
        JTextField tfNome = criarCampoEstilizado();
        JTextField tfPreco = criarCampoEstilizado();
        JTextField tfQuantidade = criarCampoEstilizado();

        tfCodigo.setText(produto.getCodigo());
        tfNome.setText(produto.getNome());
        tfPreco.setText(produto.getPreco().toString());
        tfQuantidade.setText(String.valueOf(produto.getQuantidade()));

        tfCodigo.setEditable(false);

        // Adiciona campos com labels
        adicionarCampo(painelCampos, "Código:", tfCodigo, gbc, 0);
        adicionarCampo(painelCampos, "Nome:", tfNome, gbc, 1);
        adicionarCampo(painelCampos, "Preço:", tfPreco, gbc, 2);
        adicionarCampo(painelCampos, "Quantidade:", tfQuantidade, gbc, 3);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = criarBotaoEstilizado("Salvar", new Color(40, 167, 69));
        JButton btnCancelar = criarBotaoEstilizado("Cancelar", new Color(108, 117, 125));

        btnSalvar.addActionListener(e -> {
            try {
                produto.setNome(tfNome.getText());
                produto.setPreco(new BigDecimal(tfPreco.getText().replace(",", ".")));
                produto.setQuantidade(Integer.parseInt(tfQuantidade.getText()));
                
                // Adicionar esta linha para salvar as alterações
                manager.salvarDados();
                
                atualizarTabela();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Por favor, preencha todos os campos corretamente.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        dialog.add(painelCampos, BorderLayout.CENTER);
        dialog.add(painelBotoes, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        for (Produto p : manager.getProdutos()) {
            modeloTabela.addRow(new Object[]{
                p.getCodigo(),
                p.getNome(),
                NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(p.getPreco()),
                p.getQuantidade(),
                ""  // Coluna de ações
            });
        }
        atualizarCards();
    }

    private void atualizarCards() {
        int totalProdutos = manager.getProdutos().size();
        BigDecimal valorTotal = manager.getProdutos().stream()
            .map(p -> p.getPreco().multiply(new BigDecimal(p.getQuantidade())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        lblTotalProdutos.setText(String.valueOf(totalProdutos));
        lblValorTotal.setText(NumberFormat.getCurrencyInstance(new Locale("pt", "BR"))
            .format(valorTotal));
    }

    private void removerProdutoSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            String codigo = (String) tabela.getValueAt(linha, 0);
            // Confirmar antes de remover
            int confirma = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja remover este produto?",
                "Confirmar Remoção",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirma == JOptionPane.YES_OPTION) {
                manager.removerProduto(manager.getProdutos().stream()
                    .filter(p -> p.getCodigo().equals(codigo))
                    .findFirst()
                    .orElse(null));
                atualizarTabela();
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Selecione um produto para remover",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton;
        private JButton deleteButton;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
            setOpaque(true);
            
            editButton = new JButton("✏️");
            deleteButton = new JButton("🗑️");
            
            estilizarBotaoAcao(editButton);
            estilizarBotaoAcao(deleteButton);
            
            add(editButton);
            add(deleteButton);
        }
        
        private void estilizarBotaoAcao(JButton btn) {
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            editButton.setForeground(new Color(0, 123, 255));
            deleteButton.setForeground(new Color(220, 53, 69));
            return this;
        }
    }

    private class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;
        private int currentRow;
        
        public ActionButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            
            editButton = new JButton("✏️");
            deleteButton = new JButton("🗑️");
            
            editButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            deleteButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            
            editButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            deleteButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            editButton.setContentAreaFilled(false);
            deleteButton.setContentAreaFilled(false);
            
            editButton.setFocusPainted(false);
            deleteButton.setFocusPainted(false);
            
            editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            editButton.setForeground(new Color(0, 123, 255));
            deleteButton.setForeground(new Color(220, 53, 69));
            
            editButton.addActionListener(e -> {
                fireEditingStopped();
                editarProduto();
            });
            
            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                removerProduto();
            });
            
            panel.add(editButton);
            panel.add(deleteButton);
        }

        private void editarProduto() {
            String codigo = (String) tabela.getValueAt(currentRow, 0);
            Produto produto = manager.getProdutos().stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
            if (produto != null) {
                mostrarDialogoEditar(produto);
            }
        }

        private void removerProduto() {
            String codigo = (String) tabela.getValueAt(currentRow, 0);
            int confirma = JOptionPane.showConfirmDialog(
                null,
                "Tem certeza que deseja remover este produto?",
                "Confirmar Remoção",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirma == JOptionPane.YES_OPTION) {
                manager.removerProduto(manager.getProdutos().stream()
                    .filter(p -> p.getCodigo().equals(codigo))
                    .findFirst()
                    .orElse(null));
                atualizarTabela();
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
