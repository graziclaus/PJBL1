package pucpr.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import pucpr.model.Usuario;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    private final String FILE_PATH = "usuarios.json";
    private final ObjectMapper mapper = new ObjectMapper();
    /**
     * Busca um usuário pelo e-mail dentro do arquivo JSON.
     * * TODO: O ALUNO DEVE IMPLEMENTAR:
     * 1. Carregar a lista completa de usuários usando o método findAll().
     * 2. Utilizar Java Streams para encontrar o primeiro usuário que possua o e-mail informado.
     * 3. Importante: A comparação de e-mail deve ser 'case-insensitive' (ignorar maiúsculas/minúsculas).
     * 4. Retornar um Optional.of(usuario) se encontrar, ou Optional.empty() se não existir.
     */
    public Optional<Usuario> findByEmail(String email) {
        List<Usuario> usuarios = findAll();
        return usuarios.stream().filter(usuario -> usuario.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    /**
     * Retorna todos os usuários cadastrados no arquivo JSON.
     * * TODO: O ALUNO DEVE IMPLEMENTAR:
     * 1. Verificar se o arquivo definido em 'FILE_PATH' existe no sistema.
     * 2. Se o arquivo NÃO existir, deve retornar uma lista vazia (new ArrayList<>()) para evitar erros.
     * 3. Se existir, usar o 'mapper.readValue' do Jackson para converter o conteúdo do arquivo
     * em uma List<Usuario>. Dica: Use 'new TypeReference<List<Usuario>>(){}'.
     */
    public List<Usuario> findAll() {
        File file = new File(FILE_PATH);
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }

            return mapper.readValue(file,
                    new com.fasterxml.jackson.core.type.TypeReference<List<Usuario>>() {
                    });
        }
        catch (IOException e){
            System.out.println("Erro ao ler arquivo");
            return new ArrayList<>();
        }
    }

    /**
     * Salva um novo usuário no arquivo JSON.
     * * TODO: O ALUNO DEVE IMPLEMENTAR:
     * 1. Obter a lista atual de usuários através do findAll().
     * 2. Verificar se o e-mail do novo usuário já está cadastrado (Regra de Negócio).
     * 3. Adicionar o novo objeto à lista.
     * 4. Utilizar 'mapper.writerWithDefaultPrettyPrinter().writeValue' para gravar a lista
     * atualizada no arquivo, garantindo que o JSON fique legível (formatado).
     */
    public void save(Usuario usuario) throws IOException {
        if (findByEmail(usuario.getEmail()).isPresent()){
            throw new RuntimeException("Email já cadastrado!");
        }

        File file = new File(FILE_PATH);
        List<Usuario> usuarios = findAll();
        usuarios.add(usuario);
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, usuarios);
    }
}
