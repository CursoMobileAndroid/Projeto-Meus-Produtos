package com.example.meusprodutos;
// -> Define o pacote onde essa classe está localizada no projeto.

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

// Classe da tela (Activity) que permite deletar produtos do banco
public class DeletarProdutoActivity extends AppCompatActivity {
    // -> "ListView" é o componente da tela que mostra a lista de produtos.
    private ListView listViewProdutos;
    // -> "Button" que, quando clicado, deleta o produto selecionado.
    private Button btnDeletarProduto;
    // -> Objeto que acessa o banco de dados (classe DatabaseHelper).
    private DatabaseHelper dbHelper;
    // -> Guarda qual produto o usuário selecionou na lista.
    private Produto produtoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // -> Método chamado quando a tela é criada.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deletar_produto);
        // -> Define o layout XML que será usado para essa tela.

        listViewProdutos = findViewById(R.id.listViewProdutos);
        // -> Liga a variável "listViewProdutos" ao componente da tela com esse ID.
        btnDeletarProduto = findViewById(R.id.btnDeletarProduto);
        // -> Liga a variável "btnDeletarProduto" ao botão que está no layout.
        dbHelper = new DatabaseHelper(this);
        // -> Cria uma nova instância do helper do banco de dados para manipular produtos.

        carregarProdutos(); // ✅ Chamando o método corretamente
        // -> Chama o método que busca e mostra todos os produtos na lista.

        listViewProdutos.setOnItemClickListener((adapterView, view, position, id) -> {
            // -> Define o que acontece quando o usuário clicar em um produto da lista.
            produtoSelecionado = dbHelper.listarProdutos().get(position);
            // -> Pega o produto que foi clicado (pela posição) e guarda em "produtoSelecionado".
            btnDeletarProduto.setEnabled(true);
            // -> Ativa o botão de deletar (fica clicável).
            listViewProdutos.setItemChecked(position, true); // ✅ Mantém a seleção visual
            // -> Marca visualmente o item na lista como selecionado.
            Toast.makeText(this, "Produto selecionado: " + produtoSelecionado.getNome(), Toast.LENGTH_SHORT).show();
            // -> Mostra uma mensagem com o nome do produto escolhido.
        });

        btnDeletarProduto.setOnClickListener(view -> {
            // -> Define o que acontece quando o botão de deletar for clicado.
            if (produtoSelecionado != null) {
                // -> Só deleta se algum produto estiver selecionado.
                dbHelper.deletarProduto(produtoSelecionado.getId());
                // -> Chama o método do banco que apaga o produto pelo ID.
                Toast.makeText(this, "Produto deletado com sucesso!", Toast.LENGTH_SHORT).show();
                // -> Mostra mensagem dizendo que deletou com sucesso.
                btnDeletarProduto.setEnabled(false);
                // -> Desativa o botão novamente até o usuário selecionar outro produto.
                carregarProdutos();
                // -> Recarrega a lista de produtos para atualizar a tela.
                produtoSelecionado = null;
                // -> Limpa a variável de produto selecionado (nenhum produto selecionado agora).
            }
        });
    }

    // ✅ Método está corretamente FORA do onCreate()
    private void carregarProdutos() {
        // -> Busca todos os produtos cadastrados no banco.
        List<Produto> produtos = dbHelper.listarProdutos();

        // -> Cria um adaptador que transforma a lista de Strings em itens para a ListView.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1);

        // -> Para cada produto encontrado no banco...
        for (Produto p : produtos) {
            // -> Adiciona o nome e o preço dele no adaptador (que vai para a lista).
            adapter.add(p.getNome() + " - R$ " + p.getPreco());
        }

        // -> Define o adaptador na lista para exibir os produtos.
        listViewProdutos.setAdapter(adapter);
        // -> Permite que só um item seja selecionado por vez.
        listViewProdutos.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // ✅ Ativa a seleção de um item

        // -> Se não houver produtos cadastrados, mostra aviso para o usuário.
        if (produtos.isEmpty()) {
            Toast.makeText(this, "Nenhum produto cadastrado.", Toast.LENGTH_SHORT).show();
        }
    }
}
