package com.example.meusprodutos;

import androidx.annotation.NonNull;

public class Produto {

    private int id;
    private String nome;
    private double preco;
    private String descricao;
    private int quantidade;
    private String imagem;
    public Produto(int id,String nome, double preco,String descricao, int quantidade, String imagem)
    {
    this.id = id;
    this.nome = nome;
    this.preco = preco;
    this.descricao = descricao;
    this.quantidade = quantidade;
    this.imagem = imagem;
    }

    public int getId()
    {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
    public String toString()
    {
        return nome + " - R$ " + preco + " - " + quantidade + "unidades";
    }
}