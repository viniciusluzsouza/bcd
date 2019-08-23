package exemplo01;

import java.sql.*;

public class Principal {

    private final String caminhoDB = "src/main/resources/lab01.db";

    public void inserir() throws Exception {
        Class.forName("org.sqlite.JDBC");

        Connection conexao = DriverManager.getConnection("jdbc:sqlite:" + caminhoDB);
        Statement stmt = conexao.createStatement();

        String nome = "José";
        double peso = 65;
        int altura = 160;
        String email = "jose@email.com";

        String query = "INSERT INTO Pessoa (nome,peso,altura,email)" +
                "VALUES ('"+ nome +"'," +
                peso + "," + altura + "," +
                "'" + email + "'" + ")";

        stmt.executeUpdate(query);
        stmt.close();
        conexao.close();
    }

    public void listar() throws Exception {

        Class.forName("org.sqlite.JDBC");

        Connection conexao = DriverManager.getConnection("jdbc:sqlite:" + caminhoDB);
        Statement stmt = conexao.createStatement();

        String query = "SELECT * FROM Pessoa";

        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            System.out.println("Nome: " + rs.getString("Nome"));
            System.out.println("Peso: " + rs.getDouble("Peso"));
            System.out.println("Peso: " + rs.getInt("Altura"));
            System.out.println("Nome: " + rs.getString("Email"));
            System.out.println("-------------------------");
        }

        rs.close();
        stmt.close();
        conexao.close();

    }

    public void buscarPessoa(String nome) throws Exception {
        Class.forName("org.sqlite.JDBC");

        Connection conexao = DriverManager.getConnection("jdbc:sqlite:" + caminhoDB);
        Statement stmt = conexao.createStatement();

        String query = "SELECT * FROM Pessoa WHERE Nome = " + "'" + nome + "'";

        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            System.out.println("Nome: " + rs.getString("Nome"));
            System.out.println("Peso: " + rs.getDouble("Peso"));
            System.out.println("Peso: " + rs.getInt("Altura"));
            System.out.println("Nome: " + rs.getString("Email"));
            System.out.println("-------------------------");
        }

        rs.close();
        stmt.close();
        conexao.close();
    }

    public static void main(String[] args) throws Exception {

        Principal p = new Principal();

//        p.inserir();
//        p.listar();

        String pessoa = "Juca' OR '1' = '1";
        p.buscarPessoa(pessoa);

    }

}
