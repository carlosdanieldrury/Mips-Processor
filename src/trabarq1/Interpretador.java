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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;


public class Interpretador {
    
     private static ArrayList instructions = new ArrayList();
     private static String opCode = null;
     private static String rs = null;
    private static String rt = null;
    private static String rd = null;
    private static String immediate = null;
    private static String rest = null;
     private static boolean continueExecuting = true;
     private static Pattern pattern = Pattern.compile("\\s+");
     private static Matcher matcher = null;
     private static String matcherOut = null;
     private static String[] matcherArrayOut = null;
     private static String[] IDArray;
     private static int currentInstruction = 0;
    private static int currentIntPC = 0;
    private static int i = 0;
    private static String[] memoryData = new String[262144];
    private static String[] registers = new String[32];
    private static File arquivo = null;
    private static String instrucaoIF = null;
    private static String RegHI = null;
    private static String RegLO = null;
    private static String regA = null;
    private static String regB = null;
    
     private static HashMap<String, Integer> hm = new HashMap<String, Integer>();
     
     
     // Cada instrucao tem um numero HASH
     private static void fillHm() {
        hm.put("add", 0);
        hm.put("addi", 1);
        hm.put("sub", 2);
        hm.put("and", 3);
        hm.put("or", 4);
        hm.put("nor", 5);
        hm.put("mult", 6);
        hm.put("lw", 7);
        hm.put("sw", 8);
        hm.put("lb", 9);
        hm.put("sb", 10);
        hm.put("beq", 11);
        hm.put("bne", 12);
        hm.put("bge", 13);
        hm.put("slt", 14);
        hm.put("j", 15);
        hm.put("jal", 16);
        hm.put("jr", 17);
        hm.put("mul", 18);
        hm.put("slti", 19);
    }

    
     // Retorna o valor da String IntrucaoIF que está sendo executada.
    public static String getInstrucaoIF() {
        return instrucaoIF;
    }

    // Seta o valor da String que indica instrução atual na View
    public static void setInstrucaoIF(String instrucaoIF) {
        Interpretador.instrucaoIF = instrucaoIF;
    }

   
     
     
     // retorna um padrão da instrução buscada, ha um tratamento de caracteres
     public static String[] getArrayFromInstruction() {
        matcherOut = matcherOut.replace(",", "");
        matcherOut = matcherOut.replace("(", " ");
        matcherOut = matcherOut.replace(")", "");
        matcherArrayOut = matcherOut.split("\\s");

        ++currentInstruction;

        for (i = 0; i < matcherArrayOut.length; i++) {
            matcherArrayOut[i] = utils.remove$(matcherArrayOut[i]);
        }

        return matcherArrayOut;
    }
    
    
     // retorna o numero da instrucao atual
     public static int getCurrentInstruction() {
        return currentInstruction;
    }
     
     // seta qual a instrucao que deve ser executada
     public static void setCurrentInstruction(int num) {
        currentInstruction = num;
    }

     // Pega a proxima instrucao
    public static String getNextInstruction() {
        if (currentInstruction >= instructions.size()) {
            currentInstruction = 0;

            alertDialog("Este arquivo já foi totalmente executado ou a instrução" +
                    " referenciada não existe.\nClique em 'Ok' para reexecutar.");
        }
        // padroniza a instrucao e divide depois em arrays
        matcher = pattern.matcher(instructions.get(currentInstruction).toString());
        
        
        if (matcher.find()) {
            matcherOut = matcher.replaceAll(" ");
            matcherOut = matcherOut.trim();
        }

        return matcherOut;
    }
    
    private static void errorDialog(String message) {
        JOptionPane.showConfirmDialog(null, message,
                "Erro", -1, 0);
        JOptionPane.showConfirmDialog(null, "Execução interrompida.", "Alerta", -1, 2);

        continueExecuting = false;
    }
    
    
    // Associa com o arquivo que o usuário escolheu na view
     public static void setArquivo(File arq) {
        arquivo = arq;

        fillHm();
    }
    
