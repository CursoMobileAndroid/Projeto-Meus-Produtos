
// Essa é a "caixinha" que guarda a classe e outras relacionadas no projeto
package com.example.meusprodutos;
// Importa funcionalidades do Android que serão usadas na tela
import android.Manifest; // Permissões (como câmera e galeria)
import android.app.AlertDialog; // Janela de opções
import android.content.Intent; // Abrir outra tela
import android.content.pm.PackageManager; // Verifica se tem permissão
import android.graphics.BitmapFactory; // Mostrar imagem da câmera
import android.net.Uri; // Caminho da imagem
import android.os.Build; // Saber qual versão do Android
import android.os.Bundle; // Dados da tela
import android.provider.MediaStore; // Para acessar galeria e câmera
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Classe principal da tela de "Cadastrar Produto"
public class CadastrarProdutoActivity extends AppCompatActivity {

    // Números que usamos para identificar ações (tirar foto, abrir galeria etc.)
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALERIA = 101;
    private static final int REQUEST_PERMISSOES = 200;

    // Campos da tela onde o usuário digita ou vê algo
    private EditText editNome, editPreco, editDescricao, editQuantidade;
    private Button btnSalvar;
    private ImageView imgProduto;

    // Guardam a imagem que o usuário escolheu ou tirou
    private Uri imagemSelecionadaUri;
    private String caminhoImagemAtual;

    // Ajudante para salvar e buscar dados no banco de dados
    private DatabaseHelper dbHelper;

