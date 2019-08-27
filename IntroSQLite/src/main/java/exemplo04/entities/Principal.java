package exemplo04.entities;

import exemplo04.PessoaDAO;

import java.util.List;

public class Principal {

    public static void main(String[] args) {
        PessoaDAO pessoaDao = new PessoaDAO();
        List<Pessoa> pessoas = pessoaDao.listarTodasPessoas();

        for (Pessoa p : pessoas) {
            System.out.println(p.toString());
        }

        System.out.println("==================");
        Pessoa pessoa = pessoaDao.obterPessoa(1);
        pessoa.toString();
    }

}