    // Tambem associa com o arquivo que o usuário escolheu.
    public static String getFileContents() {
        try{
            FileInputStream fstream = new FileInputStream(arquivo.getCanonicalPath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine = null;
            String out = "";

            while ((strLine = br.readLine()) != null) {
                out = out + strLine + "\n";
                instructions.add(strLine);
            }

            in.close();

            return out;
        } catch (Exception e){
            System.out.println(e.toString());

            errorDialog("Erro ao tentar ler o arquivo.\nContate o administrador do sistema.");

            return null;
        }
    }

    // utilizado na funcao Mult
     public static void setRegHI(String data) {
        RegHI = data;
    }

     // utilizado na funcao Mult
    public static void setRegLO(String data) {
        RegLO = data;
    }


    private static void alertDialog(String message) {
        JOptionPane.showConfirmDialog(null, message, "Alerta", -1, 2);
    }

    // Limpa a memoria
    private static void clearMemory() {
        i = 0;

        for (i = 0; i < 65536; i++) {
            memoryData[i] = null;
        }
    }

    // limpa o banco de registradores
    private static void clearRegisters() {
        i = 0;

        for (i = 0; i < 32; i++) {
            registers[i] = null;
        }
    }
    
    // limpa memoria e banco de registradores
     public static void clearAll() {
        continueExecuting = true;
        currentInstruction = 0;

        instructions.clear();
        
        Window.instrucaoAtual("");

        clearMemory();
        clearRegisters();
    }
     
     
     // metodo para salvar na memoria, utiliza o parametro para endereco e o dado para ser salvo
     public static void save2mem(int addr, String data) {
        if (addr > -1 && addr < 262144) {
            if (data.length() == 32) {
                addr = addr / 4;
                addr = addr * 4;

                memoryData[addr] = data.substring(0, 8);
                memoryData[addr + 1] = data.substring(8, 16);
                memoryData[addr + 2] = data.substring(16, 24);
                memoryData[addr + 3] = data.substring(24, 32);
            } else if (data.length() == 8) {
                memoryData[addr] = data;
            } else {
                errorDialog("Tamanho dos dados é diferente de 32bits e de 8bits.");
            }
        } else {
            errorDialog("O endereço de memória '" + addr + "' é inválido.");
        }
    }

     // Metodo para salvar o dado no banco de registradores
    public static void save2reg(int addr, String data) {
        --addr;

        if (addr > -1 && addr < 32) {
            if (data.length() == 32) {
                registers[addr] = data;
            } else {
                errorDialog("Tamanho dos dados é diferente de 32bits e de 8bits.");
            }
        } else {
            errorDialog("O endereço de registrador '" + addr + "' é inválido.");
        }
    }

    // fazer leitura da memória
    public static String readMem(int addr, boolean Byte) {
        if (!Byte) {
            addr = addr / 4;
            addr = addr * 4;

            if (addr > -1 && addr < 262144) {
                if (memoryData[addr] == null) {
                    return "00000000000000000000000000000000";
                } else {
                    return memoryData[addr] +
                            memoryData[addr + 1] +
                            memoryData[addr + 2] +
                            memoryData[addr + 3];
                }
            } else {
                errorDialog("O endereço de memória '" + addr + "' é inválido.");

                return null;
            }
        } else {
            if (addr > -1 && addr < 262144) {
                if (memoryData[addr] == null) {
                    return "00000000";
                } else {
                    return memoryData[addr];
                }
            } else {
                errorDialog("O endereço de memória '" + addr + "' é inválido.");

                return null;
            }
        }
    }

    // leitura do dado do registrador que e passado como parametro
    public static String readReg(int addr) {
        --addr;

        if (addr == -1) {
            return "00000000000000000000000000000000";
        } else if (addr > -1 && addr < 32) {
            if (registers[addr] == null) {
                return "00000000000000000000000000000000";
            } else {
                return registers[addr];
            }
        } else {
            errorDialog("O endereço de registrador '" + addr + "' é inválido.");

            return null;
        }
    }

     
     // roda a instruçao e completa as tabelas ID, EXE,MEM e WB
     public static void RunInstruction() {
        if (continueExecuting) {
            
            // Busca a instrução
            instrucaoIF = getNextInstruction();
            
           
            // Seta na View a instrução Atual
            Window.instrucaoAtual(instrucaoIF);
            // IDARRAY recebe a instrução já dividida em um array de 4 partes dependendo do tipo de instrucao
            // parte 1 = instrucao
            // parte 2 = Rs
            // parte 3 = Rt
            // parte 4 = Rd ou imediato
            IDArray = getArrayFromInstruction();

            // PC + 4
            currentIntPC = (currentInstruction - 1) * 4;
            // Mostra na view o valor do PC
            Window.setPc(currentIntPC);
            // Coloca o valor do PC no banco de registradores
            Window.setRegister(utils.to32bits(utils.dec2bin(currentIntPC)), 0);

            // Procura qual o nome da instrução no case através do Hash da instrução
            switch (hm.get(IDArray[0])) {
                case 0:
                    // add
                    opCode = "000000";
                    rd = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rs = utils.to5bits(utils.dec2bin(IDArray[2]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[3]));
                    rest = "00000100000";

                    Window.setInstrucaoAtualBin(opCode + rs + rt + rd + rest);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("1", "1", "1", "0", "0", "0", "0", "1", "1", "0", regA , regB , "00" );
               
                    
                    Funcoes.addSub(IDArray[2], IDArray[3], IDArray[1], false);

                    break;
                case 1:
                    // addi
                    opCode = "001000";
                    rt = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rs = utils.to5bits(utils.dec2bin(IDArray[2]));
                    immediate = utils.to16bits(utils.dec2bin(IDArray[3]));

                    Window.setInstrucaoAtualBin(opCode + rs + rt + immediate);
                                       
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("0", "1", "1", "0", "0", "0", "0", "1", "1", "0", regA , regB , "10" );
  
                    Funcoes.addi(IDArray[3], IDArray[2], IDArray[1]);

                    break;
                case 2:
                    // sub
                    opCode = "000000";
                    rd = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rs = utils.to5bits(utils.dec2bin(IDArray[2]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[3]));
                    rest = "00000100010";

                    Window.setInstrucaoAtualBin(opCode + rs + rt + rd + rest);
                    
                    
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("1", "1", "1", "0", "0", "0", "0", "1", "1", "0", regA , regB , "00" );
   
                    Funcoes.addSub(IDArray[2], IDArray[3], IDArray[1], true);

                    break;
                case 3:
                    // and
                    opCode = "000000";
                    rd = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rs = utils.to5bits(utils.dec2bin(IDArray[2]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[3]));
                    rest = "00000100100";

                    Window.setInstrucaoAtualBin(opCode + rs + rt + rd + rest);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("1", "1", "1", "0", "0", "0", "0", "1", "1", "0", regA , regB , "00" );

                    Funcoes.andOrNor(IDArray[2], IDArray[3], IDArray[1], 0);

                    break;
                case 4:
                    // or
                    opCode = "000000";
                    rd = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rs = utils.to5bits(utils.dec2bin(IDArray[2]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[3]));
                    rest = "00000100101";

                    Window.setInstrucaoAtualBin(opCode + rs + rt + rd + rest);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("1", "1", "1", "0", "0", "0", "0", "1", "1", "0", regA , regB , "00" );
   
                    Funcoes.andOrNor(IDArray[2], IDArray[3], IDArray[1], 1);

                    break;
                case 5:
                    // nor
                    opCode = "000000";
                    rd = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rs = utils.to5bits(utils.dec2bin(IDArray[2]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[3]));
                    rest = "00000100111";

                    Window.setInstrucaoAtualBin(opCode + rs + rt + rd + rest);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("1", "1", "1", "0", "0", "0", "0", "1", "1", "0", regA , regB , "00" );

                    Funcoes.andOrNor(IDArray[2], IDArray[3], IDArray[1], 2);

                    break;
                case 6:
                    // mult
                    opCode = "000000";
                    rs = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[2]));
                    rest = "0000000000011000";

                    Window.setInstrucaoAtualBin(opCode + rs + rt + rest);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("1", "1", "1", "0", "0", "0", "0", "1", "1", "0", regA , regB , "00" );
 
                   Funcoes.mult(IDArray[1], IDArray[2]);

                    break;
               
                case 7:
                    // lw
                    opCode = "100011";
                    rs = utils.to5bits(utils.dec2bin(IDArray[3]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[1]));
                    immediate = utils.to16bits(utils.dec2bin(IDArray[2]));

                    Window.setInstrucaoAtualBin(opCode + rs + rt + immediate);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("0", "1", "1", "1", "0", "1", "1", "0", "1", "0", regA , regB , "10" );
                                
                    Funcoes.lwSw(IDArray[3], IDArray[1], IDArray[2], false);

                    break;
                case 8:
                    // sw
                    opCode = "101011";
                    rs = utils.to5bits(utils.dec2bin(IDArray[3]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[1]));
                    immediate = utils.to16bits(utils.dec2bin(IDArray[2]));

                    Window.setInstrucaoAtualBin(opCode + rs + rt + immediate);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("0", "0", "1", "0", "1", "0", "1", "0", "1", "0", regA , regB , "10" );
       
                    Funcoes.lwSw(IDArray[3], IDArray[1], IDArray[2], true);

                    break;
                case 9:
                    // lb
                    opCode = "100000";
                    rs = utils.to5bits(utils.dec2bin(IDArray[3]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[1]));
                    immediate = utils.to16bits(utils.dec2bin(IDArray[2]));

                    Window.setInstrucaoAtualBin(opCode + rs + rt + immediate);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("0", "1", "1", "1", "0", "1", "1", "0", "1", "0", regA , regB , "10" );
              
                    Funcoes.lbSb(IDArray[3], IDArray[1], IDArray[2], false);

                    break;
                case 10:
                    // sb
                    opCode = "101000";
                    rs = utils.to5bits(utils.dec2bin(IDArray[3]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[1]));
                    immediate = utils.to16bits(utils.dec2bin(IDArray[2]));

                    Window.setInstrucaoAtualBin(opCode + rs + rt + immediate);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("0", "0", "1", "0", "1", "0", "1", "0", "1", "0", regA , regB , "10" );
          
                    Funcoes.lbSb(IDArray[3], IDArray[1], IDArray[2], true);

                    break;
                case 11:
                    // beq
                    opCode = "000100";
                    rs = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[2]));
                    immediate = utils.to16bits(utils.dec2bin(IDArray[3]));

