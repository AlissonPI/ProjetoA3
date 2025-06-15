import java.util.Scanner;
import Util.ValidacaoUtil;
import Service.MenuService;

public class SistemaDetran {
    public void iniciarSistema() {
        try (Scanner scanner = new Scanner(System.in)) {
            if (realizarLogin(scanner)) {
                new MenuService().exibirMenu(scanner);
            } else {
                System.out.println("Falha no login. Encerrando sistema.");
            }
        }
    }

    private boolean realizarLogin(Scanner scanner) {
        System.out.println("=== PORTAL DETRAN ===");
        String cpf = ValidacaoUtil.validarFormatoCPF(scanner);
        String senha = ValidacaoUtil.validarComplexidadeSenha(scanner);
        return true;
    }
}
