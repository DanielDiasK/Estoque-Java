package model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Produto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nome;
    private BigDecimal preco;
    private int quantidade;
    
    public Produto(String codigo, String nome, BigDecimal preco, int quantidade) {
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
    }
    
    // Getters
    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public BigDecimal getPreco() { return preco; }
    public int getQuantidade() { return quantidade; }
    
    // Setters
    public void setNome(String nome) { 
        this.nome = nome; 
    }
    
    public void setPreco(BigDecimal preco) { 
        this.preco = preco; 
    }
    
    public void setQuantidade(int quantidade) { 
        this.quantidade = quantidade; 
    }
}
