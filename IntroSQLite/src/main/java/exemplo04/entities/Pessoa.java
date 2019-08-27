package exemplo04.entities;

public class Pessoa {

    private int idPessoa;
    private String nome;
    private String email;
    private double peso;
    private int altura;

    public Pessoa() {

    }

    public Pessoa(String nome, String email, double peso, int altura) {
        this.nome = nome;
        this.email = email;
        this.peso = peso;
        this.altura = altura;
    }

    public int getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(int idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "idPessoa=" + idPessoa +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", peso=" + peso +
                ", altura=" + altura +
                '}';
    }

}
