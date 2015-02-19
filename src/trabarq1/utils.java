/*
 * Trabalho 2 - Arquitetura de Computadores - GCC-117
 * 
 * Simulador de um processador multiciclo
 * 
 * Alunos: Carlos Daniel Drury
 *	   Elder Marques
 *         Gabriel Almeida Miranda
 *         Leonardo Almeida de Araújo
 *
 * Implementação na linguagem JAVA
 * IDE utilizada para execução e compilação: NetBeans 7.3
 * Plataformas testadas: Windows 7 e Ubuntu 12.10
 *
 */

package trabarq1;

public class utils {
     private static final String ZEROS = "0000000000000000000000000000000000000000000000000000000000000000";

    public static String getZEROS(int num) {
        return ZEROS.substring(0, num);
    }

    public static String dec2bin(int num) {
        return Integer.toBinaryString(num);
    }
    

    public static String dec2bin(String snum) {
        int num = Integer.parseInt(snum);

        return Integer.toBinaryString(num);
    }

    public static int bin2dec(String num) {
        return Integer.parseInt(num, 2);
    }

    public static String to64bits(String num) {
        return ZEROS.substring(0, 64 - num.length()).concat(num);
    }

    public static String to32bits(String num) {
        return ZEROS.substring(0, 32 - num.length()).concat(num);
    }

    public static String to26bits(String num) {
        return ZEROS.substring(0, 26 - num.length()).concat(num);
    }

    public static String to16bits(String addr) {
        return ZEROS.substring(0, 16 - addr.length()).concat(addr);
    }

    public static String to5bits(String addr) {
        return ZEROS.substring(0, 5 - addr.length()).concat(addr);
    }

    public static String remove$(String ent) {
        return ent.replace("$", "");
    }
}
