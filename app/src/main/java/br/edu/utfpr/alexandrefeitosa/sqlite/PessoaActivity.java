package br.edu.utfpr.alexandrefeitosa.sqlite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import br.edu.utfpr.alexandrefeitosa.sqlite.modelo.Pessoa;
import br.edu.utfpr.alexandrefeitosa.sqlite.persistencia.ConexaoDatabase;
import br.edu.utfpr.alexandrefeitosa.sqlite.utils.UtilsGUI;

public class PessoaActivity extends AppCompatActivity {

    private EditText editTexNome;
    private EditText editTextIdade;

    private int    modo;
    private Pessoa pessoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pessoa);

        editTexNome   = (EditText) findViewById(R.id.editTextNome);
        editTextIdade = (EditText) findViewById(R.id.editTextIdade);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        modo = bundle.getInt(PrincipalActivity.MODO);

        if (modo == PrincipalActivity.ALTERAR){

            long id = bundle.getLong(PrincipalActivity.ID);

            ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);
            pessoa = conexao.pessoaDAO.pessoaPorId(id);

            editTexNome.setText(pessoa.getNome());
            editTextIdade.setText(String.valueOf(pessoa.getIdade()));

            setTitle(R.string.alterar_pessoa);

        }else{

            setTitle(R.string.nova_pessoa);
        }
    }

    private void salvar(){
        String nome  = UtilsGUI.validaCampoTexto(this,
                                                 editTexNome,
                                                 R.string.nome_vazio);
        if (nome == null){
            return;
        }

        String txtIdade = UtilsGUI.validaCampoTexto(this,
                                                    editTextIdade,
                                                    R.string.idade_vazia);
        if (txtIdade == null){
            return;
        }

        int idade = Integer.parseInt(txtIdade);

        if (idade <= 0 || idade > 200){
            UtilsGUI.avisoErro(this, R.string.idade_invalida);
            editTextIdade.requestFocus();
            return;
        }

        ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);

        if (modo == PrincipalActivity.NOVO){

            Pessoa pessoa = new Pessoa(nome);
            pessoa.setIdade(idade);

            conexao.pessoaDAO.inserir(pessoa);

        }else{

            pessoa.setNome(nome);
            pessoa.setIdade(idade);

            conexao.pessoaDAO.alterar(pessoa);
        }

        setResult(Activity.RESULT_OK);
        finish();
    }

    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pessoa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemSalvar:
                salvar();
                return true;
            case R.id.menuItemCancelar:
                cancelar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
