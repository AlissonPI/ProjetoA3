package Service;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import Util.DatabaseConnection;

public class MenuService {

    public void exibirMenu(Scanner scanner) {
        int opcao;
        do {
            mostrarOpcoes();
            opcao = lerOpcao(scanner);
            processarOpcao(opcao, scanner);
        } while (opcao != 0);
    }
    private void mostrarOpcoes() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Cadastrar veículo");
        System.out.println("2. Baixar veículo");
        System.out.println("3. Transferir veículo");
        System.out.println("4. Consultar propriedade");
        System.out.println("5. Cadastrar proprietário");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private int lerOpcao(Scanner scanner) {
        try {
            return scanner.nextInt();
        } finally {
            scanner.nextLine();
        }
    }
    private void processarOpcao(int opcao, Scanner scanner) {
        switch (opcao) {
            case 1 -> cadastrarVeiculo(scanner);
            case 2 -> baixarVeiculo(scanner);
            case 3 -> transferirVeiculo(scanner);
            case 4 -> consultarPropriedade(scanner);
            case 5 -> cadastrarProprietario(scanner);
            case 0 -> System.out.println("Encerrando sistema...");
            default -> System.out.println("Opção inválida!");
        }
    }
    private boolean existeProprietario(String cpf) {
        String sql = "SELECT 1 FROM proprietario WHERE cpf = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Erro ao verificar proprietário: " + e.getMessage());
            return false;
        }
    }
    private void cadastrarVeiculo(Scanner scanner) {
        System.out.println("\n--- Cadastro de Veículo ---");
        System.out.print("Placa: ");
        String placa = scanner.nextLine();
        System.out.print("CPF do proprietário: ");
        String cpf = scanner.nextLine();

        if (!existeProprietario(cpf)) {
            System.out.println("Proprietário com CPF " + cpf + " não encontrado. Cadastre o proprietário antes.");
            return;
        }

        String sql = "INSERT INTO veiculo (placa, cpf_proprietario) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, placa);
            stmt.setString(2, cpf);
            stmt.executeUpdate();
            System.out.println("Veículo cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar veículo: " + e.getMessage());
        }
    }
    private void baixarVeiculo(Scanner scanner) {
        System.out.println("\n--- Baixa de Veículo ---");
        System.out.print("Placa para baixa: ");
        String placa = scanner.nextLine();

        String sql = "DELETE FROM veiculo WHERE placa = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, placa);
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Veículo removido do sistema!");
            } else {
                System.out.println("Veículo não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao baixar veículo: " + e.getMessage());
        }
    }
    private void transferirVeiculo(Scanner scanner) {
        System.out.println("\n--- Transferência de Veículo ---");
        System.out.print("Placa: ");
        String placa = scanner.nextLine();
        System.out.print("CPF do novo proprietário: ");
        String novoCpf = scanner.nextLine();

        String buscarSql = "SELECT cpf_proprietario FROM veiculo WHERE placa = ?";
        String atualizarSql = "UPDATE veiculo SET cpf_proprietario = ? WHERE placa = ?";
        String inserirTransferencia = "INSERT INTO transferencia (placa, cpf_antigo_proprietario, cpf_novo_proprietario, data_transferencia) VALUES (?, ?, ?, CURDATE())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement buscarStmt = conn.prepareStatement(buscarSql)) {

            buscarStmt.setString(1, placa);
            ResultSet rs = buscarStmt.executeQuery();

            if (rs.next()) {
                String antigoCpf = rs.getString("cpf_proprietario");

                try (PreparedStatement updateStmt = conn.prepareStatement(atualizarSql);
                     PreparedStatement insertTransfer = conn.prepareStatement(inserirTransferencia)) {

                    updateStmt.setString(1, novoCpf);
                    updateStmt.setString(2, placa);
                    updateStmt.executeUpdate();

                    insertTransfer.setString(1, placa);
                    insertTransfer.setString(2, antigoCpf);
                    insertTransfer.setString(3, novoCpf);
                    insertTransfer.executeUpdate();

                    System.out.println("Transferência realizada com sucesso!");
                }
            } else {
                System.out.println("Veículo não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro na transferência: " + e.getMessage());
        }
    }
    private void consultarPropriedade(Scanner scanner) {
        System.out.println("\n--- Consulta de Propriedade ---");
        System.out.print("Placa: ");
        String placa = scanner.nextLine();

        String sql = """
        SELECT p.cpf
        FROM veiculo v
        JOIN proprietario p ON v.cpf_proprietario = p.cpf
        WHERE v.placa = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("CPF do Proprietário: " + rs.getString("cpf"));
            } else {
                System.out.println("Veículo não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro na consulta: " + e.getMessage());
        }
    }
    private void cadastrarProprietario(Scanner scanner) {
        System.out.println("\n--- Cadastro de Proprietário ---");
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        String sql = "INSERT INTO proprietario (cpf, senha) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            stmt.setString(2, senha); // ideal seria salvar hash da senha, mas vamos manter simples
            stmt.executeUpdate();
            System.out.println("Proprietário cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar proprietário: " + e.getMessage());
        }
    }
}
