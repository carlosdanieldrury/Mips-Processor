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

public class Funcoes {
    
    // Funcao addi
    public static void addi(String simm, String srs, String srt) {
        int rs = Integer.parseInt(srs);
        int rt = Integer.parseInt(srt);
        int imm = Integer.parseInt(simm);

        srs = Interpretador.readReg(rs);
        rs = utils.bin2dec(srs);

        int result = rs + imm;

        simm = utils.dec2bin(result);
        simm = utils.to32bits(simm);

        Interpretador.save2reg(rt, simm);

        // Grava no banco de Registros
        Window.setRegister(simm, rt + 2);
        // Grava os sinais do estagio de Execucao
        Window.setSinalEX(Integer.toString(result), "0");
        // Zera os dados do MDR
        Window.setMDR("0");
        // Coloca o valor do registrador de destino.
        Window.setWR(Integer.toString(result), Integer.toString(rt));
    }

    // Funcao add e sub
    public static void addSub(String srs, String srt, String srd, boolean SUB) {
        int rs = Integer.parseInt(srs);
        int rt = Integer.parseInt(srt);
        int rd = Integer.parseInt(srd);

        srs = Interpretador.readReg(rs);
        srt = Interpretador.readReg(rt);

        rs = utils.bin2dec(srs);
        rt = utils.bin2dec(srt);
        
        int result;
        

        if (!SUB) {
            result = rs + rt;
        } else {
            result = rs - rt;
        }

        srd = utils.dec2bin(result);
        srd = utils.to32bits(srd);

        Interpretador.save2reg(rd, srd);

        Window.setRegister(srd, rd + 2);
        Window.setSinalEX(Integer.toString(result), "0");
        Window.setMDR("0");
        Window.setWR(Integer.toString(result), Integer.toString(rd));
    }

    
    // And ou OR ou Nor
    public static void andOrNor(String srs, String srt, String srd, int func) {
        int rs = Integer.parseInt(srs);
        int rt = Integer.parseInt(srt);
        int rd = Integer.parseInt(srd);

        srs = Interpretador.readReg(rs);
        srt = Interpretador.readReg(rt);

        String result = "";

        switch (func) {
            case 0:
                for (int i = 0; i < 32; i++) {
                    if ( (srs.charAt(i) == '1') && (srt.charAt(i) == '1') ) {
                        result = result + "1";
                    } else {
                        result = result + "0";
                    }
                }

                break;
            case 1:
                for (int i = 0; i < 32; i++) {
                    if ( (srs.charAt(i) == '0') && (srt.charAt(i) == '0') ) {
                        result = result + "0";
                    } else {
                        result = result + "1";
                    }
                }

                break;
            case 2:
                for (int i = 0; i < 32; i++) {
                    if ( (srs.charAt(i) == '0') && (srt.charAt(i) == '0') ) {
                        result = result + "0";
                    } else {
                        result = result + "1";
                    }
                }

                srs = result;
                result = "";

                for (int i = 0; i < 32; i++) {
                    if (srs.charAt(i) == '0') {
                        result = result + "1";
                    } else {
                        result = result + "0";
                    }
                }

                break;
        }

        Interpretador.save2reg(rd, result);

        Window.setRegister(result, rd + 2);
        Window.setSinalEX(result, "0");
        Window.setMDR("0");
        Window.setWR(result, Integer.toString(rd));
    }

    // Funcao mult
    public static void mult(String SRsDec, String SRtDec) {
        int rs = Integer.parseInt(SRsDec);
        int rt = Integer.parseInt(SRtDec);

        SRsDec = Interpretador.readReg(rs);
        SRtDec = Interpretador.readReg(rt);

        rs = utils.bin2dec(SRsDec);
        rt = utils.bin2dec(SRtDec);

        int result = rs * rt;
        SRtDec = utils.dec2bin(result);

        SRtDec = utils.to64bits(SRtDec);

        SRsDec = SRtDec.substring(0, 32);
        SRtDec = SRtDec.substring(32, 64);

        Interpretador.setRegHI(SRsDec);
        Interpretador.setRegLO(SRtDec);

        Window.setRegister(SRsDec, 1);
        Window.setRegister(SRtDec, 2);
        Window.setSinalEX(Integer.toString(result), "0");
        Window.setMDR("0");
        Window.setWR(Integer.toString(result), Integer.toString(rt));
    }
    
