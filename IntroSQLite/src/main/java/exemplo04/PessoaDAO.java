package exemplo04;

import exemplo02.db.ConnectionFactory;
import exemplo04.entities.Pessoa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {

    public boolean inserir(Pessoa p) {
        String query = "INSERT INTO Pessoa (nome,peso,altura,email) " +
                "VALUES (?,?,?,?)";
        boolean inseriu = false;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(query)) {

            stmt.setString(1, p.getNome());
            stmt.setDouble(2,  p.getPeso());
            stmt.setInt(3, p.getAltura());
            stmt.setString(4, p.getEmail());

            if (stmt.executeUpdate() > 0)
                inseriu = true;

        } catch (Exception e) {
            System.out.println("erro: " + e.toString());
        }

        return inseriu;
    }

    public Pessoa obterPessoa(int idPessoa) {
        String query = "SELECT * FROM Pessoa WHERE idPessoa = ?";
        Pessoa pessoa = null;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(query)) {

            stmt.setInt(1, idPessoa);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nome = rs.getString("Nome");
                String email = rs.getString("Email");
                Double peso = rs.getDouble("Peso");
                int altura = rs.getInt("Altura");
                pessoa = new Pessoa(nome, email, peso, altura);
            }
            rs.close();

        } catch (Exception e) {
            System.out.println("erro:" + e.toString());
        }

        return pessoa;
    }

    public List<Pessoa> listarTodasPessoas() {
        List<Pessoa> pessoas = new ArrayList<>();
        String query = "SELECT * FROM Pessoa";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nome = rs.getString("Nome");
                String email = rs.getString("Email");
                Double peso = rs.getDouble("Peso");
                int altura = rs.getInt("Altura");
                Pessoa p = new Pessoa(nome, email, peso, altura);
                pessoas.add(p);
            }
            rs.close();

        } catch (Exception e) {
            System.out.println("erro:" + e.toString());
        }
        return pessoas;
    }

    public int atualizarPessoa(Pessoa p) {
        int atualizados = 0;
        String query = "UPDATE Pessoa "
                + "SET Nome = ?, "
                + "Peso = ?, "
                + "Altura = ?, "
                + "Email = ? "
                + "WHERE id = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(query)) {

            stmt.setString(1, p.getNome());
            stmt.setDouble(2,  p.getPeso());
            stmt.setInt(3, p.getAltura());
            stmt.setString(4, p.getEmail());
            stmt.setInt(5, p.getIdPessoa());

            atualizados = stmt.executeUpdate(query);

        } catch (Exception e) {
            System.out.println("erro: " + e.toString());
        }

        return atualizados;
    }

    public int atualizarVariasPessoas(String query) {
        int atualizados = 0;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(query)) {

            atualizados = stmt.executeUpdate(query);

        } catch (Exception e) {
            System.out.println("erro: " + e.toString());
        }

        return atualizados;
    }

    public int apagarPessoa(int idPessoa) {
        int apagados = 0;
        String query = "DELETE FROM Pessoa WHERE idPessoa = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(query)) {

            stmt.setInt(1, idPessoa);
            apagados = stmt.executeUpdate(query);

        } catch (Exception e) {
            System.out.println("erro: " + e.toString());
        }

        return apagados;
    }
}
