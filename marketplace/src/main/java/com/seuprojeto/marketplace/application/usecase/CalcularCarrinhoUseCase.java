package com.seuprojeto.marketplace.application.usecase;

import java.math.BigDecimal;
import java.util.List;

import com.seuprojeto.marketplace.application.dto.SelecaoCarrinho;
import com.seuprojeto.marketplace.domain.model.Produto;
import com.seuprojeto.marketplace.domain.model.ResumoCarrinho;
import com.seuprojeto.marketplace.domain.repository.ProdutoRepositorio;

public class CalcularCarrinhoUseCase {

    private final ProdutoRepositorio produtoRepositorio;

    public CalcularCarrinhoUseCase(ProdutoRepositorio produtoRepositorio) {
        this.produtoRepositorio = produtoRepositorio;
    }

    public ResumoCarrinho executar(List<SelecaoCarrinho> selecoes) {

        BigDecimal subtotal = BigDecimal.ZERO;
        int quantidadeTotal = 0;
        BigDecimal percentualCategoria = BigDecimal.ZERO;

        for (SelecaoCarrinho selecao : selecoes) {

            Produto produto = produtoRepositorio
                    .findById(selecao.getIdProduto())
                    .orElseThrow();

            int quantidade = selecao.getQuantidade();

            subtotal = subtotal.add(
                    produto.getPreco().multiply(BigDecimal.valueOf(quantidade))
            );

            quantidadeTotal += quantidade;

            BigDecimal percentualItem = switch (produto.getCategoriaProduto()) {
                case CAPINHA -> new BigDecimal("3");
                case CARREGADOR -> new BigDecimal("5");
                case FONE -> new BigDecimal("3");
                case PELICULA -> new BigDecimal("2");
                case SUPORTE -> new BigDecimal("2");
            };

            percentualCategoria = percentualCategoria.add(
                    percentualItem.multiply(BigDecimal.valueOf(quantidade))
            );
        }

        BigDecimal percentualQuantidade = BigDecimal.ZERO;

        if (quantidadeTotal == 2) {
            percentualQuantidade = new BigDecimal("5");
        } else if (quantidadeTotal == 3) {
            percentualQuantidade = new BigDecimal("7");
        } else if (quantidadeTotal >= 4) {
            percentualQuantidade = new BigDecimal("10");
        }

        BigDecimal percentualTotal = percentualQuantidade.add(percentualCategoria);

        if (percentualTotal.compareTo(new BigDecimal("25")) > 0) {
            percentualTotal = new BigDecimal("25");
        }

        BigDecimal valorDesconto = subtotal
                .multiply(percentualTotal)
                .divide(new BigDecimal("100"));

        return new ResumoCarrinho(subtotal, valorDesconto);
    }
}