    // Funcao mul - temos um registrador de destino
    public static void mul(String SRsDec, String SRtDec, String SRdDec) {
        int rs = Integer.parseInt(SRsDec);
        int rt = Integer.parseInt(SRtDec);
        int rd = Integer.parseInt(SRdDec);

        SRsDec = Interpretador.readReg(rs);
        SRtDec = Interpretador.readReg(rt);

        rs = utils.bin2dec(SRsDec);
        rt = utils.bin2dec(SRtDec);

        int result = rs * rt;
        String SRd = utils.dec2bin(result);

        
        Interpretador.setRegHI(SRsDec);
        Interpretador.setRegLO(SRtDec);
        Interpretador.save2reg(rd, SRd);

        Window.setSinalEX(Integer.toString(result), "0");
        Window.setMDR("0");
        Window.setWR(Integer.toString(result), Integer.toString(rd));
    }

    // LW ou SW
    public static void lwSw(String SRsDec, String SRtDec, String Simm, boolean SW) {
        int rs = Integer.parseInt(SRsDec);
        int rt = Integer.parseInt(SRtDec);
        int imm = Integer.parseInt(Simm);

        SRsDec = Interpretador.readReg(rs);

        rs = utils.bin2dec(SRsDec);

        if (!SW) {
            // load
            SRtDec = Interpretador.readMem(rs + imm, false);

            Interpretador.save2reg(rt, SRtDec);

            Window.setRegister(SRtDec, rt + 2);
            // Somente aqui colocamos valor no campo MDR da View
            Window.setMDR(Integer.toString(utils.bin2dec(SRtDec)));
            Window.setWR(Integer.toString(utils.bin2dec(SRtDec)),Integer.toString(rt));
            
        } else {
            SRtDec = Interpretador.readReg(rt);

            Interpretador.save2mem(rs + imm, SRtDec);
            
            Window.setMDR("0");
            Window.setMemoryStatus("'" + SRtDec + "' salvo no endereço " + String.valueOf(rs + imm));
            Window.setWR("0","0");
            
        }
        Window.setSinalEX(Integer.toString(rs+imm), "0");
    }

    // LB ou SB
    public static void lbSb(String SRsDec, String SRtDec, String Simm, boolean SB) {
        int rs = Integer.parseInt(SRsDec);
        int rt = Integer.parseInt(SRtDec);
        int imm = Integer.parseInt(Simm);

        SRsDec = Interpretador.readReg(rs);

        rs = utils.bin2dec(SRsDec);

        if (!SB) {
            // Load
            SRtDec = Interpretador.readMem(rs + imm, true);
            SRtDec = utils.to32bits(SRtDec);

            Interpretador.save2reg(rt, SRtDec);

            Window.setRegister(SRtDec, rt + 2);
            // Somente aqui colocamos valor no campo MDR da View
            Window.setMDR(Integer.toString(utils.bin2dec(SRtDec)));
            Window.setWR(Integer.toString(utils.bin2dec(SRtDec)),Integer.toString(rt));
        } else {
            // Store
            SRtDec = Interpretador.readReg(rt);
            SRtDec = SRtDec.substring(24, 32);

            Interpretador.save2mem(rs + imm, SRtDec);
            Window.setMDR("0");
            Window.setMemoryStatus("'" + SRtDec + "' salvo no endereço " + String.valueOf(rs + imm));
            Window.setWR("0","0");
        }
        Window.setSinalEX(Integer.toString(rs+imm), "0");
        
        
    }

