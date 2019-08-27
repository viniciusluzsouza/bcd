package exemplo03;

import exemplo02.db.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Principal {

    public void listar() {

        String query = "SELECT * FROM Pessoa";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Nome: " + rs.getString("Nome"));
                System.out.println("Peso: " + rs.getDouble("Peso"));
                System.out.println("Peso: " + rs.getInt("Altura"));
                System.out.println("Nome: " + rs.getString("Email"));
                System.out.println("-------------------------");
            }

            rs.close();

        } catch (Exception e) {
            System.out.println("erro:" + e.toString());
        }
    }

    public void inserir() {
        String nome = "Maria";
        double peso = 60;
        int altura = 164;
        String email = "maria@email.com";

        String query = "INSERT INTO Pessoa (nome,peso,altura,email) " +
                "VALUES (?,?,?,?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(query)) {

            stmt.setString(1, nome);
            stmt.setDouble(2, peso);
            stmt.setInt(3, altura);
            stmt.setString(4, email);

            stmt.executeUpdate(query);

        } catch (Exception e) {
            System.out.println("erro: " + e.toString());
        }

    }

    public static void main(String[] args) {
        Principal principal = new Principal();
        principal.listar();
    }

}
