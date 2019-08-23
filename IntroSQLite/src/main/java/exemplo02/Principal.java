package exemplo02;

import exemplo02.db.ConnectionFactory;

import java.sql.Connection;
import java.sql.Statement;

public class Principal {

    public void inserir() {

        try (Connection conexao = ConnectionFactory.getConnection();
             Statement stmt = conexao.createStatement()) {

            String nome = "Maria";
            double peso = 60;
            int altura = 164;
            String email = "maria@email.com";

            String query = "INSERT INTO Pessoa (nome,peso,altura,email)" +
                    "VALUES ('"+ nome +"'," +
                    peso + "," + altura + "," +
                    "'" + email + "'" + ")";

        } catch (Exception e) {
            System.out.println("erro: " + e.toString());
        }

    }

    public static void main(String[] args) {

    }

}