    // Beq ou Bne ou Bge
    public static void beqBneBge(String SRsDec, String SRtDec, String Simm, int func) {
        int rs = Integer.parseInt(SRsDec);
        int rt = Integer.parseInt(SRtDec);
        int imm = Integer.parseInt(Simm);

        SRsDec = Interpretador.readReg(rs);
        SRtDec = Interpretador.readReg(rt);

        switch (func) {
            case 0:
                if (SRsDec.equals(SRtDec)) {
                    Interpretador.setCurrentInstruction(Interpretador.getCurrentInstruction() - 1 + imm);
                    Window.setSinalEX(Integer.toString(rs-rt), "1");
                }

                break;
            case 1:
                if (!SRsDec.equals(SRtDec)) {
                    Interpretador.setCurrentInstruction(Interpretador.getCurrentInstruction() - 1 + imm);
                    Window.setSinalEX(Integer.toString(rs-rt), "1");
                } 

                break;
            case 2:
                rs = utils.bin2dec(SRsDec);
                rt = utils.bin2dec(SRtDec);

                if (rs > rt) {
                    Interpretador.setCurrentInstruction(Interpretador.getCurrentInstruction() - 1 + imm);

                    Window.setSinalEX(Integer.toString(rs-rt), "1");
                } else if (rs == rt) {
                    Interpretador.setCurrentInstruction(Interpretador.getCurrentInstruction() - 1 + imm);

                    Window.setSinalEX(Integer.toString(rs-rt), "1");
                } else {
                   
                    Window.setSinalEX(Integer.toString(rs-rt), "0");
                }

                break;
        }
        Window.setMDR("0");
        Window.setWR("0","0");
    }

    // slt
    public static void slt(String SRsDec, String SRtDec, String SRdDec) {
        int rs = Integer.parseInt(SRsDec);
        int rt = Integer.parseInt(SRtDec);
        int rd = Integer.parseInt(SRdDec);

        SRsDec = Interpretador.readReg(rs);
        SRtDec = Interpretador.readReg(rt);

        rs = utils.bin2dec(SRsDec);
        rt = utils.bin2dec(SRtDec);

        if (rs < rt) {
            Interpretador.save2reg(rd, "00000000000000000000000000000001");

            Window.setRegister("00000000000000000000000000000001", rd + 2);
            Window.setWR("1",Integer.toString(rd));
        } else {
            Interpretador.save2reg(rd, "00000000000000000000000000000000");

            Window.setRegister("00000000000000000000000000000000", rd + 2);
            Window.setWR("0",Integer.toString(rd));
        }
        Window.setMDR("0");
    }
    
    
    //slti
    public static void slti(String simm, String srs, String srt) {
        int rs = Integer.parseInt(srs);
        int rt = Integer.parseInt(srt);
        int imm = Integer.parseInt(simm);
       
        rs = utils.bin2dec(srs);

        if (rs < imm) {
              Interpretador.save2reg(rs, "00000000000000000000000000000001");

            Window.setRegister("00000000000000000000000000000001", rs + 2);
            Window.setSinalEX("1", "0");
            Window.setWR("1",Integer.toString(rs));
        } else {
            Interpretador.save2reg(rt, "00000000000000000000000000000000");

            Window.setRegister("00000000000000000000000000000000", rs + 2);
            Window.setSinalEX("0", "0");
            Window.setWR("0",Integer.toString(rs));
        }
        Window.setMDR("0");
    }

    // Jump ou Jump and link
    public static void jJal(String Simm, boolean JAL) {
        int imm = Integer.parseInt(Simm);

        if (JAL) {
            String sra = utils.to32bits(utils.dec2bin(Interpretador.getCurrentInstruction()));

            Interpretador.save2reg(32, sra);

            Window.setRegister(sra, 34);
        }

        Interpretador.setCurrentInstruction(imm);
        Window.setMDR("0");
        Window.setWR("0","0");
    }

    // jr
    public static void jr(String SRsDec) {
        int rs = Integer.parseInt(SRsDec);

        SRsDec = Interpretador.readReg(rs);

        rs = utils.bin2dec(SRsDec);

        Interpretador.setCurrentInstruction(rs);
        Window.setMDR("0");
        Window.setWR("0","0");
    }
}
