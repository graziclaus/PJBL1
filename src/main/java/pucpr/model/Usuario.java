package pucpr.model;

public class Usuario {

    private String id;
    private String nomeCompleto;
    private String email;
    private String senha;
    private String role;

    public Usuario(String id, String nomeCompleto, String email, String senha, String role){

        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senha = senha;
        this.role = role;

    }

    public Usuario() {}

    public String getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public String getRole() { return role; }

    public void setId(String id) { this.id = id; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setRole(String role) { this.role = role; }
}
