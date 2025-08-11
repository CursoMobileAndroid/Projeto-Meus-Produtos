package com.example.meusprodutos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// Classe responsável por criar e gerenciar o banco de dados SQLite
public class DatabaseHelper extends SQLiteOpenHelper {
    // Nome do arquivo do banco de dados
    private static final String DATABASE_NAME = "produtos.db";

    // Versão do banco de dados (importante para atualizações)
    private static final int DATABASE_VERSION = 2;

    // Nome da tabela e nomes das colunas
    private static final String TABLE_PRODUTOS = "produtos";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOME = "nome";
    private static final String COLUMN_PRECO = "preco";
    private static final String COLUMN_DESCRICAO = "descricao";
    private static final String COLUMN_QUANTIDADE = "quantidade";
    private static final String COLUMN_IMAGEM = "imagem"; // Caminho da imagem associada ao produto

    // Construtor da classe
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Método chamado automaticamente na criação do banco de dados
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Comando SQL para criar a tabela "produtos" com suas colunas
        String createTable = "CREATE TABLE " + TABLE_PRODUTOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // ID auto incrementável
                COLUMN_NOME + " TEXT, " +         // Nome do produto
                COLUMN_PRECO + " REAL, " +        // Preço (número com casas decimais)
                COLUMN_DESCRICAO + " TEXT, " +    // Descrição do produto
                COLUMN_QUANTIDADE + " INTEGER, " +// Quantidade disponível
                COLUMN_IMAGEM + " TEXT)";         // Caminho da imagem salva
        db.execSQL(createTable); // Executa o comando SQL
    }

    // Método chamado quando a versão do banco é alterada
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Se o banco for atualizado da versão 1 para 2, adiciona a coluna "imagem"
        if (oldVersion < 2) {
            String alterTable = "ALTER TABLE " + TABLE_PRODUTOS + " ADD COLUMN " + COLUMN_IMAGEM + " TEXT";
            db.execSQL(alterTable); // Executa a alteração
        }
    }

    // Método para adicionar um novo produto ao banco
    public boolean adicionarProduto(String nome, double preco, String descricao, int quantidade, String imagem) {
        // Validações simples dos dados antes de inserir
        if (nome == null || nome.isEmpty()) return false;
        if (preco <= 0) return false;
        if (descricao == null || descricao.isEmpty()) return false;
        if (quantidade < 0) return false;

        // Abertura do banco em modo de escrita
        SQLiteDatabase db = this.getWritableDatabase();

        // Preenche os valores que serão inseridos
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME, nome);
        values.put(COLUMN_PRECO, preco);
        values.put(COLUMN_DESCRICAO, descricao);
        values.put(COLUMN_QUANTIDADE, quantidade);
        values.put(COLUMN_IMAGEM, imagem); // Caminho da imagem

        // Insere os dados na tabela e retorna o ID inserido
        long result = db.insert(TABLE_PRODUTOS, null, values);

        db.close(); // Fecha o banco

        // Retorna true se a inserção foi bem-sucedida
        return result != -1;
    }

    // Método para buscar um produto pelo ID
    public Produto getProdutoById(int id) {
        if (id <= 0) return null; // Validação

        SQLiteDatabase db = this.getReadableDatabase();

        // Realiza a consulta SQL
        Cursor cursor = db.query(
                TABLE_PRODUTOS,                 // Tabela
                null,                           // Todas as colunas
                COLUMN_ID + "=?",               // Condição WHERE
                new String[]{String.valueOf(id)}, // Valor do parâmetro
                null, null, null
        );

        // Se encontrou um resultado
        if (cursor != null && cursor.moveToFirst()) {
            // Verifica e pega o caminho da imagem, se existir
            String imagem = cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_IMAGEM)) ? null :
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEM));

            // Cria um objeto Produto com os dados do banco
            Produto produto = new Produto(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRECO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTIDADE)),
                    imagem
            );

            cursor.close(); // Fecha o cursor
            db.close();     // Fecha o banco
            return produto;
        } else {
            if (cursor != null) cursor.close();
            db.close();
            return null; // Produto não encontrado
        }
    }

    // Método para atualizar um produto existente
    public boolean atualizarProduto(int id, String nome, double preco, String descricao, int quantidade, String imagem) {
        if (id <= 0 || nome == null || nome.isEmpty() || preco <= 0 || descricao == null || descricao.isEmpty() || quantidade < 0) {
            return false; // Validação
        }

        SQLiteDatabase db = this.getWritableDatabase();

        // Preenche os novos valores
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME, nome);
        values.put(COLUMN_PRECO, preco);
        values.put(COLUMN_DESCRICAO, descricao);
        values.put(COLUMN_QUANTIDADE, quantidade);
        values.put(COLUMN_IMAGEM, imagem);

        // Atualiza o produto onde o ID bate
        int rowsAffected = db.update(TABLE_PRODUTOS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});

        db.close(); // Fecha o banco

        // Retorna true se alguma linha foi alterada
        return rowsAffected > 0;
    }

    // Método para listar todos os produtos cadastrados
    public List<Produto> listarProdutos() {
        List<Produto> produtos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para pegar todos os produtos
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUTOS, null);

        // Enquanto houver registros no resultado
        while (cursor.moveToNext()) {
            String imagem = cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_IMAGEM)) ? null :
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEM));

            Produto produto = new Produto(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRECO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTIDADE)),
                    imagem
            );

            produtos.add(produto); // Adiciona o produto à lista
        }

        cursor.close(); // Fecha o cursor
        db.close();     // Fecha o banco

        return produtos; // Retorna a lista com todos os produtos encontrados
    }

    // Método para excluir um produto pelo ID
    public void deletarProduto(int id) {
        if (id <= 0) return; // Verificação do ID

        SQLiteDatabase db = this.getWritableDatabase();

        // Deleta o produto com base no ID
        db.delete(TABLE_PRODUTOS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});

        db.close(); // Fecha o banco após a operação
    }
}
