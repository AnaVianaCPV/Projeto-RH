package utils;

public class CpfUtils {
    public static String normalizar(String cpf) {
        return cpf != null ? cpf.replaceAll("\\D", "") : null;
    }
}

