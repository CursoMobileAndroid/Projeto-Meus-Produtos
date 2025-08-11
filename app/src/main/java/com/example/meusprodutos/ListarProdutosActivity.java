package com.example.meusprodutos;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ListarProdutosActivity extends AppCompatActivity {

    private ListView listProdutos;

    private DatabaseHelper dbHelper;

    protected  void onCreate(Bundle saveIntanceState){
        super.onCreate(saveIntanceState);
        setContentView(R.layout.activity_listar_produtos);

        listProdutos = findViewById(R.id.listProdutos);

        dbHelper = new DatabaseHelper(this);

        carregarProdutos();

    }

    private void carregarProdutos(){
        List<Produto> produtos = dbHelper.listarProdutos();
        ProdutoAdapter adapter = new ProdutoAdapter(this, produtos);

        listProdutos.setAdapter(adapter);


    }
}
