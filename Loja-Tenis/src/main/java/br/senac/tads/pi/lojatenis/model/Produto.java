package br.senac.tads.pi.lojatenis.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String modelo;

    private String avaliacao;

    private String status;

    private BigDecimal preco;

    private int qtd_estoque;

    private String marca;

    private String cor;

    private String genero;

    private String esporte;

    private List<String> tamanhos;

    public Produto() {
        this.tamanhos = new ArrayList<>();
        this.tamanhos.add("36");
        this.tamanhos.add("37");
        this.tamanhos.add("38");
        this.tamanhos.add("39");
        this.tamanhos.add("40");
        this.tamanhos.add("41");
        this.tamanhos.add("42");
        this.tamanhos.add("43");
    }

    public List<String> getTamanhos() {
        return tamanhos;
    }

    public void setTamanhos(List<String> tamanhos) {
        this.tamanhos = tamanhos;
    }

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ElementCollection
    @CollectionTable(name = "Produto_Imagens", joinColumns = @JoinColumn(name = "produto_id"))
    @Column(name = "imagem")
    private List<String> imagens = new ArrayList<>();

    @Column(name = "imagem_padrao")
    private String imagemPadrao;

    public List<String> getImagens() {
        return imagens;
    }

    public void setImagens(List<String> imagens) {
        this.imagens = imagens;
    }

    public String getImagemPadrao() {
        return imagemPadrao;
    }

    public void setImagemPadrao(String imagemPadrao) {
        this.imagemPadrao = imagemPadrao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(String avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getQtd_estoque() {
        return qtd_estoque;
    }

    public void setQtd_estoque(int qtd_estoque) {
        this.qtd_estoque = qtd_estoque;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getEsporte() {
        return esporte;
    }

    public void setEsporte(String esporte) {
        this.esporte = esporte;
    }

}
