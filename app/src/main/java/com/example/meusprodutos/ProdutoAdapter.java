package com.example.meusprodutos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class ProdutoAdapter extends ArrayAdapter<Produto> {
    public ProdutoAdapter(Context context, List<Produto> produtos){
        super(context,0, produtos);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_produto, parent, false);
        }

        Produto produto = getItem(position);

        TextView nomeProduto = convertView.findViewById(R.id.txtNomeProduto);
        TextView precoProduto = convertView.findViewById(R.id.txtPrecoProduto);
        TextView quantidadeProduto = convertView.findViewById(R.id.txtQuantidadeProduto);

        if (produto != null){
            nomeProduto.setText(produto.getNome());
            precoProduto.setText(String.format("R$ %.2f", produto.getPreco()));
            quantidadeProduto.setText("Quantidade: " + produto.getQuantidade());
        }
        return convertView;
    }
}
