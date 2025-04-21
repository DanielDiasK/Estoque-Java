package service;

import model.Produto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EstoqueManager {
    private List<Produto> produtos;
    private static final String ARQUIVO_DADOS = "estoque.dat";

    public EstoqueManager() {
        this.produtos = new ArrayList<>();
        carregarDados();
        if (this.produtos == null) { // Garantir que produtos nunca seja null
            this.produtos = new ArrayList<>();
        }
    }

    public void adicionarProduto(Produto produto) {
        produtos.add(produto);
        salvarDados();
    }

    public void removerProduto(Produto produto) {
        if (produto != null) {
            produtos.remove(produto);
            salvarDados();
        }
    }

    public void salvarDados() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(ARQUIVO_DADOS))) {
            oos.writeObject(produtos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarDados() {
        File arquivo = new File(ARQUIVO_DADOS);
        if (arquivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(ARQUIVO_DADOS))) {
                produtos = (List<Produto>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                produtos = new ArrayList<>();
            }
        }
    }

    public List<Produto> getProdutos() {
        return produtos;
    }
}
