package com.example.meusprodutos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

private Button btnCadastrar, btnListar, btnDeletar, btnAtualizar;



    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_home);

        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnListar = findViewById(R.id.btnListar);
        btnDeletar = findViewById(R.id.btnDeletar);
        btnAtualizar = findViewById(R.id.btnAtualizar);

        btnCadastrar.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, CadastrarProdutoActivity.class);
            startActivity(intent);
        });

        btnListar.setOnClickListener(view ->{
            Intent intent = new Intent(HomeActivity.this, ListarProdutosActivity.class);
            startActivity(intent);

        });
        btnDeletar.setOnClickListener(view ->{
            Intent intent = new Intent(HomeActivity.this, DeletarProdutoActivity.class);
            startActivity(intent);
        });

    }
}