    // Método chamado quando a tela é criada
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_produtos); // Define qual layout será usado na tela

        dbHelper = new DatabaseHelper(this); // Inicia o banco de dados

        // Liga os componentes visuais aos objetos do código
        editNome = findViewById(R.id.editNome);
        editPreco = findViewById(R.id.editPreco);
        editDescricao = findViewById(R.id.editDescricao);
        editQuantidade = findViewById(R.id.editQuantidade);
        btnSalvar = findViewById(R.id.btnSalvar);
        // imgProduto = findViewById(R.id.imgProduto;

        // Quando o usuário clica na imagem, mostramos um menu com opções
        imgProduto.setOnClickListener(view -> mostrarDialogoImagem());

        // Quando o botão "Salvar" é clicado
        btnSalvar.setOnClickListener(view -> {
            // Pegamos o que o usuário digitou nos campos
            String nome = editNome.getText().toString().trim();
            String precoStr = editPreco.getText().toString().trim();
            String descricao = editDescricao.getText().toString().trim();
            String quantidadeStr = editQuantidade.getText().toString().trim();

            // Verificamos se os campos obrigatórios foram preenchidos
            if (nome.isEmpty() || precoStr.isEmpty() || quantidadeStr.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convertemos os valores para número
            double preco;
            int quantidade;
            try {
                preco = Double.parseDouble(precoStr);
                quantidade = Integer.parseInt(quantidadeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Informe preço e quantidade válidos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Se tiver imagem, pegamos o caminho
            String imagem = imagemSelecionadaUri != null ? imagemSelecionadaUri.toString() : "";
// -> Cria uma variável chamada "imagem". Se o usuário já escolheu/fez uma foto (imagemSelecionadaUri != null),
//    pega o endereço/identificador dessa imagem como texto. Se não, deixa a variável vazia ("").

// Salvamos no banco de dados
            boolean sucesso = dbHelper.adicionarProduto(nome, preco, descricao, quantidade, imagem);
// -> Chama um método (adicionarProduto) do helper do banco de dados para gravar os dados do produto.
//    Passa nome, preço, descrição, quantidade e o caminho/URI da imagem.
//    O método retorna true se deu certo e false se deu errado — isso fica na variável "sucesso".

            if (sucesso) {
                Toast.makeText(this, "Produto salvo com sucesso", Toast.LENGTH_SHORT).show();
                // -> Mostra uma mensagem curta na tela dizendo que o produto foi salvo com sucesso.
                finish(); // Fecha a tela depois de salvar
                // -> Fecha a Activity atual (volta para a tela anterior).
            } else {
                Toast.makeText(this, "Erro ao salvar produto", Toast.LENGTH_SHORT).show();
                // -> Se deu errado ao salvar, mostra uma mensagem de erro para o usuário.
            }
        }); // <- provável fechamento do listener (por exemplo, fechamento do setOnClickListener)
// -> Fecha a função anônima que estava lidando com o clique ou ação que chamou esse trecho.
    } // fecha o método atual (provavelmente onCreate ou o método onde estava o listener)

    // Abre uma janela com as opções: tirar foto, escolher da galeria ou remover imagem
    private void mostrarDialogoImagem() {
        // -> Início do método que cria e mostra uma caixa de diálogo com 3 opções para a imagem.
        String[] opcoes = {"Tirar Foto", "Escolher da Galeria", "Remover Imagem"};
        // -> Define as três opções que vão aparecer no diálogo.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // -> Cria um construtor de diálogo (AlertDialog) usando a Activity atual (this).

        builder.setTitle("Imagem do Produto");
        // -> Define o título da janela de diálogo.

        builder.setItems(opcoes, (dialog, which) -> {
            // -> Define o que acontece quando o usuário toca em uma das opções.
            //    "which" é o índice da opção escolhida: 0 = primeira, 1 = segunda, 2 = terceira.
            switch (which) {
                case 0:
                    verificarPermissoesCamera(); // Vai tirar foto
                    // -> Se escolher "Tirar Foto", chama método que verifica permissão e abre a câmera.
                    break;
                case 1:
                    verificarPermissaoGaleria(); // Vai escolher da galeria
                    // -> Se escolher "Escolher da Galeria", chama método que verifica permissão e abre a galeria.
                    break;
                case 2:
                    imgProduto.setImageResource(R.drawable.logorounded); // Volta para imagem padrão
                    // -> Se escolher "Remover Imagem", define a imagem do produto para a imagem padrão do app.
                    imagemSelecionadaUri = null;
                    // -> Limpa a variável que guardava a imagem selecionada (não há mais imagem).
                    break;
            }
        });
        builder.show();
        // -> Exibe a janela de diálogo na tela.
    }

    // Verifica se o app tem permissão para usar a câmera
    private void verificarPermissoesCamera() {
        // -> Método que verifica se o usuário já deu permissão para a câmera.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // -> Se a permissão NÃO foi concedida...
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSOES);
            // -> ...pedimos a permissão ao usuário (isso abre a janela do sistema pedindo autorização).
        } else {
            abrirCamera(); // Já tem permissão
            // -> Se já tem permissão, chama o método que abre a câmera.
        }
    }

    // Verifica se pode abrir a galeria (depende da versão do Android)
    private void verificarPermissaoGaleria() {
        // -> Em algumas versões antigas do Android é preciso pedir permissão para acessar arquivos.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // -> Se a versão do Android for anterior ao Tiramisu (Android 13), então...
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // -> ...verifica se a permissão de leitura do armazenamento foi concedida.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSOES);
                // -> Se não foi, pede a permissão ao usuário.
                return;
                // -> Para não continuar abrindo a galeria sem permissão; o resultado vem em onRequestPermissionsResult.
            }
        }
        abrirGaleria();
        // -> Se está em versão nova ou já tem permissão, abre a galeria de imagens.
    }

    // Abre a câmera do celular
    private void abrirCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // -> Cria uma "intenção" para abrir o app de câmera do aparelho.

        if (intent.resolveActivity(getPackageManager()) != null) {
            // -> Verifica se existe algum app que consiga tratar essa intenção de câmera.
            File fotoArquivo = null;
            try {
                fotoArquivo = criarArquivoImagem(); // Cria um arquivo para salvar a foto
                // -> Tenta criar um arquivo temporário onde a foto será gravada.
            } catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(this, "Erro ao criar arquivo de imagem", Toast.LENGTH_SHORT).show();
                return;
                // -> Se não conseguir criar o arquivo, mostra erro e encerra o processo.
            }

            if (fotoArquivo != null) {
                Uri fotoURI = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", fotoArquivo);
                // -> Converte o arquivo em um "URI" protegido via FileProvider. Isso permite que outros apps
                //    (a câmera) escrevam nesse arquivo sem problemas de permissões inseguras.

                imagemSelecionadaUri = fotoURI;
                // -> Salva esse URI na variável para usar depois (ex.: mostrar a imagem ou salvar caminho).

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoURI); // Diz onde salvar
                // -> Diz ao app de câmera para salvar a foto exatamente nesse arquivo que criamos.

                startActivityForResult(intent, REQUEST_CAMERA); // Abre a câmera
                // -> Abre a câmera e espera um retorno (quando o usuário tirar a foto o app volta para nós).
            }
        } else {
            Toast.makeText(this, "Nenhum app de câmera disponível", Toast.LENGTH_SHORT).show();
            // -> Se não houver app de câmera no aparelho, mostra uma mensagem ao usuário.
        }
    }

    // Abre a galeria de imagens do celular
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // -> Cria uma intenção para "escolher" (pick) uma imagem da galeria.

        intent.setType("image/*");
        // -> Informa que só queremos arquivos do tipo imagem.

        startActivityForResult(intent, REQUEST_GALERIA);
        // -> Abre a galeria (ou app de fotos) e espera o usuário escolher algo.
    }

    // Cria um arquivo temporário para salvar a imagem tirada pela câmera
    private File criarArquivoImagem() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        // -> Gera uma string com data e hora (ex.: 20250807_131501) para deixar o nome único.

        String nomeImagem = "JPEG_" + timeStamp + "_";
        // -> Prefixo do nome do arquivo: "JPEG_20250807_131501_".

        File diretorio = getExternalFilesDir("Pictures");
        // -> Pega a pasta do app para guardar imagens (espaço externo, mas privado ao app).

        File imagem = File.createTempFile(nomeImagem, ".jpg", diretorio);
        // -> Cria um arquivo temporário no diretório informado, com extensão .jpg.

        caminhoImagemAtual = imagem.getAbsolutePath(); // Guarda o caminho da imagem
        // -> Salva o caminho completo do arquivo em uma variável (útil para abrir depois).

        return imagem;
        // -> Retorna o arquivo criado para quem chamou o método.
    }

    // Quando a câmera ou galeria retorna com uma imagem
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // -> Método chamado quando um app que abrimos (câmera/galeria) termina e volta para nós.

        if (resultCode == RESULT_OK) {
            // -> Só seguimos se a ação foi bem-sucedida (usuário não cancelou).
            if (requestCode == REQUEST_GALERIA && data != null && data.getData() != null) {
                // -> Se veio da galeria e o Intent tem um URI de imagem...
                imagemSelecionadaUri = data.getData();
                // -> Guardamos o URI da imagem escolhida.

                imgProduto.setImageURI(imagemSelecionadaUri); // Mostra a imagem escolhida
                // -> Mostra a imagem no ImageView (imgProduto).
            } else if (requestCode == REQUEST_CAMERA) {
                // -> Se o retorno foi da câmera (nós pedimos para salvar no arquivo criado)...
                imagemSelecionadaUri = Uri.fromFile(new File(caminhoImagemAtual));
                // -> Criamos um URI a partir do caminho que salvamos antes.

                imgProduto.setImageBitmap(BitmapFactory.decodeFile(caminhoImagemAtual)); // Mostra imagem da câmera
                // -> Carrega a imagem do arquivo e mostra no ImageView.
                //    (observação: imagens grandes podem precisar ser redimensionadas para não travar o app).
            }
        }
    }

    // Quando o usuário responde à solicitação de permissão
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // -> Método chamado quando o usuário aceita ou nega uma permissão solicitada.

        if (requestCode == REQUEST_PERMISSOES) {
            // -> Verifica se essa resposta é referente ao nosso pedido de permissão (usando o mesmo código).
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // -> Se o usuário concedeu a permissão...
                if (permissions[0].equals(Manifest.permission.CAMERA)) {
                    abrirCamera();
                    // -> ...e era permissão da câmera, abre a câmera.
                } else if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    abrirGaleria();
                    // -> ...ou se era permissão para leitura de arquivos, abre a galeria.
                }
            } else {
                Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
                // -> Se o usuário negou a permissão, mostra uma mensagem informando que foi negado.
            }
        }
    }
}