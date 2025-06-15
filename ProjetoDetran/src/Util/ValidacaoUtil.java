package Util;

import java.util.Scanner;
import java.util.regex.Pattern;

public class ValidacaoUtil {
    public static String validarFormatoCPF(Scanner scanner) {
        String cpf;
        do {
            System.out.print("CPF (apenas números): ");
            cpf = scanner.nextLine().trim();
        } while (!isCPFValido(cpf));
        return cpf;
    }

    public static String validarComplexidadeSenha(Scanner scanner) {
        String senha;
        do {
            System.out.print("Senha: ");
            senha = scanner.nextLine().trim();
        } while (!isSenhaValida(senha));
        return senha;
    }

    private static boolean isCPFValido(String cpf) {
        if (cpf.length() != 11 || !cpf.matches("\\d+")) {
            System.out.println("Erro: CPF deve ter exatamente 11 dígitos numéricos");
            return false;
        }
        return true;
    }

    private static boolean isSenhaValida(String senha) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
        boolean valida = Pattern.matches(regex, senha);

        if (!valida) {
            System.out.println("""
                Senha inválida! Requisitos:
                - Mínimo 6 caracteres
                - 1 letra minúscula
                - 1 letra maiúscula
                - 1 número
                - 1 caractere especial (@$!%*?&)""");
        }
        return valida;
    }
}