                    Window.setInstrucaoAtualBin(opCode + rs + rt + immediate);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("0", "0", "1", "0", "0", "0", "0", "1", "0", "1", regA , regB , "10" );
      
                    Funcoes.beqBneBge(IDArray[1], IDArray[2], IDArray[3], 0);

                    break;
                case 12:
                    // bne
                    opCode = "000101";
                    rs = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[2]));
                    immediate = utils.to16bits(utils.dec2bin(IDArray[3]));

                    Window.setInstrucaoAtualBin(opCode + rs + rt + immediate);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("0", "0", "1", "0", "0", "0", "0", "1", "0", "1", regA , regB , "10" );
        
                    Funcoes.beqBneBge(IDArray[1], IDArray[2], IDArray[3], 1);

                    break;
                case 13:
                    // bge (inexistente na referência)
                    opCode = "000001";
                    rs = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[2]));
                    immediate = utils.to16bits(utils.dec2bin(IDArray[3]));

                    Window.setInstrucaoAtualBin(opCode + rs + rt + immediate);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("0", "0", "1", "0", "0", "0", "0", "1", "0", "1", regA , regB , "10" );
          
                    Funcoes.beqBneBge(IDArray[1], IDArray[2], IDArray[3], 2);

                    break;
                case 14:
                    // slt
                    opCode = "000000";
                    rd = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rs = utils.to5bits(utils.dec2bin(IDArray[2]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[3]));
                    rest = "00000101010";

                    Window.setInstrucaoAtualBin(opCode + rs + rt + rd + rest);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("1", "1", "1", "0", "0", "0", "0", "1", "1", "0", regA , regB , "00" );
       
                    Funcoes.slt(IDArray[2], IDArray[3], IDArray[1]);

                    break;
                case 15:
                    // j
                    opCode = "000010";
                    immediate = utils.to26bits(utils.dec2bin(IDArray[1]));

                    Window.setInstrucaoAtualBin(opCode + immediate);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("0", "0", "0", "0", "0", "0", "0", "1", "1", "0", regA , regB , "11" );
       
                    Funcoes.jJal(IDArray[1], false);

                    break;
                case 16:
                    // jal
                    opCode = "000011";
                    immediate = utils.to26bits(utils.dec2bin(IDArray[1]));

                    Window.setInstrucaoAtualBin(opCode + immediate);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("0", "0", "0", "0", "0", "0", "0", "1", "1", "0", regA , regB , "11" );
      
                    Funcoes.jJal(IDArray[1], true);

                    break;
                case 17:
                    // jr
                    opCode = "000000";
                    rs = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rest = "000000000000000001000";

                    Window.setInstrucaoAtualBin(opCode + rs + rest);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("1", "1", "1", "0", "0", "0", "0", "1", "1", "0", regA , regB , "00" );
       
                    Funcoes.jr(IDArray[1]);

                    break;
                 case 18:
                    // mul
                    opCode = "000000";
                    rd = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rs = utils.to5bits(utils.dec2bin(IDArray[2]));
                    rt = utils.to5bits(utils.dec2bin(IDArray[3]));
                    rest = "00000011000";

                    Window.setInstrucaoAtualBin(opCode + rs + rt + rd + rest);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("1", "1", "1", "0", "0", "0", "0", "1", "1", "0", regA , regB , "00" );
                    Window.setWR(String.valueOf(rs+rt), String.valueOf(rd));
                    
                    Funcoes.mul(IDArray[2], IDArray[3], IDArray[1]);

                    break;
                 case 19:
                    // slti
                    opCode = "001000";
                    rt = utils.to5bits(utils.dec2bin(IDArray[1]));
                    rs = utils.to5bits(utils.dec2bin(IDArray[2]));
                    immediate = utils.to16bits(utils.dec2bin(IDArray[3]));

                    Window.setInstrucaoAtualBin(opCode + rs + rt + immediate);
                    regA = IDArray[2]; 
                    regB = IDArray[1];
                    Window.setSinaisDeControle("0", "1", "1", "0", "0", "0", "0", "1", "1", "0", regA , regB , "10" );
            
                    Window.setWR(String.valueOf(rs+immediate), String.valueOf(rd));

                    Funcoes.slti(IDArray[3], IDArray[2], IDArray[1]);

                    break;
            }
        } else {
            alertDialog("A execução já foi previamente interrompida.\nFeche o"
                    + " aquivo e abra-o novamente.");
        }
     }
    
}
