package br.edu.utfpr.alexandrefeitosa.sqlite;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import br.edu.utfpr.alexandrefeitosa.sqlite.modelo.Pessoa;
import br.edu.utfpr.alexandrefeitosa.sqlite.persistencia.ConexaoDatabase;
import br.edu.utfpr.alexandrefeitosa.sqlite.utils.UtilsGUI;

public class PrincipalActivity extends AppCompatActivity {

    private ListView             listViewPessoa;
    private ArrayAdapter<Pessoa> listaAdapter;

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);
        conexao.pessoaDAO.carregarTudo();

        listViewPessoa = (ListView) findViewById(R.id.listViewPessoas);

        listViewPessoa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Pessoa pessoa = (Pessoa) parent.getItemAtPosition(position);

                abrirPessoa(pessoa);
            }
        });

        popularLista();

        registerForContextMenu(listViewPessoa);
    }

    private void popularLista(){
        ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);

        listaAdapter = new ArrayAdapter<Pessoa>(this,
                             android.R.layout.simple_list_item_1,
                             conexao.pessoaDAO.lista);

        listViewPessoa.setAdapter(listaAdapter);
    }

    private void novaPessoa(){
        Intent intent = new Intent(this, PessoaActivity.class);

        intent.putExtra(MODO, NOVO);

        startActivityForResult(intent, NOVO);
    }

    private void abrirPessoa(Pessoa pessoa){
        Intent intent = new Intent(this, PessoaActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, pessoa.getId());

        startActivityForResult(intent, ALTERAR);
    }

    private void excluirPessoa(final Pessoa pessoa){

        String mensagem = getString(R.string.deseja_realmente_apagar)
                          + "\n" + pessoa.getNome();

        DialogInterface.OnClickListener listener =
            new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {

                   switch(which){
                       case DialogInterface.BUTTON_POSITIVE:
                           ConexaoDatabase conexao =
                                   ConexaoDatabase.getInstance(PrincipalActivity.this);

                           conexao.pessoaDAO.apagar(pessoa);

                           listaAdapter.notifyDataSetChanged();
                           break;
                       case DialogInterface.BUTTON_NEGATIVE:

                           break;
                   }
               }
            };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == NOVO || requestCode == ALTERAR) &&
             resultCode == Activity.RESULT_OK){

            listaAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemNovo:
                novaPessoa();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.pessoa_selecionada, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info =
         (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Pessoa pessoa =
          (Pessoa) listViewPessoa.getItemAtPosition(info.position);

        switch(item.getItemId()){
            case R.id.menuItemAbrir:
                abrirPessoa(pessoa);
                return true;
            case R.id.menuItemApagar:
                excluirPessoa(pessoa);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
